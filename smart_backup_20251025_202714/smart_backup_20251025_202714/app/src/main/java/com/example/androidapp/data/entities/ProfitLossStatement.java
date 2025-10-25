package com.example.androidapp.data.entities;

import androidx.room.*;
import androidx.annotation.NonNull;

@Entity(tableName = "profit_loss_statements")
public class ProfitLossStatement {
    @PrimaryKey
    @NonNull
    private String id;
    private String companyId;
    private String period;
    private double totalRevenue;
    private double totalExpenses;
    private double netProfit;
    private String createdDate;

    @Ignore
    public ProfitLossStatement() {}

    public ProfitLossStatement(@NonNull String id, String companyId, String period,
                              double totalRevenue, double totalExpenses, double netProfit,
                              String createdDate) {
        this.id = id;
        this.companyId = companyId;
        this.period = period;
        this.totalRevenue = totalRevenue;
        this.totalExpenses = totalExpenses;
        this.netProfit = netProfit;
        this.createdDate = createdDate;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public double getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(double totalExpenses) { this.totalExpenses = totalExpenses; }

    public double getNetProfit() { return netProfit; }
    public void setNetProfit(double netProfit) { this.netProfit = netProfit; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}
