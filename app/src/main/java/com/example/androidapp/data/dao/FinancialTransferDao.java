package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.FinancialTransfer;

@Dao
public interface FinancialTransferDao {
    @Insert
    void insert(FinancialTransfer financialtransfer);

    @Update
    void update(FinancialTransfer financialtransfer);

    @Delete
    void delete(FinancialTransfer financialtransfer);

    @Query("SELECT * FROM financial_transfers")
    List<FinancialTransfer> getAllFinancialTransfers();

    @Query("SELECT * FROM financial_transfers WHERE id = :id LIMIT 1")
    FinancialTransfer getFinancialTransferById(String id);
}
