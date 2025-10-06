package com.example.androidapp.models;

public class Notification {
    private String id;
    private String userId;
    private String type;
    private String message;
    private boolean isRead;
    private String entityId;
    private String createdAt;

    public Notification(String id, String userId, String type, String message, boolean isRead, String entityId, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.isRead = isRead;
        this.entityId = entityId;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getEntityId() {
        return entityId;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

