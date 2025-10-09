package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "purchases",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Supplier.class,
                           parentColumns = "id",
                           childColumns = "supplierId",
                           onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "supplierId")})
public class Purchase {
    @PrimaryKey
    private String id;
    private String companyId;
    private String supplierId;
    private String purchaseNumber;
    private String purchaseDate;
    private float totalAmount;
    private String status;
    private String createdAt;
    private String updatedAt;

    public Purchase(String id, String companyId, String supplierId, String purchaseNumber, String purchaseDate, float totalAmount, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.companyId = companyId;
        this.supplierId = supplierId;
        this.purchaseNumber = purchaseNumber;
        this.purchaseDate = purchaseDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getPurchaseNumber() {
        return purchaseNumber;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public void setPurchaseNumber(String purchaseNumber) {
        this.purchaseNumber = purchaseNumber;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

