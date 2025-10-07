package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "contact_sync",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {"userId", "contactIdentifier"}, unique = true),
                @Index(value = "userId"),
                @Index(value = "email"),
                @Index(value = "phoneNumber")
        })
public class ContactSync {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int userId;
    private String contactIdentifier; // Unique identifier from device contacts
    private String displayName;
    private String email;
    private String phoneNumber;
    private String photoUri;
    private Date lastSyncDate;
    private boolean isRegisteredUser;
    private int registeredUserId; // If contact is a registered user
    private String syncStatus; // SYNCED, PENDING, FAILED
    private boolean allowSync;
    private String notes;
    private Date createdDate;
    private Date updatedDate;
    
    // Constructors
    public ContactSync() {
        this.createdDate = new Date();
        this.updatedDate = new Date();
        this.allowSync = true;
        this.syncStatus = STATUS_PENDING;
    }
    
    public ContactSync(int userId, String contactIdentifier, String displayName) {
        this();
        this.userId = userId;
        this.contactIdentifier = contactIdentifier;
        this.displayName = displayName;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getContactIdentifier() {
        return contactIdentifier;
    }
    
    public void setContactIdentifier(String contactIdentifier) {
        this.contactIdentifier = contactIdentifier;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getPhotoUri() {
        return photoUri;
    }
    
    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
    
    public Date getLastSyncDate() {
        return lastSyncDate;
    }
    
    public void setLastSyncDate(Date lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }
    
    public boolean isRegisteredUser() {
        return isRegisteredUser;
    }
    
    public void setRegisteredUser(boolean registeredUser) {
        isRegisteredUser = registeredUser;
    }
    
    public int getRegisteredUserId() {
        return registeredUserId;
    }
    
    public void setRegisteredUserId(int registeredUserId) {
        this.registeredUserId = registeredUserId;
    }
    
    public String getSyncStatus() {
        return syncStatus;
    }
    
    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }
    
    public boolean isAllowSync() {
        return allowSync;
    }
    
    public void setAllowSync(boolean allowSync) {
        this.allowSync = allowSync;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    public Date getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    // Status constants
    public static final String STATUS_SYNCED = "SYNCED";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_FAILED = "FAILED";
}
