package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Calendar;
import java.util.UUID;

@Entity(tableName = "payrolls",
        foreignKeys = {
                @ForeignKey(entity = Company.class, parentColumns = "id", childColumns = "companyId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = JournalEntry.class, parentColumns = "id", childColumns = "journalEntryId", onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "journalEntryId")})
public class Payroll {
    @PrimaryKey
    @NonNull
    private String id;
    private String companyId;
    private int year;
    private int month;
    private String status;
    private float totalSalary;
    private float totalDeductions;
    private float totalBonuses;
    private float netPayable;
    private String journalEntryId;
    private String employeeId;
    private String date;
    private float amount;
    private String notes;

    public Payroll(@NonNull String id, String companyId, int year, int month, String status, float totalSalary, float totalDeductions, float totalBonuses, float netPayable, String journalEntryId) {
        this.id = id;
        this.companyId = companyId;
        this.year = year;
        this.month = month;
        this.status = status;
        this.totalSalary = totalSalary;
        this.totalDeductions = totalDeductions;
        this.totalBonuses = totalBonuses;
        this.netPayable = netPayable;
        this.journalEntryId = journalEntryId;
    }

    @Ignore
    public Payroll(@NonNull String id, String companyId, String employeeId, String date, double amount, String notes) {
        this.id = id;
        this.companyId = companyId;
        this.employeeId = employeeId;
        this.date = date;
        this.amount = (float) amount;
        this.notes = notes;
        Calendar cal = Calendar.getInstance();
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH) + 1;
        this.status = "PROCESSED";
        this.totalSalary = (float) amount;
        this.netPayable = (float) amount;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getCompanyId() { return companyId; }
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public String getStatus() { return status; }
    public float getTotalSalary() { return totalSalary; }
    public float getTotalDeductions() { return totalDeductions; }
    public float getTotalBonuses() { return totalBonuses; }
    public float getNetPayable() { return netPayable; }
    public String getJournalEntryId() { return journalEntryId; }
    public String getEmployeeId() { return employeeId; }
    public String getDate() { return date; }
    public float getAmount() { return amount; }
    public String getNotes() { return notes; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public void setYear(int year) { this.year = year; }
    public void setMonth(int month) { this.month = month; }
    public void setStatus(String status) { this.status = status; }
    public void setTotalSalary(float totalSalary) { this.totalSalary = totalSalary; }
    public void setTotalDeductions(float totalDeductions) { this.totalDeductions = totalDeductions; }
    public void setTotalBonuses(float totalBonuses) { this.totalBonuses = totalBonuses; }
    public void setNetPayable(float netPayable) { this.netPayable = netPayable; }
    public void setJournalEntryId(String journalEntryId) { this.journalEntryId = journalEntryId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setDate(String date) { this.date = date; }
    public void setAmount(float amount) { this.amount = amount; }
    public void setNotes(String notes) { this.notes = notes; }
}
