package com.example.androidapp.data.entities;
import androidx.room.Entity;

@Entity(tableName = "users")
public class User {
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

    public User(String id, String email, String password, String name, String phone, String phoneHash, int points, String createdAt, String updatedAt, String personalCompanyId) {
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
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoneHash() {
        return phoneHash;
    }

    public int getPoints() {
        return points;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getPersonalCompanyId() {
        return personalCompanyId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPhoneHash(String phoneHash) {
        this.phoneHash = phoneHash;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setPersonalCompanyId(String personalCompanyId) {
        this.personalCompanyId = personalCompanyId;
    }
}

