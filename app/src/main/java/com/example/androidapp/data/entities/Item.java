package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey
    private String id;
    private String companyId;
    private String name;
    private String description;
    private double price;
    private String category;
    private String barcode;
    private Integer quantity;
    private float cost;

    public Item(String id, String companyId, String name, String description, double price, String category, String barcode) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.barcode = barcode;
        this.quantity = 0;
        this.cost = 0.0f;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public float getCost() { return cost; }
    public void setCost(float cost) { this.cost = cost; }
}
