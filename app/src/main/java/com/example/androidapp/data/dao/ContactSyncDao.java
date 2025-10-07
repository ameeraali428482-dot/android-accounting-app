package com.example.androidapp.data.dao;
import com.example.androidapp.data.entities.Product;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.data.entities.Voucher;
import com.example.androidapp.data.entities.Company;
import com.example.androidapp.data.entities.Doctor;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.Supplier;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.data.entities.Trophy;
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.data.entities.Repair;
import com.example.androidapp.data.entities.Chat;
import com.example.androidapp.data.entities.UserReward;
import com.example.androidapp.data.entities.Reward;
import com.example.androidapp.data.entities.PointTransaction;
import com.example.androidapp.data.entities.Campaign;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.models.ContactSync;

import java.util.List;

@Dao
public interface ContactSyncDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ContactSync contactSync);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<ContactSync> contactSyncs);
    
    @Update
    int update(ContactSync contactSync);
    
    @Delete
    int delete(ContactSync contactSync);
    
    @Query("SELECT * FROM contact_sync WHERE id = :id")
    ContactSync getContactSyncById(int id);
    
    @Query("SELECT * FROM contact_sync WHERE id = :id")
    LiveData<ContactSync> getContactSyncByIdLive(int id);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId ORDER BY displayName ASC")
    LiveData<List<ContactSync>> getUserContacts(int userId);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND syncStatus = :status ORDER BY lastSyncDate DESC")
    LiveData<List<ContactSync>> getContactsByStatus(int userId, String status);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND isRegisteredUser = 1 ORDER BY displayName ASC")
    LiveData<List<ContactSync>> getRegisteredUserContacts(int userId);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND isRegisteredUser = 0 ORDER BY displayName ASC")
    LiveData<List<ContactSync>> getNonRegisteredContacts(int userId);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND allowSync = 1 ORDER BY displayName ASC")
    LiveData<List<ContactSync>> getSyncAllowedContacts(int userId);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND contactIdentifier = :contactIdentifier")
    ContactSync getContactByIdentifier(int userId, String contactIdentifier);
    
    @Query("SELECT * FROM contact_sync WHERE email = :email AND userId = :userId")
    ContactSync getContactByEmail(int userId, String email);
    
    @Query("SELECT * FROM contact_sync WHERE phoneNumber = :phoneNumber AND userId = :userId")
    ContactSync getContactByPhone(int userId, String phoneNumber);
    
    @Query("SELECT * FROM contact_sync WHERE registeredUserId = :registeredUserId AND userId = :userId")
    ContactSync getContactByRegisteredUserId(int userId, int registeredUserId);
    
    @Query("SELECT COUNT(*) FROM contact_sync WHERE userId = :userId")
    LiveData<Integer> getTotalContactsCount(int userId);
    
    @Query("SELECT COUNT(*) FROM contact_sync WHERE userId = :userId AND isRegisteredUser = 1")
    LiveData<Integer> getRegisteredContactsCount(int userId);
    
    @Query("SELECT COUNT(*) FROM contact_sync WHERE userId = :userId AND syncStatus = 'PENDING'")
    LiveData<Integer> getPendingSyncCount(int userId);
    
    @Query("UPDATE contact_sync SET syncStatus = :status, lastSyncDate = datetime('now'), updatedDate = datetime('now') WHERE id = :contactId")
    int updateSyncStatus(int contactId, String status);
    
    @Query("UPDATE contact_sync SET isRegisteredUser = :isRegistered, registeredUserId = :registeredUserId, updatedDate = datetime('now') WHERE id = :contactId")
    int updateRegistrationStatus(int contactId, boolean isRegistered, int registeredUserId);
    
    @Query("UPDATE contact_sync SET allowSync = :allowSync, updatedDate = datetime('now') WHERE id = :contactId")
    int updateSyncPermission(int contactId, boolean allowSync);
    
    @Query("UPDATE contact_sync SET displayName = :displayName, email = :email, phoneNumber = :phoneNumber, photoUri = :photoUri, updatedDate = datetime('now') WHERE id = :contactId")
    int updateContactInfo(int contactId, String displayName, String email, String phoneNumber, String photoUri);
    
    @Query("DELETE FROM contact_sync WHERE userId = :userId AND allowSync = 0")
    int deleteNonSyncContacts(int userId);
    
    @Query("DELETE FROM contact_sync WHERE userId = :userId AND syncStatus = 'FAILED'")
    int deleteFailedSyncContacts(int userId);
    
    @Query("DELETE FROM contact_sync WHERE userId = :userId")
    int deleteAllUserContacts(int userId);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND " +
           "(displayName LIKE '%' || :searchQuery || '%' OR " +
           "email LIKE '%' || :searchQuery || '%' OR " +
           "phoneNumber LIKE '%' || :searchQuery || '%') " +
           "ORDER BY displayName ASC")
    LiveData<List<ContactSync>> searchContacts(int userId, String searchQuery);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND lastSyncDate < datetime('now', '-' || :daysOld || ' days')")
    List<ContactSync> getOutdatedContacts(int userId, int daysOld);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND syncStatus = 'PENDING' ORDER BY createdDate ASC LIMIT :limit")
    List<ContactSync> getPendingSyncContacts(int userId, int limit);
    
    @Query("SELECT DISTINCT email FROM contact_sync WHERE userId = :userId AND email IS NOT NULL AND email != ''")
    List<String> getAllContactEmails(int userId);
    
    @Query("SELECT DISTINCT phoneNumber FROM contact_sync WHERE userId = :userId AND phoneNumber IS NOT NULL AND phoneNumber != ''")
    List<String> getAllContactPhones(int userId);
    
    @Query("UPDATE contact_sync SET lastSyncDate = datetime('now'), updatedDate = datetime('now') WHERE userId = :userId")
    int updateAllSyncDates(int userId);
    
    @Query("SELECT * FROM contact_sync WHERE userId = :userId AND " +
           "(email IN (SELECT email FROM users WHERE email IS NOT NULL) OR " +
           "phoneNumber IN (SELECT phoneNumber FROM users WHERE phoneNumber IS NOT NULL)) " +
           "AND isRegisteredUser = 0")
    List<ContactSync> findPotentialRegisteredUsers(int userId);
}
