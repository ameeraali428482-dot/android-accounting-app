package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.Role;
import java.util.List;

@Dao
public interface RoleDao {
    
    @Query("SELECT * FROM roles WHERE companyId = :companyId")
    LiveData<List<Role>> getAllRoles(String companyId);

    @Query("SELECT * FROM roles WHERE id = :roleId AND companyId = :companyId")
    LiveData<Role> getRoleById(String roleId, String companyId);

    @Query("SELECT * FROM roles WHERE id = :roleId")
    Role getRoleByIdSync(String roleId);

    @Query("SELECT * FROM roles WHERE name = :roleName AND companyId = :companyId")
    Role getRoleByName(String roleName, String companyId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Role role);

    @Update
    void update(Role role);

    @Delete
    void delete(Role role);

    @Query("DELETE FROM roles WHERE companyId = :companyId")
    void deleteAllRoles(String companyId);
}
