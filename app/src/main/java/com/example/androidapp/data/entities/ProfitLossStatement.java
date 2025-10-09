package com.example.androidapp.data.entities;

public class ProfitLossStatement {
    private float totalRevenue;
    private float totalCostOfGoodsSold;
    private float grossProfit;
    private float operatingExpenses;
    private float netProfit;

    public ProfitLossStatement(float totalRevenue, float totalCostOfGoodsSold, float grossProfit, float operatingExpenses, float netProfit) {
        this.totalRevenue = totalRevenue;
        this.totalCostOfGoodsSold = totalCostOfGoodsSold;
        this.grossProfit = grossProfit;
        this.operatingExpenses = operatingExpenses;
        this.netProfit = netProfit;
    }

    public float getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(float totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public float getTotalCostOfGoodsSold() {
        return totalCostOfGoodsSold;
    }

    public void setTotalCostOfGoodsSold(float totalCostOfGoodsSold) {
        this.totalCostOfGoodsSold = totalCostOfGoodsSold;
    }

    public float getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(float grossProfit) {
        this.grossProfit = grossProfit;
    }

    public float getOperatingExpenses() {
        return operatingExpenses;
    }

    public void setOperatingExpenses(float operatingExpenses) {
        this.operatingExpenses = operatingExpenses;
    }

    public float getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(float netProfit) {
        this.netProfit = netProfit;
    }
}
