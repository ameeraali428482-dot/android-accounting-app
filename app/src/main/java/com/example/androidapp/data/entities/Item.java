package com.example.androidapp.data.entities;

import androidx.room.*;
import java.util.Date;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public String name;
    public String description;
    public String barcode;
    public String sku;
    public double price;
    public double cost;
    public int stockQuantity;
    public int minStockLevel;
    public String unit;
    public long categoryId;
    public String imageUrl;
    
    @ColumnInfo(name = "is_active", defaultValue = "1")
    public boolean isActive;
    
    @ColumnInfo(name = "created_date")
    public long createdDate;
    
    @ColumnInfo(name = "last_modified")
    public long lastModified;
    
    public String createdBy;
    
    public Item() {
        this.createdDate = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.isActive = true;
        this.stockQuantity = 0;
        this.minStockLevel = 0;
    }
}
