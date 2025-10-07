package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import java.util.Date;

import com.example.androidapp.data.DateConverter;

@Entity(tableName = "repairs",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "companyId",
                            onDelete = ForeignKey.CASCADE)
        })
@TypeConverters({DateConverter.class})
public class Repair {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int companyId;
    public String title;
    public String description;
    public Date requestDate;
    public Date completionDate;
    public String status; // e.g., "Pending", "In Progress", "Completed", "Cancelled"
    public String assignedTo; // e.g., Employee ID or Name
    public double totalCost;

    public Repair(int companyId, String title, String description, Date requestDate, Date completionDate, String status, String assignedTo, double totalCost) {
        this.companyId = companyId;
        this.title = title;
        this.description = description;
        this.requestDate = requestDate;
        this.completionDate = completionDate;
        this.status = status;
        this.assignedTo = assignedTo;
        this.totalCost = totalCost;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
