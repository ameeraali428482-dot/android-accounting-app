package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PurchaseDao;
import com.example.androidapp.data.entities.Purchase;
import java.util.List;
import java.util.concurrent.Future;

public class PurchaseRepository {
    private PurchaseDao purchaseDao;

    public PurchaseRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        purchaseDao = db.purchaseDao();
    }

    public Future<Void> insert(Purchase purchase) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            purchaseDao.insert(purchase);
            return null;
        });
    }

    public Future<Void> update(Purchase purchase) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            purchaseDao.update(purchase);
            return null;
        });
    }

    public Future<Void> delete(Purchase purchase) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            purchaseDao.delete(purchase);
            return null;
        });
    }

    public LiveData<List<Purchase>> getAllPurchases(String companyId) {
        return purchaseDao.getAllPurchases(companyId);
    }

    public Future<Float> getTotalPurchasesByDateRange(String companyId, String startDate, String endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> purchaseDao.getTotalPurchasesByDateRange(companyId, startDate, endDate));
    }

    public Future<Integer> countPurchaseByReferenceNumber(String referenceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> purchaseDao.countPurchaseByReferenceNumber(referenceNumber, companyId));
    }
}
