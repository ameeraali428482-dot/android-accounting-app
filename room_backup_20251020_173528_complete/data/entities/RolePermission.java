package com.example.androidapp.data.entities;

import androidx.room.*;
import androidx.annotation.NonNull;

@Entity(tableName = "role_permissions",
        primaryKeys = {"roleId", "permissionId"})
public class RolePermission {
    @NonNull
    public String roleId;
    
    @NonNull
    public String permissionId;
    
    @ColumnInfo(name = "granted_date")
    public long grantedDate;
    
    public String grantedBy;
    
    @Ignore
    public RolePermission() {
        this.grantedDate = System.currentTimeMillis();
    }
    
    public RolePermission(@NonNull String roleId, @NonNull String permissionId, String grantedBy) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.grantedBy = grantedBy;
        this.grantedDate = System.currentTimeMillis();
    }
}
