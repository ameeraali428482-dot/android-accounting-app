package com.example.androidapp.data.repositories;

import android.app.Application;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.ItemDao;
import com.example.androidapp.data.entities.Item;

import java.util.List;
import java.util.concurrent.Future;

public class ItemRepository {
    private ItemDao itemDao;

    public ItemRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        itemDao = database.itemDao();
    }

    public Future<?> insert(Item item) {
        return AppDatabase.databaseWriteExecutor.submit(() -> itemDao.insert(item));
    }

    public Future<?> update(Item item) {
        return AppDatabase.databaseWriteExecutor.submit(() -> itemDao.update(item));
    }

    public Future<?> delete(Item item) {
        return AppDatabase.databaseWriteExecutor.submit(() -> itemDao.delete(item));
    }

    public Future<Item> getItemById(String id, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> itemDao.getItemById(id, companyId));
    }

    public Future<List<Item>> getAllItems(String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> itemDao.getAllItems(companyId));
    }
}
