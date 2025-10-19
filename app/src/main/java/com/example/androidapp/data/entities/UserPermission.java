package com.example.androidapp.data.entities;

import androidx.room.*;

@Entity(tableName = "user_permissions", primaryKeys = {"userId", "permissionId"})
public class UserPermission {
    public String userId;
    public String permissionId;
    
    @ColumnInfo(name = "granted_date")
    public long grantedDate;
    
    public String grantedBy;
    
    public UserPermission() {
        this.grantedDate = System.currentTimeMillis();
    }
}
