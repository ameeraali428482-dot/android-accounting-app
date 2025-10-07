package com.example.androidapp.data.entities;
import androidx.room.ForeignKey;import androidx.room.Index;import androidx.room.TypeConverters;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;

public class DeliveryReceipt {
    private String id;
    private String campaignId;
    private String companyId;
    private String customerId;
    private String status;
    private String sentAt;

    public DeliveryReceipt(String id, String campaignId, String companyId, String customerId, String status, String sentAt) {
        this.id = id;
        this.campaignId = campaignId;
        this.companyId = companyId;
        this.customerId = customerId;
        this.status = status;
        this.sentAt = sentAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getStatus() {
        return status;
    }

    public String getSentAt() {
        return sentAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }
}

