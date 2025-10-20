package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "users",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id", 
                                  childColumns = "company_id",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index("company_id")})
public class User {
    @PrimaryKey
    @NonNull
    public String id;
    
    public String name;
    public String email;
    
    @ColumnInfo(name = "phone")
    public String phone;
    
    public String role;
    
    @ColumnInfo(name = "company_id")
    public String companyId;
    
    @ColumnInfo(name = "created_at")
    public long createdAt;
    
    @ColumnInfo(name = "last_login")
    public long lastLogin;
    
    @ColumnInfo(name = "is_active")
    public boolean isActive;

    // Constructor الرئيسي مع جميع الحقول المطلوبة
    public User(@NonNull String id, String name, String email, String phone, 
                String role, String companyId, long createdAt, long lastLogin, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.companyId = companyId;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
    }

    // Constructor مبسط مع قيم افتراضية
    @Ignore
    public User(@NonNull String id, String name, String email, String companyId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.companyId = companyId;
        this.createdAt = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
        this.isActive = true;
    }

    // Constructor فارغ للـ Room
    @Ignore
    public User() {
        this.createdAt = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
        this.isActive = true;
    }

    // Getters & Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getLastLogin() { return lastLogin; }
    public void setLastLogin(long lastLogin) { this.lastLogin = lastLogin; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
