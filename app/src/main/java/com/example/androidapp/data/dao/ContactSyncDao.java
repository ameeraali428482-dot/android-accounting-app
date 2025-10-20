package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.entities.ContactSync;
import java.util.List;

@Dao
public interface ContactSyncDao {
    @Query("SELECT * FROM contact_syncs")
    List<ContactSync> getAllContactSyncs();
    
    @Query("SELECT * FROM contact_syncs WHERE id = :id")
    ContactSync getContactSyncById(int id);
    
    @Query("SELECT * FROM contact_syncs WHERE user_id = :userId")
    List<ContactSync> getContactSyncsByUserId(int userId);
    
    @Query("SELECT * FROM contact_syncs WHERE user_id = :userId AND contact_identifier = :contactIdentifier")
    ContactSync getContactByIdentifier(int userId, String contactIdentifier);
    
    @Query("SELECT * FROM contact_syncs WHERE user_id = :userId AND contact_identifier = :contactIdentifier")
    ContactSync getContactByIdentifier(String userId, String contactIdentifier);
    
    @Query("SELECT * FROM contact_syncs WHERE contact_identifier = :contactIdentifier")
    ContactSync getContactSyncByIdentifier(String contactIdentifier);
    
    @Query("SELECT * FROM contact_syncs WHERE phone_number = :phoneNumber")
    ContactSync getContactSyncByPhoneNumber(String phoneNumber);
    
    @Query("SELECT * FROM contact_syncs WHERE allow_sync = :allowSync")
    List<ContactSync> getContactSyncsByAllowSync(boolean allowSync);
    
    @Query("SELECT * FROM contact_syncs WHERE user_id = :userId AND is_registered_user = 1")
    LiveData<List<ContactSync>> getRegisteredUserContacts(int userId);
    
    @Query("SELECT * FROM contact_syncs WHERE user_id = :userId AND is_registered_user = 1")
    LiveData<List<ContactSync>> getRegisteredUserContacts(String userId);
    
    @Query("SELECT * FROM contact_syncs WHERE user_id = :userId")
    List<ContactSync> findPotentialRegisteredUsers(int userId);
    
    @Query("SELECT * FROM contact_syncs WHERE user_id = :userId")
    List<ContactSync> findPotentialRegisteredUsers(String userId);
    
    @Query("SELECT COUNT(*) FROM contact_syncs WHERE user_id = :userId")
    LiveData<Integer> getTotalContactsCount(int userId);
    
    @Query("SELECT COUNT(*) FROM contact_syncs WHERE user_id = :userId")
    LiveData<Integer> getTotalContactsCount(String userId);
    
    @Query("SELECT COUNT(*) FROM contact_syncs WHERE user_id = :userId AND is_registered_user = 1")
    LiveData<Integer> getRegisteredContactsCount(int userId);
    
    @Query("SELECT COUNT(*) FROM contact_syncs WHERE user_id = :userId AND is_registered_user = 1")
    LiveData<Integer> getRegisteredContactsCount(String userId);
    
    @Query("SELECT COUNT(*) FROM contact_syncs WHERE user_id = :userId AND sync_status = 'PENDING'")
    LiveData<Integer> getPendingSyncCount(int userId);
    
    @Query("SELECT COUNT(*) FROM contact_syncs WHERE user_id = :userId AND sync_status = 'PENDING'")
    LiveData<Integer> getPendingSyncCount(String userId);
    
    @Insert
    void insertContactSync(ContactSync contactSync);
    
    @Insert
    void insertAll(List<ContactSync> contactSyncs);
    
    @Update
    void updateContactSync(ContactSync contactSync);
    
    @Update
    void update(ContactSync contactSync);
    
    @Delete
    void deleteContactSync(ContactSync contactSync);
    
    @Query("UPDATE contact_syncs SET allow_sync = :allowSync WHERE contact_identifier = :contactId")
    void updateSyncPermission(String contactId, boolean allowSync);
    
    @Query("DELETE FROM contact_syncs WHERE user_id = :userId")
    int deleteAllUserContacts(int userId);
    
    @Query("DELETE FROM contact_syncs WHERE user_id = :userId")
    int deleteAllUserContacts(String userId);
}
