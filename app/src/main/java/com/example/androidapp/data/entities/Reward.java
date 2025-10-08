package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "rewards",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class Reward {
    @PrimaryKey
    public String id;
    public String name;
    public int pointsRequired;
    private String companyId;

    public Reward(String id, String name, int pointsRequired, String companyId) {
        this.id = id;
        this.name = name;
        this.pointsRequired = pointsRequired;
        this.companyId = companyId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

    public String getCompanyId() {
        return companyId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPointsRequired(int pointsRequired) {
        this.pointsRequired = pointsRequired;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}

