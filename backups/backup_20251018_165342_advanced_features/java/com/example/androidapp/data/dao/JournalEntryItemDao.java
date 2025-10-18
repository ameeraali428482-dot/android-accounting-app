package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.androidapp.data.entities.JournalEntryItem;
import java.util.List;

@Dao
public interface JournalEntryItemDao {
    @Insert
    void insert(JournalEntryItem journalEntryItem);

    @Update
    void update(JournalEntryItem journalEntryItem);

    @Delete
    void delete(JournalEntryItem journalEntryItem);

    @Query("SELECT * FROM journal_entry_items WHERE journalEntryId = :journalEntryId")
    LiveData<List<JournalEntryItem>> getJournalEntryItems(String journalEntryId);

    @Query("SELECT * FROM journal_entry_items WHERE journalEntryId = :journalEntryId")
    List<JournalEntryItem> getJournalEntryItemsSync(String journalEntryId);

    @Query("SELECT * FROM journal_entry_items WHERE id = :id LIMIT 1")
    JournalEntryItem getJournalEntryItemById(int id);

    @Query("SELECT COUNT(*) FROM journal_entry_items WHERE journalEntryId = :journalEntryId")
    int countJournalEntryItemsForJournalEntry(String journalEntryId);

    @Query("SELECT SUM(CASE WHEN jei.debit > 0 THEN jei.debit ELSE -jei.credit END) " +
           "FROM journal_entry_items jei " +
           "JOIN accounts a ON jei.accountId = a.id " +
           "JOIN journal_entries je ON jei.journalEntryId = je.id " +
           "WHERE a.type = :accountType AND je.companyId = :companyId AND je.entryDate BETWEEN :startDate AND :endDate")
    float getTotalAmountForAccountTypeAndDateRange(String accountType, String companyId, String startDate, String endDate);
}
