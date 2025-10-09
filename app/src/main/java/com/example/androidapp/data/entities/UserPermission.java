package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "user_permissions",
        primaryKeys = {"userId", "permissionId", "companyId"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                            parentColumns = "id",
                            childColumns = "userId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Permission.class,
                            parentColumns = "id",
                            childColumns = "permissionId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "companyId",
                            onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "userId"), @Index(value = "permissionId"), @Index(value = "companyId")})
public class UserPermission {
    public @NonNull String userId;
    public @NonNull String permissionId;
    public @NonNull String companyId;

    public UserPermission(String userId, String permissionId, String companyId) {
        this.userId = userId;
        this.permissionId = permissionId;
        this.companyId = companyId;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public String getCompanyId() {
        return companyId;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}

