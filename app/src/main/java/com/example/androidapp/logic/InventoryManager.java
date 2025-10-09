package com.example.androidapp.logic;

import android.content.Context;
import android.util.Log;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.InventoryDao;
// ✅ تصحيح: إضافة الاستيراد المفقود الذي سبب الخطأ
import com.example.androidapp.data.dao.ItemDao; 
import com.example.androidapp.data.entities.Inventory;
import com.example.androidapp.data.entities.Item;

import java.util.List;

public class InventoryManager {
    private static final String TAG = "InventoryManager";
    private final InventoryDao inventoryDao;
    private final ItemDao itemDao;

    public InventoryManager(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.inventoryDao = db.inventoryDao();
        this.itemDao = db.itemDao();
    }

    public void addInventory(String itemId, String warehouseId, float quantity, String companyId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Inventory inventory = inventoryDao.getInventoryByItemAndWarehouse(itemId, warehouseId, companyId);
                if (inventory != null) {
                    inventory.setQuantity(inventory.getQuantity() + quantity);
                    inventoryDao.update(inventory);
                } else {
                    inventory = new Inventory(itemId, warehouseId, quantity, companyId);
                    inventoryDao.insert(inventory);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error adding inventory: " + e.getMessage());
            }
        });
    }

    public void removeInventory(String itemId, String warehouseId, float quantity, String companyId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Inventory inventory = inventoryDao.getInventoryByItemAndWarehouse(itemId, warehouseId, companyId);
                if (inventory != null) {
                    float newQuantity = inventory.getQuantity() - quantity;
                    inventory.setQuantity(newQuantity);
                    inventoryDao.update(inventory);
                } else {
                    Log.e(TAG, "No inventory found for item " + itemId + " in warehouse " + warehouseId);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error removing inventory: " + e.getMessage());
            }
        });
    }

    public void transferItem(String itemId, String fromWarehouseId, String toWarehouseId, float quantity, String companyId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            removeInventory(itemId, fromWarehouseId, quantity, companyId);
            addInventory(itemId, toWarehouseId, quantity, companyId);
        });
    }

    public void checkLowStockAlerts(String companyId, LowStockAlertCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<Item> allItems = itemDao.getAllItems(companyId);
                for (Item item : allItems) {
                    List<Inventory> inventories = inventoryDao.getInventoryForItem(item.getId(), companyId);
                    float totalStock = 0;
                    for(Inventory inventory : inventories) {
                        totalStock += inventory.getQuantity();
                    }

                    if (item.getMinStockLevel() > 0 && totalStock < item.getMinStockLevel()) {
                        if (callback != null) callback.onLowStock(item, totalStock);
                    }
                }
            } catch (Exception e) {
                if (callback != null) callback.onFailure(e.getMessage());
            }
        });
    }

    public interface LowStockAlertCallback {
        void onLowStock(Item item, float currentStock);
        void onFailure(String message);
    }
}
