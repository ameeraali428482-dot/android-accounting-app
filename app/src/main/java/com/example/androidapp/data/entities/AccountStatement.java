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


import java.util.Date;

@Entity(tableName = "account_statements",
        foreignKeys = {
                @ForeignKey(entity = Account.class,
                            parentColumns = "id",
                            childColumns = "accountId",
                            onDelete = ForeignKey.CASCADE)
        })
@Entity(tableName = "account_statements")
public class AccountStatement {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int accountId;
    public Date date;
    public String description;
    public double debit;
    public double credit;
    public double balance;
    public boolean reconciliationStatus; // For matching with other statements
    public String reconciliationNotes;

    public AccountStatement(int accountId, Date date, String description, double debit, double credit, double balance, boolean reconciliationStatus, String reconciliationNotes) {
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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
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
