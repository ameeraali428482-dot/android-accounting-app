package com.example.androidapp.utils;

import android.content.Context;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Permission;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.UserPermission;
import com.example.androidapp.data.entities.UserRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Advanced Permission Management System
 * Handles role-based access control (RBAC) with granular permissions
 */
public class PermissionManager {
    
    // Permission Categories
    public static final String CATEGORY_FINANCIAL = "financial";
    public static final String CATEGORY_INVENTORY = "inventory";
    public static final String CATEGORY_USERS = "users";
    public static final String CATEGORY_REPORTS = "reports";
    public static final String CATEGORY_SYSTEM = "system";
    public static final String CATEGORY_COMMUNICATION = "communication";
    
    // Financial Permissions
    public static final String PERMISSION_VIEW_INVOICES = "view_invoices";
    public static final String PERMISSION_CREATE_INVOICES = "create_invoices";
    public static final String PERMISSION_EDIT_INVOICES = "edit_invoices";
    public static final String PERMISSION_DELETE_INVOICES = "delete_invoices";
    public static final String PERMISSION_APPROVE_INVOICES = "approve_invoices";
    public static final String PERMISSION_VIEW_ACCOUNT_STATEMENTS = "view_account_statements";
    public static final String PERMISSION_EDIT_ACCOUNT_STATEMENTS = "edit_account_statements";
    public static final String PERMISSION_RECONCILE_ACCOUNTS = "reconcile_accounts";
    public static final String PERMISSION_VIEW_FINANCIAL_REPORTS = "view_financial_reports";
    public static final String PERMISSION_EXPORT_FINANCIAL_DATA = "export_financial_data";
    
    // Inventory Permissions
    public static final String PERMISSION_VIEW_ITEMS = "view_items";
    public static final String PERMISSION_CREATE_ITEMS = "create_items";
    public static final String PERMISSION_EDIT_ITEMS = "edit_items";
    public static final String PERMISSION_DELETE_ITEMS = "delete_items";
    public static final String PERMISSION_MANAGE_STOCK = "manage_stock";
    public static final String PERMISSION_VIEW_STOCK_REPORTS = "view_stock_reports";
    public static final String PERMISSION_IMPORT_ITEMS = "import_items";
    public static final String PERMISSION_EXPORT_ITEMS = "export_items";
    
    // User Management Permissions
    public static final String PERMISSION_VIEW_USERS = "view_users";
    public static final String PERMISSION_CREATE_USERS = "create_users";
    public static final String PERMISSION_EDIT_USERS = "edit_users";
    public static final String PERMISSION_DELETE_USERS = "delete_users";
    public static final String PERMISSION_MANAGE_ROLES = "manage_roles";
    public static final String PERMISSION_ASSIGN_PERMISSIONS = "assign_permissions";
    public static final String PERMISSION_VIEW_USER_ACTIVITY = "view_user_activity";
    public static final String PERMISSION_RESET_PASSWORDS = "reset_passwords";
    
    // System Permissions
    public static final String PERMISSION_SYSTEM_ADMIN = "system_admin";
    public static final String PERMISSION_BACKUP_DATA = "backup_data";
    public static final String PERMISSION_RESTORE_DATA = "restore_data";
    public static final String PERMISSION_VIEW_SYSTEM_LOGS = "view_system_logs";
    public static final String PERMISSION_MANAGE_SETTINGS = "manage_settings";
    public static final String PERMISSION_MANAGE_INTEGRATIONS = "manage_integrations";
    
    // Communication Permissions
    public static final String PERMISSION_SEND_NOTIFICATIONS = "send_notifications";
    public static final String PERMISSION_MANAGE_CHAT = "manage_chat";
    public static final String PERMISSION_VIEW_ALL_MESSAGES = "view_all_messages";
    public static final String PERMISSION_MODERATE_CONTENT = "moderate_content";
    
    // Report Permissions
    public static final String PERMISSION_VIEW_ALL_REPORTS = "view_all_reports";
    public static final String PERMISSION_CREATE_CUSTOM_REPORTS = "create_custom_reports";
    public static final String PERMISSION_SCHEDULE_REPORTS = "schedule_reports";
    public static final String PERMISSION_SHARE_REPORTS = "share_reports";
    
