package com.example.androidapp.models;

public class Doctor {
    private String id;
    private String companyId;
    private String name;
    private String specialty;

    public Doctor(String id, String companyId, String name, String specialty) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.specialty = specialty;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}

