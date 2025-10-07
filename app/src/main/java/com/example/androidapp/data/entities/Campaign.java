package com.example.androidapp.data.entities;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;


import java.util.Date;

import com.example.androidapp.data.DateConverter;

@Entity(tableName = "campaigns",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "companyId",
                            onDelete = ForeignKey.CASCADE)
        })
@TypeConverters({DateConverter.class})
public class Campaign {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int companyId;
    public String name;
    public String description;
    public Date startDate;
    public Date endDate;
    public String status; // e.g., "Draft", "Active", "Completed", "Cancelled"
    public String targetAudience; // e.g., "All Users", "New Users", "High Spenders"
    public String campaignType; // e.g., "Email", "SMS", "In-App Notification"

    public Campaign(int companyId, String name, String description, Date startDate, Date endDate, String status, String targetAudience, String campaignType) {
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.targetAudience = targetAudience;
        this.campaignType = campaignType;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(String campaignType) {
        this.campaignType = campaignType;
    }
}
