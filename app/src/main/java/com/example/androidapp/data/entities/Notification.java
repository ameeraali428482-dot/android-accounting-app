package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications",
        foreignKeys = @ForeignKey(entity = User.class,
                                  parentColumns = "id",
                                  childColumns = "userId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "userId")})
public class Notification {
    @PrimaryKey
    @NonNull
    private String id;
    private String userId;
    private String type;
    private String title; // Added title
    private String message;
    private boolean isRead;
    private String entityId;
    private String createdAt;

    public Notification(@NonNull String id, String userId, String type, String title, String message, boolean isRead, String entityId, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.entityId = entityId;
        this.createdAt = createdAt;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public String getEntityId() { return entityId; }
    public String getCreatedAt() { return createdAt; }
    public String getTimestamp() { return createdAt; }
    public String getNotificationType() { return type; }


    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setRead(boolean read) { isRead = read; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
