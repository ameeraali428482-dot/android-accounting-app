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
        indices = {@Index("companyId")})
public class Trophy {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String description;
    @NonNull
    private String companyId;
    private int pointsRequired;
    private String imageUrl;

    public Trophy(@NonNull String id, @NonNull String companyId, String name, String description, int pointsRequired, String imageUrl) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.pointsRequired = pointsRequired;
        this.imageUrl = imageUrl;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPointsRequired() { return pointsRequired; }
    public String getImageUrl() { return imageUrl; }
    public String getTrophyName() { return name; }
    public String getTrophyDescription() { return description; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setTrophyName(String name) { this.name = name; }
    public void setTrophyDescription(String description) { this.description = description; }
}
