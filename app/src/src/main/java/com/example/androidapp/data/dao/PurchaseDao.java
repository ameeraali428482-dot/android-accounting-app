package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.Purchase;

@Dao
public interface PurchaseDao {
    @Insert
    void insert(Purchase purchase);

    @Update
    void update(Purchase purchase);

    @Delete
    void delete(Purchase purchase);

    @Query("SELECT * FROM purchases WHERE companyId = :companyId")
    List<Purchase> getAllPurchases(String companyId);

    @Query("SELECT * FROM purchases WHERE id = :id AND companyId = :companyId LIMIT 1")
    Purchase getPurchaseById(String id, String companyId);

    @Query("SELECT SUM(totalAmount) FROM purchases WHERE companyId = :companyId AND purchaseDate BETWEEN :startDate AND :endDate")
    float getTotalPurchasesByDateRange(String companyId, String startDate, String endDate);
}

