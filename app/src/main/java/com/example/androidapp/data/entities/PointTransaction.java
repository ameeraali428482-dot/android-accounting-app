package com.example.androidapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import java.util.Date;

import com.example.androidapp.data.DateConverter;

@Entity(tableName = "point_transactions",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                            parentColumns = "id",
                            childColumns = "userId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "orgId",
                            onDelete = ForeignKey.CASCADE)
        })
@TypeConverters({DateConverter.class})
public class PointTransaction {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public int orgId;
    public int points;
    public String type; // e.g., "EARN", "REDEEM"
    public String description;
    public Date transactionDate;

    public PointTransaction(int userId, int orgId, int points, String type, String description, Date transactionDate) {
        this.userId = userId;
        this.orgId = orgId;
        this.points = points;
        this.type = type;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}

