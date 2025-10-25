package com.example.androidapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.app.Activity;
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
import java.util.stream.Collectors;

public class PermissionManager {
    
    private static final String TAG = "PermissionManager";
    
    // Predefined Roles
    public static final String ROLE_SUPER_ADMIN = "super_admin";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_MANAGER = "manager";
    public static final String ROLE_ACCOUNTANT = "accountant";
    public static final String ROLE_CASHIER = "cashier";
    public static final String ROLE_VIEWER = "viewer";
    public static final String ROLE_USER = "user";
    
    // Predefined Permissions
    public static final String PERM_VIEW_ACCOUNTS = "view_accounts";
    public static final String PERM_EDIT_ACCOUNTS = "edit_accounts";
    public static final String PERM_DELETE_ACCOUNTS = "delete_accounts";
    public static final String PERM_VIEW_TRANSACTIONS = "view_transactions";
    public static final String PERM_EDIT_TRANSACTIONS = "edit_transactions";
    public static final String PERM_DELETE_TRANSACTIONS = "delete_transactions";
    public static final String PERM_VIEW_INVOICES = "view_invoices";
    public static final String PERM_EDIT_INVOICES = "edit_invoices";
    public static final String PERM_DELETE_INVOICES = "delete_invoices";
    public static final String PERM_VIEW_REPORTS = "view_reports";
    public static final String PERM_GENERATE_REPORTS = "generate_reports";
    public static final String PERM_MANAGE_USERS = "manage_users";
    public static final String PERM_MANAGE_COMPANIES = "manage_companies";
    public static final String PERM_BACKUP_RESTORE = "backup_restore";
    public static final String PERM_SYSTEM_SETTINGS = "system_settings";
    public static final String PERM_VIEW_AUDIT_LOG = "view_audit_log";
    public static final String PERM_EXPORT_DATA = "export_data";
    public static final String PERM_IMPORT_DATA = "import_data";
    
    // Android System Permissions
    public static final String[] ANDROID_PERMISSIONS = {
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.CALL_PHONE,
        android.Manifest.permission.SEND_SMS,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.POST_NOTIFICATIONS
    };
    
    private AppDatabase database;
    private SessionManager sessionManager;
    private Context context;
    private Map<String, List<String>> rolePermissionsCache;
    private Map<String, List<String>> userPermissionsCache;
    
