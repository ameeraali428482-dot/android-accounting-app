package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "receipts",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = JournalEntry.class,
                           parentColumns = "id",
                           childColumns = "journalEntryId",
                           onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "journalEntryId")})
public class Receipt {
    @PrimaryKey
    @NonNull
    public String id;
    @NonNull
    public String companyId;
    public String receiptDate;
    public String payerId; // Customer or Supplier ID
    public String payerType; // 'Customer' or 'Supplier'
    public float amount;
    public String paymentMethod;
    public String referenceNumber; // e.g., check number, transaction ID
    public String notes;
    public String journalEntryId;

    public Receipt(@NonNull String id, @NonNull String companyId, String receiptDate, String payerId, String payerType, float amount, String paymentMethod, String referenceNumber, String notes, String journalEntryId) {
        this.id = id;
        this.companyId = companyId;
        this.receiptDate = receiptDate;
        this.payerId = payerId;
        this.payerType = payerType;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.referenceNumber = referenceNumber;
        this.notes = notes;
        this.journalEntryId = journalEntryId;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public String getReceiptDate() { return receiptDate; }
    public String getPayerId() { return payerId; }
    public String getPayerType() { return payerType; }
    public float getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getReferenceNumber() { return referenceNumber; }
    public String getNotes() { return notes; }
    public String getJournalEntryId() { return journalEntryId; }
    public String getReceiptNumber() { return referenceNumber; } // Alias
    public float getTotalAmount() { return amount; } // Alias

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public void setReceiptDate(String receiptDate) { this.receiptDate = receiptDate; }
    public void setPayerId(String payerId) { this.payerId = payerId; }
    public void setPayerType(String payerType) { this.payerType = payerType; }
    public void setAmount(float amount) { this.amount = amount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setJournalEntryId(String journalEntryId) { this.journalEntryId = journalEntryId; }
}
