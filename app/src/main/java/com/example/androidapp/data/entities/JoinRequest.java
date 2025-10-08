package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "join_requests",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "userId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "userId"), @Index(value = "companyId")})
public class JoinRequest {
    @PrimaryKey
    private String id;
    private String userId;
    private String companyId;
    private String status;
    private String createdAt;

    public JoinRequest(String id, String userId, String companyId, String status, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.status = status;
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

    public String getStatus() {
        return status;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

