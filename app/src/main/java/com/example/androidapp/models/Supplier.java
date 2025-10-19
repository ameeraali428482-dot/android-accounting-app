package com.example.androidapp.models;

public class Supplier {
    public String id;
    public String name;
    public String email;
    public String phone;
    public String address;
    public String contactPerson;
    public double balance;
    public boolean isActive;
    
    public Supplier() {}
    
    public Supplier(String name, String email) {
        this.name = name;
        this.email = email;
        this.isActive = true;
    }
}
