package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    public int itemId;
    public String itemName;
    public String description;
    public double price;
    public int quantity;
    public String category;
    public String barcode;
    public boolean isActive;
    public String companyId;
    public long createdAt;
    public long updatedAt;

    public Item() {}

    @Ignore
    public Item(String itemName, String description, double price, int quantity, String category) {
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
