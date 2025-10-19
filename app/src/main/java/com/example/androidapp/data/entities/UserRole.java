import androidx.room.Ignore;
import androidx.annotation.NonNull;
package com.example.androidapp.data.entities;

import androidx.room.*;

@Entity(tableName = "user_roles", primaryKeys = {"userId", "roleId"})
public class UserRole {
    @NonNull
    public String userId;
    @NonNull
    public String roleId;
    
    @ColumnInfo(name = "assigned_date")
    public long assignedDate;
    
    public String assignedBy;
    
    public UserRole() {
        this.assignedDate = System.currentTimeMillis();
    }
    
    public UserRole(String userId, String roleId, long assignedDate) {
        this.userId = userId;
        this.roleId = roleId;
        this.assignedDate = assignedDate;
    }
    
    public String getUserId() { return userId; }
    public String getRoleId() { return roleId; }
}
