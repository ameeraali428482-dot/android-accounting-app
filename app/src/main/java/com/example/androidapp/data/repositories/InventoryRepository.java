package com.example.androidapp.data.repositories;

import android.app.Application;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.InventoryDao;
import com.example.androidapp.data.entities.Inventory;

import java.util.List;
import java.util.concurrent.Future;

public class InventoryRepository {
    private InventoryDao inventoryDao;

    public InventoryRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        inventoryDao = database.inventoryDao();
    }

    public Future<?> insert(Inventory inventory) {
        return AppDatabase.databaseWriteExecutor.submit(() -> inventoryDao.insert(inventory));
    }

    public Future<?> update(Inventory inventory) {
        return AppDatabase.databaseWriteExecutor.submit(() -> inventoryDao.update(inventory));
    }

    public Future<?> delete(Inventory inventory) {
        return AppDatabase.databaseWriteExecutor.submit(() -> inventoryDao.delete(inventory));
    }

    public Future<List<Inventory>> getInventoryForItem(String itemId, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> inventoryDao.getInventoryForItem(itemId, companyId));
    }

    public Future<Inventory> getInventoryById(String id, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> inventoryDao.getInventoryById(id, companyId));
    }
}
