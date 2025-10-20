package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "user_roles",
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "userId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Role.class, parentColumns = "roleId", childColumns = "roleId", onDelete = ForeignKey.CASCADE)
    },
    indices = {
        @Index(value = "userId"),
        @Index(value = "roleId")
    }
)
public class UserRole {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String userId;
    @NonNull
    public String roleId;
    public long assignedAt;
    public String assignedBy;

    // Default constructor for Room
    public UserRole() {}

    // Constructor for creating new user roles
    @Ignore
    public UserRole(@NonNull String userId, @NonNull String roleId, String assignedBy) {
        this.userId = userId;
        this.roleId = roleId;
        this.assignedAt = System.currentTimeMillis();
        this.assignedBy = assignedBy;
    }

    // Full constructor
    @Ignore
    public UserRole(int id, @NonNull String userId, @NonNull String roleId, long assignedAt, String assignedBy) {
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
        this.assignedAt = assignedAt;
        this.assignedBy = assignedBy;
    }
}
