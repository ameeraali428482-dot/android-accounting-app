package com.example.androidapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventory")
public class Inventory {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String companyId;
    public String itemId;
    public String warehouseId;
    public float quantity;
    public float costPrice; // Cost price at the time of inventory entry
    public String lastUpdated;

    public Inventory(String companyId, String itemId, String warehouseId, float quantity, float costPrice, String lastUpdated) {
        this.companyId = companyId;
        this.itemId = itemId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.costPrice = costPrice;
        this.lastUpdated = lastUpdated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(float costPrice) {
        this.costPrice = costPrice;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

