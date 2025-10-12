package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "trophies",
        foreignKeys = @ForeignKey(entity = Company.class,
                parentColumns = "id",
                childColumns = "companyId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class Trophy {
    @PrimaryKey
    public @NonNull String id;
    public String name;
    public String description;
    private @NonNull String companyId;
    private int pointsRequired;
    private String imageUrl;

    public Trophy(@NonNull String id, String name, String description, @NonNull String companyId, int pointsRequired, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.companyId = companyId;
        this.pointsRequired = pointsRequired;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    
    public int getPointsRequired() { return pointsRequired; }
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
