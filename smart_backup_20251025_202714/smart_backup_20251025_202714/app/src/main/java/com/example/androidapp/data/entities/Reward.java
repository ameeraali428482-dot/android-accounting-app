package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;
import java.util.UUID;

@Entity(tableName = "rewards",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class Reward {
    @PrimaryKey
    @NonNull
    public String id;
    @NonNull
    public String companyId;
    public String name;
    public String description;
    public int pointsRequired;
    public Date validUntil;
    public boolean isActive;

    public Reward(@NonNull String id, @NonNull String companyId, String name, String description, int pointsRequired, Date validUntil, boolean isActive) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.pointsRequired = pointsRequired;
        this.validUntil = validUntil;
        this.isActive = isActive;
    }

    @Ignore
    public Reward(@NonNull String companyId, String name, String description, int pointsRequired, Date validUntil, boolean isActive) {
        this.id = UUID.randomUUID().toString();
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.pointsRequired = pointsRequired;
        this.validUntil = validUntil;
        this.isActive = isActive;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPointsRequired() { return pointsRequired; }
    public Date getValidUntil() { return validUntil; }
    public boolean isActive() { return isActive; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }
    public void setValidUntil(Date validUntil) { this.validUntil = validUntil; }
    public void setActive(boolean active) { isActive = active; }
}
