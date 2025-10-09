package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.InventoryDao;
import com.example.androidapp.data.entities.Inventory;
import com.example.androidapp.data.entities.InvoiceItem;

import java.util.List;
import java.util.concurrent.Future;

public class InventoryRepository {
    private InventoryDao inventoryDao;

    public InventoryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        inventoryDao = db.inventoryDao();
    }

    public Future<Void> insert(Inventory inventory) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            inventoryDao.insert(inventory);
            return null;
        });
    }

    public Future<Void> update(Inventory inventory) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            inventoryDao.update(inventory);
            return null;
        });
    }

    public Future<Void> delete(Inventory inventory) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            inventoryDao.delete(inventory);
            return null;
        });
    }

    public LiveData<List<Inventory>> getAllInventory(String companyId) {
        return inventoryDao.getAllInventory(companyId);
    }

    public Future<List<Inventory>> getInventoryForItem(String itemId, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> inventoryDao.getInventoryForItem(itemId, companyId));
    }
}
