package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class Notification {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public String type;
    public String title;
    public String content;
    public String message;
    public long relatedId;
    public long timestamp;
    public boolean isRead;
    public String entityId;

    // Default constructor for Room
    public Notification() {
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Main constructor
    public Notification(int userId, String type, String title, String content, long relatedId) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.message = content; // Keep in sync
        this.relatedId = relatedId;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Getters for compatibility
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getMessage() { return message != null ? message : content; }
    public long getRelatedId() { return relatedId; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    public String getEntityId() { return entityId; }
    public String getCreatedAt() { return String.valueOf(timestamp); }
    public String getNotificationType() { return type; }

    // Setters with sync
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { 
        this.content = content;
        this.message = content; // Keep in sync
    }
    public void setMessage(String message) { 
        this.message = message;
        this.content = message; // Keep in sync
    }
    public void setRelatedId(long relatedId) { this.relatedId = relatedId; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setRead(boolean read) { isRead = read; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
}
