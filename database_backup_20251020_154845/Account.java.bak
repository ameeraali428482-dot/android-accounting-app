package com.example.androidapp.data.entities;

import androidx.room.*;

@Entity(tableName = "accounts")
public class Account {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public String name;
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
}
