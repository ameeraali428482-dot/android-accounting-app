package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Index;
import androidx.room.Ignore;

@Entity(tableName = "notifications",
        indices = {@Index("user_id")})
public class Notification {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "type")
    private String type;
    
    @ColumnInfo(name = "title")
    private String title;
    
    @ColumnInfo(name = "content")
    private String content;
    
    @ColumnInfo(name = "user_id")
    private String userId;
    
    @ColumnInfo(name = "reference_id")
    private String referenceId;
    
    @ColumnInfo(name = "is_read")
    private boolean isRead;
    
    @ColumnInfo(name = "created_at")
    private String createdAt;
    
    @ColumnInfo(name = "updated_at")
    private String updatedAt;

    // Empty constructor for compatibility
    @Ignore
    public Notification() {
        this.isRead = false;
        this.createdAt = String.valueOf(System.currentTimeMillis());
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }

    // Constructor for Room
    public Notification(String type, String title, String content, String userId, String referenceId, boolean isRead, String createdAt, String updatedAt) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.referenceId = referenceId;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
