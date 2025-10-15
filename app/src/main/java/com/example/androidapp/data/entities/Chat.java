package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chats")
public class Chat {
    @PrimaryKey
    private String id;
    private String name;
    private String lastMessage;
    private String lastMessageTime;
    private String companyId;

    public Chat() {
    }

    public Chat(String id, String name, String lastMessage, String lastMessageTime, String companyId) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.companyId = companyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    // Added methods to fix ChatListActivity errors
    public String getChatName() {
        return name;
    }

    public void setChatName(String chatName) {
        this.name = chatName;
    }
}
