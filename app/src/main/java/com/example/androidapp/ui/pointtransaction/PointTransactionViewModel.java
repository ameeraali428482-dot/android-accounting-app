package com.example.androidapp.ui.pointtransaction;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.PointTransaction;
import java.util.List;






public class PointTransactionViewModel extends AndroidViewModel {
    private final LiveData<List<PointTransaction>> allPointTransactions;
    private final AppDatabase database;

    public PointTransactionViewModel(Application application) {
        super(application);
        database = AppDatabase.getDatabase(application);
        // This will need to be updated to filter by orgId and potentially userId
        allPointTransactions = database.pointTransactionDao().getAllPointTransactions("0"); // Placeholder orgId
    }

    public LiveData<List<PointTransaction>> getAllPointTransactions(int orgId) {
        return database.pointTransactionDao().getAllPointTransactions(String.valueOf(orgId));
    }

    public LiveData<PointTransaction> getPointTransactionById(int id, int orgId) {
        return database.pointTransactionDao().getPointTransactionById(id, String.valueOf(orgId));
    }

    public LiveData<Integer> getTotalPointsForUser(int userId, int orgId) {
        return database.pointTransactionDao().getTotalPointsForUser(userId, String.valueOf(orgId));
    }

    public void insert(PointTransaction pointTransaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.pointTransactionDao().insert(pointTransaction);
        });
    }

    public void update(PointTransaction pointTransaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.pointTransactionDao().update(pointTransaction);
        });
    }

    public void delete(PointTransaction pointTransaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.pointTransactionDao().delete(pointTransaction);
        });
    }
}

