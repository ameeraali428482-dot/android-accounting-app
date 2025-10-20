package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "permissions")
public class Permission {
    @PrimaryKey
    @NonNull
    public String permissionId;
    public String permissionName;
    public String description;
    public String category;
    public long createdAt;
    public long updatedAt;

    // Default constructor for Room
    public Permission() {}

    // Constructor for creating new permissions
    @Ignore
    public Permission(@NonNull String permissionId, String permissionName, String description, String category) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
        this.description = description;
        this.category = category;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Full constructor
    @Ignore
    public Permission(@NonNull String permissionId, String permissionName, String description, String category, long createdAt, long updatedAt) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
        this.description = description;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
