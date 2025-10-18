package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey
    @NonNull
    private String id;
    private String companyId;
    private String name;
    private String description;
    private double price;
    private String category;
    private String barcode;
    private Integer quantity;
    private Float minStockLevel;
    private float cost;

    public Item(@NonNull String id, String companyId, String name, String description, double price, String category, String barcode, Integer quantity, Float minStockLevel, float cost) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.barcode = barcode;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
        this.cost = cost;
    }

    @Ignore
    public Item() {}

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getCompanyId() { return companyId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getBarcode() { return barcode; }
    public Integer getQuantity() { return quantity; }
    public Float getMinStockLevel() { return minStockLevel; }
    public float getCost() { return cost; }
    public String getItemName() { return name; } // Alias for compatibility
    public Float getReorderLevel() { return minStockLevel; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setMinStockLevel(Float minStockLevel) { this.minStockLevel = minStockLevel; }
    public void setCost(float cost) { this.cost = cost; }
    public void setItemName(String name) { this.name = name; } // Alias for compatibility
}
