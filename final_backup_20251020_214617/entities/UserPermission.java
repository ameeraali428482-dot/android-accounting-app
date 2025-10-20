package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "user_permissions",
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "userId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Permission.class, parentColumns = "permissionId", childColumns = "permissionId", onDelete = ForeignKey.CASCADE)
    },
    indices = {
        @Index(value = "userId"),
        @Index(value = "permissionId")
    }
)
public class UserPermission {
    @PrimaryKey
    @NonNull
    public String permissionId;
    @NonNull
    public String userId;
    public boolean isGranted;
    public long grantedAt;
    public String grantedBy;

    // Default constructor for Room
    public UserPermission() {}

    // Constructor for creating new user permissions
    @Ignore
    public UserPermission(@NonNull String permissionId, @NonNull String userId, boolean isGranted, String grantedBy) {
        this.permissionId = permissionId;
        this.userId = userId;
        this.isGranted = isGranted;
        this.grantedAt = System.currentTimeMillis();
        this.grantedBy = grantedBy;
    }

    // Full constructor
    @Ignore
    public UserPermission(@NonNull String permissionId, @NonNull String userId, boolean isGranted, long grantedAt, String grantedBy) {
        this.permissionId = permissionId;
        this.userId = userId;
        this.isGranted = isGranted;
        this.grantedAt = grantedAt;
        this.grantedBy = grantedBy;
    }
}
