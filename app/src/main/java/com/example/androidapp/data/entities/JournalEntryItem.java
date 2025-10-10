package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;



@Entity(tableName = "journal_entry_items",
        foreignKeys = {
                @ForeignKey(entity = JournalEntry.class,
                           parentColumns = "id",
                           childColumns = "journalEntryId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Account.class,
                           parentColumns = "id",
                           childColumns = "accountId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "journalEntryId"), @Index(value = "accountId")})
public class JournalEntryItem {
    @PrimaryKey
    public @NonNull String id;
    public String journalEntryId;
    public String accountId;
    public float debit;
    public float credit;
    public String description;

    public JournalEntryItem(String id, String journalEntryId, String accountId, float debit, float credit, String description) {
        this.id = id;
        this.journalEntryId = journalEntryId;
        this.accountId = accountId;
        this.debit = debit;
        this.credit = credit;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJournalEntryId() {
        return journalEntryId;
    }

    public void setJournalEntryId(String journalEntryId) {
        this.journalEntryId = journalEntryId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public float getDebit() {
        return debit;
    }

    public void setDebit(float debit) {
        this.debit = debit;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

