package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "role_permissions",
        primaryKeys = {"roleId", "permissionId"},
        foreignKeys = {
                @ForeignKey(entity = Role.class,
                        parentColumns = "id",
                        childColumns = "roleId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Permission.class,
                        parentColumns = "id",
                        childColumns = "permissionId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "roleId"), @Index(value = "permissionId")})
public class RolePermission {
    @NonNull
    public String roleId;
    @NonNull
    public String permissionId;

    public RolePermission(@NonNull String roleId, @NonNull String permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }
}
