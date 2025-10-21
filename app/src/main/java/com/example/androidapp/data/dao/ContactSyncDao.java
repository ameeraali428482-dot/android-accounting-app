package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.ContactSync;
import java.util.List;

@Dao
public interface ContactSyncDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ContactSync contactSync);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ContactSync> contactSyncs);
    
    @Update
    int update(ContactSync contactSync);
    
    @Delete
    int delete(ContactSync contactSync);
    
    @Query("SELECT * FROM contact_sync WHERE id = :id")
    ContactSync getContactSyncById(String id);
    
    @Query("SELECT * FROM contact_sync WHERE id = :id")
    LiveData<ContactSync> getContactSyncByIdLive(String id);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId ORDER BY displayName ASC")
    LiveData<List<ContactSync>> getUserContacts(String userId);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND syncStatus = :status ORDER BY lastSyncDate DESC")
    LiveData<List<ContactSync>> getContactsByStatus(String userId, String status);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND isRegisteredUser = 1 ORDER BY displayName ASC")
    LiveData<List<ContactSync>> getRegisteredUserContacts(String userId);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND isRegisteredUser = 0 ORDER BY displayName ASC")
    LiveData<List<ContactSync>> getNonRegisteredContacts(String userId);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND allowSync = 1 ORDER BY displayName ASC")
    LiveData<List<ContactSync>> getSyncAllowedContacts(String userId);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND contactIdentifier = :contactIdentifier")
    ContactSync getContactByIdentifier(String userId, String contactIdentifier);
    
    @Query("SELECT * FROM contact_sync WHERE email = :email AND userId = :userId")
    ContactSync getContactByEmail(String userId, String email);
    
    @Query("SELECT * FROM contact_sync WHERE phoneNumber = :phoneNumber AND userId = :userId")
    ContactSync getContactByPhone(String userId, String phoneNumber);
    
    @Query("SELECT * FROM contact_sync WHERE registeredUserId = :registeredUserId AND userId = :userId")
    ContactSync getContactByRegisteredUserId(String userId, String registeredUserId);
    
    @Query("SELECT COUNT(*) FROM contact_sync WHERE userId = :userId")
    LiveData<Integer> getTotalContactsCount(String userId);
    
    @Query("SELECT COUNT(*) FROM contact_sync WHERE userId = :userId AND isRegisteredUser = 1")
    LiveData<Integer> getRegisteredContactsCount(String userId);
    
    @Query("SELECT COUNT(*) FROM contact_sync WHERE userId = :userId AND syncStatus = 'PENDING'")
    LiveData<Integer> getPendingSyncCount(String userId);
    
    @Query("UPDATE contact_sync SET syncStatus = :status, lastSyncDate = CURRENT_TIMESTAMP, updatedDate = CURRENT_TIMESTAMP WHERE id = :contactId")
    int updateSyncStatus(String contactId, String status);
    
    @Query("UPDATE contact_sync SET isRegisteredUser = :isRegistered, registeredUserId = :registeredUserId, updatedDate = CURRENT_TIMESTAMP WHERE id = :contactId")
    int updateRegistrationStatus(String contactId, boolean isRegistered, String registeredUserId);
    
    @Query("UPDATE contact_sync SET allowSync = :allowSync, updatedDate = CURRENT_TIMESTAMP WHERE id = :contactId")
    int updateSyncPermission(String contactId, boolean allowSync);
    
    @Query("UPDATE contact_sync SET displayName = :displayName, email = :email, phoneNumber = :phoneNumber, photoUri = :photoUri, updatedDate = CURRENT_TIMESTAMP WHERE id = :contactId")
    int updateContactInfo(String contactId, String displayName, String email, String phoneNumber, String photoUri);
    
    @Query("DELETE FROM contact_sync WHERE userId = :userId AND allowSync = 0")
    int deleteNonSyncContacts(String userId);
    
    @Query("DELETE FROM contact_sync WHERE userId = :userId AND syncStatus = 'FAILED'")
    int deleteFailedSyncContacts(String userId);
    
    @Query("DELETE FROM contact_sync WHERE userId = :userId")
    int deleteAllUserContacts(String userId);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND " +
           "(displayName LIKE '%' || :searchQuery || '%' OR " +
           "email LIKE '%' || :searchQuery || '%' OR " +
           "phoneNumber LIKE '%' || :searchQuery || '%') " +
           "ORDER BY displayName ASC")
    LiveData<List<ContactSync>> searchContacts(String userId, String searchQuery);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND lastSyncDate < date('now', '-' || :daysOld || ' days')")
    List<ContactSync> getOutdatedContacts(String userId, int daysOld);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND syncStatus = 'PENDING' ORDER BY createdDate ASC LIMIT :limit")
    List<ContactSync> getPendingSyncContacts(String userId, int limit);
    
    @Query("SELECT DISTINCT email FROM contact_sync WHERE userId = :userId AND email IS NOT NULL AND email != ''")
    List<String> getAllContactEmails(String userId);
    
    @Query("SELECT DISTINCT phoneNumber FROM contact_sync WHERE userId = :userId AND phoneNumber IS NOT NULL AND phoneNumber != ''")
    List<String> getAllContactPhones(String userId);
    
    @Query("UPDATE contact_sync SET lastSyncDate = CURRENT_TIMESTAMP, updatedDate = CURRENT_TIMESTAMP WHERE userId = :userId")
    int updateAllSyncDates(String userId);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND " +
           "(email IN (SELECT email FROM users WHERE email IS NOT NULL) OR " +
           "phoneNumber IN (SELECT phone FROM users WHERE phone IS NOT NULL)) " +
           "AND isRegisteredUser = 0")
    List<ContactSync> findPotentialRegisteredUsers(String userId);
}
