package com.example.androidapp.data.entities;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey
    private String id;
    private String name;
    private String scientificName;
    private String description;
    private String brand;
    private String agent;
    private String barcode;
    private String companyId;
    private Integer reorderLevel;

    public Item(String id, String name, String scientificName, String description, String brand, String agent, String barcode, String companyId, Integer reorderLevel) {
        this.id = id;
        this.name = name;
        this.scientificName = scientificName;
        this.description = description;
        this.brand = brand;
        this.agent = agent;
        this.barcode = barcode;
        this.companyId = companyId;
        this.reorderLevel = reorderLevel;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getDescription() {
        return description;
    }

    public String getBrand() {
        return brand;
    }

    public String getAgent() {
        return agent;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getCompanyId() {
        return companyId;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
}

