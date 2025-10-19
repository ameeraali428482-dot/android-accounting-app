package com.example.androidapp.data.entities;

import androidx.room.*;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public double amount;
    public long date;
    public String description;
    
    @ColumnInfo(name = "from_account_id")
    public long fromAccountId;
    
    @ColumnInfo(name = "to_account_id")
    public long toAccountId;
    
    @ColumnInfo(name = "category_id")
    public long categoryId;
    
    public String type;
    public String userId;
    
    @ColumnInfo(name = "last_modified")
    public long lastModified;
    
    public Transaction() {
        this.date = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
    }
}
