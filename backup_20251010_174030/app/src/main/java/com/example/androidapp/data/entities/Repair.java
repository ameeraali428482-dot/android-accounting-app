package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "repairs",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Customer.class,
                           parentColumns = "id",
                           childColumns = "customerId",
                           onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "customerId")})
public class Repair {
    @PrimaryKey
    public @NonNull String id;
    public @NonNull String companyId;
    public String customerId;
    public String deviceName;
    public String issueDescription;
    public String status;
    public @NonNull Date startDate;
    public Date endDate;
    public float cost;
    public @NonNull Date requestDate;

    public Repair(@NonNull String id, @NonNull String companyId, String customerId, String deviceName, String issueDescription, String status, @NonNull Date startDate, Date endDate, float cost, @NonNull Date requestDate) {
        this.id = id;
        this.companyId = companyId;
        this.customerId = customerId;
        this.deviceName = deviceName;
        this.issueDescription = issueDescription;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.requestDate = requestDate;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    @NonNull
    public Date getStartDate() { return startDate; }
    public void setStartDate(@NonNull Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public float getCost() { return cost; }
    public void setCost(float cost) { this.cost = cost; }
    @NonNull
    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(@NonNull Date requestDate) { this.requestDate = requestDate; }
}
