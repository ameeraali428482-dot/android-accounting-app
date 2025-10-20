package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.UserRole;
import java.util.List;

@Dao
public interface UserRoleDao {
    @Query("SELECT * FROM user_roles")
    List<UserRole> getAllUserRoles();
    
    @Query("SELECT * FROM user_roles WHERE id = :id")
    UserRole getUserRoleById(int id);
    
    @Query("SELECT * FROM user_roles WHERE user_id = :userId")
    List<UserRole> getUserRolesByUserId(int userId);
    
    @Query("SELECT * FROM user_roles WHERE role_name = :roleName")
    List<UserRole> getUserRolesByRoleName(String roleName);
    
    @Insert
    void insertUserRole(UserRole userRole);
    
    @Update
    void updateUserRole(UserRole userRole);
    
    @Delete
    void deleteUserRole(UserRole userRole);
}
