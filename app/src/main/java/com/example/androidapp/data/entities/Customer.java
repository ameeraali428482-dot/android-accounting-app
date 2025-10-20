package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "customers")
public class Customer {
    @PrimaryKey(autoGenerate = true)
    public int customerId;
    public String customerName;
    public String email;
    public String phone;
    public String address;
    public String company;
    public boolean isActive;
    public double creditLimit;
    public double currentBalance;
    public long createdAt;
    public long updatedAt;

    public Customer() {}

    @Ignore
    public Customer(String customerName, String email, String phone, String address) {
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.isActive = true;
        this.creditLimit = 0.0;
        this.currentBalance = 0.0;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    @Ignore
    public Customer(int customerId, String customerName, String email, String phone, String address, String company, boolean isActive, double creditLimit, double currentBalance, long createdAt, long updatedAt) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.company = company;
        this.isActive = isActive;
        this.creditLimit = creditLimit;
        this.currentBalance = currentBalance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
