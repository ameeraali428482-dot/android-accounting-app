package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contact_syncs")
public class ContactSync {
    public static final String STATUS_SYNCED = "SYNCED";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_FAILED = "FAILED";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    @NonNull
    private String userId;

    @ColumnInfo(name = "contact_identifier")
    @NonNull
    private String contactIdentifier;

    @ColumnInfo(name = "display_name")
    private String displayName;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "photo_uri")
    private String photoUri;

    @ColumnInfo(name = "is_registered_user")
    private boolean isRegisteredUser;

    @ColumnInfo(name = "registered_user_id")
    private String registeredUserId;

    @ColumnInfo(name = "sync_status")
    private String syncStatus;

    @ColumnInfo(name = "allow_sync")
    private boolean allowSync;

    @ColumnInfo(name = "last_sync_date")
    private long lastSyncDate;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public ContactSync() {}

    @Ignore
    public ContactSync(@NonNull String userId, @NonNull String contactIdentifier, String displayName) {
        this.userId = userId;
        this.contactIdentifier = contactIdentifier;
        this.displayName = displayName;
        this.isRegisteredUser = false;
        this.syncStatus = STATUS_PENDING;
        this.allowSync = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters
    public int getId() { return id; }
    @NonNull
    public String getUserId() { return userId; }
    @NonNull
    public String getContactIdentifier() { return contactIdentifier; }
    public String getDisplayName() { return displayName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getPhotoUri() { return photoUri; }
    public boolean isRegisteredUser() { return isRegisteredUser; }
    public String getRegisteredUserId() { return registeredUserId; }
    public String getSyncStatus() { return syncStatus; }
    public boolean isAllowSync() { return allowSync; }
    public long getLastSyncDate() { return lastSyncDate; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }
    public void setContactIdentifier(@NonNull String contactIdentifier) { this.contactIdentifier = contactIdentifier; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
    public void setRegisteredUser(boolean registeredUser) { isRegisteredUser = registeredUser; }
    public void setRegisteredUserId(String registeredUserId) { this.registeredUserId = registeredUserId; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    public void setAllowSync(boolean allowSync) { this.allowSync = allowSync; }
    public void setLastSyncDate(long lastSyncDate) { this.lastSyncDate = lastSyncDate; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
