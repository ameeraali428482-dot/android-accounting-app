package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.ReceiptDao;
import com.example.androidapp.data.entities.Receipt;
import java.util.List;
import java.util.concurrent.Future;

public class ReceiptRepository {
    private ReceiptDao receiptDao;

    public ReceiptRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        receiptDao = db.receiptDao();
    }

    public Future<Void> insert(Receipt receipt) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            receiptDao.insert(receipt);
            return null;
        });
    }

    public Future<Void> update(Receipt receipt) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            receiptDao.update(receipt);
            return null;
        });
    }

    public Future<Void> delete(Receipt receipt) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            receiptDao.delete(receipt);
            return null;
        });
    }

    public LiveData<List<Receipt>> getAllReceipts(String companyId) {
        return receiptDao.getAllReceipts(companyId);
    }

    public Future<Integer> countReceiptByReferenceNumber(String referenceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> receiptDao.countReceiptByReferenceNumber(referenceNumber, companyId));
    }
}
