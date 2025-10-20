package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;

@Entity(tableName = "user_roles",
        foreignKeys = @ForeignKey(entity = User.class,
                                  parentColumns = "id",
                                  childColumns = "user_id",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index("user_id")})
public class UserRole {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "user_id")
    private int userId;
    
    @ColumnInfo(name = "role_name")
    private String roleName;
    
    @ColumnInfo(name = "permissions")
    private String permissions;

    // Empty constructor (ignored by Room)
    @Ignore
    public UserRole() {}

    // Constructor for Room
    public UserRole(int userId, String roleName, String permissions) {
        this.userId = userId;
        this.roleName = roleName;
        this.permissions = permissions;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    
    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
}
