package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.Permission;

@Dao
public interface PermissionDao {
    @Insert
    void insert(Permission permission);

    @Update
    void update(Permission permission);

    @Delete
    void delete(Permission permission);

    @Query("SELECT * FROM permissions")
    List<Permission> getAllPermissions();

    @Query("SELECT * FROM permissions WHERE id = :id LIMIT 1")
    Permission getPermissionById(String id);
}
