package com.example.androidapp.ui.reports;

import java.util.Date;

/**
 * فئة عنصر التقرير
 */
public class ReportItem {
    
    private String id;
    private String customerName;
    private String type;
    private String category;
    private String status;
    private double amount;
    private Date date;
    private String description;
    private String reference;
    private String paymentMethod;
    private double taxAmount;
    private double discountAmount;
    private String notes;

    public ReportItem() {
    }

    public ReportItem(String id, String customerName, String type, String category, 
                     String status, double amount, Date date) {
        this.id = id;
        this.customerName = customerName;
        this.type = type;
        this.category = category;
        this.status = status;
        this.amount = amount;
        this.date = date;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "ReportItem{" +
                "id='" + id + '\'' +
                ", customerName='" + customerName + '\'' +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}