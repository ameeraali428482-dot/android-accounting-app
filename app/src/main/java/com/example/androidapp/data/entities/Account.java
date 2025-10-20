package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class Account {
    @PrimaryKey(autoGenerate = true)
    public int accountId;
    public String accountName;
    public String accountType;
    public double balance;
    public String description;
    public boolean isActive;
    public long createdAt;
    public long updatedAt;

    // Default constructor for Room
    public Account() {}

    // Constructor for creating new accounts
    @Ignore
    public Account(String accountName, String accountType, double balance, String description) {
        this.accountName = accountName;
        this.accountType = accountType;
        this.balance = balance;
        this.description = description;
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Full constructor
    @Ignore
    public Account(int accountId, String accountName, String accountType, double balance, String description,
                  boolean isActive, long createdAt, long updatedAt) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountType = accountType;
        this.balance = balance;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
