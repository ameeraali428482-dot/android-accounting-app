package com.example.androidapp.data.entities;

import androidx.room.*;

@Entity(tableName = "role_permissions",
        primaryKeys = {"roleId", "permissionId"})
public class RolePermission {
    public String roleId;
    public String permissionId;
    
    @ColumnInfo(name = "granted_date")
    public long grantedDate;
    
    public String grantedBy;
    
    public RolePermission() {
        this.grantedDate = System.currentTimeMillis();
    }
    
    public RolePermission(String roleId, String permissionId, String grantedBy) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.grantedBy = grantedBy;
        this.grantedDate = System.currentTimeMillis();
    }
}
