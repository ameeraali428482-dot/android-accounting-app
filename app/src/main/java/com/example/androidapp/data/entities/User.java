import androidx.annotation.NonNull;
package com.example.androidapp.data.entities;

import androidx.room.*;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String id;
    
    public String name;
    public String email;
    public String phoneNumber;
    public String role;
    
    @ColumnInfo(name = "created_at")
    public long createdAt;
    
    @ColumnInfo(name = "last_login")
    public long lastLogin;
    
    @ColumnInfo(name = "is_active")
    public boolean isActive;
    
    public User() {
        this.createdAt = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
        this.isActive = true;
    }
}
