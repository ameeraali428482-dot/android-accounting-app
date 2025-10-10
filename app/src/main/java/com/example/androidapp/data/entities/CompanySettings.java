package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "company_settings",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class CompanySettings {
    @PrimaryKey
    private @NonNull String id;
    private @NonNull String companyId;
    private String companyName;
    private String companyAddress;
    private String companyPhone;
    private String companyEmail;

    public CompanySettings(@NonNull String id, @NonNull String companyId, String companyName, String companyAddress, String companyPhone, String companyEmail) {
        this.id = id;
        this.companyId = companyId;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
    }

    // Getters
    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    // Setters
    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setCompanyId(@NonNull String companyId) {
        this.companyId = companyId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }
}
