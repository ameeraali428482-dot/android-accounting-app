package com.example.androidapp.models;

import java.util.Date;


public class JournalEntry {
    private String id;
    private String companyId;
    private String date;
    private String description;
    private String invoiceId;
    private Double amount;
    private String type; // e.g., "income", "expense"

    public JournalEntry(String id, String companyId, String date, String description, String invoiceId, Double amount, String type) {
        this.id = id;
        this.companyId = companyId;
        this.date = date;
        this.description = description;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.type = type;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public Double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}

