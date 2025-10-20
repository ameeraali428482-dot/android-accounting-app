package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class Notification {
    @PrimaryKey(autoGenerate = true)
    public int notificationId;
    public int userId;
    public String title;
    public String message;
    public String type;
    public boolean isRead;
    public long createdAt;
    public long readAt;

    // Default constructor for Room
    public Notification() {}

    // Constructor for creating new notifications
    @Ignore
    public Notification(int userId, String title, String message, String type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = false;
        this.createdAt = System.currentTimeMillis();
        this.readAt = 0;
    }

    // Full constructor
    @Ignore
    public Notification(int notificationId, int userId, String title, String message, String type,
                       boolean isRead, long createdAt, long readAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }
}
