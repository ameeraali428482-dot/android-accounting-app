package com.example.androidapp.data.entities;

import androidx.room.TypeConverters;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.example.androidapp.data.DatabaseContract.VoucherType;

@Entity(tableName = "vouchers")
public class Voucher {
    private String id;
    private String companyId;
    private VoucherType type;
    private String date;
    private float amount;
    private String description;
    private String journalEntryId;

    public Voucher(String id, String companyId, VoucherType type, String date, float amount, String description, String journalEntryId) {
        this.id = id;
        this.companyId = companyId;
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.journalEntryId = journalEntryId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public VoucherType getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public float getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
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

    public void setType(VoucherType type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setJournalEntryId(String journalEntryId) {
        this.journalEntryId = journalEntryId;
    }
}

