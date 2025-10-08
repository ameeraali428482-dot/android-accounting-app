package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "connections",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "fromId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "toId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "fromId"), @Index(value = "toId"), @Index(value = "companyId")})
public class Connection {
    @PrimaryKey
    private String id;
    private String fromId;
    private String toId;
    private String status;
    private String createdAt;
    private String companyId;

    public Connection(String id, String fromId, String toId, String status, String createdAt, String companyId) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.status = status;
        this.createdAt = createdAt;
        this.companyId = companyId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getFromId() {
        return fromId;
    }

    public String getToId() {
        return toId;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getCompanyId() {
        return companyId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}

