package com.example.androidapp.data.repositories;

import android.app.Application;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.JournalEntryDao;
import com.example.androidapp.data.entities.JournalEntry;

import java.util.List;
import java.util.concurrent.Future;

public class JournalEntryRepository {
    private JournalEntryDao journalEntryDao;

    public JournalEntryRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        journalEntryDao = database.journalEntryDao();
    }

    public Future<?> insert(JournalEntry journalEntry) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryDao.insert(journalEntry));
    }

    public Future<?> update(JournalEntry journalEntry) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryDao.update(journalEntry));
    }

    public Future<?> delete(JournalEntry journalEntry) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryDao.delete(journalEntry));
    }

    public Future<JournalEntry> getJournalEntryById(String id, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryDao.getJournalEntryById(id, companyId));
    }

    public Future<List<JournalEntry>> getAllJournalEntries(String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryDao.getAllJournalEntries(companyId));
    }

    public Future<Integer> countJournalEntryByReferenceNumber(String referenceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryDao.countJournalEntryByReferenceNumber(referenceNumber, companyId));
    }
}
