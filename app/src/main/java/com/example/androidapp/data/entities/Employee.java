package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "employees")
public class Employee {
    @PrimaryKey(autoGenerate = true)
    public int employeeId;
    public String employeeName;
    public String email;
    public String phone;
    public String position;
    public String department;
    public double salary;
    public boolean isActive;
    public long hireDate;
    public long createdAt;
    public long updatedAt;

    // Default constructor for Room
    public Employee() {}

    // Constructor for creating new employees
    @Ignore
    public Employee(String employeeName, String email, String phone, String position, String department, double salary) {
        this.employeeName = employeeName;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.department = department;
        this.salary = salary;
        this.isActive = true;
        this.hireDate = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Full constructor
    @Ignore
    public Employee(int employeeId, String employeeName, String email, String phone, String position, String department,
                   double salary, boolean isActive, long hireDate, long createdAt, long updatedAt) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.department = department;
        this.salary = salary;
        this.isActive = isActive;
        this.hireDate = hireDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
