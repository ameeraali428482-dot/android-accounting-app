package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;



@Entity(tableName = "payroll_items",
        foreignKeys = {
                @ForeignKey(entity = Payroll.class,
                           parentColumns = "id",
                           childColumns = "payrollId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Employee.class,
                           parentColumns = "id",
                           childColumns = "employeeId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "payrollId"), @Index(value = "employeeId"), @Index(value = "companyId")})
public class PayrollItem {
    @PrimaryKey
    private @NonNull String id;
    private String payrollId;
    private String employeeId;
    private String companyId;
    private float baseSalary;
    private float netSalary;

    public PayrollItem(String id, String payrollId, String employeeId, String companyId, float baseSalary, float netSalary) {
        this.id = id;
        this.payrollId = payrollId;
        this.employeeId = employeeId;
        this.companyId = companyId;
        this.baseSalary = baseSalary;
        this.netSalary = netSalary;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getPayrollId() {
        return payrollId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public float getBaseSalary() {
        return baseSalary;
    }

    public float getNetSalary() {
        return netSalary;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setPayrollId(String payrollId) {
        this.payrollId = payrollId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setBaseSalary(float baseSalary) {
        this.baseSalary = baseSalary;
    }

    public void setNetSalary(float netSalary) {
        this.netSalary = netSalary;
    }
}

