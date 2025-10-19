package com.example.androidapp.models;

public class Customer {
    public String id;
    public String name;
    public String email;
    public String phone;
    public String address;
    public double balance;
    public boolean isActive;
    
    public Customer() {}
    
    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
        this.isActive = true;
    }
}
