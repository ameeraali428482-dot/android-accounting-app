package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;



@Entity(tableName = "items",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class Item {
    @PrimaryKey
    private @NonNull String id;
    private String name;
    private double price;
    private @NonNull String companyId;
    private String description;
    private float costPrice;
    private Integer reorderLevel;

    public Item(@NonNull String id, String name, double price, @NonNull String companyId, String description, float costPrice, Integer reorderLevel) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.companyId = companyId;
        this.description = description;
        this.costPrice = costPrice;
        this.reorderLevel = reorderLevel;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public float getCostPrice() { return costPrice; }
    public void setCostPrice(float costPrice) { this.costPrice = costPrice; }
    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }
}
