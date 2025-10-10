package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Payroll;
import java.util.List;



@Dao
public interface PayrollDao {
    @Insert
    void insert(Payroll payroll);

    @Update
    void update(Payroll payroll);

    @Delete
    void delete(Payroll payroll);

    @Query("SELECT * FROM payrolls WHERE companyId = :companyId")
    LiveData<List<Payroll>> getPayrollsByCompanyId(String companyId);

    @Query("SELECT * FROM payrolls WHERE id = :id LIMIT 1")
    Payroll getById(String id);
}
