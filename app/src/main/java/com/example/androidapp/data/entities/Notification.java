package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class Notification {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String type;
    public String title;
    public String content;
    public long relatedId;
    public long timestamp;
    public boolean isRead;
    public int userId;

    // Default constructor for Room
    public Notification() {}

    // Main constructor
    public Notification(String type, String title, String content, long relatedId, int userId) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.relatedId = relatedId;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }
}
