package com.example.androidapp.data.dao;

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
    List<JournalEntryItem> getJournalEntryItemsForJournalEntry(String journalEntryId);

    @Query("SELECT * FROM journal_entry_items WHERE id = :id LIMIT 1")
    JournalEntryItem getJournalEntryItemById(int id);

    @Query("SELECT COUNT(*) FROM journal_entry_items WHERE journalEntryId = :journalEntryId")
    int countJournalEntryItemsForJournalEntry(String journalEntryId);
}
