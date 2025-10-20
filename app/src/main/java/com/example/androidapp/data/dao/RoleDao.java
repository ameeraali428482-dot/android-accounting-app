package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Role;
import java.util.List;

@Dao
public interface RoleDao extends BaseDao<Role> {
    
    @Query("SELECT * FROM roles WHERE role_id = :roleId")
    Role getById(String roleId);

    @Query("SELECT * FROM roles ORDER BY name")
    List<Role> getAll();

    @Query("SELECT * FROM roles WHERE name = :name")
    Role getByName(String name);

    @Query("DELETE FROM roles WHERE role_id = :roleId")
    void deleteById(String roleId);
}
