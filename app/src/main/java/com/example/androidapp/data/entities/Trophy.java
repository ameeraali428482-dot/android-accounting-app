package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(tableName = "trophies",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "companyId",
                            onDelete = ForeignKey.CASCADE)
        })
public class Trophy {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int companyId;
    public String name;
    public String description;
    public String imageUrl; // URL or path to trophy image
    public int pointsRequired; // Points required to earn this trophy

    public Trophy(int companyId, String name, String description, String imageUrl, int pointsRequired) {
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.pointsRequired = pointsRequired;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

    public void setPointsRequired(int pointsRequired) {
        this.pointsRequired = pointsRequired;
    }
}
