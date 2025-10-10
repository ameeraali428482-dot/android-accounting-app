import java.util.Date;
package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "payments",
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
public class Payment {
    @PrimaryKey
    public @NonNull String id;
    public @NonNull String companyId;
    public String paymentDate;
    public String payerId; // Customer or Supplier ID
    public String payerType; // 'Customer' or 'Supplier'
    public float amount;
    public String paymentMethod;
    public String referenceNumber; // e.g., check number, transaction ID
    public String notes;
    public String journalEntryId;

    public Payment(String id, String companyId, String paymentDate, String payerId, String payerType, float amount, String paymentMethod, String referenceNumber, String notes, String journalEntryId) {
        this.id = id;
        this.companyId = companyId;
        this.paymentDate = paymentDate;
        this.payerId = payerId;
        this.payerType = payerType;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.referenceNumber = referenceNumber;
        this.notes = notes;
        this.journalEntryId = journalEntryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getPayerType() {
        return payerType;
    }

    public void setPayerType(String payerType) {
        this.payerType = payerType;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getJournalEntryId() {
        return journalEntryId;
    }

    public void setJournalEntryId(String journalEntryId) {
        this.journalEntryId = journalEntryId;
    }
}

