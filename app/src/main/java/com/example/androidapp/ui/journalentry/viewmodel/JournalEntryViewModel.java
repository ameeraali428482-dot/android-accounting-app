package com.example.androidapp.ui.journalentry.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.JournalEntryDao;
import com.example.androidapp.data.entities.JournalEntry;
import java.util.List;



public class JournalEntryViewModel extends AndroidViewModel {
    private JournalEntryDao journalEntryDao;

    public JournalEntryViewModel(@NonNull Application application) {
        super(application);
        journalEntryDao = AppDatabase.getDatabase(application).journalEntryDao();
    }

    public LiveData<List<JournalEntry>> getAllJournalEntries(String companyId) {
        return journalEntryDao.getAllJournalEntries(companyId);
    }

    public LiveData<JournalEntry> getJournalEntryById(String journalEntryId, String companyId) {
        return journalEntryDao.getJournalEntryById(journalEntryId, companyId);
    }

    public void insert(JournalEntry journalEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> journalEntryDao.insert(journalEntry));
    }

    public void update(JournalEntry journalEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> journalEntryDao.update(journalEntry));
    }

    public void delete(JournalEntry journalEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> journalEntryDao.delete(journalEntry));
    }
}
