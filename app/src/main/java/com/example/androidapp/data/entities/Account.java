package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "accounts",
        foreignKeys = @ForeignKey(entity = User.class,
                                  parentColumns = "id",
                                  childColumns = "user_id",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index("user_id")})
public class Account {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public String name;
    public String type;
    public double balance;
    public String currency;
    public String description;
    
    @ColumnInfo(name = "user_id")
    public String userId;
    
    @ColumnInfo(name = "created_at")
    public long createdAt;
    
    @ColumnInfo(name = "last_modified")
    public long lastModified;
    
    @ColumnInfo(name = "is_active", defaultValue = "1")
    public boolean isActive;

    // Constructor الرئيسي لـ Room
    public Account(long id, String name, String type, double balance, String currency, 
                   String description, String userId, long createdAt, long lastModified, boolean isActive) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.currency = currency;
        this.description = description;
        this.userId = userId;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.isActive = isActive;
    }

    // Constructor مبسط
    @Ignore
    public Account(String name, String type, String userId) {
        this.name = name;
        this.type = type;
        this.userId = userId;
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.currency = "SAR";
        this.balance = 0.0;
        this.isActive = true;
    }

    // Constructor فارغ
    @Ignore
    public Account() {
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.currency = "SAR";
        this.balance = 0.0;
        this.isActive = true;
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
