package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "transactions",
        foreignKeys = {
                @ForeignKey(entity = Account.class,
                        parentColumns = "id",
                        childColumns = "from_account_id",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Account.class,
                        parentColumns = "id",
                        childColumns = "to_account_id",
                        onDelete = ForeignKey.SET_NULL)
        })
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private String id;

    @ColumnInfo(name = "from_account_id")
    private String fromAccountId;

    @ColumnInfo(name = "to_account_id")
    private String toAccountId;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "transaction_date")
    private Date transactionDate;

    @ColumnInfo(name = "transaction_type")
    private String transactionType; // DEBIT, CREDIT, TRANSFER

    @ColumnInfo(name = "reference_number")
    private String referenceNumber;

    @ColumnInfo(name = "category_id")
    private String categoryId;

    @ColumnInfo(name = "created_date")
    private Date createdDate;

    @ColumnInfo(name = "updated_date")
    private Date updatedDate;

    @ColumnInfo(name = "status")
    private String status; // PENDING, COMPLETED, CANCELLED

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "company_id")
    private String companyId;

    // Default constructor for Room
    public Transaction() {
        this.id = java.util.UUID.randomUUID().toString();
        this.createdDate = new Date();
        this.updatedDate = new Date();
        this.status = "PENDING";
    }

    // Constructor with essential fields
    @Ignore
    public Transaction(String fromAccountId, String toAccountId, double amount, String description, String transactionType) {
        this();
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.description = description;
        this.transactionType = transactionType;
        this.transactionDate = new Date();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
