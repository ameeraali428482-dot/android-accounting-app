package com.example.androidapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import java.util.Date;

import com.example.androidapp.data.DateConverter;

@Entity(tableName = "chats",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                            parentColumns = "id",
                            childColumns = "senderId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                            parentColumns = "id",
                            childColumns = "receiverId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "orgId",
                            onDelete = ForeignKey.CASCADE)
        })
@TypeConverters({DateConverter.class})
public class Chat {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int senderId;
    public int receiverId;
    public int orgId;
    public String message;
    public Date timestamp;
    public boolean isRead;
    public String messageType; // TEXT, IMAGE, FILE, etc.

    public Chat(int senderId, int receiverId, int orgId, String message, Date timestamp, boolean isRead, String messageType) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.orgId = orgId;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.messageType = messageType;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
