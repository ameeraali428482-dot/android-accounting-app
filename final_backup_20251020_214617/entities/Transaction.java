package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int transactionId;
    public int accountId;
    public double amount;
    public String type;
    public String description;
    public String status;
    public String referenceNumber;
    public String companyId;
    public long createdAt;
    public long updatedAt;

    // Default constructor for Room
    public Transaction() {}

    // Constructor for creating new transactions
    @Ignore
    public Transaction(int accountId, double amount, String type, String description) {
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.status = "PENDING";
        this.referenceNumber = generateReferenceNumber();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Full constructor
    @Ignore
    public Transaction(int transactionId, int accountId, double amount, String type, String description, 
                      String status, String referenceNumber, String companyId, long createdAt, long updatedAt) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.status = status;
        this.referenceNumber = referenceNumber;
        this.companyId = companyId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Ignore
    private String generateReferenceNumber() {
        return "TXN" + System.currentTimeMillis();
    }
}
