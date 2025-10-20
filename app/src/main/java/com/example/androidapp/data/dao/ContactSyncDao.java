package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
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
    
    @Query("SELECT * FROM contact_syncs WHERE contact_identifier = :contactIdentifier")
    ContactSync getContactSyncByIdentifier(String contactIdentifier);
    
    @Query("SELECT * FROM contact_syncs WHERE phone_number = :phoneNumber")
    ContactSync getContactSyncByPhoneNumber(String phoneNumber);
    
    @Query("SELECT * FROM contact_syncs WHERE allow_sync = :allowSync")
    List<ContactSync> getContactSyncsByAllowSync(boolean allowSync);
    
    @Insert
    void insertContactSync(ContactSync contactSync);
    
    @Update
    void updateContactSync(ContactSync contactSync);
    
    @Delete
    void deleteContactSync(ContactSync contactSync);
}
