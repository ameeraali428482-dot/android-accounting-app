package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "account_statements",
        foreignKeys = {
                @ForeignKey(entity = Account.class,
                            parentColumns = "id",
                            childColumns = "accountId",
                            onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "accountId")})
public class AccountStatement {
    @PrimaryKey
    private @NonNull String id;
    private String accountId;
    private Date date;
    private String description;
    private double debit;
    private double credit;
    private double balance;
    private boolean reconciliationStatus;
    private String reconciliationNotes;

    public AccountStatement(String id, String accountId, Date date, String description, double debit, double credit, double balance, boolean reconciliationStatus, String reconciliationNotes) {
        this.id = id;
        this.accountId = accountId;
        this.date = date;
        this.description = description;
        this.debit = debit;
        this.credit = credit;
        this.balance = balance;
        this.reconciliationStatus = reconciliationStatus;
        this.reconciliationNotes = reconciliationNotes;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isReconciliationStatus() {
        return reconciliationStatus;
    }

    public void setReconciliationStatus(boolean reconciliationStatus) {
        this.reconciliationStatus = reconciliationStatus;
    }

    public String getReconciliationNotes() {
        return reconciliationNotes;
    }

    public void setReconciliationNotes(String reconciliationNotes) {
        this.reconciliationNotes = reconciliationNotes;
    }
}

