package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;



@Entity(tableName = "audit_logs",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                            parentColumns = "id",
                            childColumns = "userId",
                            onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "companyId",
                            onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "userId"), @Index(value = "companyId")})
public class AuditLog {
    @PrimaryKey
    private @NonNull String id;
    private String timestamp;
    private String userId;
    private String companyId;
    private String action;
    private String entity;
    private String entityId;
    private String details; // JSON string

    public AuditLog(String id, String timestamp, String userId, String companyId, String action, String entity, String entityId, String details) {
        this.id = id;
        this.timestamp = timestamp;
        this.userId = userId;
        this.companyId = companyId;
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
        this.details = details;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getAction() {
        return action;
    }

    public String getEntity() {
        return entity;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getDetails() {
        return details;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

