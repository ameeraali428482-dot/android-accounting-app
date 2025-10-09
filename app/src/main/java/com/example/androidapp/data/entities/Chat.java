package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "chats",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "userId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "toUserId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "companyId"), @Index(value = "userId"), @Index(value = "toUserId")})
public class Chat {
    @PrimaryKey
    private @NonNull String id;
    private String message;
    private @NonNull String companyId;
    private @NonNull String userId;
    private @NonNull String toUserId;
    private @NonNull Date createdAt;
    private boolean isRead;
    private String senderId;
    private String receiverId;
    private String orgId;

    public Chat(@NonNull String id, String message, @NonNull String companyId, @NonNull String userId, @NonNull String toUserId, @NonNull Date createdAt, boolean isRead, String senderId, String receiverId, String orgId) {
        this.id = id;
        this.message = message;
        this.companyId = companyId;
        this.userId = userId;
        this.toUserId = toUserId;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.orgId = orgId;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }
    @NonNull
    public String getToUserId() { return toUserId; }
    public void setToUserId(@NonNull String toUserId) { this.toUserId = toUserId; }
    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
}
