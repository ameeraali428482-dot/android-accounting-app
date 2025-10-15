package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "invoices",
        foreignKeys = {
                @ForeignKey(entity = Company.class, parentColumns = "id", childColumns = "companyId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Customer.class, parentColumns = "id", childColumns = "customerId", onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Supplier.class, parentColumns = "id", childColumns = "supplierId", onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index("companyId"), @Index("customerId"), @Index("supplierId")})
public class Invoice {
    @PrimaryKey
    @NonNull
    public String id;
    @NonNull
    public String companyId;
    public String customerId;
    public String supplierId;
    public String invoiceNumber;
    public String invoiceDate;
    public String dueDate;
    public float totalAmount;
    public String status;
    public String invoiceType;
    public float cashAmount;
    public float subTotal;
    public float taxAmount;
    public float discountAmount;

    public Invoice(@NonNull String id, @NonNull String companyId, String customerId, String supplierId, String invoiceNumber, String invoiceDate, String dueDate, float totalAmount, String status, String invoiceType, float cashAmount, float subTotal, float taxAmount, float discountAmount) {
        this.id = id;
        this.companyId = companyId;
        this.customerId = customerId;
        this.supplierId = supplierId;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.invoiceType = invoiceType;
        this.cashAmount = cashAmount;
        this.subTotal = subTotal;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
    }

    @NonNull
    public String getId() { return id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public String getCustomerId() { return customerId; }
    public String getSupplierId() { return supplierId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getInvoiceDate() { return invoiceDate; }
    public String getDueDate() { return dueDate; }
    public float getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getInvoiceType() { return invoiceType; }
    public float getCashAmount() { return cashAmount; }
    public float getSubTotal() { return subTotal; }
    public float getTaxAmount() { return taxAmount; }
    public float getDiscountAmount() { return discountAmount; }
    public String getCustomerName() { return "Customer " + customerId; }
    public float getGrandTotal() { return totalAmount; }

    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setTotalAmount(float totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }
    public void setCashAmount(float cashAmount) { this.cashAmount = cashAmount; }
    public void setSubTotal(float subTotal) { this.subTotal = subTotal; }
    public void setTaxAmount(float taxAmount) { this.taxAmount = taxAmount; }
    public void setDiscountAmount(float discountAmount) { this.discountAmount = discountAmount; }
}
