package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "delivery_receipts",
        foreignKeys = {
                @ForeignKey(entity = Campaign.class,
                        parentColumns = "id",
                        childColumns = "campaignId",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Company.class,
                        parentColumns = "id",
                        childColumns = "companyId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Customer.class,
                        parentColumns = "customerId",
                        childColumns = "customerId",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "campaignId"), @Index(value = "companyId"), @Index(value = "customerId")})
public class DeliveryReceipt {
    @PrimaryKey
    @NonNull
    private String id;
    private String campaignId;
    private String companyId;
    private String customerId;
    private String status;
    private String sentAt;

    public DeliveryReceipt(@NonNull String id, String campaignId, String companyId, String customerId, String status, String sentAt) {
        this.id = id;
        this.campaignId = campaignId;
        this.companyId = companyId;
        this.customerId = customerId;
        this.status = status;
        this.sentAt = sentAt;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }
}
