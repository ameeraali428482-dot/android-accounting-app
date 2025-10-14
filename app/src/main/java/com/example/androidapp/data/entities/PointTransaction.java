package com.example.androidapp.data.entities;

import androidx.room.*;
import androidx.annotation.NonNull;
import java.util.Date;
import java.util.UUID;

@Entity(tableName = "point_transactions")
public class PointTransaction {
    @PrimaryKey
    @NonNull
    private String id;
    private String companyId;
    private String userId;
    private int points;
    private String transactionType;
    private String description;
    private String transactionDate;
    private String referenceId;
    private String referenceType;

    @Ignore
    public PointTransaction() {}

    // Constructor الأساسي لـ Room
    public PointTransaction(@NonNull String id, String companyId, String userId, int points,
                           String transactionType, String description, String transactionDate,
                           String referenceId, String referenceType) {
        this.id = id;
        this.companyId = companyId;
        this.userId = userId;
        this.points = points;
        this.transactionType = transactionType;
        this.description = description;
        this.transactionDate = transactionDate;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
    }

    // Constructor المساعد
    @Ignore
    public PointTransaction(String userId, String companyId, int points, String transactionType, String description, Date date) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.companyId = companyId;
        this.points = points;
        this.transactionType = transactionType;
        this.description = description;
        this.transactionDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(date);
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
}