    public PermissionManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getDatabase(context);
        this.sessionManager = new SessionManager(context);
        this.rolePermissionsCache = new HashMap<>();
        this.userPermissionsCache = new HashMap<>();
        initializeDefaultData();
    }
    
    // ============== App Permission Management ==============
    
    /**
     * Check if current user has a specific permission
     */
    public CompletableFuture<Boolean> hasPermission(String permission) {
        String userId = sessionManager.getCurrentUserId();
        return hasPermission(userId, permission);
    }
    
    /**
     * Check if specific user has a permission
     */
    public CompletableFuture<Boolean> hasPermission(String userId, String permission) {
        return CompletableFuture.supplyAsync(() -> {
            if (userId == null) {
                Log.w(TAG, "User ID is null for permission check: " + permission);
                return false;
            }
            
            try {
                // Check if user is super admin (has all permissions)
                if (isSuperAdmin(userId)) {
                    Log.d(TAG, "User " + userId + " is super admin - granting permission: " + permission);
                    return true;
                }
                
                List<String> userPermissions = loadUserPermissions(userId);
                boolean hasPermission = userPermissions.contains(permission);
                
                Log.d(TAG, "Permission check for user " + userId + ", permission " + permission + ": " + hasPermission);
                return hasPermission;
                
            } catch (Exception e) {
                Log.e(TAG, "Error checking permission " + permission + " for user " + userId, e);
                return false;
            }
        }, AppDatabase.databaseWriteExecutor);
    }

    /**
     * Check if user has any of the specified permissions
     */
    public CompletableFuture<Boolean> hasAnyPermission(String[] permissions) {
        return CompletableFuture.supplyAsync(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId == null) return false;
            
            if (isSuperAdmin(userId)) return true;
            
            List<String> userPermissions = loadUserPermissions(userId);
            for (String permission : permissions) {
                if (userPermissions.contains(permission)) {
                    return true;
                }
            }
            return false;
        }, AppDatabase.databaseWriteExecutor);
    }

    /**
     * Check if user has all specified permissions
     */
    public CompletableFuture<Boolean> hasAllPermissions(String[] permissions) {
        return CompletableFuture.supplyAsync(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId == null) return false;
            
            if (isSuperAdmin(userId)) return true;
            
            List<String> userPermissions = loadUserPermissions(userId);
            return userPermissions.containsAll(Arrays.asList(permissions));
        }, AppDatabase.databaseWriteExecutor);
    }

    /**
     * Check if current user is super admin
     */
    public boolean isSuperAdmin() {
        String userId = sessionManager.getCurrentUserId();
        return isSuperAdmin(userId);
    }
    
    /**
     * Check if specific user is super admin
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
     * Get all permissions for current user
     */
    public CompletableFuture<List<String>> getUserPermissions() {
        return CompletableFuture.supplyAsync(() -> {
            String userId = sessionManager.getCurrentUserId();
            return loadUserPermissions(userId);
        }, AppDatabase.databaseWriteExecutor);
    }
    
    /**
     * Get all roles for current user
     */
    public CompletableFuture<List<String>> getUserRoles() {
        return CompletableFuture.supplyAsync(() -> {
            String userId = sessionManager.getCurrentUserId();
            return loadUserRoles(userId);
        }, AppDatabase.databaseWriteExecutor);
    }

    // ============== Android System Permission Management ==============
    
    /**
     * Check if Android system permission is granted
     */
    public boolean hasSystemPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Permissions are granted at install time for older versions
    }
    
    /**
     * Check multiple system permissions
     */
    public Map<String, Boolean> checkSystemPermissions(String[] permissions) {
        Map<String, Boolean> result = new HashMap<>();
        for (String permission : permissions) {
            result.put(permission, hasSystemPermission(permission));
        }
        return result;
    }
    
    /**
     * Request system permission (requires Activity context)
     */
    public void requestSystemPermission(Activity activity, String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }
    
    /**
     * Request multiple system permissions
     */
    public void requestSystemPermissions(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }
    
    /**
     * Check if permission should show rationale
     */
    public boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        }
        return false;
    }

    // ============== Permission Assignment ==============
    
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
    
    /**
     * Remove role from user
     */
    public CompletableFuture<Boolean> removeRoleFromUser(String userId, String roleId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                database.userRoleDao().deleteByUserAndRole(userId, roleId);
                
                // Clear cache for this user
                userPermissionsCache.remove(userId);
                
                Log.d(TAG, "Removed role " + roleId + " from user " + userId);
                return true;
                
            } catch (Exception e) {
                Log.e(TAG, "Error removing role " + roleId + " from user " + userId, e);
                return false;
            }
        }, AppDatabase.databaseWriteExecutor);
    }

    // ============== Private Helper Methods ==============
    
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
            
            // Get direct permissions
            List<UserPermission> userPermissions = database.userPermissionDao().getByUserId(userId);
            for (UserPermission up : userPermissions) {
                Permission permission = database.permissionDao().getById(up.getPermissionId());
                if (permission != null) {
                    permissions.add(permission.getName());
                }
            }
            
            // Get role-based permissions
            List<String> rolePermissions = getRolePermissions(userId);
            permissions.addAll(rolePermissions);
            
            // Remove duplicates
            permissions = permissions.stream().distinct().collect(Collectors.toList());
            
            // Cache the result
            userPermissionsCache.put(userId, permissions);
            
            Log.d(TAG, "Loaded " + permissions.size() + " permissions for user " + userId);
            return permissions;
            
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
                
                // Load from database
                List<String> rolePerms = new ArrayList<>();
                // Note: You need to implement RolePermission entity and DAO
                // For now, we'll use hardcoded permissions based on role names
                Role role = database.roleDao().getById(roleId);
                if (role != null) {
                    rolePerms = getDefaultPermissionsForRole(role.getName());
                    rolePermissionsCache.put(roleId, rolePerms);
                }
                
                permissions.addAll(rolePerms);
            }
            
            return permissions.stream().distinct().collect(Collectors.toList());
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting role permissions for user " + userId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get default permissions for a role (hardcoded for now)
     */
    private List<String> getDefaultPermissionsForRole(String roleName) {
        List<String> permissions = new ArrayList<>();
        
        switch (roleName) {
            case ROLE_SUPER_ADMIN:
                // Super admin has all permissions
                permissions.addAll(Arrays.asList(
                    PERM_VIEW_ACCOUNTS, PERM_EDIT_ACCOUNTS, PERM_DELETE_ACCOUNTS,
                    PERM_VIEW_TRANSACTIONS, PERM_EDIT_TRANSACTIONS, PERM_DELETE_TRANSACTIONS,
                    PERM_VIEW_INVOICES, PERM_EDIT_INVOICES, PERM_DELETE_INVOICES,
                    PERM_VIEW_REPORTS, PERM_GENERATE_REPORTS,
                    PERM_MANAGE_USERS, PERM_MANAGE_COMPANIES,
                    PERM_BACKUP_RESTORE, PERM_SYSTEM_SETTINGS,
                    PERM_VIEW_AUDIT_LOG, PERM_EXPORT_DATA, PERM_IMPORT_DATA
                ));
                break;
                
            case ROLE_ADMIN:
                permissions.addAll(Arrays.asList(
                    PERM_VIEW_ACCOUNTS, PERM_EDIT_ACCOUNTS,
                    PERM_VIEW_TRANSACTIONS, PERM_EDIT_TRANSACTIONS,
                    PERM_VIEW_INVOICES, PERM_EDIT_INVOICES,
                    PERM_VIEW_REPORTS, PERM_GENERATE_REPORTS,
                    PERM_MANAGE_USERS, PERM_BACKUP_RESTORE,
                    PERM_VIEW_AUDIT_LOG, PERM_EXPORT_DATA
                ));
                break;
                
            case ROLE_MANAGER:
                permissions.addAll(Arrays.asList(
                    PERM_VIEW_ACCOUNTS, PERM_EDIT_ACCOUNTS,
                    PERM_VIEW_TRANSACTIONS, PERM_EDIT_TRANSACTIONS,
                    PERM_VIEW_INVOICES, PERM_EDIT_INVOICES,
                    PERM_VIEW_REPORTS, PERM_GENERATE_REPORTS,
                    PERM_EXPORT_DATA
                ));
                break;
                
            case ROLE_ACCOUNTANT:
                permissions.addAll(Arrays.asList(
                    PERM_VIEW_ACCOUNTS, PERM_EDIT_ACCOUNTS,
                    PERM_VIEW_TRANSACTIONS, PERM_EDIT_TRANSACTIONS,
                    PERM_VIEW_INVOICES, PERM_EDIT_INVOICES,
                    PERM_VIEW_REPORTS, PERM_EXPORT_DATA
                ));
                break;
                
            case ROLE_CASHIER:
                permissions.addAll(Arrays.asList(
                    PERM_VIEW_ACCOUNTS,
                    PERM_VIEW_TRANSACTIONS, PERM_EDIT_TRANSACTIONS,
                    PERM_VIEW_INVOICES, PERM_EDIT_INVOICES
                ));
                break;
                
            case ROLE_VIEWER:
                permissions.addAll(Arrays.asList(
                    PERM_VIEW_ACCOUNTS,
                    PERM_VIEW_TRANSACTIONS,
                    PERM_VIEW_INVOICES,
                    PERM_VIEW_REPORTS
                ));
                break;
                
            case ROLE_USER:
                permissions.addAll(Arrays.asList(
                    PERM_VIEW_ACCOUNTS,
                    PERM_VIEW_TRANSACTIONS,
                    PERM_VIEW_INVOICES
                ));
                break;
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
                
                // Create default permissions
                database.permissionDao().insert(new Permission(PERM_VIEW_ACCOUNTS, "عرض الحسابات", "صلاحية عرض الحسابات"));
                database.permissionDao().insert(new Permission(PERM_EDIT_ACCOUNTS, "تعديل الحسابات", "صلاحية تعديل الحسابات"));
                database.permissionDao().insert(new Permission(PERM_DELETE_ACCOUNTS, "حذف الحسابات", "صلاحية حذف الحسابات"));
                database.permissionDao().insert(new Permission(PERM_VIEW_TRANSACTIONS, "عرض المعاملات", "صلاحية عرض المعاملات"));
                database.permissionDao().insert(new Permission(PERM_EDIT_TRANSACTIONS, "تعديل المعاملات", "صلاحية تعديل المعاملات"));
                database.permissionDao().insert(new Permission(PERM_DELETE_TRANSACTIONS, "حذف المعاملات", "صلاحية حذف المعاملات"));
                database.permissionDao().insert(new Permission(PERM_VIEW_INVOICES, "عرض الفواتير", "صلاحية عرض الفواتير"));
                database.permissionDao().insert(new Permission(PERM_EDIT_INVOICES, "تعديل الفواتير", "صلاحية تعديل الفواتير"));
                database.permissionDao().insert(new Permission(PERM_DELETE_INVOICES, "حذف الفواتير", "صلاحية حذف الفواتير"));
                database.permissionDao().insert(new Permission(PERM_VIEW_REPORTS, "عرض التقارير", "صلاحية عرض التقارير"));
                database.permissionDao().insert(new Permission(PERM_GENERATE_REPORTS, "إنشاء التقارير", "صلاحية إنشاء التقارير"));
                database.permissionDao().insert(new Permission(PERM_MANAGE_USERS, "إدارة المستخدمين", "صلاحية إدارة المستخدمين"));
                database.permissionDao().insert(new Permission(PERM_MANAGE_COMPANIES, "إدارة الشركات", "صلاحية إدارة الشركات"));
                database.permissionDao().insert(new Permission(PERM_BACKUP_RESTORE, "النسخ الاحتياطي", "صلاحية النسخ الاحتياطي والاستعادة"));
                database.permissionDao().insert(new Permission(PERM_SYSTEM_SETTINGS, "إعدادات النظام", "صلاحية تعديل إعدادات النظام"));
                database.permissionDao().insert(new Permission(PERM_VIEW_AUDIT_LOG, "عرض سجل المراجعة", "صلاحية عرض سجل المراجعة"));
                database.permissionDao().insert(new Permission(PERM_EXPORT_DATA, "تصدير البيانات", "صلاحية تصدير البيانات"));
                database.permissionDao().insert(new Permission(PERM_IMPORT_DATA, "استيراد البيانات", "صلاحية استيراد البيانات"));
                
                Log.d(TAG, "Default roles and permissions initialized successfully");
                
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
    
    /**
     * Clear cache for specific user
     */
    public void clearUserCache(String userId) {
        userPermissionsCache.remove(userId);
        Log.d(TAG, "Permission cache cleared for user: " + userId);
    }
}
