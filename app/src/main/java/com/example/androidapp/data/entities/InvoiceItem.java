package com.example.androidapp.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "invoice_items",
        foreignKeys = {
                @ForeignKey(entity = Invoice.class,
                        parentColumns = "id",
                        childColumns = "invoice_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Item.class,
                        parentColumns = "itemId",
                        childColumns = "item_id",
                        onDelete = ForeignKey.RESTRICT)
        },
        indices = {@Index(value = "invoice_id"), @Index(value = "item_id")})
public class InvoiceItem {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "invoice_id")
    public String invoiceId;

    @ColumnInfo(name = "item_id")
    public String itemId;

    @ColumnInfo(name = "quantity")
    public int quantity;

    @ColumnInfo(name = "unit_price")
    public double unitPrice;

    @ColumnInfo(name = "discount")
    public double discount;

    @ColumnInfo(name = "tax_rate")
    public double taxRate;

    @ColumnInfo(name = "tax_amount")
    public double taxAmount;

    @ColumnInfo(name = "subtotal")
    public double subtotal;

    @ColumnInfo(name = "total")
    public double total;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    public InvoiceItem() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
