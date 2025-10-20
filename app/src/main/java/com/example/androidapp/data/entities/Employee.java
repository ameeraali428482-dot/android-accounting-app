package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "employees")
public class Employee {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String employeeId;
    public String name;
    public String email;
    public String phone;
    public String department;
    public String position;
    public double salary;
    public long hireDate;
    public boolean isActive;
    public long createdAt;

    public Employee() {
        this.createdAt = System.currentTimeMillis();
        this.hireDate = System.currentTimeMillis();
        this.isActive = true;
        this.salary = 0.0;
    }

    public Employee(String employeeId, String name, String department, String position) {
        this();
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.position = position;
    }

    // Getters
    public int getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
    public double getSalary() { return salary; }
    public long getHireDate() { return hireDate; }
    public boolean isActive() { return isActive; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setDepartment(String department) { this.department = department; }
    public void setPosition(String position) { this.position = position; }
    public void setSalary(double salary) { this.salary = salary; }
    public void setHireDate(long hireDate) { this.hireDate = hireDate; }
    public void setActive(boolean active) { isActive = active; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
