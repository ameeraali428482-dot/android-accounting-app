package com.example.androidapp.models;

public class FinancialTransfer {
    private String id;
    private String companyId;
    private String fromCashBoxId;
    private String toCashBoxId;
    private float amount;
    private Float commission;
    private String date;
    private String journalEntryId;

    public FinancialTransfer(String id, String companyId, String fromCashBoxId, String toCashBoxId, float amount, Float commission, String date, String journalEntryId) {
        this.id = id;
        this.companyId = companyId;
        this.fromCashBoxId = fromCashBoxId;
        this.toCashBoxId = toCashBoxId;
        this.amount = amount;
        this.commission = commission;
        this.date = date;
        this.journalEntryId = journalEntryId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getFromCashBoxId() {
        return fromCashBoxId;
    }

    public String getToCashBoxId() {
        return toCashBoxId;
    }

    public float getAmount() {
        return amount;
    }

    public Float getCommission() {
        return commission;
    }

    public String getDate() {
        return date;
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

    public void setFromCashBoxId(String fromCashBoxId) {
        this.fromCashBoxId = fromCashBoxId;
    }

    public void setToCashBoxId(String toCashBoxId) {
        this.toCashBoxId = toCashBoxId;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setCommission(Float commission) {
        this.commission = commission;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setJournalEntryId(String journalEntryId) {
        this.journalEntryId = journalEntryId;
    }
}

