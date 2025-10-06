package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.androidapp.data.entities.Inventory;

import java.util.List;

@Dao
public interface InventoryDao {
    @Insert
    void insert(Inventory inventory);

    @Update
    void update(Inventory inventory);

    @Delete
    void delete(Inventory inventory);

    @Query("SELECT * FROM inventory WHERE companyId = :companyId")
    List<Inventory> getAllInventory(String companyId);

    @Query("SELECT * FROM inventory WHERE itemId = :itemId AND warehouseId = :warehouseId AND companyId = :companyId LIMIT 1")
    Inventory getInventoryByItemAndWarehouse(String itemId, String warehouseId, String companyId);

    @Query("SELECT SUM(quantity) FROM inventory WHERE itemId = :itemId AND companyId = :companyId")
    float getTotalQuantityByItem(String itemId, String companyId);

    @Query("SELECT COUNT(*) FROM inventory WHERE itemId = :itemId AND warehouseId = :warehouseId AND companyId = :companyId")
    int countInventoryByItemAndWarehouse(String itemId, String warehouseId, String companyId);

    @Query("SELECT * FROM inventory WHERE itemId = :itemId AND companyId = :companyId")
    List<Inventory> getInventoryForItem(String itemId, String companyId);
}

