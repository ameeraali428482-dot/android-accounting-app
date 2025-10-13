package com.example.androidapp.data.entities;

import androidx.room.*;

@Entity(tableName = "roles")
public class Role {
    @PrimaryKey
    private String id;
    private String companyId;
    private String name;
    private String description;
    private String permissions;
    private String createdDate;
    private String updatedDate;

    public Role() {}

    public Role(String id, String companyId, String name, String description, 
                String permissions, String createdDate, String updatedDate) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }
}
