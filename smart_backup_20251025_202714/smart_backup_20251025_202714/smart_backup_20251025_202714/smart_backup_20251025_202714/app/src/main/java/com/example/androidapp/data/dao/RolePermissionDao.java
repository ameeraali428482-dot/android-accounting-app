package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.androidapp.data.entities.RolePermission;
import com.example.androidapp.data.entities.Permission;
import com.example.androidapp.data.entities.Role;
import java.util.List;

@Dao
public interface RolePermissionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RolePermission rolePermission);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RolePermission> rolePermissions);
    
    @Delete
    void delete(RolePermission rolePermission);
    
    @Query("DELETE FROM role_permissions WHERE roleId = :roleId AND permissionId = :permissionId")
    void deleteRolePermission(String roleId, String permissionId);
    
    @Query("DELETE FROM role_permissions WHERE roleId = :roleId")
    void deleteAllRolePermissions(String roleId);
    
    @Query("DELETE FROM role_permissions WHERE permissionId = :permissionId")
    void deleteAllPermissionRoles(String permissionId);
    
    @Query("SELECT * FROM role_permissions WHERE roleId = :roleId")
    List<RolePermission> getRolePermissions(String roleId);
    
    @Query("SELECT * FROM role_permissions WHERE roleId = :roleId")
    LiveData<List<RolePermission>> getRolePermissionsLive(String roleId);
    
    @Query("SELECT * FROM role_permissions WHERE permissionId = :permissionId")
    List<RolePermission> getPermissionRoles(String permissionId);
    
    @Query("SELECT p.* FROM permissions p " +
           "INNER JOIN role_permissions rp ON p.id = rp.permissionId " +
           "WHERE rp.roleId = :roleId " +
           "ORDER BY p.action")
    List<Permission> getPermissionsByRoleId(String roleId);
    
    @Query("SELECT p.* FROM permissions p " +
           "INNER JOIN role_permissions rp ON p.id = rp.permissionId " +
           "WHERE rp.roleId = :roleId " +
           "ORDER BY p.action")
    LiveData<List<Permission>> getPermissionsByRoleIdLive(String roleId);
    
    @Query("SELECT r.* FROM roles r " +
           "INNER JOIN role_permissions rp ON r.id = rp.roleId " +
           "WHERE rp.permissionId = :permissionId " +
           "ORDER BY r.name")
    List<Role> getRolesByPermissionId(String permissionId);
    
    @Query("SELECT r.* FROM roles r " +
           "INNER JOIN role_permissions rp ON r.id = rp.roleId " +
           "WHERE rp.permissionId = :permissionId " +
           "ORDER BY r.name")
    LiveData<List<Role>> getRolesByPermissionIdLive(String permissionId);
    
    @Query("SELECT EXISTS(SELECT 1 FROM role_permissions WHERE roleId = :roleId AND permissionId = :permissionId LIMIT 1)")
    boolean hasRolePermission(String roleId, String permissionId);
    
    @Query("SELECT EXISTS(SELECT 1 FROM role_permissions WHERE roleId = :roleId AND permissionId = :permissionId LIMIT 1)")
    LiveData<Boolean> hasRolePermissionLive(String roleId, String permissionId);
    
    @Query("SELECT COUNT(*) FROM role_permissions WHERE roleId = :roleId")
    int getPermissionsCountForRole(String roleId);
    
    @Query("SELECT COUNT(*) FROM role_permissions WHERE permissionId = :permissionId")
    int getRolesCountForPermission(String permissionId);
}