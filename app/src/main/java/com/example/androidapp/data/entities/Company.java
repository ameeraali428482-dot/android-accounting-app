package com.example.androidapp.models;

@Entity(tableName = "companies")
public class Company {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String createdAt;
    private String updatedAt;
    private String defaultCashAccountId;
    private String defaultExchangeDiffAccountId;
    private String defaultPayrollExpenseAccountId;
    private String defaultSalariesPayableAccountId;

    public Company(String id, String name, String address, String phone, String createdAt, String updatedAt, String defaultCashAccountId, String defaultExchangeDiffAccountId, String defaultPayrollExpenseAccountId, String defaultSalariesPayableAccountId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.defaultCashAccountId = defaultCashAccountId;
        this.defaultExchangeDiffAccountId = defaultExchangeDiffAccountId;
        this.defaultPayrollExpenseAccountId = defaultPayrollExpenseAccountId;
        this.defaultSalariesPayableAccountId = defaultSalariesPayableAccountId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getDefaultCashAccountId() {
        return defaultCashAccountId;
    }

    public String getDefaultExchangeDiffAccountId() {
        return defaultExchangeDiffAccountId;
    }

    public String getDefaultPayrollExpenseAccountId() {
        return defaultPayrollExpenseAccountId;
    }

    public String getDefaultSalariesPayableAccountId() {
        return defaultSalariesPayableAccountId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDefaultCashAccountId(String defaultCashAccountId) {
        this.defaultCashAccountId = defaultCashAccountId;
    }

    public void setDefaultExchangeDiffAccountId(String defaultExchangeDiffAccountId) {
        this.defaultExchangeDiffAccountId = defaultExchangeDiffAccountId;
    }

    public void setDefaultPayrollExpenseAccountId(String defaultPayrollExpenseAccountId) {
        this.defaultPayrollExpenseAccountId = defaultPayrollExpenseAccountId;
    }

    public void setDefaultSalariesPayableAccountId(String defaultSalariesPayableAccountId) {
        this.defaultSalariesPayableAccountId = defaultSalariesPayableAccountId;
    }
}

