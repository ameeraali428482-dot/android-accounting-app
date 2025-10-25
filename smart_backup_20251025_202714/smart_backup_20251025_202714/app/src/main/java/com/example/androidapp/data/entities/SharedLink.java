package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "shared_links",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId"), @Index(value = "token", unique = true)})
public class SharedLink {
    @PrimaryKey
    @NonNull
    private String id;
    private String companyId;
    private String token;
    private String type;
    private String filters; // JSON string
    private String password;
    private String createdAt;
    private String expiresAt;

    public SharedLink(@NonNull String id, String companyId, String token, String type, String filters, String password, String createdAt, String expiresAt) {
        this.id = id;
        this.companyId = companyId;
        this.token = token;
        this.type = type;
        this.filters = filters;
        this.password = password;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getCompanyId() { return companyId; }
    public String getToken() { return token; }
    public String getType() { return type; }
    public String getFilters() { return filters; }
    public String getPassword() { return password; }
    public String getCreatedAt() { return createdAt; }
    public String getExpiresAt() { return expiresAt; }
    public String getName() { return "Link for " + type; } // Placeholder name
    public String getUrl() { return "https://example.com/shared/" + token; } // Placeholder URL

    // Setters
    public void setId(@NonNull String id ) { this.id = id; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public void setToken(String token) { this.token = token; }
    public void setType(String type) { this.type = type; }
    public void setFilters(String filters) { this.filters = filters; }
    public void setPassword(String password) { this.password = password; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
}
