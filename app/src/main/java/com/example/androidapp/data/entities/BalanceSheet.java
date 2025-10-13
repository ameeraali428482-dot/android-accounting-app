package com.example.androidapp.data.entities;

import androidx.room.*;
import androidx.annotation.NonNull;

@Entity(tableName = "balance_sheets")
public class BalanceSheet {
    @PrimaryKey
    @NonNull
    private String id;
    private String companyId;
    private String period;
    private double totalAssets;
    private double totalLiabilities;
    private double totalEquity;
    private String createdDate;

    public BalanceSheet() {}

    public BalanceSheet(@NonNull String id, String companyId, String period, 
                       double totalAssets, double totalLiabilities, double totalEquity, 
                       String createdDate) {
        this.id = id;
        this.companyId = companyId;
        this.period = period;
        this.totalAssets = totalAssets;
        this.totalLiabilities = totalLiabilities;
        this.totalEquity = totalEquity;
        this.createdDate = createdDate;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public double getTotalAssets() { return totalAssets; }
    public void setTotalAssets(double totalAssets) { this.totalAssets = totalAssets; }

    public double getTotalLiabilities() { return totalLiabilities; }
    public void setTotalLiabilities(double totalLiabilities) { this.totalLiabilities = totalLiabilities; }

    public double getTotalEquity() { return totalEquity; }
    public void setTotalEquity(double totalEquity) { this.totalEquity = totalEquity; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}
