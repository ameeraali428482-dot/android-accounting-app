package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "roles")
public class Role {
    @PrimaryKey
    @NonNull
    public String roleId;
    public String roleName;
    public String description;
    public long createdAt;
    public long updatedAt;

    // Default constructor for Room
    public Role() {}

    // Constructor for creating new roles
    @Ignore
    public Role(@NonNull String roleId, String roleName, String description) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Full constructor
    @Ignore
    public Role(@NonNull String roleId, String roleName, String description, long createdAt, long updatedAt) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
