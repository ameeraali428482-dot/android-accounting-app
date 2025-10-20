package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Ignore;

@Entity(tableName = "roles",
        indices = {@Index("role_id")})
public class Role {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "role_id")
    private String roleId;
    
    @ColumnInfo(name = "name")
    private String name;
    
    @ColumnInfo(name = "description")
    private String description;
    
    @ColumnInfo(name = "permissions")
    private String permissions;
    
    @ColumnInfo(name = "created_at")
    private long createdAt;

    // Constructor for Role with all parameters
    @Ignore
    public Role(String roleId, String name, String description, String permissions, long createdAt) {
        this.roleId = roleId;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.createdAt = createdAt;
    }
    
    // Constructor for compatibility with old code
    @Ignore
    public Role(String roleId, String name, String description, long createdAt) {
        this.roleId = roleId;
        this.name = name;
        this.description = description;
        this.permissions = "";
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
