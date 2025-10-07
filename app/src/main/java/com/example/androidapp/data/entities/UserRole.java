package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "user_roles",
        primaryKeys = {"userId", "roleId"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                            parentColumns = "id",
                            childColumns = "userId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Role.class,
                            parentColumns = "id",
                            childColumns = "roleId",
                            onDelete = ForeignKey.CASCADE)
        })
public class UserRole {
    public int userId;
    public int roleId;

    public UserRole(int userId, int roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public int getRoleId() {
        return roleId;
    }
}
