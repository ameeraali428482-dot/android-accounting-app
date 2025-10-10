package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "point_transactions",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "userId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "userId"), @Index(value = "companyId")})
public class PointTransaction {
    @PrimaryKey
    public @NonNull String id;
    public @NonNull String userId;
    public @NonNull String companyId;
    public int points;
    public @NonNull Date createdAt;
    public String orgId;
    public String type;
    public String description;
    public Date transactionDate;

    public PointTransaction(@NonNull String id, @NonNull String userId, @NonNull String companyId, int points, @NonNull Date createdAt, String orgId, String type, String description, Date transactionDate) {
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.points = points;
        this.createdAt = createdAt;
        this.orgId = orgId;
        this.type = type;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    
    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }
    
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }
    
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getDate() { return transactionDate != null ? transactionDate : createdAt; }
    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }
}
