package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.Role;

@Dao
public interface RoleDao {
    @Insert
    void insert(Role role);

    @Update
    void update(Role role);

    @Delete
    void delete(Role role);

    @Query("SELECT * FROM roles")
    List<Role> getAllRoles();

    @Query("SELECT * FROM roles WHERE id = :id LIMIT 1")
    Role getRoleById(String id);
}
