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
                           childColumns = "senderId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "receiverId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "companyId"), @Index(value = "senderId"), @Index(value = "receiverId")})
public class Chat {
    @PrimaryKey
    private @NonNull String id;
    private String message;
    private @NonNull String companyId;
    private @NonNull String senderId;
    private @NonNull String receiverId;
    private @NonNull Date createdAt;
    private boolean isRead;
    private String messageType;

    public Chat(@NonNull String id, String message, @NonNull String companyId, @NonNull String senderId, @NonNull String receiverId, @NonNull Date createdAt, boolean isRead, String messageType) {
        this.id = id;
        this.message = message;
        this.companyId = companyId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.messageType = messageType;
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
    public String getSenderId() { return senderId; }
    public void setSenderId(@NonNull String senderId) { this.senderId = senderId; }
    @NonNull
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(@NonNull String receiverId) { this.receiverId = receiverId; }
    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
}
