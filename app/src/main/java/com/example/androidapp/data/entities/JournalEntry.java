package com.example.androidapp.data.entities;
import androidx.room.ForeignKey;import androidx.room.Index;import androidx.room.TypeConverters;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;


@Entity(tableName = "journal_entries")
public class JournalEntry {
    @PrimaryKey
    public String id;
    public String companyId;
    public String entryDate;
    public String description;
    public String referenceNumber; // For linking to invoices, receipts, payments
    public String entryType; // e.g., 'Manual', 'Invoice', 'Receipt', 'Payment'
    public float totalDebit;
    public float totalCredit;

    public JournalEntry(String id, String companyId, String entryDate, String description, String referenceNumber, String entryType, float totalDebit, float totalCredit) {
        this.id = id;
        this.companyId = companyId;
        this.entryDate = entryDate;
        this.description = description;
        this.referenceNumber = referenceNumber;
        this.entryType = entryType;
        this.totalDebit = totalDebit;
        this.totalCredit = totalCredit;
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

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public float getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(float totalDebit) {
        this.totalDebit = totalDebit;
    }

    public float getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(float totalCredit) {
        this.totalCredit = totalCredit;
    }
}

