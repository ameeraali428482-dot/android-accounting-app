package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;



@Entity(tableName = "memberships",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "userId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Role.class,
                           parentColumns = "id",
                           childColumns = "roleId",
                           onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "userId"), @Index(value = "companyId"), @Index(value = "roleId")})
public class Membership {
    @PrimaryKey
    private @NonNull String id;
    private String userId;
    private String companyId;
    private String roleId;
    private String createdAt;

    public Membership(String id, String userId, String companyId, String roleId, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.roleId = roleId;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

