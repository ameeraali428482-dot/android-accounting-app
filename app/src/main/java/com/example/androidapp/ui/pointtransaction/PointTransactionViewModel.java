package com.example.androidapp.ui.pointtransaction;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PointTransactionDao;
import com.example.androidapp.data.entities.PointTransaction;
import java.util.List;

public class PointTransactionViewModel extends AndroidViewModel {
    private PointTransactionDao pointTransactionDao;
    private AppDatabase database;

    public PointTransactionViewModel(Application application) {
        super(application);
        database = AppDatabase.getDatabase(application);
        pointTransactionDao = database.pointTransactionDao();
    }

    public LiveData<List<PointTransaction>> getAllPointTransactions(String companyId) {
        return pointTransactionDao.getAllPointTransactions(companyId);
    }

    public LiveData<List<PointTransaction>> getPointTransactionsByUserId(String userId, String companyId) {
        return pointTransactionDao.getPointTransactionsByUserId(userId, companyId);
    }

    public LiveData<PointTransaction> getPointTransactionById(String id, String companyId) {
        return pointTransactionDao.getPointTransactionById(id, companyId);
    }

    public LiveData<Integer> getTotalPointsForUser(String userId, String companyId) {
        return pointTransactionDao.getTotalPointsForUser(userId, companyId);
    }

    public void insert(PointTransaction pointTransaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> pointTransactionDao.insert(pointTransaction));
    }

    public void update(PointTransaction pointTransaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> pointTransactionDao.update(pointTransaction));
    }

    public void delete(PointTransaction pointTransaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> pointTransactionDao.delete(pointTransaction));
    }
}
