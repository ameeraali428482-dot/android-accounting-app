package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.UUID;

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
    @NonNull
    public String id;
    public String journalEntryId;
    public String accountId;
    public float debit;
    public float credit;
    public String description;

    // هذا هو المُنشئ الأساسي الذي ستستخدمه Room.
    // لا نضع @Ignore هنا.
    public JournalEntryItem(@NonNull String id, String journalEntryId, String accountId, float debit, float credit, String description) {
        this.id = id;
        this.journalEntryId = journalEntryId;
        this.accountId = accountId;
        this.debit = debit;
        this.credit = credit;
        this.description = description;
    }

    // هذا هو المُنشئ الإضافي. سنطلب من Room تجاهله.
    @Ignore
    public JournalEntryItem(String journalEntryId, String accountId, float debit, float credit, String description) {
        this.id = UUID.randomUUID().toString();
        this.journalEntryId = journalEntryId;
        this.accountId = accountId;
        this.debit = debit;
        this.credit = credit;
        this.description = description;
    }

    // Getters and Setters
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
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
