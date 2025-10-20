package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "customers")
public class Customer {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public String email;
    public String phone;
    public String address;
    public String companyName;
    public String taxNumber;
    public double totalPurchases;
    public long createdAt;

    public Customer() {
        this.createdAt = System.currentTimeMillis();
        this.totalPurchases = 0.0;
    }

    public Customer(String name, String email, String phone) {
        this();
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getCompanyName() { return companyName; }
    public String getTaxNumber() { return taxNumber; }
    public double getTotalPurchases() { return totalPurchases; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setTaxNumber(String taxNumber) { this.taxNumber = taxNumber; }
    public void setTotalPurchases(double totalPurchases) { this.totalPurchases = totalPurchases; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
