package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Entity(tableName = "repairs",
        foreignKeys = {
                @ForeignKey(entity = Company.class, parentColumns = "id", childColumns = "companyId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Customer.class, parentColumns = "id", childColumns = "customerId", onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "customerId")})
public class Repair {
    @PrimaryKey
    @NonNull
    public String id;
    @NonNull
    public String companyId;
    public String customerId;
    public String deviceName;
    public String issueDescription;
    public String status;
    @NonNull
    public Date requestDate;
    public Date completionDate;
    public float totalCost;
    public String assignedTo;
    public String title;

    public Repair(@NonNull String id, @NonNull String companyId, String customerId, String deviceName, String issueDescription, String status, @NonNull Date requestDate, Date completionDate, float totalCost, String assignedTo, String title) {
        this.id = id;
        this.companyId = companyId;
        this.customerId = customerId;
        this.deviceName = deviceName;
        this.issueDescription = issueDescription;
        this.status = status;
        this.requestDate = requestDate;
        this.completionDate = completionDate;
        this.totalCost = totalCost;
        this.assignedTo = assignedTo;
        this.title = title;
    }

    @Ignore
    public Repair(String companyId, String title, String description, Date requestDate, Date completionDate, String status, String assignedTo, double totalCost) {
        this.id = UUID.randomUUID().toString();
        this.companyId = companyId;
        this.title = title;
        this.issueDescription = description;
        this.requestDate = requestDate;
        this.completionDate = completionDate;
        this.status = status;
        this.assignedTo = assignedTo;
        this.totalCost = (float) totalCost;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public String getCustomerId() { return customerId; }
    public String getDeviceName() { return deviceName; }
    public String getIssueDescription() { return issueDescription; }
    public String getStatus() { return status; }
    @NonNull
    public Date getRequestDate() { return requestDate; }
    public Date getCompletionDate() { return completionDate; }
    public float getTotalCost() { return totalCost; }
    public String getAssignedTo() { return assignedTo; }
    public String getTitle() { return title; }
    public String getRepairDate() { return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(requestDate); }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }
    public void setStatus(String status) { this.status = status; }
    public void setRequestDate(@NonNull Date requestDate) { this.requestDate = requestDate; }
    public void setCompletionDate(Date completionDate) { this.completionDate = completionDate; }
    public void setTotalCost(float totalCost) { this.totalCost = totalCost; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public void setTitle(String title) { this.title = title; }
}
