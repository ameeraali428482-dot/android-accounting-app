package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "point_transactions")
public class PointTransaction {
    @PrimaryKey
    private String id;
    private String companyId;
    private String type;
    private int points;
    private Date date;
    private String userId;
    private String description;
    private String referenceId;
    private Date createdAt;

    public PointTransaction(String id, String companyId, String type, int points, Date date, String userId) {
        this.id = id;
        this.companyId = companyId;
        this.type = type;
        this.points = points;
        this.date = date;
        this.userId = userId;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
