package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "invoices",
        foreignKeys = {
                @ForeignKey(entity = Customer.class,
                           parentColumns = "id",
                           childColumns = "customer_id",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "created_by",
                           onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "company_id",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("customer_id"),
                @Index("created_by"),
                @Index("company_id"),
                @Index("invoice_number"),
                @Index("status")
        })
public class Invoice {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    @ColumnInfo(name = "invoice_number")
    public String invoiceNumber;
    
    @ColumnInfo(name = "customer_id")
    public String customerId;
    
    @ColumnInfo(name = "supplier_id")
    public String supplierId; // للفواتير الواردة
    
    @ColumnInfo(name = "total_amount")
    public double totalAmount;
    
    @ColumnInfo(name = "paid_amount")
    public double paidAmount;
    
    @ColumnInfo(name = "remaining_amount")
    public double remainingAmount;
    
    public String status; // PENDING, PAID, CANCELLED, OVERDUE
    public String currency;
    public String notes;
    
    @ColumnInfo(name = "tax_amount")
    public double taxAmount;
    
    @ColumnInfo(name = "discount_amount")
    public double discountAmount;
    
    @ColumnInfo(name = "created_date")
    public long createdDate;
    
    @ColumnInfo(name = "due_date")
    public long dueDate;
    
    @ColumnInfo(name = "last_modified")
    public long lastModified;
    
    @ColumnInfo(name = "created_by")
    public String createdBy;
    
    @ColumnInfo(name = "company_id")
    public String companyId;
    
    @ColumnInfo(name = "is_draft", defaultValue = "0")
    public boolean isDraft;

    // Constructor الرئيسي
    public Invoice(long id, String invoiceNumber, String customerId, String supplierId,
                   double totalAmount, double paidAmount, double remainingAmount,
                   String status, String currency, String notes, double taxAmount,
                   double discountAmount, long createdDate, long dueDate, long lastModified,
                   String createdBy, String companyId, boolean isDraft) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.customerId = customerId;
        this.supplierId = supplierId;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.remainingAmount = remainingAmount;
        this.status = status;
        this.currency = currency;
        this.notes = notes;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
        this.lastModified = lastModified;
        this.createdBy = createdBy;
        this.companyId = companyId;
        this.isDraft = isDraft;
    }

    @Ignore
    public Invoice() {
        this.createdDate = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.status = "PENDING";
        this.currency = "SAR";
        this.paidAmount = 0.0;
        this.taxAmount = 0.0;
        this.discountAmount = 0.0;
        this.isDraft = false;
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { 
        this.totalAmount = totalAmount;
        updateRemainingAmount();
    }
    
    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { 
        this.paidAmount = paidAmount;
        updateRemainingAmount();
    }
    
    public double getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(double remainingAmount) { this.remainingAmount = remainingAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }
    
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    
    public long getCreatedDate() { return createdDate; }
    public void setCreatedDate(long createdDate) { this.createdDate = createdDate; }
    
    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }
    
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    
    public boolean isDraft() { return isDraft; }
    public void setDraft(boolean draft) { isDraft = draft; }

    private void updateRemainingAmount() {
        this.remainingAmount = this.totalAmount - this.paidAmount;
    }
}
