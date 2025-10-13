package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Role;

import java.util.List;

@Dao
public interface RoleDao {
    @Insert
    void insert(Role role);

    @Update
    void update(Role role);

    @Delete
    void delete(Role role);

    @Query("SELECT * FROM roles WHERE companyId = :companyId ORDER BY name ASC")
    LiveData<List<Role>> getAllRoles(String companyId);

    @Query("SELECT * FROM roles WHERE id = :roleId AND companyId = :companyId")
    LiveData<Role> getRoleById(String roleId, String companyId);

    @Query("SELECT * FROM roles WHERE id = :roleId")
    Role getRoleByIdSync(String roleId);

    @Query("SELECT * FROM roles WHERE name = :roleName AND companyId = :companyId")
    Role getRoleByName(String roleName, String companyId);

    @Query("DELETE FROM roles WHERE companyId = :companyId")
    void deleteAllRoles(String companyId);
}
