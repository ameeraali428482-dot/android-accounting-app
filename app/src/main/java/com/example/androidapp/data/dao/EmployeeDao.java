package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Employee;
import java.util.List;

@Dao
public interface EmployeeDao extends BaseDao<Employee> {
    
    @Query("SELECT * FROM employees WHERE id = :employeeId")
    Employee getById(String employeeId);

    @Query("SELECT * FROM employees ORDER BY name")
    List<Employee> getAll();
    
    @Query("SELECT * FROM employees WHERE companyId = :companyId ORDER BY name")
    LiveData<List<Employee>> getAllEmployees(String companyId);

    @Query("SELECT * FROM employees WHERE id = :employeeId")
    Employee getByEmployeeId(String employeeId);

    @Query("SELECT * FROM employees WHERE position = :position ORDER BY name")
    List<Employee> getByDepartment(String position);

    @Query("SELECT * FROM employees WHERE isActive = 1 ORDER BY name")
    List<Employee> getActiveEmployees();

    @Query("SELECT * FROM employees WHERE name LIKE '%' || :searchTerm || '%' OR id LIKE '%' || :searchTerm || '%' ORDER BY name")
    List<Employee> searchEmployees(String searchTerm);

    @Query("SELECT COUNT(*) FROM employees WHERE isActive = 1")
    int getActiveCount();

    @Query("DELETE FROM employees WHERE id = :employeeId")
    void deleteById(String employeeId);
}
