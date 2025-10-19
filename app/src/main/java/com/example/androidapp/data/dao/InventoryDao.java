package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Inventory;

import java.util.List;

@Dao
public interface InventoryDao extends BaseDao<Inventory> {
    
    @Query("SELECT * FROM inventory WHERE id = :id")
    Inventory getById(long id);

    @Query("SELECT * FROM inventory ORDER BY item_id ASC")
    List<Inventory> getAll();

    @Query("SELECT * FROM inventory WHERE item_id = :itemId AND warehouse_id = :warehouseId")
    Inventory getInventoryByItemAndWarehouse(long itemId, long warehouseId);

    @Query("SELECT SUM(quantity) FROM inventory WHERE item_id = :itemId")
    double getTotalQuantityByItem(long itemId);

    @Query("SELECT COUNT(*) FROM inventory WHERE item_id = :itemId AND warehouse_id = :warehouseId")
    int countInventoryByItemAndWarehouse(long itemId, long warehouseId);

    @Query("SELECT * FROM inventory WHERE item_id = :itemId")
    List<Inventory> getInventoryForItem(long itemId);

    @Query("SELECT * FROM inventory WHERE warehouse_id = :warehouseId")
    List<Inventory> getInventoryForWarehouse(long warehouseId);

    @Query("SELECT * FROM inventory WHERE quantity <= reorder_point")
    List<Inventory> getLowStockItems();

    @Query("UPDATE inventory SET quantity = quantity + :amount WHERE item_id = :itemId AND warehouse_id = :warehouseId")
    void updateStock(long itemId, long warehouseId, double amount);

    @Query("DELETE FROM inventory WHERE item_id = :itemId")
    void deleteByItemId(long itemId);

    @Query("DELETE FROM inventory WHERE warehouse_id = :warehouseId")
    void deleteByWarehouseId(long warehouseId);
}
