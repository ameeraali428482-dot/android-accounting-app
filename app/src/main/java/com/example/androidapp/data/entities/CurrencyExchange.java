package com.example.androidapp.data.entities;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

public class CurrencyExchange {
    private String id;
    private String companyId;
    private String fromCurrencyId;
    private float fromAmount;
    private String toCurrencyId;
    private float toAmount;
    private float rate;
    private String date;
    private String journalEntryId;

    public CurrencyExchange(String id, String companyId, String fromCurrencyId, float fromAmount, String toCurrencyId, float toAmount, float rate, String date, String journalEntryId) {
        this.id = id;
        this.companyId = companyId;
        this.fromCurrencyId = fromCurrencyId;
        this.fromAmount = fromAmount;
        this.toCurrencyId = toCurrencyId;
        this.toAmount = toAmount;
        this.rate = rate;
        this.date = date;
        this.journalEntryId = journalEntryId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getFromCurrencyId() {
        return fromCurrencyId;
    }

    public float getFromAmount() {
        return fromAmount;
    }

    public String getToCurrencyId() {
        return toCurrencyId;
    }

    public float getToAmount() {
        return toAmount;
    }

    public float getRate() {
        return rate;
    }

    public String getDate() {
        return date;
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

    public void setFromCurrencyId(String fromCurrencyId) {
        this.fromCurrencyId = fromCurrencyId;
    }

    public void setFromAmount(float fromAmount) {
        this.fromAmount = fromAmount;
    }

    public void setToCurrencyId(String toCurrencyId) {
        this.toCurrencyId = toCurrencyId;
    }

    public void setToAmount(float toAmount) {
        this.toAmount = toAmount;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setJournalEntryId(String journalEntryId) {
        this.journalEntryId = journalEntryId;
    }
}

