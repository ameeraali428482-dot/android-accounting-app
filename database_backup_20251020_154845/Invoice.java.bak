package com.example.androidapp.data.entities;

import androidx.room.*;
import java.util.Date;

@Entity(tableName = "invoices")
public class Invoice {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public String invoiceNumber;
    public long customerId;
    public long supplierId;
    public double totalAmount;
    public double paidAmount;
    public double remainingAmount;
    public String status; // PENDING, PAID, CANCELLED
    public String currency;
    public String notes;
    
    @ColumnInfo(name = "created_date")
    public long createdDate;
    
    @ColumnInfo(name = "due_date")
    public long dueDate;
    
    @ColumnInfo(name = "last_modified")
    public long lastModified;
    
    public String createdBy;
    
    public Invoice() {
        this.createdDate = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.status = "PENDING";
        this.currency = "SAR";
    }
}
