package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventory",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Item.class,
                           parentColumns = "id",
                           childColumns = "itemId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Warehouse.class,
                           parentColumns = "id",
                           childColumns = "warehouseId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "companyId"), @Index(value = "itemId"), @Index(value = "warehouseId")})
public class Inventory {
    @PrimaryKey
    private String id;
    private String companyId;
    private String itemId;
    private String warehouseId;
    private float quantity;
    private float costPrice; // Cost price at the time of inventory entry
    private String lastUpdated;

    public Inventory(String id, String companyId, String itemId, String warehouseId, float quantity, float costPrice, String lastUpdated) {
        this.id = id;
        this.companyId = companyId;
        this.itemId = itemId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.costPrice = costPrice;
        this.lastUpdated = lastUpdated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

