package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "user_roles",
        primaryKeys = {"userId", "roleId", "companyId"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                            parentColumns = "id",
                            childColumns = "userId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Role.class,
                            parentColumns = "id",
                            childColumns = "roleId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "companyId",
                            onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "userId"), @Index(value = "roleId"), @Index(value = "companyId")})
public class UserRole {
    public @NonNull String userId;
    public @NonNull String roleId;
    public @NonNull String companyId;

    public UserRole(String userId, String roleId, String companyId) {
        this.userId = userId;
        this.roleId = roleId;
        this.companyId = companyId;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getCompanyId() {
        return companyId;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}

