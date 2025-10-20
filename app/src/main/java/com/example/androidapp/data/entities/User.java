package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String id;
    public String username;
    public String email;
    public String password;
    public String firstName;
    public String lastName;
    public String phone;
    public String name;
    public boolean is_active;
    public String company_id;
    public long last_login;
    public long createdAt;
    public long updatedAt;

    public User() {}

    @Ignore
    public User(@NonNull String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.is_active = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
