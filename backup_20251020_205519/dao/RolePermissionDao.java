package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.RolePermission;
import java.util.List;

@Dao
public interface RolePermissionDao extends BaseDao<RolePermission> {
    
    @Query("SELECT * FROM role_permissions WHERE roleId = :roleId AND permissionId = :permissionId")
    RolePermission getByRoleAndPermission(String roleId, String permissionId);
    
    @Query("SELECT * FROM role_permissions WHERE roleId = :roleId")
    List<RolePermission> getByRoleId(String roleId);
    
    @Query("SELECT * FROM role_permissions WHERE permissionId = :permissionId")
    List<RolePermission> getByPermissionId(String permissionId);
    
    @Query("SELECT COUNT(*) FROM role_permissions WHERE roleId = :roleId")
    int getPermissionCountByRole(String roleId);
    
    @Query("DELETE FROM role_permissions WHERE roleId = :roleId")
    void deleteByRoleId(String roleId);
    
    @Query("DELETE FROM role_permissions WHERE permissionId = :permissionId")
    void deleteByPermissionId(String permissionId);
    
    @Query("DELETE FROM role_permissions WHERE roleId = :roleId AND permissionId = :permissionId")
    void deleteByRoleAndPermission(String roleId, String permissionId);
}
