package com.example.androidapp.data.entities;

public class JoinRequest {
    private String id;
    private String userId;
    private String companyId;
    private String status;
    private String createdAt;

    public JoinRequest(String id, String userId, String companyId, String status, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

