package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class Account {
    @PrimaryKey(autoGenerate = true)
    public int id;                    // مطابق للاستعلامات التي تستخدم 'id'
    public String name;               // مطابق للاستعلامات التي تستخدم 'name'
    public String type;               // مطابق للاستعلامات التي تستخدم 'type'
    public double balance;
    public String description;
    public boolean isActive;
    public long createdAt;
    public long updatedAt;

    // Default constructor for Room
    public Account() {}

    // Constructor for creating new accounts
    @Ignore
    public Account(String name, String type, double balance, String description) {
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.description = description;
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
