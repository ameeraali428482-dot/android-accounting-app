package com.example.androidapp.ui.journalentry.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.JournalEntryDao;
import com.example.androidapp.data.entities.JournalEntry;
import java.util.List;
import java.util.concurrent.Executors;

public class JournalEntryViewModel extends AndroidViewModel {
    private final JournalEntryDao dao;

    public JournalEntryViewModel(@NonNull Application app) {
        super(app);
        dao = AppDatabase.getDatabase(app).journalEntryDao();
    }

    public LiveData<List<JournalEntry>> getAllJournalEntries(String companyId) {
        return dao.getAllJournalEntries(companyId);
    }

    public LiveData<JournalEntry> getJournalEntryById(String id, String companyId) {
        return dao.getJournalEntryById(id, companyId);
    }

    public void insert(JournalEntry je) {
        Executors.newSingleThreadExecutor().execute(() -> dao.insert(je));
    }

    public void update(JournalEntry je) {
        Executors.newSingleThreadExecutor().execute(() -> dao.update(je));
    }

    public void delete(JournalEntry je) {
        Executors.newSingleThreadExecutor().execute(() -> dao.delete(je));
    }
}
