package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "employees",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class Employee {
    @PrimaryKey
    private String id;
    private String companyId;
    private String name;
    private String position;
    private String hireDate;
    private float salary;
    private String contractType;

    public Employee(String id, String companyId, String name, String position, String hireDate, float salary, String contractType) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.position = position;
        this.hireDate = hireDate;
        this.salary = salary;
        this.contractType = contractType;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getHireDate() {
        return hireDate;
    }

    public float getSalary() {
        return salary;
    }

    public String getContractType() {
        return contractType;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }
}

