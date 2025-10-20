package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "user_permissions", primaryKeys = {"userId", "permissionId"})
public class UserPermission {
    @NonNull
    public String userId;
    @NonNull
    public String permissionId;
    
    @ColumnInfo(name = "granted_date")
    public long grantedDate;
    
    public String grantedBy;
    
    public UserPermission() {
        this.grantedDate = System.currentTimeMillis();
    }
}
