package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "invoices",
        foreignKeys = {
            @ForeignKey(entity = Company.class,
                        parentColumns = "id",
                        childColumns = "customer_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("customer_id")})
public class Invoice {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "customer_id")
    private int customerId;
    
    @ColumnInfo(name = "invoice_number")
    private String invoiceNumber;
    
    @ColumnInfo(name = "total_amount")
    private double totalAmount;
    
    @ColumnInfo(name = "issue_date")
    private long issueDate;
    
    @ColumnInfo(name = "due_date")
    private long dueDate;
    
    @ColumnInfo(name = "status")
    private String status;

    // Constructor
    public Invoice(int customerId, String invoiceNumber, double totalAmount, long issueDate, long dueDate, String status) {
        this.customerId = customerId;
        this.invoiceNumber = invoiceNumber;
        this.totalAmount = totalAmount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public long getIssueDate() { return issueDate; }
    public void setIssueDate(long issueDate) { this.issueDate = issueDate; }
    
    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
