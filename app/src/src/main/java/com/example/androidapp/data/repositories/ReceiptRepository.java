package com.example.androidapp.data.repositories;

import android.app.Application;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.ReceiptDao;
import com.example.androidapp.data.entities.Receipt;

import java.util.List;
import java.util.concurrent.Future;

public class ReceiptRepository {
    private ReceiptDao receiptDao;

    public ReceiptRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        receiptDao = database.receiptDao();
    }

    public Future<?> insert(Receipt receipt) {
        return AppDatabase.databaseWriteExecutor.submit(() -> receiptDao.insert(receipt));
    }

    public Future<?> update(Receipt receipt) {
        return AppDatabase.databaseWriteExecutor.submit(() -> receiptDao.update(receipt));
    }

    public Future<?> delete(Receipt receipt) {
        return AppDatabase.databaseWriteExecutor.submit(() -> receiptDao.delete(receipt));
    }

    public Future<Receipt> getReceiptById(String id, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> receiptDao.getReceiptById(id, companyId));
    }

    public Future<List<Receipt>> getAllReceipts(String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> receiptDao.getAllReceipts(companyId));
    }

    public Future<Integer> countReceiptByReferenceNumber(String referenceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> receiptDao.countReceiptByReferenceNumber(referenceNumber, companyId));
    }
}
