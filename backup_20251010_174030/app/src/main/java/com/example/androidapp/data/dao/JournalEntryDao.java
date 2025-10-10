package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.androidapp.data.entities.JournalEntry;
import java.util.List;

@Dao
public interface JournalEntryDao {
    @Insert
    void insert(JournalEntry journalEntry);

    @Update
    void update(JournalEntry journalEntry);

    @Delete
    void delete(JournalEntry journalEntry);

    @Query("SELECT * FROM journal_entries WHERE companyId = :companyId")
    LiveData<List<JournalEntry>> getAllJournalEntries(String companyId);

    @Query("SELECT * FROM journal_entries WHERE id = :id AND companyId = :companyId LIMIT 1")
    LiveData<JournalEntry> getJournalEntryById(String id, String companyId);

    @Query("SELECT * FROM journal_entries WHERE companyId = :companyId AND entryType = :type")
    List<JournalEntry> getJournalEntriesByCompanyIdAndType(String companyId, String type);

    @Query("SELECT COUNT(*) FROM journal_entries WHERE referenceNumber = :referenceNumber AND companyId = :companyId")
    int countJournalEntriesByReferenceNumber(String referenceNumber, String companyId);
}
