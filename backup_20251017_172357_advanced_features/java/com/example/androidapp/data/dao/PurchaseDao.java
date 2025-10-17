package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Purchase;
import java.util.List;

@Dao
public interface PurchaseDao {
    @Insert
    void insert(Purchase purchase);

    @Update
    void update(Purchase purchase);

    @Delete
    void delete(Purchase purchase);

    @Query("SELECT * FROM purchases WHERE companyId = :companyId ORDER BY purchaseDate DESC")
    LiveData<List<Purchase>> getAllPurchases(String companyId);

    @Query("SELECT SUM(totalAmount) FROM purchases WHERE companyId = :companyId AND purchaseDate BETWEEN :startDate AND :endDate")
    float getTotalPurchasesByDateRange(String companyId, String startDate, String endDate);

    @Query("SELECT COUNT(*) FROM purchases WHERE referenceNumber = :referenceNumber AND companyId = :companyId")
    int countPurchaseByReferenceNumber(String referenceNumber, String companyId);
}
