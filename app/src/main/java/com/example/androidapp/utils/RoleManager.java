package com.example.androidapp.utils;

import android.content.Context;
import android.util.Log;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Permission;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.UserPermission;
import com.example.androidapp.data.entities.UserRole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RoleManager {
    
    private static final String TAG = "RoleManager";
    
    // Predefined Roles
    public static final String ROLE_SUPER_ADMIN = "super_admin";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_MANAGER = "manager";
    public static final String ROLE_ACCOUNTANT = "accountant";
    public static final String ROLE_CASHIER = "cashier";
    public static final String ROLE_VIEWER = "viewer";
    public static final String ROLE_USER = "user";
    
    // Predefined Permissions
    public static final String PERMISSION_ACCESS_AI_CHAT = "ai_chat";
    public static final String PERMISSION_MANAGE_USERS = "manage_users";
    public static final String PERMISSION_VIEW_REPORTS = "view_reports";
    public static final String PERMISSION_MANAGE_ACCOUNTS = "manage_accounts";
    
    private AppDatabase database;
    private SessionManager sessionManager;
    private Context context;
    private Map<String, List<String>> rolePermissionsCache;
    private Map<String, List<String>> userPermissionsCache;
    
    public RoleManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getDatabase(context);
        this.sessionManager = new SessionManager(context);
        this.rolePermissionsCache = new HashMap<>();
        this.userPermissionsCache = new HashMap<>();
        initializeDefaultData();
    }
    
    /**
     * Check if user has a specific permission
     */
    public CompletableFuture<Boolean> hasPermission(String userId, String permission) {
        return CompletableFuture.supplyAsync(() -> {
            if (userId == null) {
                return false;
            }
            
            try {
                // Check if user is super admin (has all permissions)
                if (isSuperAdmin(userId)) {
                    return true;
                }
                
                List<String> userPermissions = loadUserPermissions(userId);
                return userPermissions.contains(permission);
                
            } catch (Exception e) {
                Log.e(TAG, "Error checking permission " + permission + " for user " + userId, e);
                return false;
            }
        }, AppDatabase.databaseWriteExecutor);
    }
    
    /**
     * Check if user is super admin
     */
    public boolean isSuperAdmin(String userId) {
        if (userId == null) return false;
        
        try {
            List<String> roles = loadUserRoles(userId);
            return roles.contains(ROLE_SUPER_ADMIN);
        } catch (Exception e) {
            Log.e(TAG, "Error checking super admin status for user " + userId, e);
            return false;
        }
    }
    
    /**
     * Check if user is admin
     */
    public boolean isAdmin(String userId) {
        if (userId == null) return false;
        
        try {
            List<String> roles = loadUserRoles(userId);
            return roles.contains(ROLE_ADMIN) || roles.contains(ROLE_SUPER_ADMIN);
        } catch (Exception e) {
            Log.e(TAG, "Error checking admin status for user " + userId, e);
            return false;
        }
    }
    
    /**
     * Get all permissions for user
     */
    public CompletableFuture<List<String>> getUserPermissions(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            return loadUserPermissions(userId);
        }, AppDatabase.databaseWriteExecutor);
    }
    
    /**
     * Get all roles for user
     */
    public CompletableFuture<List<String>> getUserRoles(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            return loadUserRoles(userId);
        }, AppDatabase.databaseWriteExecutor);
    }
    
    /**
     * Assign role to user
     */
    public CompletableFuture<Boolean> assignRoleToUser(String userId, String roleId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserRole userRole = new UserRole(userId, roleId, System.currentTimeMillis());
                database.userRoleDao().insert(userRole);
                
                // Clear cache for this user
                userPermissionsCache.remove(userId);
                
                Log.d(TAG, "Assigned role " + roleId + " to user " + userId);
                return true;
                
            } catch (Exception e) {
                Log.e(TAG, "Error assigning role " + roleId + " to user " + userId, e);
                return false;
            }
        }, AppDatabase.databaseWriteExecutor);
    }
    
    // Private helper methods
    
    /**
     * Load all permissions for a user (including role-based permissions)
     */
    private List<String> loadUserPermissions(String userId) {
        if (userId == null) return new ArrayList<>();
        
        // Check cache first
        if (userPermissionsCache.containsKey(userId)) {
            return userPermissionsCache.get(userId);
        }
        
        try {
            List<String> permissions = new ArrayList<>();
            
            // Get role-based permissions
            List<String> rolePermissions = getRolePermissions(userId);
            permissions.addAll(rolePermissions);
            
            // Remove duplicates
            List<String> uniquePermissions = new ArrayList<>();
            for (String permission : permissions) {
                if (!uniquePermissions.contains(permission)) {
                    uniquePermissions.add(permission);
                }
            }
            
            // Cache the result
            userPermissionsCache.put(userId, uniquePermissions);
            
            Log.d(TAG, "Loaded " + uniquePermissions.size() + " permissions for user " + userId);
            return uniquePermissions;
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading permissions for user " + userId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Load all roles for a user
     */
    private List<String> loadUserRoles(String userId) {
        if (userId == null) return new ArrayList<>();
        
        try {
            List<String> roles = new ArrayList<>();
            List<UserRole> userRoles = database.userRoleDao().getByUserId(userId);
            
            for (UserRole ur : userRoles) {
                Role role = database.roleDao().getById(ur.getRoleId());
                if (role != null) {
                    roles.add(role.getName());
                }
            }
            
            Log.d(TAG, "Loaded " + roles.size() + " roles for user " + userId);
            return roles;
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading roles for user " + userId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get permissions through user roles
     */
    private List<String> getRolePermissions(String userId) {
        try {
            List<String> permissions = new ArrayList<>();
            List<UserRole> userRoles = database.userRoleDao().getByUserId(userId);
            
            for (UserRole userRole : userRoles) {
                String roleId = userRole.getRoleId();
                
                // Check cache first
                if (rolePermissionsCache.containsKey(roleId)) {
                    permissions.addAll(rolePermissionsCache.get(roleId));
                    continue;
                }
                
                // Load from database or use defaults
                List<String> rolePerms = getDefaultPermissionsForRole(roleId);
                rolePermissionsCache.put(roleId, rolePerms);
                permissions.addAll(rolePerms);
            }
            
            return permissions;
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting role permissions for user " + userId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get default permissions for a role
     */
    private List<String> getDefaultPermissionsForRole(String roleId) {
        List<String> permissions = new ArrayList<>();
        
        // هذا تنفيذ مبسط - يمكن جلب الصلاحيات من قاعدة البيانات
        if (ROLE_SUPER_ADMIN.equals(roleId) || ROLE_ADMIN.equals(roleId)) {
            permissions.addAll(Arrays.asList(
                PERMISSION_ACCESS_AI_CHAT,
                PERMISSION_MANAGE_USERS,
                PERMISSION_VIEW_REPORTS,
                PERMISSION_MANAGE_ACCOUNTS
            ));
        } else if (ROLE_MANAGER.equals(roleId)) {
            permissions.addAll(Arrays.asList(
                PERMISSION_VIEW_REPORTS,
                PERMISSION_MANAGE_ACCOUNTS
            ));
        } else if (ROLE_ACCOUNTANT.equals(roleId)) {
            permissions.addAll(Arrays.asList(
                PERMISSION_VIEW_REPORTS
            ));
        }
        
        return permissions;
    }
    
    /**
     * Initialize default roles and permissions
     */
    private void initializeDefaultData() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Check if data already exists
                List<Role> existingRoles = database.roleDao().getAll();
                if (!existingRoles.isEmpty()) {
                    Log.d(TAG, "Default data already exists");
                    return;
                }
                
                // Create default roles
                database.roleDao().insert(new Role(ROLE_SUPER_ADMIN, "مدير النظام", "صلاحيات كاملة", System.currentTimeMillis()));
                database.roleDao().insert(new Role(ROLE_ADMIN, "مدير", "صلاحيات إدارية", System.currentTimeMillis()));
                database.roleDao().insert(new Role(ROLE_MANAGER, "مدير قسم", "صلاحيات إدارة القسم", System.currentTimeMillis()));
                database.roleDao().insert(new Role(ROLE_ACCOUNTANT, "محاسب", "صلاحيات محاسبية", System.currentTimeMillis()));
                database.roleDao().insert(new Role(ROLE_CASHIER, "كاشير", "صلاحيات الكاشير", System.currentTimeMillis()));
                database.roleDao().insert(new Role(ROLE_VIEWER, "مراقب", "صلاحيات المشاهدة فقط", System.currentTimeMillis()));
                database.roleDao().insert(new Role(ROLE_USER, "مستخدم عادي", "صلاحيات أساسية", System.currentTimeMillis()));
                
                Log.d(TAG, "Default roles initialized successfully");
                
            } catch (Exception e) {
                Log.e(TAG, "Error initializing default data", e);
            }
        });
    }
    
    /**
     * Clear all permission caches
     */
    public void clearCache() {
        rolePermissionsCache.clear();
        userPermissionsCache.clear();
        Log.d(TAG, "Permission cache cleared");
    }
}
