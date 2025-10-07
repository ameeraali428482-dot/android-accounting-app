package com.example.androidapp.data.entities;
import androidx.room.ForeignKey;import androidx.room.Index;import androidx.room.TypeConverters;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders")
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int customerId;
    public double totalAmount;
}
