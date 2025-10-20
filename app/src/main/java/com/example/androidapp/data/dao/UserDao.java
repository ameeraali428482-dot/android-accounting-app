package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.User;
import java.util.List;

@Dao
public interface UserDao extends BaseDao<User> {
    
    @Query("SELECT * FROM users WHERE id = :id")
    User getById(int id);
    
    @Query("SELECT * FROM users WHERE id = :id")
    User getUserByIdSync(int id);
    
    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
    
    @Query("SELECT * FROM users WHERE phone = :phone")
    User getUserByPhone(String phone);
    
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);
    
    @Query("SELECT * FROM users ORDER BY name ASC")
    List<User> getAll();
    
    @Query("SELECT * FROM users WHERE company_id = :companyId")
    List<User> getByCompanyId(String companyId);
    
    @Query("SELECT * FROM users WHERE is_active = 1")
    List<User> getActiveUsers();
    
    @Query("SELECT * FROM users WHERE name LIKE '%' || :searchTerm || '%'")
    List<User> searchByName(String searchTerm);
    
    @Query("UPDATE users SET last_login = :timestamp WHERE id = :userId")
    void updateLastLogin(int userId, long timestamp);
    
    @Query("UPDATE users SET is_active = :isActive WHERE id = :userId")
    void updateUserStatus(int userId, boolean isActive);
    
    @Query("SELECT COUNT(*) FROM users WHERE company_id = :companyId")
    int getCountByCompany(String companyId);
    
    @Query("DELETE FROM users WHERE company_id = :companyId")
    void deleteByCompanyId(String companyId);
    
    @Query("DELETE FROM users")
    void deleteAll();
}
