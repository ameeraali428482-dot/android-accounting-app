package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.Employee;

@Dao
public interface EmployeeDao {
    @Insert
    void insert(Employee employee);

    @Update
    void update(Employee employee);

    @Delete
    void delete(Employee employee);

    @Query("SELECT * FROM employees")
    List<Employee> getAllEmployees();

    @Query("SELECT * FROM employees WHERE id = :id LIMIT 1")
    Employee getById(String id);

    @Query("SELECT * FROM employees WHERE companyId = :companyId")
    List<Employee> getEmployeesByCompanyId(String companyId);
}
