package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.User;
import java.util.List;

@Dao
public interface UserDao extends BaseDao<User> {
    
    @Query("SELECT * FROM users ORDER BY name")
    List<User> getAllUsers();
    
    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(String id);
    
    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
    
    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber")
    User getUserByPhone(String phoneNumber);
    
    @Query("SELECT * FROM users WHERE role = :role ORDER BY name")
    List<User> getUsersByRole(String role);
    
    @Query("SELECT * FROM users WHERE name LIKE '%' || :searchQuery || '%' OR email LIKE '%' || :searchQuery || '%' ORDER BY name")
    List<User> searchUsers(String searchQuery);
    
    @Query("UPDATE users SET lastLogin = :timestamp WHERE id = :userId")
    void updateLastLogin(String userId, long timestamp);
    
    @Query("UPDATE users SET isActive = :isActive WHERE id = :userId")
    void updateUserStatus(String userId, boolean isActive);
    
    @Query("DELETE FROM users WHERE id = :id")
    void deleteUser(String id);
}
