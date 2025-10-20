package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String email;
    public String phoneNumber;
    public long createdAt;
    public long updatedAt;

    // Default constructor for Room
    public User() {}

    // Primary constructor for Room
    public User(String name, String email, String phoneNumber, long createdAt, long updatedAt) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Ignore
    public User(String name, String email, String phone, long createdAt, long updatedAt) {
        this(name, email, phone, createdAt, updatedAt);
    }

    @Ignore
    public User(String name, String email, long createdAt, long updatedAt) {
        this(name, email, null, createdAt, updatedAt);
    }
}
