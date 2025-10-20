package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "order_items",
        foreignKeys = {
            @ForeignKey(entity = Order.class,
                        parentColumns = "id",
                        childColumns = "order_id",
                        onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Item.class,
                        parentColumns = "id",
                        childColumns = "item_id",
                        onDelete = ForeignKey.RESTRICT)
        },
        indices = {
            @Index(value = {"order_id"}),
            @Index(value = {"item_id"}),
            @Index(value = {"order_id", "item_id"}, unique = true)
        })
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "order_id")
    public String orderId;

    @ColumnInfo(name = "item_id")
    public String itemId;

    @ColumnInfo(name = "quantity")
    public int quantity;

    @ColumnInfo(name = "unit_price")
    public double unitPrice;

    @ColumnInfo(name = "total_price")
    public double totalPrice;

    @ColumnInfo(name = "discount")
    public double discount;

    @ColumnInfo(name = "tax_rate")
    public double taxRate;

    @ColumnInfo(name = "tax_amount")
    public double taxAmount;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    public OrderItem() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
