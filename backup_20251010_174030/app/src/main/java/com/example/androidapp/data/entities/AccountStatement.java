package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "account_statements",
        foreignKeys = {
                @ForeignKey(entity = Account.class,
                            parentColumns = "id",
                            childColumns = "accountId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "companyId",
                            onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "accountId"), @Index(value = "companyId")})
public class AccountStatement {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private @NonNull String companyId;
    private @NonNull String accountId;
    private @NonNull String date;
    private @NonNull String description;
    private float debit;
    private float credit;
    private float runningBalance;
    private String referenceType;
    private String referenceId;

    public AccountStatement(@NonNull String companyId, @NonNull String accountId, @NonNull String date, @NonNull String description, float debit, float credit, float runningBalance, String referenceType, String referenceId) {
        this.companyId = companyId;
        this.accountId = accountId;
        this.date = date;
        this.description = description;
        this.debit = debit;
        this.credit = credit;
        this.runningBalance = runningBalance;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    @NonNull
    public String getAccountId() { return accountId; }
    public void setAccountId(@NonNull String accountId) { this.accountId = accountId; }
    @NonNull
    public String getDate() { return date; }
    public void setDate(@NonNull String date) { this.date = date; }
    @NonNull
    public String getDescription() { return description; }
    public void setDescription(@NonNull String description) { this.description = description; }
    public float getDebit() { return debit; }
    public void setDebit(float debit) { this.debit = debit; }
    public float getCredit() { return credit; }
    public void setCredit(float credit) { this.credit = credit; }
    public float getRunningBalance() { return runningBalance; }
    public void setRunningBalance(float runningBalance) { this.runningBalance = runningBalance; }
    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
}
