package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;



@Entity(tableName = "companies",
        foreignKeys = {
                @ForeignKey(entity = Account.class,
                           parentColumns = "id",
                           childColumns = "defaultCashAccountId",
                           onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Account.class,
                           parentColumns = "id",
                           childColumns = "defaultExchangeDiffAccountId",
                           onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Account.class,
                           parentColumns = "id",
                           childColumns = "defaultPayrollExpenseAccountId",
                           onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Account.class,
                           parentColumns = "id",
                           childColumns = "defaultSalariesPayableAccountId",
                           onDelete = ForeignKey.SET_NULL)
        },
        indices = {
                @Index(value = "defaultCashAccountId"),
                @Index(value = "defaultExchangeDiffAccountId"),
                @Index(value = "defaultPayrollExpenseAccountId"),
                @Index(value = "defaultSalariesPayableAccountId")
        })
public class Company {
    @PrimaryKey
    private @NonNull String id;
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

