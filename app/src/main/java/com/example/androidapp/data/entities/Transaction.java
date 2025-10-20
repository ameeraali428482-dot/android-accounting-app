package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;                    // مطابق للاستعلامات التي تستخدم 'id'
    public int from_account_id;       // مطابق للاستعلامات التي تستخدم 'from_account_id'
    public int to_account_id;         // مطابق للاستعلامات التي تستخدم 'to_account_id'
    public double amount;
    public String type;
    public String description;
    public String status;             // مطابق للاستعلامات التي تستخدم 'status'
    public String reference_number;   // مطابق للاستعلامات التي تستخدم 'reference_number'
    public String company_id;         // مطابق للاستعلامات التي تستخدم 'company_id'
    public int userId;                // مطابق للاستعلامات التي تستخدم 'userId'
    public int category_id;           // مطابق للاستعلامات التي تستخدم 'category_id'
    public long date;                 // مطابق للاستعلامات التي تستخدم 'date'
    public long createdAt;
    public long updatedAt;

    // Default constructor for Room
    public Transaction() {}

    // Constructor for creating new transactions
    @Ignore
    public Transaction(int from_account_id, int to_account_id, double amount, String type, String description) {
        this.from_account_id = from_account_id;
        this.to_account_id = to_account_id;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.status = "PENDING";
        this.reference_number = generateReferenceNumber();
        this.date = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    @Ignore
    private String generateReferenceNumber() {
        return "TXN" + System.currentTimeMillis();
    }
}
