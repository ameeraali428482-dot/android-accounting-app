package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.androidapp.models.UserRole;

@Dao
public interface UserRoleDao {
    @Insert
    void insert(UserRole userRole);

    @Query("DELETE FROM user_roles WHERE userId = :userId AND roleId = :roleId")
    void delete(int userId, int roleId);

    @Query("DELETE FROM user_roles WHERE userId = :userId")
    void deleteAllUserRoles(int userId);

    @Query("SELECT roleId FROM user_roles WHERE userId = :userId")
    int[] getRoleIdsForUser(int userId);
}
