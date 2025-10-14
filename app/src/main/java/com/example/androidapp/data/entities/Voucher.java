package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.example.androidapp.database.DatabaseContract.VoucherType;

@Entity(tableName = "vouchers",
        foreignKeys = {
                @ForeignKey(entity = Company.class, parentColumns = "id", childColumns = "companyId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = JournalEntry.class, parentColumns = "id", childColumns = "journalEntryId", onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "journalEntryId")})
public class Voucher {
    @PrimaryKey
    @NonNull
    private String id;
    private String companyId;
    private VoucherType type;
    private String date;
    private float amount;
    private String description;
    private String journalEntryId;

    public Voucher(@NonNull String id, String companyId, VoucherType type, String date, float amount, String description, String journalEntryId) {
        this.id = id;
        this.companyId = companyId;
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.journalEntryId = journalEntryId;
    }

    @Ignore
    public Voucher(@NonNull String id, String companyId, String type, String date, double amount, String description) {
        this.id = id;
        this.companyId = companyId;
        try {
            this.type = VoucherType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.type = VoucherType.PAYMENT; // Default value
        }
        this.date = date;
        this.amount = (float) amount;
        this.description = description;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getCompanyId() { return companyId; }
    public VoucherType getType() { return type; }
    public String getDate() { return date; }
    public float getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getJournalEntryId() { return journalEntryId; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public void setType(VoucherType type) { this.type = type; }
    public void setDate(String date) { this.date = date; }
    public void setAmount(float amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setJournalEntryId(String journalEntryId) { this.journalEntryId = journalEntryId; }
}
