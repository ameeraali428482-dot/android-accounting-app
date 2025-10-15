package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Employee;
import java.util.List;

@Dao
public interface EmployeeDao {
    @Query("SELECT * FROM employees WHERE companyId = :companyId")
    LiveData<List<Employee>> getAllEmployees(String companyId);

    @Query("SELECT * FROM employees WHERE id = :employeeId AND companyId = :companyId")
    Employee getEmployeeById(String employeeId, String companyId);

    @Insert
    void insert(Employee employee);

    @Update
    void update(Employee employee);

    @Delete
    void delete(Employee employee);
    
    // Add delete by ID method to fix the error
    @Query("DELETE FROM employees WHERE id = :employeeId")
    void deleteById(String employeeId);
}
