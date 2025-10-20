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
import com.example.androidapp.data.entities.ContactSync;
import com.example.androidapp.data.entities.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    
    public boolean hasContactsPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    public CompletableFuture<SyncResult> syncAllContacts() {
        return CompletableFuture.supplyAsync(() -> {
            if (!hasContactsPermission()) {
                return new SyncResult(false, "Contacts permission not granted", 0, 0, 0);
            }
            
            String userId = sessionManager.getCurrentUserId();
            if (userId == null) {
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
                        contact.setSyncStatus(ContactSync.STATUS_SYNCED);
                        contact.setLastSyncDate(System.currentTimeMillis());
                        newContacts.add(contact);
                    } else {
                        if (hasContactChanged(existingContact, contact)) {
                            existingContact.setDisplayName(contact.getDisplayName());
                            existingContact.setEmail(contact.getEmail());
                            existingContact.setPhoneNumber(contact.getPhoneNumber());
                            existingContact.setPhotoUri(contact.getPhotoUri());
                            existingContact.setLastSyncDate(System.currentTimeMillis());
                            existingContact.setUpdatedDate(System.currentTimeMillis());
                            existingContact.setSyncStatus(ContactSync.STATUS_SYNCED);
                            updatedContacts.add(existingContact);
                        }
                    }
                }
                
                if (!newContacts.isEmpty()) {
                    database.contactSyncDao().insertAll(newContacts);
                }
                
                for (ContactSync contact : updatedContacts) {
                    database.contactSyncDao().update(contact);
                }
                
                int matchedUsers = matchContactsWithRegisteredUsers(userId);
                
                return new SyncResult(true, "Sync completed successfully", 
                        newContacts.size(), updatedContacts.size(), matchedUsers);
                        
            } catch (Exception e) {
                Log.e(TAG, "Error syncing contacts", e);
                return new SyncResult(false, "Error syncing contacts: " + e.getMessage(), 0, 0, 0);
            }
        }, AppDatabase.databaseWriteExecutor);
    }
    
    private List<ContactSync> readDeviceContacts(String userId) {
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
            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int photoIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
            int hasPhoneIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

            do {
                String contactId = cursor.getString(idIndex);
                String displayName = cursor.getString(nameIndex);
                String photoUri = cursor.getString(photoIndex);
                boolean hasPhoneNumber = cursor.getInt(hasPhoneIndex) > 0;
                
                ContactSync contact = new ContactSync(userId, contactId, displayName);
                contact.setPhotoUri(photoUri);
                
                String email = getContactEmail(contentResolver, contactId);
                contact.setEmail(email);
                
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
            int emailIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            if (emailIndex != -1) {
                email = emailCursor.getString(emailIndex);
            }
            emailCursor.close();
        }
        
        return email;
    }
    
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
            int phoneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            if (phoneIndex != -1) {
                phoneNumber = phoneCursor.getString(phoneIndex);
            }
            phoneCursor.close();
        }
        
        return phoneNumber;
    }
    
    private boolean hasContactChanged(ContactSync existing, ContactSync updated) {
        return !equals(existing.getDisplayName(), updated.getDisplayName()) ||
               !equals(existing.getEmail(), updated.getEmail()) ||
               !equals(existing.getPhoneNumber(), updated.getPhoneNumber()) ||
               !equals(existing.getPhotoUri(), updated.getPhotoUri());
    }
    
    private boolean equals(String str1, String str2) {
        return (str1 == null && str2 == null) || (str1 != null && str1.equals(str2));
    }
    
    private int matchContactsWithRegisteredUsers(String userId) {
        int matchedCount = 0;
        List<ContactSync> potentialMatches = database.contactSyncDao().findPotentialRegisteredUsers(userId);
        
        for (ContactSync contact : potentialMatches) {
            User matchedUser = null;
            
            if (contact.getEmail() != null && !contact.getEmail().isEmpty()) {
                matchedUser = database.userDao().getUserByEmail(contact.getEmail());
            }
            
            if (matchedUser == null && contact.getPhoneNumber() != null && !contact.getPhoneNumber().isEmpty()) {
                matchedUser = database.userDao().getUserByPhone(contact.getPhoneNumber());
            }
            
            if (matchedUser != null) {
                contact.setRegisteredUser(true);
                contact.setRegisteredUserId(matchedUser.getId());
                contact.setSyncStatus(ContactSync.STATUS_SYNCED);
                contact.setLastSyncDate(System.currentTimeMillis());
                contact.setUpdatedDate(System.currentTimeMillis());
                
                database.contactSyncDao().update(contact);
                matchedCount++;
            }
        }
        
        return matchedCount;
    }
    
    public CompletableFuture<List<ContactSync>> findRegisteredContacts() {
        return CompletableFuture.supplyAsync(() -> {
            String userId = sessionManager.getCurrentUserId();
            return database.contactSyncDao().getRegisteredUserContacts(userId).getValue();
        }, AppDatabase.databaseWriteExecutor);
    }
    
    public CompletableFuture<List<User>> suggestFriendsFromContacts() {
        return CompletableFuture.supplyAsync(() -> {
            String userId = sessionManager.getCurrentUserId();
            List<ContactSync> registeredContacts = database.contactSyncDao().getRegisteredUserContacts(userId).getValue();
            List<User> suggestions = new ArrayList<>();
            
            if (registeredContacts != null) {
                for (ContactSync contact : registeredContacts) {
                    if (contact.isRegisteredUser() && !contact.getRegisteredUserId().equals(userId)) {
                        if (database.friendDao().getFriendship(userId, contact.getRegisteredUserId()) == null) {
                            User user = database.userDao().getUserByIdSync(contact.getRegisteredUserId());
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
    
    public void updateContactSyncPermission(String contactId, boolean allowSync) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.contactSyncDao().updateSyncPermission(contactId, allowSync);
        });
    }
    
    public CompletableFuture<Boolean> deleteAllContacts() {
        return CompletableFuture.supplyAsync(() -> {
            String userId = sessionManager.getCurrentUserId();
            int deletedCount = database.contactSyncDao().deleteAllUserContacts(userId);
            return deletedCount > 0;
        }, AppDatabase.databaseWriteExecutor);
    }
    
    public CompletableFuture<SyncStats> getSyncStats() {
        return CompletableFuture.supplyAsync(() -> {
            String userId = sessionManager.getCurrentUserId();
            
            Integer totalContacts = database.contactSyncDao().getTotalContactsCount(userId).getValue();
            Integer registeredContacts = database.contactSyncDao().getRegisteredContactsCount(userId).getValue();
            Integer pendingSync = database.contactSyncDao().getPendingSyncCount(userId).getValue();
            
            return new SyncStats(
                totalContacts != null ? totalContacts : 0,
                registeredContacts != null ? registeredContacts : 0,
                pendingSync != null ? pendingSync : 0
            );
        }, AppDatabase.databaseWriteExecutor);
    }
    
    public static class SyncResult {
        public boolean success;
        public String message;
        public int newContacts;
        public int updatedContacts;
        public int matchedUsers;
        
        public SyncResult(boolean success, String message, int newContacts, int updatedContacts, int matchedUsers) {
            this.success = success;
            this.message = message;
            this.newContacts = newContacts;
            this.updatedContacts = updatedContacts;
            this.matchedUsers = matchedUsers;
        }
    }
    
    public static class SyncStats {
        public int totalContacts;
        public int registeredContacts;
        public int pendingSync;
        
        public SyncStats(int totalContacts, int registeredContacts, int pendingSync) {
            this.totalContacts = totalContacts;
            this.registeredContacts = registeredContacts;
            this.pendingSync = pendingSync;
        }
    }
}
