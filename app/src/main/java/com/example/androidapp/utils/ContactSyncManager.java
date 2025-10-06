package com.example.androidapp.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.models.ContactSync;
import com.example.androidapp.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Manager class for synchronizing device contacts with the application
 * Handles contact import, matching with registered users, and privacy settings
 */
public class ContactSyncManager {
    
    private static final String TAG = "ContactSyncManager";
    
    private Context context;
    private AppDatabase database;
    private SessionManager sessionManager;
    
    public ContactSyncManager(Context context) {
        this.context = context;
        this.database = AppDatabase.getDatabase(context);
        this.sessionManager = new SessionManager(context);
    }
    
    /**
     * Check if contacts permission is granted
     */
    public boolean hasContactsPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Sync all device contacts with the application
     */
    public CompletableFuture<SyncResult> syncAllContacts() {
        return CompletableFuture.supplyAsync(() -> {
            if (!hasContactsPermission()) {
                return new SyncResult(false, "Contacts permission not granted", 0, 0, 0);
            }
            
            int userId = sessionManager.getCurrentUserId();
            if (userId == -1) {
                return new SyncResult(false, "User not logged in", 0, 0, 0);
            }
            
            try {
                List<ContactSync> deviceContacts = readDeviceContacts(userId);
                List<ContactSync> newContacts = new ArrayList<>();
                List<ContactSync> updatedContacts = new ArrayList<>();
                
                for (ContactSync contact : deviceContacts) {
                    ContactSync existingContact = database.contactSyncDao()
                            .getContactByIdentifier(userId, contact.getContactIdentifier());
                    
                    if (existingContact == null) {
                        // New contact
                        contact.setSyncStatus(ContactSync.STATUS_SYNCED);
                        contact.setLastSyncDate(new Date());
                        newContacts.add(contact);
                    } else {
                        // Update existing contact if information changed
                        if (hasContactChanged(existingContact, contact)) {
                            existingContact.setDisplayName(contact.getDisplayName());
                            existingContact.setEmail(contact.getEmail());
                            existingContact.setPhoneNumber(contact.getPhoneNumber());
                            existingContact.setPhotoUri(contact.getPhotoUri());
                            existingContact.setLastSyncDate(new Date());
                            existingContact.setUpdatedDate(new Date());
                            existingContact.setSyncStatus(ContactSync.STATUS_SYNCED);
                            updatedContacts.add(existingContact);
                        }
                    }
                }
                
                // Insert new contacts
                if (!newContacts.isEmpty()) {
                    database.contactSyncDao().insertAll(newContacts);
                }
                
                // Update existing contacts
                for (ContactSync contact : updatedContacts) {
                    database.contactSyncDao().update(contact);
                }
                
                // Match contacts with registered users
                int matchedUsers = matchContactsWithRegisteredUsers(userId);
                
                return new SyncResult(true, "Sync completed successfully", 
                        newContacts.size(), updatedContacts.size(), matchedUsers);
                        
            } catch (Exception e) {
                Log.e(TAG, "Error syncing contacts", e);
                return new SyncResult(false, "Error syncing contacts: " + e.getMessage(), 0, 0, 0);
            }
        }, AppDatabase.databaseWriteExecutor);
    }
    
