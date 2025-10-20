package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "item_units",
        foreignKeys = {
                @ForeignKey(entity = Item.class,
                        parentColumns = "itemId",
                        childColumns = "itemId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                        parentColumns = "id",
                        childColumns = "companyId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "itemId"), @Index(value = "companyId")})
public class ItemUnit {
    @PrimaryKey
    @NonNull
    private String id;
    private String itemId;
    private String name;
    private float conversionFactor;
    private float price;
    private float cost;
    private boolean isBaseUnit;
    private String companyId;

    public ItemUnit(@NonNull String id, String itemId, String name, float conversionFactor, float price, float cost, boolean isBaseUnit, String companyId) {
        this.id = id;
        this.itemId = itemId;
        this.name = name;
        this.conversionFactor = conversionFactor;
        this.price = price;
        this.cost = cost;
        this.isBaseUnit = isBaseUnit;
        this.companyId = companyId;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public float getConversionFactor() { return conversionFactor; }
    public void setConversionFactor(float conversionFactor) { this.conversionFactor = conversionFactor; }
    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    public float getCost() { return cost; }
    public void setCost(float cost) { this.cost = cost; }
    public boolean isBaseUnit() { return isBaseUnit; }
    public void setBaseUnit(boolean baseUnit) { isBaseUnit = baseUnit; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
}
