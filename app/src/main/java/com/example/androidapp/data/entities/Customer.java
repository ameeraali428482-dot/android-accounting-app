package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "customers",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index("companyId")})
public class Customer {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String companyId;

    public Customer(@NonNull String id, String companyId, String name, String email, String phone, String address) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    @Ignore
    public Customer(String id, String name, String email, String companyId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.companyId = companyId;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getCompanyId() { return companyId; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
}
