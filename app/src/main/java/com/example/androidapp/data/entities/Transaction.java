package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "transactions",
        foreignKeys = {
            @ForeignKey(entity = Account.class,
                        parentColumns = "id",
                        childColumns = "account_id",
                        onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index("account_id"), @Index("category_id")})
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "account_id")
    private int accountId;
    
    @ColumnInfo(name = "category_id")
    private Integer categoryId;
    
    @ColumnInfo(name = "amount")
    private double amount;
    
    @ColumnInfo(name = "description")
    private String description;
    
    @ColumnInfo(name = "transaction_date")
    private long transactionDate;
    
    @ColumnInfo(name = "type")
    private String type;
    
    // For compatibility with existing code
    public long date;
    public String userId;

    // Constructor
    public Transaction(int accountId, Integer categoryId, double amount, String description, long transactionDate, String type) {
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
        this.type = type;
        this.date = transactionDate; // for compatibility
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { 
        this.amount = amount;
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description;
    }
    
    public long getTransactionDate() { return transactionDate; }
    public void setTransactionDate(long transactionDate) { 
        this.transactionDate = transactionDate;
        this.date = transactionDate; // for compatibility
    }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public long getDate() { return date; }
    public void setDate(long date) { 
        this.date = date;
        this.transactionDate = date;
    }
}
