package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.ContactSync;

import java.util.List;

@Dao
public interface ContactSyncDao extends BaseDao<ContactSync> {
    
    @Query("SELECT * FROM contact_sync WHERE id = :id")
    ContactSync getById(String id);

    @Query("SELECT * FROM contact_sync WHERE user_id = :userId ORDER BY display_name ASC")
    List<ContactSync> getByUserId(String userId);

    @Query("SELECT * FROM contact_sync WHERE user_id = :userId AND is_registered = 1 ORDER BY display_name ASC")
    List<ContactSync> getRegisteredUserContacts(String userId);

    @Query("SELECT * FROM contact_sync WHERE contact_identifier = :contactIdentifier AND user_id = :userId")
    ContactSync getContactByIdentifier(String userId, String contactIdentifier);

    @Query("SELECT * FROM contact_sync WHERE (email IS NOT NULL AND email != '') OR (phone_number IS NOT NULL AND phone_number != '')")
    List<ContactSync> findPotentialRegisteredUsers(String userId);

    @Query("SELECT COUNT(*) FROM contact_sync WHERE user_id = :userId")
    int getTotalContactsCount(String userId);

    @Query("SELECT COUNT(*) FROM contact_sync WHERE user_id = :userId AND is_registered = 1")
    int getRegisteredContactsCount(String userId);

    @Query("SELECT COUNT(*) FROM contact_sync WHERE user_id = :userId AND sync_status = 'PENDING'")
    int getPendingSyncCount(String userId);

    @Query("UPDATE contact_sync SET allow_sync = :allowSync WHERE id = :contactId")
    void updateSyncPermission(String contactId, boolean allowSync);

    @Query("DELETE FROM contact_sync WHERE user_id = :userId")
    int deleteAllUserContacts(String userId);
}
