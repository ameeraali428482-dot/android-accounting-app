package com.example.androidapp.data.repositories;

import android.app.Application;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.JournalEntryItemDao;
import com.example.androidapp.data.entities.JournalEntryItem;

import java.util.List;
import java.util.concurrent.Future;

public class JournalEntryItemRepository {
    private JournalEntryItemDao journalEntryItemDao;

    public JournalEntryItemRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        journalEntryItemDao = database.journalEntryItemDao();
    }

    public Future<?> insert(JournalEntryItem journalEntryItem) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryItemDao.insert(journalEntryItem));
    }

    public Future<?> update(JournalEntryItem journalEntryItem) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryItemDao.update(journalEntryItem));
    }

    public Future<?> delete(JournalEntryItem journalEntryItem) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryItemDao.delete(journalEntryItem));
    }

    public Future<List<JournalEntryItem>> getJournalEntryItemsForJournalEntry(String journalEntryId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryItemDao.getJournalEntryItemsForJournalEntry(journalEntryId));
    }

    public Future<JournalEntryItem> getJournalEntryItemById(int id) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryItemDao.getJournalEntryItemById(id));
    }

    public Future<Integer> countJournalEntryItemsForJournalEntry(String journalEntryId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryItemDao.countJournalEntryItemsForJournalEntry(journalEntryId));
    }

    public Future<Float> getTotalAmountForAccountTypeAndDateRange(String accountName, String companyId, String startDate, String endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryItemDao.getTotalAmountForAccountTypeAndDateRange(accountName, companyId, startDate, endDate));
    }
}
