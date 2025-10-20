package com.example.androidapp.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "roles")
public class Role {
    @PrimaryKey
    @ColumnInfo(name = "role_id")
    public String roleId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "permissions")
    public String permissions;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    // Default constructor for Room
    public Role() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Primary constructor for Room
    public Role(String roleId, String name, String description, String permissions, long createdAt) {
        this.roleId = roleId;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.createdAt = createdAt;
        this.updatedAt = System.currentTimeMillis();
    }

    @Ignore
    public Role(String roleId, String name, String description, long createdAt) {
        this(roleId, name, description, "", createdAt);
    }

    // Getters
    public String getRoleId() { return roleId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPermissions() { return permissions; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
