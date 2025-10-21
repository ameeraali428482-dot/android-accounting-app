package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.androidapp.data.entities.UserPermission;
import com.example.androidapp.data.entities.Permission;
import com.example.androidapp.data.entities.User;
import java.util.List;

@Dao
public interface UserPermissionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserPermission userPermission);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UserPermission> userPermissions);
    
    @Delete
    void delete(UserPermission userPermission);
    
    @Query("DELETE FROM user_permissions WHERE userId = :userId AND permissionId = :permissionId AND companyId = :companyId")
    void deleteUserPermission(String userId, String permissionId, String companyId);
    
    @Query("DELETE FROM user_permissions WHERE userId = :userId AND companyId = :companyId")
    void deleteAllUserPermissions(String userId, String companyId);
    
    @Query("DELETE FROM user_permissions WHERE userId = :userId")
    void deleteAllUserPermissionsAllCompanies(String userId);
    
    @Query("DELETE FROM user_permissions WHERE permissionId = :permissionId")
    void deleteAllPermissionUsers(String permissionId);
    
    @Query("DELETE FROM user_permissions WHERE companyId = :companyId")
    void deleteAllCompanyUserPermissions(String companyId);
    
    @Query("SELECT * FROM user_permissions WHERE userId = :userId AND companyId = :companyId")
    List<UserPermission> getUserPermissions(String userId, String companyId);
    
    @Query("SELECT * FROM user_permissions WHERE userId = :userId AND companyId = :companyId")
    LiveData<List<UserPermission>> getUserPermissionsLive(String userId, String companyId);
    
    @Query("SELECT * FROM user_permissions WHERE userId = :userId")
    List<UserPermission> getAllUserPermissions(String userId);
    
    @Query("SELECT * FROM user_permissions WHERE permissionId = :permissionId AND companyId = :companyId")
    List<UserPermission> getPermissionUsers(String permissionId, String companyId);
    
    @Query("SELECT p.* FROM permissions p " +
           "INNER JOIN user_permissions up ON p.id = up.permissionId " +
           "WHERE up.userId = :userId AND up.companyId = :companyId " +
           "ORDER BY p.action")
    List<Permission> getPermissionsByUserId(String userId, String companyId);
    
    @Query("SELECT p.* FROM permissions p " +
           "INNER JOIN user_permissions up ON p.id = up.permissionId " +
           "WHERE up.userId = :userId AND up.companyId = :companyId " +
           "ORDER BY p.action")
    LiveData<List<Permission>> getPermissionsByUserIdLive(String userId, String companyId);
    
    @Query("SELECT u.* FROM users u " +
           "INNER JOIN user_permissions up ON u.id = up.userId " +
           "WHERE up.permissionId = :permissionId AND up.companyId = :companyId " +
           "ORDER BY u.name")
    List<User> getUsersByPermissionId(String permissionId, String companyId);
    
    @Query("SELECT u.* FROM users u " +
           "INNER JOIN user_permissions up ON u.id = up.userId " +
           "WHERE up.permissionId = :permissionId AND up.companyId = :companyId " +
           "ORDER BY u.name")
    LiveData<List<User>> getUsersByPermissionIdLive(String permissionId, String companyId);
    
    @Query("SELECT EXISTS(SELECT 1 FROM user_permissions WHERE userId = :userId AND permissionId = :permissionId AND companyId = :companyId LIMIT 1)")
    boolean hasPermission(String userId, String permissionId, String companyId);
    
    @Query("SELECT EXISTS(SELECT 1 FROM user_permissions WHERE userId = :userId AND permissionId = :permissionId AND companyId = :companyId LIMIT 1)")
    LiveData<Boolean> hasPermissionLive(String userId, String permissionId, String companyId);
    
    @Query("SELECT COUNT(*) FROM user_permissions WHERE userId = :userId AND companyId = :companyId")
    int getPermissionsCountForUser(String userId, String companyId);
    
    @Query("SELECT COUNT(*) FROM user_permissions WHERE permissionId = :permissionId AND companyId = :companyId")
    int getUsersCountForPermission(String permissionId, String companyId);
    
    @Query("SELECT COUNT(*) FROM user_permissions WHERE companyId = :companyId")
    int getTotalUserPermissionsForCompany(String companyId);
}