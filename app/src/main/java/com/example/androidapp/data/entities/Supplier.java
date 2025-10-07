package com.example.androidapp.models;

@Entity(tableName = "suppliers")
public class Supplier {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Float openingBalance;
    private String companyId;

    public Supplier(String id, String name, String email, String phone, String address, Float openingBalance, String companyId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
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

    public void setOpeningBalance(Float openingBalance) {
        this.openingBalance = openingBalance;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}

