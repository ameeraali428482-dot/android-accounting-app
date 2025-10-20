package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "transactions",
        foreignKeys = {
                @ForeignKey(entity = Account.class,
                           parentColumns = "id",
                           childColumns = "from_account_id",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Account.class,
                           parentColumns = "id",
                           childColumns = "to_account_id",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Category.class,
                           parentColumns = "id",
                           childColumns = "category_id",
                           onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "user_id",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "company_id",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("from_account_id"),
                @Index("to_account_id"),
                @Index("category_id"),
                @Index("user_id"),
                @Index("company_id"),
                @Index("reference_number"),
                @Index("date")
        })
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
    public Long categoryId; // nullable
    
    @ColumnInfo(name = "transaction_type")
    public String transactionType; // "DEBIT", "CREDIT", "TRANSFER"
    
    public String status; // "PENDING", "COMPLETED", "CANCELLED"
    
    @ColumnInfo(name = "reference_number")
    public String referenceNumber;
    
    @ColumnInfo(name = "user_id")
    public String userId;
    
    @ColumnInfo(name = "company_id")
    public String companyId;
    
    @ColumnInfo(name = "created_at")
    public long createdAt;
    
    @ColumnInfo(name = "last_modified")
    public long lastModified;
    
    public String notes;
    
    @ColumnInfo(name = "is_reconciled", defaultValue = "0")
    public boolean isReconciled;

    // Constructor الرئيسي لـ Room
    public Transaction(long id, double amount, long date, String description,
                      long fromAccountId, long toAccountId, Long categoryId,
                      String transactionType, String status, String referenceNumber,
                      String userId, String companyId, long createdAt, long lastModified,
                      String notes, boolean isReconciled) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.categoryId = categoryId;
        this.transactionType = transactionType;
        this.status = status;
        this.referenceNumber = referenceNumber;
        this.userId = userId;
        this.companyId = companyId;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.notes = notes;
        this.isReconciled = isReconciled;
    }

    // Constructor مبسط
    @Ignore
    public Transaction(double amount, String description, long fromAccountId, 
                      long toAccountId, String transactionType, String userId, String companyId) {
        this.amount = amount;
        this.description = description;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.transactionType = transactionType;
        this.userId = userId;
        this.companyId = companyId;
        this.date = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.status = "PENDING";
        this.isReconciled = false;
        this.referenceNumber = generateReferenceNumber();
    }

    // Constructor فارغ
    @Ignore
    public Transaction() {
        this.date = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.status = "PENDING";
        this.isReconciled = false;
    }

    private String generateReferenceNumber() {
        return "TXN" + System.currentTimeMillis();
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public long getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(long fromAccountId) { this.fromAccountId = fromAccountId; }
    
    public long getToAccountId() { return toAccountId; }
    public void setToAccountId(long toAccountId) { this.toAccountId = toAccountId; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public boolean isReconciled() { return isReconciled; }
    public void setReconciled(boolean reconciled) { isReconciled = reconciled; }
}
