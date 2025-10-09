package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.JournalEntryItemDao;
import com.example.androidapp.data.entities.JournalEntryItem;

import java.util.List;
import java.util.concurrent.Future;

public class JournalEntryItemRepository {
    private JournalEntryItemDao journalEntryItemDao;

    public JournalEntryItemRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        journalEntryItemDao = db.journalEntryItemDao();
    }

    public Future<Void> insert(JournalEntryItem journalEntryItem) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            journalEntryItemDao.insert(journalEntryItem);
            return null;
        });
    }

    public Future<Void> update(JournalEntryItem journalEntryItem) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            journalEntryItemDao.update(journalEntryItem);
            return null;
        });
    }

    public Future<Void> delete(JournalEntryItem journalEntryItem) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            journalEntryItemDao.delete(journalEntryItem);
            return null;
        });
    }

    public LiveData<List<JournalEntryItem>> getJournalEntryItems(String journalEntryId) {
        return journalEntryItemDao.getJournalEntryItems(journalEntryId);
    }

    public Future<Float> getTotalAmountForAccountTypeAndDateRange(String accountType, String companyId, String startDate, String endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryItemDao.getTotalAmountForAccountTypeAndDateRange(accountType, companyId, startDate, endDate));
    }
}