    /**
     * Read contacts from device
     */
    private List<ContactSync> readDeviceContacts(int userId) {
        List<ContactSync> contacts = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        
        String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.PHOTO_URI
        };
        
        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        );
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                boolean hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0;
                
                ContactSync contact = new ContactSync(userId, contactId, displayName);
                contact.setPhotoUri(photoUri);
                
                // Get email addresses
                String email = getContactEmail(contentResolver, contactId);
                contact.setEmail(email);
                
                // Get phone numbers
                if (hasPhoneNumber) {
                    String phoneNumber = getContactPhoneNumber(contentResolver, contactId);
                    contact.setPhoneNumber(phoneNumber);
                }
                
                contacts.add(contact);
                
            } while (cursor.moveToNext());
            
            cursor.close();
        }
        
        return contacts;
    }
    
    /**
     * Get primary email address for a contact
     */
    private String getContactEmail(ContentResolver contentResolver, String contactId) {
        String email = null;
        
        Cursor emailCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS},
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contactId},
                null
        );
        
        if (emailCursor != null && emailCursor.moveToFirst()) {
            email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            emailCursor.close();
        }
        
        return email;
    }
    
    /**
     * Get primary phone number for a contact
     */
    private String getContactPhoneNumber(ContentResolver contentResolver, String contactId) {
        String phoneNumber = null;
        
        Cursor phoneCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{contactId},
                null
        );
        
        if (phoneCursor != null && phoneCursor.moveToFirst()) {
            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneCursor.close();
        }
        
        return phoneNumber;
    }
    
    /**
     * Check if contact information has changed
     */
    private boolean hasContactChanged(ContactSync existing, ContactSync updated) {
        return !equals(existing.getDisplayName(), updated.getDisplayName()) ||
               !equals(existing.getEmail(), updated.getEmail()) ||
               !equals(existing.getPhoneNumber(), updated.getPhoneNumber()) ||
               !equals(existing.getPhotoUri(), updated.getPhotoUri());
    }
    
    private boolean equals(String str1, String str2) {
        return (str1 == null && str2 == null) || (str1 != null && str1.equals(str2));
    }
    
    /**
     * Match synced contacts with registered users in the system
     */
    private int matchContactsWithRegisteredUsers(int userId) {
        int matchedCount = 0;
        
        // Get all contacts that might be registered users
        List<ContactSync> potentialMatches = database.contactSyncDao().findPotentialRegisteredUsers(userId);
        
        for (ContactSync contact : potentialMatches) {
            User matchedUser = null;
            
            // Try to match by email first
            if (contact.getEmail() != null && !contact.getEmail().isEmpty()) {
                matchedUser = database.userDao().getUserByEmail(contact.getEmail());
            }
            
            // Try to match by phone number if email didn't match
            if (matchedUser == null && contact.getPhoneNumber() != null && !contact.getPhoneNumber().isEmpty()) {
                matchedUser = database.userDao().getUserByPhoneNumber(contact.getPhoneNumber());
            }
            
            if (matchedUser != null) {
                contact.setRegisteredUser(true);
                contact.setRegisteredUserId(matchedUser.getId());
                contact.setSyncStatus(ContactSync.STATUS_SYNCED);
                contact.setLastSyncDate(new Date());
                contact.setUpdatedDate(new Date());
                
                database.contactSyncDao().update(contact);
                matchedCount++;
            }
        }
        
        return matchedCount;
    }
    
    /**
     * Find contacts that are registered users
     */
    public CompletableFuture<List<ContactSync>> findRegisteredContacts() {
        return CompletableFuture.supplyAsync(() -> {
            int userId = sessionManager.getCurrentUserId();
            return database.contactSyncDao().getRegisteredUserContacts(userId).getValue();
        }, AppDatabase.databaseWriteExecutor);
    }
    
    /**
     * Suggest friends based on contacts
     */
    public CompletableFuture<List<User>> suggestFriendsFromContacts() {
        return CompletableFuture.supplyAsync(() -> {
            int userId = sessionManager.getCurrentUserId();
            List<ContactSync> registeredContacts = database.contactSyncDao().getRegisteredUserContacts(userId).getValue();
            List<User> suggestions = new ArrayList<>();
            
            if (registeredContacts != null) {
                for (ContactSync contact : registeredContacts) {
                    if (contact.isRegisteredUser() && contact.getRegisteredUserId() != userId) {
                        // Check if they're not already friends
                        if (database.friendDao().getFriendship(userId, contact.getRegisteredUserId()) == null) {
                            User user = database.userDao().getUserById(contact.getRegisteredUserId());
                            if (user != null) {
                                suggestions.add(user);
                            }
                        }
                    }
                }
            }
            
            return suggestions;
        }, AppDatabase.databaseWriteExecutor);
    }
    
    /**
     * Enable or disable sync for a specific contact
     */
    public void updateContactSyncPermission(int contactId, boolean allowSync) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.contactSyncDao().updateSyncPermission(contactId, allowSync);
        });
    }
    
    /**
     * Delete all contacts for current user
     */
    public CompletableFuture<Boolean> deleteAllContacts() {
        return CompletableFuture.supplyAsync(() -> {
            int userId = sessionManager.getCurrentUserId();
            int deletedCount = database.contactSyncDao().deleteAllUserContacts(userId);
            return deletedCount > 0;
        }, AppDatabase.databaseWriteExecutor);
    }
    
    /**
     * Get sync statistics
     */
    public CompletableFuture<SyncStats> getSyncStats() {
        return CompletableFuture.supplyAsync(() -> {
            int userId = sessionManager.getCurrentUserId();
            
            int totalContacts = database.contactSyncDao().getTotalContactsCount(userId).getValue();
            int registeredContacts = database.contactSyncDao().getRegisteredContactsCount(userId).getValue();
            int pendingSync = database.contactSyncDao().getPendingSyncCount(userId).getValue();
            
            return new SyncStats(totalContacts, registeredContacts, pendingSync);
        }, AppDatabase.databaseWriteExecutor);
    }
    
    /**
     * Result class for sync operations
     */
    public static class SyncResult {
        private boolean success;
        private String message;
        private int newContacts;
        private int updatedContacts;
        private int matchedUsers;
        
        public SyncResult(boolean success, String message, int newContacts, int updatedContacts, int matchedUsers) {
            this.success = success;
            this.message = message;
            this.newContacts = newContacts;
            this.updatedContacts = updatedContacts;
            this.matchedUsers = matchedUsers;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getNewContacts() { return newContacts; }
        public int getUpdatedContacts() { return updatedContacts; }
        public int getMatchedUsers() { return matchedUsers; }
    }
    
    /**
     * Statistics class for sync information
     */
    public static class SyncStats {
        private int totalContacts;
        private int registeredContacts;
        private int pendingSync;
        
        public SyncStats(int totalContacts, int registeredContacts, int pendingSync) {
            this.totalContacts = totalContacts;
            this.registeredContacts = registeredContacts;
            this.pendingSync = pendingSync;
        }
        
        // Getters
        public int getTotalContacts() { return totalContacts; }
        public int getRegisteredContacts() { return registeredContacts; }
        public int getPendingSync() { return pendingSync; }
    }
}
