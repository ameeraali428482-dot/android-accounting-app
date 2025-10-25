package com.example.androidapp.models;

import com.example.androidapp.data.entities.Item;

/**
 * Product wrapper class that extends Item for backward compatibility
 * This allows existing code that expects Product objects to work with Item entities
 */
public class Product extends Item {
    
    public Product() {
        super();
    }
    
    public Product(Item item) {
        super();
        this.setId(item.getId());
        this.setName(item.getName());
        this.setDescription(item.getDescription());
        this.setPrice(item.getPrice());
        this.setQuantity(item.getQuantity());
        this.setCategoryId(item.getCategoryId());
        this.setCompanyId(item.getCompanyId());
        this.setBarcode(item.getBarcode());
        this.setMinStockLevel(item.getMinStockLevel());
        this.setUnit(item.getUnit());
        this.setCreatedAt(item.getCreatedAt());
        this.setUpdatedAt(item.getUpdatedAt());
    }
    
    // Wrapper methods for Product-specific naming
    public String getProductName() {
        return getName();
    }
    
    public void setProductName(String productName) {
        setName(productName);
    }
    
    public String getProductDescription() {
        return getDescription();
    }
    
    public void setProductDescription(String productDescription) {
        setDescription(productDescription);
    }
    
    public double getProductPrice() {
        return getPrice();
    }
    
    public void setProductPrice(double productPrice) {
        setPrice(productPrice);
    }
    
    public double getCurrentStock() {
        return getQuantity();
    }
    
    public void setCurrentStock(double currentStock) {
        setQuantity(currentStock);
    }
    
    public double getMinimumStock() {
        return getMinStockLevel();
    }
    
    public void setMinimumStock(double minimumStock) {
        setMinStockLevel(minimumStock);
    }
    
    public boolean isLowStock() {
        return getQuantity() < getMinStockLevel();
    }
    
    public double getStockValue() {
        return getQuantity() * getPrice();
    }
}
