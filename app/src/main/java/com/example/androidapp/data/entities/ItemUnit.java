package com.example.androidapp.data.entities;

public class ItemUnit {
    private String id;
    private String itemId;
    private String name;
    private float conversionFactor;
    private float price;
    private float cost;
    private boolean isBaseUnit;

    public ItemUnit(String id, String itemId, String name, float conversionFactor, float price, float cost, boolean isBaseUnit) {
        this.id = id;
        this.itemId = itemId;
        this.name = name;
        this.conversionFactor = conversionFactor;
        this.price = price;
        this.cost = cost;
        this.isBaseUnit = isBaseUnit;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public float getConversionFactor() {
        return conversionFactor;
    }

    public float getPrice() {
        return price;
    }

    public float getCost() {
        return cost;
    }

    public boolean isBaseUnit() {
        return isBaseUnit;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConversionFactor(float conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setBaseUnit(boolean baseUnit) {
        isBaseUnit = baseUnit;
    }
}

