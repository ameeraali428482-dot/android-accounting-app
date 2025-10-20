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
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SYNCED = "SYNCED";
    public static final String STATUS_FAILED = "FAILED";
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "user_id")
    private int userId;
    
    @ColumnInfo(name = "contact_identifier")
    private String contactIdentifier;
    
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;
    
    @ColumnInfo(name = "display_name")
    private String displayName;
    
    @ColumnInfo(name = "email")
    private String email;
    
    @ColumnInfo(name = "photo_uri")
    private String photoUri;
    
    @ColumnInfo(name = "allow_sync")
    private boolean allowSync;
    
    @ColumnInfo(name = "is_registered_user")
    private boolean isRegisteredUser;
    
    @ColumnInfo(name = "registered_user_id")
    private Integer registeredUserId;
    
    @ColumnInfo(name = "sync_status")
    private String syncStatus;
    
    @ColumnInfo(name = "last_sync_date")
    private long lastSyncDate;
    
    @ColumnInfo(name = "created_date")
    private long createdDate;
    
    @ColumnInfo(name = "updated_date")
    private long updatedDate;

    // Constructor for Room
    public ContactSync(int userId, String contactIdentifier, String phoneNumber, boolean allowSync, long lastSyncDate) {
        this.userId = userId;
        this.contactIdentifier = contactIdentifier;
        this.phoneNumber = phoneNumber;
        this.allowSync = allowSync;
        this.lastSyncDate = lastSyncDate;
        this.syncStatus = STATUS_PENDING;
        this.isRegisteredUser = false;
        this.createdDate = System.currentTimeMillis();
        this.updatedDate = System.currentTimeMillis();
    }
    
    // Constructor with display name
    public ContactSync(int userId, String contactIdentifier, String displayName) {
        this(userId, contactIdentifier, "", true, System.currentTimeMillis());
        this.displayName = displayName;
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
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
    
    public boolean isAllowSync() { return allowSync; }
    public void setAllowSync(boolean allowSync) { this.allowSync = allowSync; }
    
    public boolean isRegisteredUser() { return isRegisteredUser; }
    public void setRegisteredUser(boolean registeredUser) { isRegisteredUser = registeredUser; }
    
    public Integer getRegisteredUserId() { return registeredUserId; }
    public void setRegisteredUserId(Integer registeredUserId) { this.registeredUserId = registeredUserId; }
    
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    
    public long getLastSyncDate() { return lastSyncDate; }
    public void setLastSyncDate(long lastSyncDate) { this.lastSyncDate = lastSyncDate; }
    
    public long getCreatedDate() { return createdDate; }
    public void setCreatedDate(long createdDate) { this.createdDate = createdDate; }
    
    public long getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(long updatedDate) { this.updatedDate = updatedDate; }
    
    public void setUpdatedDate(java.util.Date date) { 
        this.updatedDate = date.getTime(); 
    }
}
