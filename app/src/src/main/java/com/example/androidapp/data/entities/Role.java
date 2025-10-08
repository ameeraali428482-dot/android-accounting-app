package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "roles",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class Role {
    @PrimaryKey
    private String id;
    private String name;
    private String description;
    private String companyId;
    private boolean isDefault;

    public Role(String id, String name, String description, String companyId, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.companyId = companyId;
        this.isDefault = isDefault;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCompanyId() {
        return companyId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}

