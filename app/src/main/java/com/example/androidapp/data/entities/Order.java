package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "orders",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                        parentColumns = "id",
                        childColumns = "companyId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Customer.class,
                        parentColumns = "id",
                        childColumns = "customerId",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "customerId")})
public class Order {
    @PrimaryKey
    private @NonNull String id;
    private @NonNull String companyId;
    private String customerId;
    private @NonNull Date orderDate;
    private double totalAmount;
    private String status; // e.g., "Processing", "Completed", "Cancelled"
    private String notes;

    public Order(@NonNull String id, @NonNull String companyId, String customerId, @NonNull Date orderDate, double totalAmount, String status, String notes) {
        this.id = id;
        this.companyId = companyId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.notes = notes;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public String getCustomerId() { return customerId; }
    @NonNull
    public Date getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setOrderDate(@NonNull Date orderDate) { this.orderDate = orderDate; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
}
