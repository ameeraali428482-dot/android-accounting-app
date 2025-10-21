package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.androidapp.data.entities.UserRole;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.data.entities.User;
import java.util.List;

@Dao
public interface UserRoleDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserRole userRole);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UserRole> userRoles);
    
    @Delete
    void delete(UserRole userRole);
    
    @Query("DELETE FROM user_roles WHERE userId = :userId AND roleId = :roleId AND companyId = :companyId")
    void deleteUserRole(String userId, String roleId, String companyId);
    
    @Query("DELETE FROM user_roles WHERE userId = :userId AND companyId = :companyId")
    void deleteAllUserRoles(String userId, String companyId);
    
    @Query("DELETE FROM user_roles WHERE userId = :userId")
    void deleteAllUserRolesAllCompanies(String userId);
    
    @Query("DELETE FROM user_roles WHERE roleId = :roleId")
    void deleteAllRoleUsers(String roleId);
    
    @Query("DELETE FROM user_roles WHERE companyId = :companyId")
    void deleteAllCompanyUserRoles(String companyId);
    
    @Query("SELECT * FROM user_roles WHERE userId = :userId AND companyId = :companyId")
    List<UserRole> getUserRoles(String userId, String companyId);
    
    @Query("SELECT * FROM user_roles WHERE userId = :userId AND companyId = :companyId")
    LiveData<List<UserRole>> getUserRolesLive(String userId, String companyId);
    
    @Query("SELECT * FROM user_roles WHERE userId = :userId")
    List<UserRole> getAllUserRoles(String userId);
    
    @Query("SELECT * FROM user_roles WHERE roleId = :roleId AND companyId = :companyId")
    List<UserRole> getRoleUsers(String roleId, String companyId);
    
    @Query("SELECT r.* FROM roles r " +
           "INNER JOIN user_roles ur ON r.id = ur.roleId " +
           "WHERE ur.userId = :userId AND ur.companyId = :companyId " +
           "ORDER BY r.name")
    List<Role> getRolesByUserId(String userId, String companyId);
    
    @Query("SELECT r.* FROM roles r " +
           "INNER JOIN user_roles ur ON r.id = ur.roleId " +
           "WHERE ur.userId = :userId AND ur.companyId = :companyId " +
           "ORDER BY r.name")
    LiveData<List<Role>> getRolesByUserIdLive(String userId, String companyId);
    
    @Query("SELECT u.* FROM users u " +
           "INNER JOIN user_roles ur ON u.id = ur.userId " +
           "WHERE ur.roleId = :roleId AND ur.companyId = :companyId " +
           "ORDER BY u.name")
    List<User> getUsersByRoleId(String roleId, String companyId);
    
    @Query("SELECT u.* FROM users u " +
           "INNER JOIN user_roles ur ON u.id = ur.userId " +
           "WHERE ur.roleId = :roleId AND ur.companyId = :companyId " +
           "ORDER BY u.name")
    LiveData<List<User>> getUsersByRoleIdLive(String roleId, String companyId);
    
    @Query("SELECT ur.roleId FROM user_roles ur WHERE ur.userId = :userId AND ur.companyId = :companyId")
    List<String> getRoleIdsForUser(String userId, String companyId);
    
    @Query("SELECT ur.roleId FROM user_roles ur WHERE ur.userId = :userId AND ur.companyId = :companyId")
    LiveData<List<String>> getRoleIdsForUserLive(String userId, String companyId);
    
    @Query("SELECT ur.userId FROM user_roles ur WHERE ur.roleId = :roleId AND ur.companyId = :companyId")
    List<String> getUserIdsForRole(String roleId, String companyId);
    
    @Query("SELECT EXISTS(SELECT 1 FROM user_roles WHERE userId = :userId AND roleId = :roleId AND companyId = :companyId LIMIT 1)")
    boolean hasUserRole(String userId, String roleId, String companyId);
    
    @Query("SELECT EXISTS(SELECT 1 FROM user_roles WHERE userId = :userId AND roleId = :roleId AND companyId = :companyId LIMIT 1)")
    LiveData<Boolean> hasUserRoleLive(String userId, String roleId, String companyId);
    
    @Query("SELECT COUNT(*) FROM user_roles WHERE userId = :userId AND companyId = :companyId")
    int getRolesCountForUser(String userId, String companyId);
    
    @Query("SELECT COUNT(*) FROM user_roles WHERE roleId = :roleId AND companyId = :companyId")
    int getUsersCountForRole(String roleId, String companyId);
    
    @Query("SELECT COUNT(*) FROM user_roles WHERE companyId = :companyId")
    int getTotalUserRolesForCompany(String companyId);
}