package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Employee;
import java.util.List;

@Dao
public interface EmployeeDao extends BaseDao<Employee> {
    @Query("SELECT * FROM employees ORDER BY employeeName")
    List<Employee> getAll();

    @Query("SELECT * FROM employees WHERE companyId = :companyId ORDER BY employeeName")
    LiveData<List<Employee>> getAllEmployees(String companyId);

    @Query("SELECT * FROM employees WHERE employeeId = :id")
    Employee getById(int id);

    @Query("SELECT * FROM employees WHERE position = :position ORDER BY employeeName")
    List<Employee> getByDepartment(String position);

    @Query("SELECT * FROM employees WHERE isActive = 1 ORDER BY employeeName")
    List<Employee> getActiveEmployees();

    @Query("SELECT * FROM employees WHERE employeeName LIKE '%' || :searchTerm || '%' OR email LIKE '%' || :searchTerm || '%'")
    List<Employee> searchEmployees(String searchTerm);
}
