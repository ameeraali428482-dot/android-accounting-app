package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;
import java.util.UUID;

@Entity(tableName = "contact_sync",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class, parentColumns = "id", childColumns = "companyId", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {"userId", "contactIdentifier"}, unique = true),
                @Index(value = "userId"),
                @Index(value = "companyId"),
                @Index(value = "email"),
                @Index(value = "phoneNumber")
        })
public class ContactSync {
    @PrimaryKey
    @NonNull
    private String id;
    
    private String userId;
    private String companyId;
    private String contactIdentifier;
    private String displayName;
    private String email;
    private String phoneNumber;
    private String photoUri;
    private Date lastSyncDate;
    private boolean isRegisteredUser;
    private String registeredUserId;
    private String syncStatus;
    private boolean allowSync;
    private String notes;
    private Date createdDate;
    private Date updatedDate;
    
    public ContactSync(@NonNull String id, String userId, String companyId, String contactIdentifier, String displayName, String email, String phoneNumber, String photoUri, Date lastSyncDate, boolean isRegisteredUser, String registeredUserId, String syncStatus, boolean allowSync, String notes, Date createdDate, Date updatedDate) {
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.contactIdentifier = contactIdentifier;
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.photoUri = photoUri;
        this.lastSyncDate = lastSyncDate;
        this.isRegisteredUser = isRegisteredUser;
        this.registeredUserId = registeredUserId;
        this.syncStatus = syncStatus;
        this.allowSync = allowSync;
        this.notes = notes;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @Ignore
    public ContactSync(String userId, String contactIdentifier, String displayName) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.contactIdentifier = contactIdentifier;
        this.displayName = displayName;
        this.createdDate = new Date();
        this.updatedDate = new Date();
        this.allowSync = true;
        this.syncStatus = STATUS_PENDING;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getContactIdentifier() { return contactIdentifier; }
    public void setContactIdentifier(String contactIdentifier) { this.contactIdentifier = contactIdentifier; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
    public Date getLastSyncDate() { return lastSyncDate; }
    public void setLastSyncDate(Date lastSyncDate) { this.lastSyncDate = lastSyncDate; }
    public boolean isRegisteredUser() { return isRegisteredUser; }
    public void setRegisteredUser(boolean registeredUser) { isRegisteredUser = registeredUser; }
    public String getRegisteredUserId() { return registeredUserId; }
    public void setRegisteredUserId(String registeredUserId) { this.registeredUserId = registeredUserId; }
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    public boolean isAllowSync() { return allowSync; }
    public void setAllowSync(boolean allowSync) { this.allowSync = allowSync; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
    
    public static final String STATUS_SYNCED = "SYNCED";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_FAILED = "FAILED";
}
