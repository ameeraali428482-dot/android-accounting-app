package com.example.androidapp.models;

import java.util.Date;

public class Order {
    public String id;
    public String customerId;
    public String customerName;
    public double totalAmount;
    public String status;
    public Date orderDate;
    public Date deliveryDate;
    
    public Order() {}
    
    public Order(String customerId, double totalAmount) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = "PENDING";
        this.orderDate = new Date();
    }
}
