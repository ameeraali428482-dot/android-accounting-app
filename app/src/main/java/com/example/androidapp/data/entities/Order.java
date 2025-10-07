package com.example.androidapp.data.entities;
import androidx.room.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import java.util.Date;

import com.example.androidapp.data.DateConverter;

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
        })
@TypeConverters({DateConverter.class})
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int companyId;
    public Integer customerId; // Nullable if order is not tied to a specific customer
    public Date orderDate;
    public String status; // e.g., "Pending", "Processing", "Completed", "Cancelled"
    public double totalAmount;
    public String notes;

    public Order(int companyId, Integer customerId, Date orderDate, String status, double totalAmount, String notes) {
        this.companyId = companyId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
