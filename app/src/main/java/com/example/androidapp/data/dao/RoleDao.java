package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.data.entities.Permission;

@Dao
public interface RoleDao {
    @Insert
    void insert(Role role);

    @Update
    void update(Role role);

    @Delete
    void delete(Role role);

    @Query("SELECT * FROM roles WHERE companyId = :companyId")
    LiveData<List<Role>> getAllRoles(String companyId);

    @Query("SELECT * FROM roles WHERE id = :id LIMIT 1")
    LiveData<Role> getRoleById(String id);

    @Query("SELECT p.* FROM permissions p INNER JOIN role_permissions rp ON p.id = rp.permissionId WHERE rp.roleId = :roleId")
    LiveData<List<Permission>> getPermissionsForRole(String roleId);

    @Query("INSERT INTO role_permissions (roleId, permissionId) VALUES (:roleId, :permissionId)")
    void insertRolePermission(String roleId, String permissionId);

    @Query("DELETE FROM role_permissions WHERE roleId = :roleId")
    void deleteRolePermissions(String roleId);
}
