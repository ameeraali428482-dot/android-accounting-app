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

    public float getTotalCostOfGoodsSold() {
        return totalCostOfGoodsSold;
    }

    public float getGrossProfit() {
        return grossProfit;
    }

    public float getOperatingExpenses() {
        return operatingExpenses;
    }

    public float getNetProfit() {
        return netProfit;
    }
}
