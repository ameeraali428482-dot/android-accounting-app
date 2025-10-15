package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "personalCompanyId",
                                  onDelete = ForeignKey.SET_NULL),
        indices = {@Index(value = "personalCompanyId")})
public class User {
    @PrimaryKey
    @NonNull
    private String id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String phoneHash;
    private int points;
    private String createdAt;
    private String updatedAt;
    private String personalCompanyId;
    private boolean isOnline;
    private boolean isActive; // Added field

    public User(@NonNull String id, String email, String password, String name, String phone, String phoneHash, int points, String createdAt, String updatedAt, String personalCompanyId, boolean isOnline, boolean isActive) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.phoneHash = phoneHash;
        this.points = points;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.personalCompanyId = personalCompanyId;
        this.isOnline = isOnline;
        this.isActive = isActive;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getPhoneHash() { return phoneHash; }
    public int getPoints() { return points; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getPersonalCompanyId() { return personalCompanyId; }
    public boolean isOnline() { return isOnline; }
    public boolean isActive() { return isActive; }
    public String getUsername() { return name; } // Alias

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPhoneHash(String phoneHash) { this.phoneHash = phoneHash; }
    public void setPoints(int points) { this.points = points; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setPersonalCompanyId(String personalCompanyId) { this.personalCompanyId = personalCompanyId; }
    public void setOnline(boolean online) { isOnline = online; }
    public void setIsActive(boolean active) { isActive = active; }
}
