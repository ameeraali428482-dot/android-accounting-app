package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.RolePermission;

import java.util.List;

@Dao
public interface RolePermissionDao extends BaseDao<RolePermission> {
    
    @Query("SELECT * FROM role_permissions WHERE role_id = :roleId AND permission_id = :permissionId")
    RolePermission getByRoleAndPermission(String roleId, String permissionId);

    @Query("SELECT * FROM role_permissions WHERE role_id = :roleId")
    List<RolePermission> getByRoleId(String roleId);

    @Query("SELECT * FROM role_permissions WHERE permission_id = :permissionId")
    List<RolePermission> getByPermissionId(String permissionId);

    @Query("SELECT COUNT(*) FROM role_permissions WHERE role_id = :roleId")
    int getPermissionCountByRole(String roleId);

    @Query("DELETE FROM role_permissions WHERE role_id = :roleId")
    void deleteByRoleId(String roleId);

    @Query("DELETE FROM role_permissions WHERE permission_id = :permissionId")
    void deleteByPermissionId(String permissionId);
}
