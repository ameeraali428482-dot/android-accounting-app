package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.UserRole;
import java.util.List;

@Dao
public interface UserRoleDao extends BaseDao<UserRole> {
    
    @Query("SELECT * FROM user_roles WHERE userId = :userId")
    List<UserRole> getByUserId(String userId);

    @Query("SELECT * FROM user_roles WHERE roleId = :roleId")
    List<UserRole> getByRoleId(String roleId);

    @Query("SELECT * FROM user_roles WHERE userId = :userId AND roleId = :roleId")
    UserRole getUserRole(String userId, String roleId);

    @Query("DELETE FROM user_roles WHERE userId = :userId AND roleId = :roleId")
    void deleteByUserAndRole(String userId, String roleId);

    @Query("DELETE FROM user_roles WHERE userId = :userId")
    void deleteByUserId(String userId);
}
