package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Permission;
import java.util.List;

@Dao
public interface PermissionDao extends BaseDao<Permission> {
    
    @Query("SELECT * FROM permissions WHERE permissionId = :id")
    Permission getById(String id);

    @Query("SELECT * FROM permissions ORDER BY name")
    List<Permission> getAll();

    @Query("SELECT * FROM permissions WHERE category = :category ORDER BY name")
    List<Permission> getByCategory(String category);

    @Query("DELETE FROM permissions WHERE permissionId = :id")
    void deleteById(String id);
}
