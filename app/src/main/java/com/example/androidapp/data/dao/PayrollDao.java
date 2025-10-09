package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.Payroll;

@Dao
public interface PayrollDao {
    @Insert
    void insert(Payroll payroll);

    @Update
    void update(Payroll payroll);

    @Delete
    void delete(Payroll payroll);

    @Query("SELECT * FROM payrolls")
    List<Payroll> getAllPayrolls();

    @Query("SELECT * FROM payrolls WHERE id = :id LIMIT 1")
    Payroll getPayrollById(String id);
}
