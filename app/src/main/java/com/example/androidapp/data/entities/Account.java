package com.example.androidapp.data.entities;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class Account {
    @PrimaryKey
    private String id;
    private String name;
    private String code;
    private String type;
    private boolean isDebit;
    private String parentCode;
    private String description;
    private String companyId;
    private String createdAt;
    private String updatedAt;

    public Account(String id, String name, String code, String type, boolean isDebit, String parentCode, String description, String companyId, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.type = type;
        this.isDebit = isDebit;
        this.parentCode = parentCode;
        this.description = description;
        this.companyId = companyId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public boolean isDebit() {
        return isDebit;
    }

    public String getParentCode() {
        return parentCode;
    }

    public String getDescription() {
        return description;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDebit(boolean debit) {
        isDebit = debit;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

