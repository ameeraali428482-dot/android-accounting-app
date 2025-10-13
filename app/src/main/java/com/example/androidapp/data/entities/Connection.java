package com.example.androidapp.data.entities;

import androidx.room.*;
import androidx.annotation.NonNull;

@Entity(tableName = "connections")
public class Connection {
    @PrimaryKey
    @NonNull
    private String id;
    private String companyId;
    private String connectionName;
    private String connectionType;
    private String status;
    private String description;
    private String createdDate;
    private String updatedDate;

    public Connection() {}

    @Ignore
    public Connection(@NonNull String id, String companyId, String connectionName, String connectionType, 
                     String status, String description, String createdDate, String updatedDate) {
        this.id = id;
        this.companyId = companyId;
        this.connectionName = connectionName;
        this.connectionType = connectionType;
        this.status = status;
        this.description = description;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getConnectionName() { return connectionName; }
    public void setConnectionName(String connectionName) { this.connectionName = connectionName; }

    public String getConnectionType() { return connectionType; }
    public void setConnectionType(String connectionType) { this.connectionType = connectionType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }
}
