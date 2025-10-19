package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "order_items",
        foreignKeys = {
                @ForeignKey(entity = Item.class,
                        parentColumns = "id",
                        childColumns = "item_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private String id;

    @ColumnInfo(name = "order_id")
    private String orderId;

    @ColumnInfo(name = "item_id")
    private String itemId;

    @ColumnInfo(name = "quantity")
    private double quantity;

    @ColumnInfo(name = "unit_price")
    private double unitPrice;

    @ColumnInfo(name = "total_price")
    private double totalPrice;

    @ColumnInfo(name = "discount")
    private double discount;

    @ColumnInfo(name = "tax_amount")
    private double taxAmount;

    @ColumnInfo(name = "unit_id")
    private String unitId;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "created_date")
    private Date createdDate;

    @ColumnInfo(name = "updated_date")
    private Date updatedDate;

    // Default constructor for Room
    public OrderItem() {
        this.id = java.util.UUID.randomUUID().toString();
        this.createdDate = new Date();
        this.updatedDate = new Date();
    }

    // Constructor with essential fields
    @Ignore
    public OrderItem(String orderId, String itemId, double quantity, double unitPrice) {
        this();
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        updateTotalPrice();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        updateTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
        updateTotalPrice();
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    private void updateTotalPrice() {
        this.totalPrice = (quantity * unitPrice) - discount;
    }
}
