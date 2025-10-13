package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
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

    @Query("SELECT * FROM roles WHERE companyId = :companyId")
    LiveData<List<Role>> getAllRoles(String companyId);

    @Query("SELECT * FROM roles WHERE id = :id LIMIT 1")
    LiveData<Role> getRoleById(String id);

    @Query("SELECT * FROM roles WHERE id = :id LIMIT 1")
    Role getRoleByIdSync(String id);
}
