package com.example.androidapp.data.entities;

import androidx.room.*;

@Entity(tableName = "accounts")
public class Account {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public String code;
    public String type;
    public double balance;
    public String currency;
    public String description;
    
    @ColumnInfo(name = "created_at")
    public long createdAt;
    
    @ColumnInfo(name = "last_modified")
    public long lastModified;
    
    public String userId;
    
    public Account() {
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.currency = "SAR";
        this.balance = 0.0;
    }

    public Account(String name, String code, double balance, String type, long createdAt) {
        this.name = name;
        this.code = code;
        this.balance = balance;
        this.type = type;
        this.createdAt = createdAt;
        this.lastModified = System.currentTimeMillis();
        this.currency = "SAR";
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getType() { return type; }
    public double getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public long getCreatedAt() { return createdAt; }
    public long getLastModified() { return lastModified; }
    public String getUserId() { return userId; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCode(String code) { this.code = code; }
    public void setType(String type) { this.type = type; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    public void setUserId(String userId) { this.userId = userId; }
}
