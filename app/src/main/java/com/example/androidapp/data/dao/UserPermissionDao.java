package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.UserPermission;
import java.util.List;

@Dao
public interface UserPermissionDao extends BaseDao<UserPermission> {
    
    @Query("SELECT * FROM user_permissions WHERE userId = :userId")
    List<UserPermission> getByUserId(int userId);

    @Query("SELECT * FROM user_permissions WHERE permissionId = :permissionId")
    List<UserPermission> getByPermissionId(String permissionId);

    @Query("SELECT * FROM user_permissions WHERE userId = :userId AND permissionId = :permissionId")
    UserPermission getUserPermission(int userId, String permissionId);

    @Query("DELETE FROM user_permissions WHERE userId = :userId AND permissionId = :permissionId")
    void deleteUserPermission(int userId, String permissionId);

    @Query("DELETE FROM user_permissions WHERE userId = :userId")
    void deleteByUserId(int userId);
}
