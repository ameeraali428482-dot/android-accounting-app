package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "employees",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index("companyId")})
public class Employee {
    @PrimaryKey
    @NonNull
    private String id;
    private String companyId;
    private String name;
    private String email;
    private String phone;
    private String position;
    private double salary;
    private String hireDate;

    public Employee(@NonNull String id, String companyId, String name, String email, String phone, String position, double salary, String hireDate) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getCompanyId() { return companyId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPosition() { return position; }
    public double getSalary() { return salary; }
    public String getHireDate() { return hireDate; }

    // Setters - مطلوبة لـ Room Database
    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPosition(String position) { this.position = position; }
    public void setSalary(double salary) { this.salary = salary; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }
}
