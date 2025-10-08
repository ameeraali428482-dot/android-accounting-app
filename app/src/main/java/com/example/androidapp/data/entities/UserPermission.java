package com.example.androidapp.data.entities;

import androidx.room.TypeConverters;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;


@Entity(tableName = "user_permissions",
        primaryKeys = {"userId", "permissionId"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                            parentColumns = "id",
                            childColumns = "userId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Permission.class,
                            parentColumns = "id",
                            childColumns = "permissionId",
                            onDelete = ForeignKey.CASCADE)
        })
public class UserPermission {
    public int userId;
    public int permissionId;

    public UserPermission(int userId, int permissionId) {
        this.userId = userId;
        this.permissionId = permissionId;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public int getPermissionId() {
        return permissionId;
    }
}
