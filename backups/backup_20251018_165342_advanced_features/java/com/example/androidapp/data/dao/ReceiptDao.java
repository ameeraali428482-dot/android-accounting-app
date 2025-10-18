package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.androidapp.data.entities.Receipt;
import java.util.List;

@Dao
public interface ReceiptDao {
    @Insert
    void insert(Receipt receipt);

    @Update
    void update(Receipt receipt);

    @Delete
    void delete(Receipt receipt);

    @Query("SELECT * FROM receipts WHERE companyId = :companyId")
    LiveData<List<Receipt>> getAllReceipts(String companyId);

    @Query("SELECT * FROM receipts WHERE id = :id AND companyId = :companyId LIMIT 1")
    Receipt getReceiptById(String id, String companyId);

    @Query("SELECT COUNT(*) FROM receipts WHERE referenceNumber = :referenceNumber AND companyId = :companyId")
    int countReceiptByReferenceNumber(String referenceNumber, String companyId);

    @Query("SELECT COUNT(*) FROM receipts WHERE id = :id AND companyId = :companyId")
    int countReceiptById(String id, String companyId);
}
