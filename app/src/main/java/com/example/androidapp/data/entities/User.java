package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int userId;
    @NonNull
    public String username;
    @NonNull
    public String email;
    @NonNull
    public String password;
    public String firstName;
    public String lastName;
    public String phone;
    public boolean isActive;
    public String companyId;
    public long lastLogin;
    public long createdAt;
    public long updatedAt;

    // Default constructor for Room
    public User() {}

    // Constructor for creating new users
    @Ignore
    public User(@NonNull String username, @NonNull String email, @NonNull String password, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.lastLogin = 0;
    }

    // Full constructor
    @Ignore
    public User(int userId, @NonNull String username, @NonNull String email, @NonNull String password, 
                String firstName, String lastName, String phone, boolean isActive, String companyId, 
                long lastLogin, long createdAt, long updatedAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.isActive = isActive;
        this.companyId = companyId;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
