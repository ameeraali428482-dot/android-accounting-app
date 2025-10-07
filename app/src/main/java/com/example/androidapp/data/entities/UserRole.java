package com.example.androidapp.data.entities;
import androidx.room.ForeignKey;import androidx.room.Index;import androidx.room.TypeConverters;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;


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
