package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "journal_entries",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class JournalEntry {
    @PrimaryKey
    @NonNull
    public String id;
    @NonNull
    public String companyId;
    public String entryDate;
    public String description;
    public String referenceNumber;
    public String entryType;
    public float totalDebit;
    public float totalCredit;

    public JournalEntry(@NonNull String id, @NonNull String companyId, String entryDate, String description, String referenceNumber, String entryType, float totalDebit, float totalCredit) {
        this.id = id;
        this.companyId = companyId;
        this.entryDate = entryDate;
        this.description = description;
        this.referenceNumber = referenceNumber;
        this.entryType = entryType;
        this.totalDebit = totalDebit;
        this.totalCredit = totalCredit;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public String getEntryDate() { return entryDate; }
    public String getDescription() { return description; }
    public String getReferenceNumber() { return referenceNumber; }
    public String getEntryType() { return entryType; }
    public float getTotalDebit() { return totalDebit; }
    public float getTotalCredit() { return totalCredit; }
    public float getAmount() { return totalDebit > 0 ? totalDebit : totalCredit; } // Added for compatibility

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public void setEntryDate(String entryDate) { this.entryDate = entryDate; }
    public void setDescription(String description) { this.description = description; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public void setEntryType(String entryType) { this.entryType = entryType; }
    public void setTotalDebit(float totalDebit) { this.totalDebit = totalDebit; }
    public void setTotalCredit(float totalCredit) { this.totalCredit = totalCredit; }
}
