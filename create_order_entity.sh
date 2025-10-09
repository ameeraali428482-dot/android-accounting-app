#!/bin/bash

echo "Creating missing entity file: Order.java"

# Define the file path
ENTITY_FILE="app/src/main/java/com/example/androidapp/data/entities/Order.java"

# Create the directory if it doesn't exist
mkdir -p "$(dirname "$ENTITY_FILE")"

# Create the Order.java file with the correct content
cat > "$ENTITY_FILE" << 'EOP'
package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.androidapp.data.DateConverter;

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
@TypeConverters(DateConverter.class)
public class Order {
    @PrimaryKey
    @NonNull
    private String id;

    private String customerId;

    @NonNull
    private String companyId;

    private double totalAmount;

    @NonNull
    private Date createdAt;

    @NonNull
    private Date orderDate;

    private String status;
    private String notes;

    public Order(@NonNull String id, String customerId, @NonNull String companyId, double totalAmount, @NonNull Date createdAt, @NonNull Date orderDate, String status, String notes) {
        this.id = id;
        this.customerId = customerId;
        this.companyId = companyId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.orderDate = orderDate;
        this.status = status;
        this.notes = notes;
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
EOP

echo "File Order.java created successfully."
