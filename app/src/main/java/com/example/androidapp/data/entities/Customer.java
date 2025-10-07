package com.example.androidapp.data.entities;
import androidx.room.Entity;

@Entity(tableName = "customers")
public class Customer {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String region;
    private String category;
    private Float creditLimit;
    private Float openingBalance;
    private String companyId;

    public Customer(String id, String name, String email, String phone, String address, String region, String category, Float creditLimit, Float openingBalance, String companyId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.region = region;
        this.category = category;
        this.creditLimit = creditLimit;
        this.openingBalance = openingBalance;
        this.companyId = companyId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getRegion() {
        return region;
    }

    public String getCategory() {
        return category;
    }

    public Float getCreditLimit() {
        return creditLimit;
    }

    public Float getOpeningBalance() {
        return openingBalance;
    }

    public String getCompanyId() {
        return companyId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCreditLimit(Float creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setOpeningBalance(Float openingBalance) {
        this.openingBalance = openingBalance;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}

