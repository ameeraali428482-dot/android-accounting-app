package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "user_permissions",
        primaryKeys = {"userId", "permissionId"},
        foreignKeys = {
            @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"),
            @ForeignKey(entity = Permission.class, parentColumns = "permissionId", childColumns = "permissionId")
        })
public class UserPermission {
    public int userId;
    public String permissionId;
    public long grantedAt;
    public String grantedBy;

    public UserPermission() {
        this.grantedAt = System.currentTimeMillis();
    }

    public UserPermission(int userId, String permissionId, String grantedBy) {
        this();
        this.userId = userId;
        this.permissionId = permissionId;
        this.grantedBy = grantedBy;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getPermissionId() { return permissionId; }
    public long getGrantedAt() { return grantedAt; }
    public String getGrantedBy() { return grantedBy; }

    // Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }
    public void setGrantedAt(long grantedAt) { this.grantedAt = grantedAt; }
    public void setGrantedBy(String grantedBy) { this.grantedBy = grantedBy; }
}
