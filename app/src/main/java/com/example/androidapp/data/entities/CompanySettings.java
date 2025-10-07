package com.example.androidapp.data.entities;
import androidx.room.Entity;

public class CompanySettings {
    private String id;
    private String companyId;

    public CompanySettings(String id, String companyId) {
        this.id = id;
        this.companyId = companyId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}

