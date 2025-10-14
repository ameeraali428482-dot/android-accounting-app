package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.ItemDao;
import com.example.androidapp.data.entities.Item;
import java.util.List;
import java.util.concurrent.Future;

public class ItemRepository {
    private ItemDao itemDao;

    public ItemRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        itemDao = db.itemDao();
    }

    public Future<Void> insert(Item item) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            itemDao.insert(item);
            return null;
        });
    }

    public Future<Void> update(Item item) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            itemDao.update(item);
            return null;
        });
    }

    public Future<Void> delete(Item item) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            itemDao.delete(item);
            return null;
        });
    }

    public LiveData<List<Item>> getAllItems(String companyId) {
        return itemDao.getAllItems(companyId);
    }

    public Future<Item> getItemById(String itemId, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> itemDao.getItemById(itemId, companyId));
    }
}
