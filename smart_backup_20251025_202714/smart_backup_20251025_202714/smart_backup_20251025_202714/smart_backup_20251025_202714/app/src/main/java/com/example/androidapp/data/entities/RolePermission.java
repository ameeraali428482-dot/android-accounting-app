package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "role_permissions",
        primaryKeys = {"roleId", "permissionId"},
        foreignKeys = {
                @ForeignKey(entity = Role.class, parentColumns = "id", childColumns = "roleId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Permission.class, parentColumns = "id", childColumns = "permissionId", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "roleId"), @Index(value = "permissionId")})
public class RolePermission {
    @NonNull
    private String roleId;
    @NonNull
    private String permissionId;

    public RolePermission(@NonNull String roleId, @NonNull String permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    // Getters
    @NonNull
    public String getRoleId() { return roleId; }
    @NonNull
    public String getPermissionId() { return permissionId; }

    // Setters - مطلوبة لـ Room Database
    public void setRoleId(@NonNull String roleId) { this.roleId = roleId; }
    public void setPermissionId(@NonNull String permissionId) { this.permissionId = permissionId; }
}
