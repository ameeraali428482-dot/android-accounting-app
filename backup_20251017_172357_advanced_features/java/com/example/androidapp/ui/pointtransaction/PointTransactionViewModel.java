package com.example.androidapp.ui.pointtransaction;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.PointTransaction;

import java.util.List;
import java.util.concurrent.Executors;

public class PointTransactionViewModel extends AndroidViewModel {
    private AppDatabase database;

    public PointTransactionViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public LiveData<List<PointTransaction>> getAllPointTransactions(String orgId) {
        return database.pointTransactionDao().getAllPointTransactions(orgId);
    }

    public LiveData<PointTransaction> getPointTransactionById(String id, String orgId) {
        return database.pointTransactionDao().getPointTransactionById(id, orgId);
    }

    public LiveData<Integer> getTotalPointsForUser(String userId, String orgId) {
        return database.pointTransactionDao().getTotalPointsForUser(userId, orgId);
    }

    public void insert(PointTransaction pointTransaction) {
        Executors.newSingleThreadExecutor().execute(() -> database.pointTransactionDao().insert(pointTransaction));
    }

    public void update(PointTransaction pointTransaction) {
        Executors.newSingleThreadExecutor().execute(() -> database.pointTransactionDao().update(pointTransaction));
    }

    public void delete(PointTransaction pointTransaction) {
        Executors.newSingleThreadExecutor().execute(() -> database.pointTransactionDao().delete(pointTransaction));
    }
}
