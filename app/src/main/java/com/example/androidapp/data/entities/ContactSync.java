package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "contact_syncs",
        foreignKeys = @ForeignKey(entity = User.class,
                                  parentColumns = "id",
                                  childColumns = "user_id",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index("user_id")})
public class ContactSync {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "user_id")
    private int userId;
    
    @ColumnInfo(name = "contact_identifier")
    private String contactIdentifier;
    
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;
    
    @ColumnInfo(name = "allow_sync")
    private boolean allowSync;
    
    @ColumnInfo(name = "last_sync_date")
    private long lastSyncDate;

    // Constructor
    public ContactSync(int userId, String contactIdentifier, String phoneNumber, boolean allowSync, long lastSyncDate) {
        this.userId = userId;
        this.contactIdentifier = contactIdentifier;
        this.phoneNumber = phoneNumber;
        this.allowSync = allowSync;
        this.lastSyncDate = lastSyncDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getContactIdentifier() { return contactIdentifier; }
    public void setContactIdentifier(String contactIdentifier) { this.contactIdentifier = contactIdentifier; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public boolean isAllowSync() { return allowSync; }
    public void setAllowSync(boolean allowSync) { this.allowSync = allowSync; }
    
    public long getLastSyncDate() { return lastSyncDate; }
    public void setLastSyncDate(long lastSyncDate) { this.lastSyncDate = lastSyncDate; }
}
