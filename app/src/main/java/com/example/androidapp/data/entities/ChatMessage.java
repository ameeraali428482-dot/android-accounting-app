package com.example.androidapp.data.entities;

import androidx.room.*;
import java.util.Date;

@Entity(tableName = "chat_messages")
public class ChatMessage {
    @PrimaryKey
    private String id;
    private String chatId;
    private String senderId;
    private String message;
    private String messageType;
    private Date timestamp;
    private boolean isRead;
    private String companyId;

    public ChatMessage() {}

    public ChatMessage(String id, String chatId, String senderId, String message, 
                      String messageType, Date timestamp, boolean isRead, String companyId) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.message = message;
        this.messageType = messageType;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.companyId = companyId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
}
