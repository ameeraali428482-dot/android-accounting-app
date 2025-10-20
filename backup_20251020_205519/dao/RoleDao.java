package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Role;
import java.util.List;

@Dao
public interface RoleDao {
    @Query("SELECT * FROM roles")
    List<Role> getAll();
    
    @Query("SELECT * FROM roles WHERE id = :id")
    Role getById(int id);
    
    @Query("SELECT * FROM roles WHERE role_id = :roleId")
    Role getByRoleId(String roleId);
    
    @Query("SELECT * FROM roles WHERE name LIKE :name")
    List<Role> getByName(String name);
    
    @Insert
    void insert(Role role);
    
    @Insert
    void insertAll(List<Role> roles);
    
    @Update
    void update(Role role);
    
    @Delete
    void delete(Role role);
}
