package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
import com.example.androidapp.data.entities.Permission;

@Dao
public interface PermissionDao {
    @Insert
    void insert(Permission permission);

    @Query("SELECT * FROM permissions")
    LiveData<List<Permission>> getAllPermissions();
    
    @Query("SELECT * FROM permissions WHERE id = :id")
    Permission getPermissionById(String id);
    
    @Query("SELECT p.* FROM permissions p INNER JOIN role_permissions rp ON p.id = rp.permissionId WHERE rp.roleId = :roleId")
    List<Permission> getPermissionsByRoleId(String roleId);
}
