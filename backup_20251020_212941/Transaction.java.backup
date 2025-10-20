package com.example.androidapp.data.entities;

import androidx.room.*;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public int userId;
    @ColumnInfo(name = "category_id")
    public Integer categoryId;
    public double amount;
    public String description;
    public long timestamp;
    public String type;
    
    // Additional fields for compatibility
    public long date;
    
    @ColumnInfo(name = "from_account_id")
    public Integer fromAccountId;
    
    @ColumnInfo(name = "to_account_id")
    public Integer toAccountId;
    
    @ColumnInfo(name = "last_modified")
    public long lastModified;

    // Default constructor for Room
    public Transaction() {
        this.timestamp = System.currentTimeMillis();
        this.date = this.timestamp; // Keep in sync
        this.lastModified = System.currentTimeMillis();
    }

    // Main constructor
    public Transaction(int userId, Integer categoryId, double amount, String description, long timestamp, String type) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.date = timestamp; // Keep in sync
        this.type = type;
        this.lastModified = System.currentTimeMillis();
    }

    // Getters with compatibility
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public Integer getCategoryId() { return categoryId; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public long getDate() { return date != 0 ? date : timestamp; }
    public String getType() { return type; }
    public Integer getFromAccountId() { return fromAccountId; }
    public Integer getToAccountId() { return toAccountId; }
    public long getLastModified() { return lastModified; }

    // Setters with sync
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setTimestamp(long timestamp) { 
        this.timestamp = timestamp;
        this.date = timestamp; // Keep in sync
    }
    public void setDate(long date) { 
        this.date = date;
        this.timestamp = date; // Keep in sync
    }
    public void setType(String type) { this.type = type; }
    public void setFromAccountId(Integer fromAccountId) { this.fromAccountId = fromAccountId; }
    public void setToAccountId(Integer toAccountId) { this.toAccountId = toAccountId; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
}
