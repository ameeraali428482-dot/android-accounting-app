package com.example.androidapp.data.entities;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

public class PayrollItem {
    private String id;
    private String payrollId;
    private String employeeId;
    private float baseSalary;
    private float netSalary;

    public PayrollItem(String id, String payrollId, String employeeId, float baseSalary, float netSalary) {
        this.id = id;
        this.payrollId = payrollId;
        this.employeeId = employeeId;
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

    public void setBaseSalary(float baseSalary) {
        this.baseSalary = baseSalary;
    }

    public void setNetSalary(float netSalary) {
        this.netSalary = netSalary;
    }
}

