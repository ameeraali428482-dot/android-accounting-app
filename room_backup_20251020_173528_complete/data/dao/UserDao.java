package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.User;

import java.util.List;

@Dao
public interface UserDao extends BaseDao<User> {
    
    @Query("SELECT * FROM users WHERE id = :id")
    User getById(String id);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE phone = :phone")
    User getUserByPhone(String phone);

    @Query("SELECT * FROM users ORDER BY name ASC")
    List<User> getAll();

    @Query("SELECT * FROM users WHERE company_id = :companyId ORDER BY name ASC")
    List<User> getByCompanyId(String companyId);

    @Query("SELECT * FROM users WHERE is_active = 1 ORDER BY name ASC")
    List<User> getActiveUsers();

    @Query("SELECT * FROM users WHERE name LIKE '%' || :searchTerm || '%' ORDER BY name ASC")
    List<User> searchByName(String searchTerm);

    @Query("SELECT * FROM users WHERE email LIKE '%' || :searchTerm || '%' OR name LIKE '%' || :searchTerm || '%' ORDER BY name ASC")
    List<User> searchUsers(String searchTerm);

    @Query("UPDATE users SET last_login = :timestamp WHERE id = :userId")
    void updateLastLogin(String userId, long timestamp);

    @Query("UPDATE users SET is_active = :isActive WHERE id = :userId")
    void updateUserStatus(String userId, boolean isActive);

    @Query("UPDATE users SET role = :role WHERE id = :userId")
    void updateUserRole(String userId, String role);

    @Query("SELECT COUNT(*) FROM users WHERE company_id = :companyId")
    int getCountByCompany(String companyId);

    @Query("SELECT COUNT(*) FROM users WHERE company_id = :companyId AND is_active = 1")
    int getActiveCountByCompany(String companyId);

    @Query("SELECT COUNT(*) FROM users WHERE role = :role")
    int getCountByRole(String role);

    @Query("SELECT * FROM users WHERE role = :role ORDER BY name ASC")
    List<User> getUsersByRole(String role);

    @Query("SELECT * FROM users WHERE company_id = :companyId AND role = :role ORDER BY name ASC")
    List<User> getUsersByCompanyAndRole(String companyId, String role);

    @Query("DELETE FROM users WHERE company_id = :companyId")
    void deleteByCompanyId(String companyId);

    @Query("DELETE FROM users WHERE id = :userId")
    void deleteById(String userId);

    // استعلامات إضافية مفيدة
    @Query("SELECT * FROM users WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    List<User> getUsersByDateRange(long startDate, long endDate);

    @Query("SELECT * FROM users WHERE last_login >= :since ORDER BY last_login DESC")
    List<User> getRecentlyActiveUsers(long since);
}
