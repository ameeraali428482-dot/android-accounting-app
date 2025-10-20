package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contact_sync")
public class ContactSync {
    @PrimaryKey(autoGenerate = true)
    public int syncId;
    @NonNull
    public String contactId;
    @NonNull
    public String name;
    public String phone;
    public String email;
    public boolean isSynced;
    public long lastSyncTime;
    public String syncStatus;
    public long createdAt;
    public long updatedAt;

    // Default constructor for Room (REQUIRED)
    public ContactSync() {}

    // Constructor for creating new contact sync entries
    @Ignore
    public ContactSync(@NonNull String contactId, @NonNull String name, String phone, String email) {
        this.contactId = contactId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.isSynced = false;
        this.lastSyncTime = 0;
        this.syncStatus = "PENDING";
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Full constructor
    @Ignore
    public ContactSync(int syncId, @NonNull String contactId, @NonNull String name, String phone, String email,
                      boolean isSynced, long lastSyncTime, String syncStatus, long createdAt, long updatedAt) {
        this.syncId = syncId;
        this.contactId = contactId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.isSynced = isSynced;
        this.lastSyncTime = lastSyncTime;
        this.syncStatus = syncStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
