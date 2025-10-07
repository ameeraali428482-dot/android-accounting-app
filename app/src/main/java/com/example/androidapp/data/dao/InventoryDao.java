package com.example.androidapp.data.dao;
import com.example.androidapp.data.entities.Product;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.data.entities.Voucher;
import com.example.androidapp.data.entities.Company;
import com.example.androidapp.data.entities.Doctor;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.Supplier;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.data.entities.Trophy;
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.data.entities.Repair;
import com.example.androidapp.data.entities.Chat;
import com.example.androidapp.data.entities.UserReward;
import com.example.androidapp.data.entities.Reward;
import com.example.androidapp.data.entities.PointTransaction;
import com.example.androidapp.data.entities.Campaign;

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

