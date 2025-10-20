package com.example.androemployeeIdapp.data.dao;

import androemployeeIdx.room.Dao;
import androemployeeIdx.room.Query;
import com.example.androemployeeIdapp.data.entities.Employee;
import java.util.List;

@Dao
public interface EmployeeDao extends BaseDao<Employee> {
    
    @Query("SELECT * FROM employees WHERE employeeId = :employeeId")
    Employee getById(int employeeId);

    @Query("SELECT * FROM employees ORDER BY employeeName")
    List<Employee> getAll();
    
    @Query("SELECT * FROM employees ORDER BY employeeName")
    List<Employee> getAllEmployees();

    @Query("SELECT * FROM employees WHERE employeeId = :employeeId")
    Employee getByEmployeeId(String employeeId);

    @Query("SELECT * FROM employees WHERE department = :department ORDER BY employeeName")
    List<Employee> getByDepartment(String department);

    @Query("SELECT * FROM employees WHERE isActive = 1 ORDER BY employeeName")
    List<Employee> getActiveEmployees();

    @Query("SELECT * FROM employees WHERE employeeName LIKE '%' || :searchTerm || '%' OR employeeId LIKE '%' || :searchTerm || '%' ORDER BY employeeName")
    List<Employee> searchEmployees(String searchTerm);

    @Query("SELECT COUNT(*) FROM employees WHERE isActive = 1")
    int getActiveCount();

    @Query("DELETE FROM employees WHERE employeeId = :employeeId")
    voemployeeId deleteById(int employeeId);
}