    // Predefined Roles
    public static final String ROLE_SUPER_ADMIN = "super_admin";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_MANAGER = "manager";
    public static final String ROLE_ACCOUNTANT = "accountant";
    public static final String ROLE_INVENTORY_MANAGER = "inventory_manager";
    public static final String ROLE_SALES_REPRESENTATIVE = "sales_representative";
    public static final String ROLE_VIEWER = "viewer";
    public static final String ROLE_GUEST = "guest";
    
    private AppDatabase database;
    private SessionManager sessionManager;
    private Context context;
    
    // Cache for user permissions
    private Map<Integer, List<String>> userPermissionsCache = new HashMap<>();
    private Map<Integer, List<String>> userRolesCache = new HashMap<>();
    
    public PermissionManager(Context context) {
        this.context = context;
        this.database = AppDatabase.getDatabase(context);
        this.sessionManager = new SessionManager(context);
    }
    
    /**
     * Initialize default permissions and roles
     */
    public void initializeDefaultPermissions() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Create default permissions
            createDefaultPermissions();
            
            // Create default roles
            createDefaultRoles();
            
            // Assign permissions to roles
            assignPermissionsToRoles();
        });
    }
    
    private void createDefaultPermissions() {
        List<Permission> permissions = new ArrayList<>();
        
        // Financial permissions
        permissions.add(new Permission(PERMISSION_VIEW_INVOICES, "عرض الفواتير", CATEGORY_FINANCIAL, "إمكانية عرض جميع الفواتير"));
        permissions.add(new Permission(PERMISSION_CREATE_INVOICES, "إنشاء الفواتير", CATEGORY_FINANCIAL, "إمكانية إنشاء فواتير جديدة"));
        permissions.add(new Permission(PERMISSION_EDIT_INVOICES, "تعديل الفواتير", CATEGORY_FINANCIAL, "إمكانية تعديل الفواتير الموجودة"));
        permissions.add(new Permission(PERMISSION_DELETE_INVOICES, "حذف الفواتير", CATEGORY_FINANCIAL, "إمكانية حذف الفواتير"));
        permissions.add(new Permission(PERMISSION_APPROVE_INVOICES, "اعتماد الفواتير", CATEGORY_FINANCIAL, "إمكانية اعتماد الفواتير"));
        permissions.add(new Permission(PERMISSION_VIEW_ACCOUNT_STATEMENTS, "عرض كشوفات الحساب", CATEGORY_FINANCIAL, "إمكانية عرض كشوفات الحساب"));
        permissions.add(new Permission(PERMISSION_EDIT_ACCOUNT_STATEMENTS, "تعديل كشوفات الحساب", CATEGORY_FINANCIAL, "إمكانية تعديل كشوفات الحساب"));
        permissions.add(new Permission(PERMISSION_RECONCILE_ACCOUNTS, "مراجعة الحسابات", CATEGORY_FINANCIAL, "إمكانية مراجعة ومطابقة الحسابات"));
        permissions.add(new Permission(PERMISSION_VIEW_FINANCIAL_REPORTS, "عرض التقارير المالية", CATEGORY_FINANCIAL, "إمكانية عرض التقارير المالية"));
        permissions.add(new Permission(PERMISSION_EXPORT_FINANCIAL_DATA, "تصدير البيانات المالية", CATEGORY_FINANCIAL, "إمكانية تصدير البيانات المالية"));
        
        // Inventory permissions
        permissions.add(new Permission(PERMISSION_VIEW_ITEMS, "عرض الأصناف", CATEGORY_INVENTORY, "إمكانية عرض جميع الأصناف"));
        permissions.add(new Permission(PERMISSION_CREATE_ITEMS, "إنشاء الأصناف", CATEGORY_INVENTORY, "إمكانية إنشاء أصناف جديدة"));
        permissions.add(new Permission(PERMISSION_EDIT_ITEMS, "تعديل الأصناف", CATEGORY_INVENTORY, "إمكانية تعديل الأصناف الموجودة"));
        permissions.add(new Permission(PERMISSION_DELETE_ITEMS, "حذف الأصناف", CATEGORY_INVENTORY, "إمكانية حذف الأصناف"));
        permissions.add(new Permission(PERMISSION_MANAGE_STOCK, "إدارة المخزون", CATEGORY_INVENTORY, "إمكانية إدارة كميات المخزون"));
        permissions.add(new Permission(PERMISSION_VIEW_STOCK_REPORTS, "عرض تقارير المخزون", CATEGORY_INVENTORY, "إمكانية عرض تقارير المخزون"));
        permissions.add(new Permission(PERMISSION_IMPORT_ITEMS, "استيراد الأصناف", CATEGORY_INVENTORY, "إمكانية استيراد الأصناف من ملفات خارجية"));
        permissions.add(new Permission(PERMISSION_EXPORT_ITEMS, "تصدير الأصناف", CATEGORY_INVENTORY, "إمكانية تصدير الأصناف إلى ملفات"));
        
        // User management permissions
        permissions.add(new Permission(PERMISSION_VIEW_USERS, "عرض المستخدمين", CATEGORY_USERS, "إمكانية عرض جميع المستخدمين"));
        permissions.add(new Permission(PERMISSION_CREATE_USERS, "إنشاء المستخدمين", CATEGORY_USERS, "إمكانية إنشاء مستخدمين جدد"));
        permissions.add(new Permission(PERMISSION_EDIT_USERS, "تعديل المستخدمين", CATEGORY_USERS, "إمكانية تعديل بيانات المستخدمين"));
        permissions.add(new Permission(PERMISSION_DELETE_USERS, "حذف المستخدمين", CATEGORY_USERS, "إمكانية حذف المستخدمين"));
        permissions.add(new Permission(PERMISSION_MANAGE_ROLES, "إدارة الأدوار", CATEGORY_USERS, "إمكانية إدارة أدوار المستخدمين"));
        permissions.add(new Permission(PERMISSION_ASSIGN_PERMISSIONS, "تعيين الصلاحيات", CATEGORY_USERS, "إمكانية تعيين الصلاحيات للمستخدمين"));
        permissions.add(new Permission(PERMISSION_VIEW_USER_ACTIVITY, "عرض نشاط المستخدمين", CATEGORY_USERS, "إمكانية عرض سجل نشاط المستخدمين"));
        permissions.add(new Permission(PERMISSION_RESET_PASSWORDS, "إعادة تعيين كلمات المرور", CATEGORY_USERS, "إمكانية إعادة تعيين كلمات مرور المستخدمين"));
        
        // System permissions
        permissions.add(new Permission(PERMISSION_SYSTEM_ADMIN, "مدير النظام", CATEGORY_SYSTEM, "صلاحيات مدير النظام الكاملة"));
        permissions.add(new Permission(PERMISSION_BACKUP_DATA, "نسخ احتياطي للبيانات", CATEGORY_SYSTEM, "إمكانية إنشاء نسخ احتياطية"));
        permissions.add(new Permission(PERMISSION_RESTORE_DATA, "استعادة البيانات", CATEGORY_SYSTEM, "إمكانية استعادة البيانات من النسخ الاحتياطية"));
        permissions.add(new Permission(PERMISSION_VIEW_SYSTEM_LOGS, "عرض سجلات النظام", CATEGORY_SYSTEM, "إمكانية عرض سجلات النظام"));
        permissions.add(new Permission(PERMISSION_MANAGE_SETTINGS, "إدارة الإعدادات", CATEGORY_SYSTEM, "إمكانية إدارة إعدادات النظام"));
        permissions.add(new Permission(PERMISSION_MANAGE_INTEGRATIONS, "إدارة التكاملات", CATEGORY_SYSTEM, "إمكانية إدارة التكاملات الخارجية"));
        
        // Communication permissions
        permissions.add(new Permission(PERMISSION_SEND_NOTIFICATIONS, "إرسال الإشعارات", CATEGORY_COMMUNICATION, "إمكانية إرسال الإشعارات"));
        permissions.add(new Permission(PERMISSION_MANAGE_CHAT, "إدارة الدردشة", CATEGORY_COMMUNICATION, "إمكانية إدارة نظام الدردشة"));
        permissions.add(new Permission(PERMISSION_VIEW_ALL_MESSAGES, "عرض جميع الرسائل", CATEGORY_COMMUNICATION, "إمكانية عرض جميع الرسائل"));
        permissions.add(new Permission(PERMISSION_MODERATE_CONTENT, "إدارة المحتوى", CATEGORY_COMMUNICATION, "إمكانية إدارة ومراقبة المحتوى"));
        
        // Report permissions
        permissions.add(new Permission(PERMISSION_VIEW_ALL_REPORTS, "عرض جميع التقارير", CATEGORY_REPORTS, "إمكانية عرض جميع التقارير"));
        permissions.add(new Permission(PERMISSION_CREATE_CUSTOM_REPORTS, "إنشاء تقارير مخصصة", CATEGORY_REPORTS, "إمكانية إنشاء تقارير مخصصة"));
        permissions.add(new Permission(PERMISSION_SCHEDULE_REPORTS, "جدولة التقارير", CATEGORY_REPORTS, "إمكانية جدولة التقارير التلقائية"));
        permissions.add(new Permission(PERMISSION_SHARE_REPORTS, "مشاركة التقارير", CATEGORY_REPORTS, "إمكانية مشاركة التقارير"));
        
        // Insert permissions into database
        for (Permission permission : permissions) {
            database.permissionDao().insert(permission);
        }
    }
    
    private void createDefaultRoles() {
        List<Role> roles = new ArrayList<>();
        
        roles.add(new Role(ROLE_SUPER_ADMIN, "مدير عام", "مدير عام للنظام مع جميع الصلاحيات"));
        roles.add(new Role(ROLE_ADMIN, "مدير", "مدير مع صلاحيات إدارية واسعة"));
        roles.add(new Role(ROLE_MANAGER, "مدير قسم", "مدير قسم مع صلاحيات محدودة"));
        roles.add(new Role(ROLE_ACCOUNTANT, "محاسب", "محاسب مع صلاحيات مالية"));
        roles.add(new Role(ROLE_INVENTORY_MANAGER, "مدير مخزون", "مدير مخزون مع صلاحيات المخزون"));
        roles.add(new Role(ROLE_SALES_REPRESENTATIVE, "مندوب مبيعات", "مندوب مبيعات مع صلاحيات محدودة"));
        roles.add(new Role(ROLE_VIEWER, "مشاهد", "مستخدم مع صلاحيات عرض فقط"));
        roles.add(new Role(ROLE_GUEST, "ضيف", "ضيف مع صلاحيات محدودة جداً"));
        
        // Insert roles into database
        for (Role role : roles) {
            database.roleDao().insert(role);
        }
    }
    
    private void assignPermissionsToRoles() {
        // Super Admin - All permissions
        assignAllPermissionsToRole(ROLE_SUPER_ADMIN);
        
        // Admin - Most permissions except system admin
        List<String> adminPermissions = getAllPermissionsExcept(PERMISSION_SYSTEM_ADMIN, PERMISSION_BACKUP_DATA, PERMISSION_RESTORE_DATA);
        assignPermissionsToRole(ROLE_ADMIN, adminPermissions);
        
        // Manager - Management permissions
        List<String> managerPermissions = List.of(
            PERMISSION_VIEW_INVOICES, PERMISSION_CREATE_INVOICES, PERMISSION_EDIT_INVOICES,
            PERMISSION_VIEW_ACCOUNT_STATEMENTS, PERMISSION_VIEW_FINANCIAL_REPORTS,
            PERMISSION_VIEW_ITEMS, PERMISSION_CREATE_ITEMS, PERMISSION_EDIT_ITEMS,
            PERMISSION_MANAGE_STOCK, PERMISSION_VIEW_STOCK_REPORTS,
            PERMISSION_VIEW_USERS, PERMISSION_CREATE_USERS, PERMISSION_EDIT_USERS,
            PERMISSION_VIEW_ALL_REPORTS, PERMISSION_SEND_NOTIFICATIONS
        );
        assignPermissionsToRole(ROLE_MANAGER, managerPermissions);
        
        // Accountant - Financial permissions
        List<String> accountantPermissions = List.of(
            PERMISSION_VIEW_INVOICES, PERMISSION_CREATE_INVOICES, PERMISSION_EDIT_INVOICES,
            PERMISSION_APPROVE_INVOICES, PERMISSION_VIEW_ACCOUNT_STATEMENTS, PERMISSION_EDIT_ACCOUNT_STATEMENTS,
            PERMISSION_RECONCILE_ACCOUNTS, PERMISSION_VIEW_FINANCIAL_REPORTS, PERMISSION_EXPORT_FINANCIAL_DATA
        );
        assignPermissionsToRole(ROLE_ACCOUNTANT, accountantPermissions);
        
        // Inventory Manager - Inventory permissions
        List<String> inventoryPermissions = List.of(
            PERMISSION_VIEW_ITEMS, PERMISSION_CREATE_ITEMS, PERMISSION_EDIT_ITEMS, PERMISSION_DELETE_ITEMS,
            PERMISSION_MANAGE_STOCK, PERMISSION_VIEW_STOCK_REPORTS, PERMISSION_IMPORT_ITEMS, PERMISSION_EXPORT_ITEMS
        );
        assignPermissionsToRole(ROLE_INVENTORY_MANAGER, inventoryPermissions);
        
        // Sales Representative - Limited permissions
        List<String> salesPermissions = List.of(
            PERMISSION_VIEW_INVOICES, PERMISSION_CREATE_INVOICES,
            PERMISSION_VIEW_ITEMS, PERMISSION_VIEW_STOCK_REPORTS
        );
        assignPermissionsToRole(ROLE_SALES_REPRESENTATIVE, salesPermissions);
        
        // Viewer - View only permissions
        List<String> viewerPermissions = List.of(
            PERMISSION_VIEW_INVOICES, PERMISSION_VIEW_ACCOUNT_STATEMENTS,
            PERMISSION_VIEW_ITEMS, PERMISSION_VIEW_STOCK_REPORTS,
            PERMISSION_VIEW_FINANCIAL_REPORTS, PERMISSION_VIEW_ALL_REPORTS
        );
        assignPermissionsToRole(ROLE_VIEWER, viewerPermissions);
        
        // Guest - Very limited permissions
        List<String> guestPermissions = List.of(
            PERMISSION_VIEW_ITEMS
        );
        assignPermissionsToRole(ROLE_GUEST, guestPermissions);
    }
    
    private void assignAllPermissionsToRole(String roleName) {
        // This would assign all permissions to the specified role
        // Implementation would involve getting all permissions and creating role-permission associations
    }
    
    private List<String> getAllPermissionsExcept(String... excludedPermissions) {
        // This would return all permissions except the specified ones
        // Implementation would involve querying all permissions and filtering out excluded ones
        return new ArrayList<>();
    }
    
    private void assignPermissionsToRole(String roleName, List<String> permissions) {
        // This would assign the specified permissions to the role
        // Implementation would involve creating role-permission associations in the database
    }
    
    /**
     * Check if current user has a specific permission
     */
    public CompletableFuture<Boolean> hasPermission(String permission) {
        int userId = sessionManager.getCurrentUserId();
        return hasPermission(userId, permission);
    }
    
    /**
     * Check if a specific user has a permission
     */
    public CompletableFuture<Boolean> hasPermission(int userId, String permission) {
        return CompletableFuture.supplyAsync(() -> {
            // Check cache first
            List<String> cachedPermissions = userPermissionsCache.get(userId);
            if (cachedPermissions != null) {
                return cachedPermissions.contains(permission);
            }
            
            // Load permissions from database
            List<String> userPermissions = loadUserPermissions(userId);
            userPermissionsCache.put(userId, userPermissions);
            
            return userPermissions.contains(permission);
        }, AppDatabase.databaseWriteExecutor);
    }
    
    /**
     * Check if current user has any of the specified permissions
     */
    public CompletableFuture<Boolean> hasAnyPermission(String... permissions) {
        int userId = sessionManager.getCurrentUserId();
        return hasAnyPermission(userId, permissions);
    }
    
    /**
     * Check if a specific user has any of the specified permissions
     */
    public CompletableFuture<Boolean> hasAnyPermission(int userId, String... permissions) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> userPermissions = getUserPermissions(userId);
            for (String permission : permissions) {
                if (userPermissions.contains(permission)) {
                    return true;
                }
            }
            return false;
        }, AppDatabase.databaseWriteExecutor);
    }
    
    /**
     * Check if current user has all of the specified permissions
     */
    public CompletableFuture<Boolean> hasAllPermissions(String... permissions) {
        int userId = sessionManager.getCurrentUserId();
        return hasAllPermissions(userId, permissions);
    }
    
    /**
     * Check if a specific user has all of the specified permissions
     */
    public CompletableFuture<Boolean> hasAllPermissions(int userId, String... permissions) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> userPermissions = getUserPermissions(userId);
            for (String permission : permissions) {
                if (!userPermissions.contains(permission)) {
                    return false;
                }
            }
            return true;
        }, AppDatabase.databaseWriteExecutor);
    }
    
    /**
     * Get all permissions for a user
     */
    public List<String> getUserPermissions(int userId) {
        List<String> cachedPermissions = userPermissionsCache.get(userId);
        if (cachedPermissions != null) {
            return cachedPermissions;
        }
        
        List<String> permissions = loadUserPermissions(userId);
        userPermissionsCache.put(userId, permissions);
        return permissions;
    }
    
    private List<String> loadUserPermissions(int userId) {
        List<String> permissions = new ArrayList<>();
        
        // Get direct user permissions
        List<UserPermission> userPermissions = database.userPermissionDao().getUserPermissions(userId);
        for (UserPermission up : userPermissions) {
            Permission permission = database.permissionDao().getPermissionById(up.getPermissionId());
            if (permission != null) {
                permissions.add(permission.getName());
            }
        }
        
        // Get permissions from user roles
        List<UserRole> userRoles = database.userRoleDao().getUserRoles(userId);
        for (UserRole ur : userRoles) {
            List<Permission> rolePermissions = database.permissionDao().getPermissionsByRoleId(ur.getRoleId());
            for (Permission permission : rolePermissions) {
                if (!permissions.contains(permission.getName())) {
                    permissions.add(permission.getName());
                }
            }
        }
        
        return permissions;
    }
    
    /**
     * Assign a role to a user
     */
    public void assignRoleToUser(int userId, int roleId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            database.userRoleDao().insert(userRole);
            
            // Clear cache
            userPermissionsCache.remove(userId);
            userRolesCache.remove(userId);
        });
    }
    
    /**
     * Remove a role from a user
     */
    public void removeRoleFromUser(int userId, int roleId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.userRoleDao().deleteUserRole(userId, roleId);
            
            // Clear cache
            userPermissionsCache.remove(userId);
            userRolesCache.remove(userId);
        });
    }
    
    /**
     * Assign a permission directly to a user
     */
    public void assignPermissionToUser(int userId, int permissionId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserPermission userPermission = new UserPermission();
            userPermission.setUserId(userId);
            userPermission.setPermissionId(permissionId);
            database.userPermissionDao().insert(userPermission);
            
            // Clear cache
            userPermissionsCache.remove(userId);
        });
    }
    
    /**
     * Remove a permission directly from a user
     */
    public void removePermissionFromUser(int userId, int permissionId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.userPermissionDao().deleteUserPermission(userId, permissionId);
            
            // Clear cache
            userPermissionsCache.remove(userId);
        });
    }
    
    /**
     * Clear permission cache for a user
     */
    public void clearUserCache(int userId) {
        userPermissionsCache.remove(userId);
        userRolesCache.remove(userId);
    }
    
    /**
     * Clear all permission caches
     */
    public void clearAllCaches() {
        userPermissionsCache.clear();
        userRolesCache.clear();
    }
    
    /**
     * Check if current user is super admin
     */
    public boolean isSuperAdmin() {
        int userId = sessionManager.getCurrentUserId();
        return isSuperAdmin(userId);
    }
    
    /**
     * Check if a specific user is super admin
     */
    public boolean isSuperAdmin(int userId) {
        List<String> userRoles = getUserRoles(userId);
        return userRoles.contains(ROLE_SUPER_ADMIN);
    }
    
    /**
     * Get all roles for a user
     */
    public List<String> getUserRoles(int userId) {
        List<String> cachedRoles = userRolesCache.get(userId);
        if (cachedRoles != null) {
            return cachedRoles;
        }
        
        List<String> roles = new ArrayList<>();
        List<UserRole> userRoles = database.userRoleDao().getUserRoles(userId);
        for (UserRole ur : userRoles) {
            Role role = database.roleDao().getRoleById(ur.getRoleId());
            if (role != null) {
                roles.add(role.getName());
            }
        }
        
        userRolesCache.put(userId, roles);
        return roles;
    }
}
