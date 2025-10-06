package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.androidapp.models.UserPermission;

@Dao
public interface UserPermissionDao {
    @Insert
    void insert(UserPermission userPermission);

    @Query("DELETE FROM user_permissions WHERE userId = :userId AND permissionId = :permissionId")
    void delete(int userId, int permissionId);

    @Query("DELETE FROM user_permissions WHERE userId = :userId")
    void deleteAllUserPermissions(int userId);

    @Query("SELECT EXISTS(SELECT 1 FROM user_permissions WHERE userId = :userId AND permissionId = :permissionId LIMIT 1)")
    boolean hasPermission(int userId, int permissionId);
}
