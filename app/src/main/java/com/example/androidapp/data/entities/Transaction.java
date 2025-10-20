package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public double amount;
    public String description;
    public long timestamp;
    public String type;

    @ColumnInfo(name = "category_id")
    public Integer categoryId;

    @ColumnInfo(name = "from_account_id")
    public Integer fromAccountId;

    @ColumnInfo(name = "to_account_id") 
    public Integer toAccountId;

    public long lastModified;

    // Default constructor for Room
    public Transaction() {}

    // Main constructor
    public Transaction(int userId, Integer categoryId, double amount, String description, long timestamp, String type) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.type = type;
        this.lastModified = System.currentTimeMillis();
    }
}
