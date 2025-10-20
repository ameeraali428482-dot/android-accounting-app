package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Employee;
import java.util.List;

@Dao
public interface EmployeeDao extends BaseDao<Employee> {
    
    @Query("SELECT * FROM employees WHERE id = :id")
    Employee getById(int id);

    @Query("SELECT * FROM employees ORDER BY name")
    List<Employee> getAll();
    
    @Query("SELECT * FROM employees ORDER BY name")
    List<Employee> getAllEmployees();

    @Query("SELECT * FROM employees WHERE employeeId = :employeeId")
    Employee getByEmployeeId(String employeeId);

    @Query("SELECT * FROM employees WHERE department = :department ORDER BY name")
    List<Employee> getByDepartment(String department);

    @Query("SELECT * FROM employees WHERE isActive = 1 ORDER BY name")
    List<Employee> getActiveEmployees();

    @Query("SELECT * FROM employees WHERE name LIKE '%' || :searchTerm || '%' OR employeeId LIKE '%' || :searchTerm || '%' ORDER BY name")
    List<Employee> searchEmployees(String searchTerm);

    @Query("SELECT COUNT(*) FROM employees WHERE isActive = 1")
    int getActiveCount();

    @Query("DELETE FROM employees WHERE id = :id")
    void deleteById(int id);
}
