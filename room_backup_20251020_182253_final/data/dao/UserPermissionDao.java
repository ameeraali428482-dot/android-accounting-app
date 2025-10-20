package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.UserPermission;
import java.util.List;

@Dao
public interface UserPermissionDao extends BaseDao<UserPermission> {
    
    @Query("SELECT * FROM user_permissions WHERE userId = :userId")
    List<UserPermission> getByUserId(String userId);
    
    @Query("SELECT * FROM user_permissions WHERE permissionId = :permissionId")
    List<UserPermission> getByPermissionId(String permissionId);
    
    @Query("DELETE FROM user_permissions WHERE userId = :userId AND permissionId = :permissionId")
    void deleteByUserAndPermission(String userId, String permissionId);
    
    @Query("DELETE FROM user_permissions WHERE userId = :userId")
    void deleteByUser(String userId);
}
