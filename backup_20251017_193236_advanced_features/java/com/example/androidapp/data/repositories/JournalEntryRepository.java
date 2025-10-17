package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.JournalEntryDao;
import com.example.androidapp.data.entities.JournalEntry;
import java.util.List;
import java.util.concurrent.Future;

public class JournalEntryRepository {
    private JournalEntryDao journalEntryDao;

    public JournalEntryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        journalEntryDao = db.journalEntryDao();
    }

    public Future<Void> insert(JournalEntry journalEntry) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            journalEntryDao.insert(journalEntry);
            return null;
        });
    }

    public Future<Void> update(JournalEntry journalEntry) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            journalEntryDao.update(journalEntry);
            return null;
        });
    }

    public Future<Void> delete(JournalEntry journalEntry) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            journalEntryDao.delete(journalEntry);
            return null;
        });
    }

    public LiveData<List<JournalEntry>> getAllJournalEntries(String companyId) {
        return journalEntryDao.getAllJournalEntries(companyId);
    }

    public Future<Integer> countJournalEntryByReferenceNumber(String referenceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryDao.countJournalEntryByReferenceNumber(referenceNumber, companyId));
    }
}
