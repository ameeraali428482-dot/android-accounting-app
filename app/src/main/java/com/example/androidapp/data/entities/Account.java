package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class Account {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String code;
    public double balance;
    public String type;
    public long createdAt;
    public long lastModified;

    // Default constructor for Room
    public Account() {}

    // Main constructor
    public Account(String name, String code, double balance, String type, long createdAt) {
        this.name = name;
        this.code = code;
        this.balance = balance;
        this.type = type;
        this.createdAt = createdAt;
        this.lastModified = System.currentTimeMillis();
    }

    // Getters for compatibility
    public String getName() { return name; }
    public String getCode() { return code; }
    public double getBalance() { return balance; }
}
