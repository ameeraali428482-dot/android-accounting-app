package com.example.androidapp.models;

import java.util.Date;


public class Invoice {
    private String id;
    private String companyId;
    private String invoiceType;
    private String paymentType;
    private String date;
    private String dueDate;
    private String customerId;
    private String supplierId;
    private float subTotal;
    private float discount;
    private float tax;
    private float grandTotal;
    private float paidAmount;
    private float remainingAmount;
    private String status;
    private String notes;

    public Invoice(String id, String companyId, String invoiceType, String paymentType, String date, String dueDate, String customerId, String supplierId, float subTotal, float discount, float tax, float grandTotal, float paidAmount, float remainingAmount, String status, String notes) {
        this.id = id;
        this.companyId = companyId;
        this.invoiceType = invoiceType;
        this.paymentType = paymentType;
        this.date = date;
        this.dueDate = dueDate;
        this.customerId = customerId;
        this.supplierId = supplierId;
        this.subTotal = subTotal;
        this.grandTotal = grandTotal;
        this.paidAmount = paidAmount;
        this.remainingAmount = remainingAmount;
        this.status = status;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getDate() {
        return date;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public float getSubTotal() {
        return subTotal;
    }

    public float getGrandTotal() {
        return grandTotal;
    }

    public float getPaidAmount() {
        return paidAmount;
    }

    public float getRemainingAmount() {
        return remainingAmount;
    }

    public String getStatus() {
        return status;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public void setSubTotal(float subTotal) {
        this.subTotal = subTotal;
    }

    public void setGrandTotal(float grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void setPaidAmount(float paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setRemainingAmount(float remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

