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
                        parentColumns = "customerId",
                        childColumns = "customerId",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "customerId")})
public class Order {
    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String companyId;
    private String customerId;
    @NonNull
    private Date orderDate;
    private double totalAmount;
    private String status;
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

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    @NonNull
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(@NonNull Date orderDate) { this.orderDate = orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
