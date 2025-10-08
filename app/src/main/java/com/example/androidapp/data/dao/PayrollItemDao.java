package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.PayrollItem;

@Dao
public interface PayrollItemDao {
    @Insert
    void insert(PayrollItem payrollitem);

    @Update
    void update(PayrollItem payrollitem);

    @Delete
    void delete(PayrollItem payrollitem);

    @Query("SELECT * FROM payrollitems")
    List<PayrollItem> getAllPayrollItems();

    @Query("SELECT * FROM payrollitems WHERE id = :id LIMIT 1")
    PayrollItem getPayrollItemById(String id);
}
