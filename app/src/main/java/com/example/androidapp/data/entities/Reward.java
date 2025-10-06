package com.example.androidapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import java.util.Date;

import com.example.androidapp.data.DateConverter;

@Entity(tableName = "rewards",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "orgId",
                            onDelete = ForeignKey.CASCADE)
        })
@TypeConverters({DateConverter.class})
public class Reward {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int orgId;
    public String name;
    public String description;
    public int pointsRequired;
    public Date validUntil;
    public boolean isActive;

    public Reward(int orgId, String name, String description, int pointsRequired, Date validUntil, boolean isActive) {
        this.orgId = orgId;
        this.name = name;
        this.description = description;
        this.pointsRequired = pointsRequired;
        this.validUntil = validUntil;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
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

    public int getPointsRequired() {
        return pointsRequired;
    }

    public void setPointsRequired(int pointsRequired) {
        this.pointsRequired = pointsRequired;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

