package com.example.androidapp.data.entities;

public class Payroll {
    private String id;
    private String companyId;
    private int year;
    private int month;
    private String status;
    private float totalSalary;
    private float totalDeductions;
    private float totalBonuses;
    private float netPayable;
    private String journalEntryId;

    public Payroll(String id, String companyId, int year, int month, String status, float totalSalary, float totalDeductions, float totalBonuses, float netPayable, String journalEntryId) {
        this.id = id;
        this.companyId = companyId;
        this.year = year;
        this.month = month;
        this.status = status;
        this.totalSalary = totalSalary;
        this.totalDeductions = totalDeductions;
        this.totalBonuses = totalBonuses;
        this.netPayable = netPayable;
        this.journalEntryId = journalEntryId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public String getStatus() {
        return status;
    }

    public float getTotalSalary() {
        return totalSalary;
    }

    public float getTotalDeductions() {
        return totalDeductions;
    }

    public float getTotalBonuses() {
        return totalBonuses;
    }

    public float getNetPayable() {
        return netPayable;
    }

    public String getJournalEntryId() {
        return journalEntryId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotalSalary(float totalSalary) {
        this.totalSalary = totalSalary;
    }

    public void setTotalDeductions(float totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public void setTotalBonuses(float totalBonuses) {
        this.totalBonuses = totalBonuses;
    }

    public void setNetPayable(float netPayable) {
        this.netPayable = netPayable;
    }

    public void setJournalEntryId(String journalEntryId) {
        this.journalEntryId = journalEntryId;
    }
}

