package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "connections")
public class Connection {
    @PrimaryKey
    @NonNull
    private String id;
    private String companyId;
    private String name;
    private String type;
    private String status;
    private String connectionData;
    private String createdBy;
    private Date createdAt;
    private Date updatedAt;

    public Connection(@NonNull String id, String companyId, String name, String type, String status) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.type = type;
        this.status = status;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getConnectionData() { return connectionData; }
    public void setConnectionData(String connectionData) { this.connectionData = connectionData; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
