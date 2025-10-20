package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "permissions")
public class Permission {
    @PrimaryKey
    public String permissionId;
    
    public String name;
    public String description;
    public String category;
    public long createdAt;

    public Permission() {
        this.createdAt = System.currentTimeMillis();
    }

    public Permission(String permissionId, String name, String description, String category) {
        this.permissionId = permissionId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public String getPermissionId() { return permissionId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
