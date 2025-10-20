package com.example.androidapp.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "phone")
    public String phone;
    
    // Additional field for compatibility
    @ColumnInfo(name = "phone_number")
    public String phoneNumber;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    // Default constructor for Room
    public User() {}

    // Primary constructor for Room
    @Ignore
    public User(String name, String email, String phone, long createdAt, long updatedAt) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.phoneNumber = phone; // Sync both fields
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Ignore
    public User(String name, String email, long createdAt, long updatedAt) {
        this(name, email, null, createdAt, updatedAt);
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone != null ? phone : phoneNumber; }
    public String getPhoneNumber() { return phoneNumber != null ? phoneNumber : phone; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { 
        this.phone = phone;
        this.phoneNumber = phone; // Keep in sync
    }
    public void setPhoneNumber(String phoneNumber) { 
        this.phoneNumber = phoneNumber;
        this.phone = phoneNumber; // Keep in sync
    }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
