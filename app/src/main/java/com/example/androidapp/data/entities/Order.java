package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "orders",
        foreignKeys = {
                @ForeignKey(entity = Customer.class,
                           parentColumns = "id",
                           childColumns = "customerId",
                           onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "customerId"), @Index(value = "companyId")})
public class Order {
    @PrimaryKey
    public @NonNull String id;
    public String customerId;
    public @NonNull String companyId;
    public double totalAmount;
    public @NonNull Date createdAt;
    public @NonNull Date orderDate;

    public Order(@NonNull String id, String customerId, @NonNull String companyId, double totalAmount, @NonNull Date createdAt, @NonNull Date orderDate) {
        this.id = id;
        this.customerId = customerId;
        this.companyId = companyId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.orderDate = orderDate;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }
    @NonNull
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(@NonNull Date orderDate) { this.orderDate = orderDate; }
}
