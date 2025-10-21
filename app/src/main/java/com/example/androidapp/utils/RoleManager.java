package com.example.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.room.Room;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.AuditLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * مدير الأدوار والصلاحيات المتقدم - نظام شامل ومرن لإدارة أذونات المستخدمين
 * Advanced Role Manager - comprehensive and flexible user permissions management system
 */
public class RoleManager {
    
    private static final String TAG = "RoleManager";
    private static final String PREFS_NAME = "role_manager_prefs";
    private static final String USER_ROLES_KEY = "user_roles";
    private static final String CUSTOM_PERMISSIONS_KEY = "custom_permissions";
    private static final String ROLE_DEFINITIONS_KEY = "role_definitions";
    
    // الأدوار المُعرَّفة مسبقاً
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_ACCOUNTANT = "ACCOUNTANT";
    public static final String ROLE_CASHIER = "CASHIER";
    public static final String ROLE_AUDITOR = "AUDITOR";
    public static final String ROLE_VIEWER = "VIEWER";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_SUPPLIER = "SUPPLIER";
    
    // فئات الصلاحيات
    public static final String CATEGORY_ACCOUNTS = "ACCOUNTS";
    public static final String CATEGORY_TRANSACTIONS = "TRANSACTIONS";
    public static final String CATEGORY_REPORTS = "REPORTS";
    public static final String CATEGORY_USERS = "USERS";
    public static final String CATEGORY_SETTINGS = "SETTINGS";
    public static final String CATEGORY_BACKUP = "BACKUP";
    public static final String CATEGORY_AUDIT = "AUDIT";
    public static final String CATEGORY_INVENTORY = "INVENTORY";
    public static final String CATEGORY_INVOICES = "INVOICES";
    public static final String CATEGORY_CATEGORIES = "CATEGORIES";
    
    // العمليات الأساسية
    public static final String ACTION_CREATE = "CREATE";
    public static final String ACTION_READ = "READ";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_DELETE = "DELETE";
    public static final String ACTION_APPROVE = "APPROVE";
    public static final String ACTION_EXPORT = "EXPORT";
    public static final String ACTION_IMPORT = "IMPORT";
    public static final String ACTION_PRINT = "PRINT";
    public static final String ACTION_SHARE = "SHARE";
    public static final String ACTION_MANAGE = "MANAGE";
    
    // صلاحيات محددة
    public static final String PERMISSION_VIEW_ALL_ACCOUNTS = "VIEW_ALL_ACCOUNTS";
    public static final String PERMISSION_MODIFY_SYSTEM_SETTINGS = "MODIFY_SYSTEM_SETTINGS";
    public static final String PERMISSION_ACCESS_FINANCIAL_REPORTS = "ACCESS_FINANCIAL_REPORTS";
    public static final String PERMISSION_MANAGE_USERS = "MANAGE_USERS";
    public static final String PERMISSION_PERFORM_BACKUP = "PERFORM_BACKUP";
    public static final String PERMISSION_VIEW_AUDIT_LOGS = "VIEW_AUDIT_LOGS";
    public static final String PERMISSION_APPROVE_TRANSACTIONS = "APPROVE_TRANSACTIONS";
    public static final String PERMISSION_DELETE_TRANSACTIONS = "DELETE_TRANSACTIONS";
    public static final String PERMISSION_EXPORT_DATA = "EXPORT_DATA";
    public static final String PERMISSION_IMPORT_DATA = "IMPORT_DATA";
    public static final String PERMISSION_ACCESS_AI_CHAT = "ACCESS_AI_CHAT";
    public static final String PERMISSION_MANAGE_INVENTORY = "MANAGE_INVENTORY";
    public static final String PERMISSION_CREATE_INVOICES = "CREATE_INVOICES";
    public static final String PERMISSION_VIEW_SENSITIVE_DATA = "VIEW_SENSITIVE_DATA";
    
    private Context context;
    private SharedPreferences prefs;
    private AppDatabase database;
    private Gson gson;
    private ExecutorService executorService;
    
    // تخزين الأدوار والصلاحيات في الذاكرة للوصول السريع
    private Map<String, Set<String>> userRoles = new HashMap<>();
    private Map<String, Set<String>> rolePermissions = new HashMap<>();
    private Map<String, Set<String>> userCustomPermissions = new HashMap<>();
    
    public RoleManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.database = Room.databaseBuilder(context, AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration()
                .build();
        this.gson = new Gson();
        this.executorService = Executors.newCachedThreadPool();
        
        // تهيئة الأدوار والصلاحيات الافتراضية
        initializeDefaultRoles();
        loadUserRoles();
    }
    
    /**
     * تهيئة الأدوار والصلاحيات الافتراضية
     * Initialize default roles and permissions
     */
    private void initializeDefaultRoles() {
        
        // دور المدير العام
        Set<String> superAdminPermissions = new HashSet<>(Arrays.asList(
            PERMISSION_VIEW_ALL_ACCOUNTS,
            PERMISSION_MODIFY_SYSTEM_SETTINGS,
            PERMISSION_ACCESS_FINANCIAL_REPORTS,
            PERMISSION_MANAGE_USERS,
            PERMISSION_PERFORM_BACKUP,
            PERMISSION_VIEW_AUDIT_LOGS,
            PERMISSION_APPROVE_TRANSACTIONS,
            PERMISSION_DELETE_TRANSACTIONS,
            PERMISSION_EXPORT_DATA,
            PERMISSION_IMPORT_DATA,
            PERMISSION_ACCESS_AI_CHAT,
            PERMISSION_MANAGE_INVENTORY,
            PERMISSION_CREATE_INVOICES,
            PERMISSION_VIEW_SENSITIVE_DATA,
            // جميع العمليات على جميع الفئات
            createPermission(CATEGORY_ACCOUNTS, ACTION_CREATE),
            createPermission(CATEGORY_ACCOUNTS, ACTION_READ),
            createPermission(CATEGORY_ACCOUNTS, ACTION_UPDATE),
            createPermission(CATEGORY_ACCOUNTS, ACTION_DELETE),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_CREATE),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_READ),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_UPDATE),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_DELETE),
            createPermission(CATEGORY_REPORTS, ACTION_READ),
            createPermission(CATEGORY_REPORTS, ACTION_EXPORT),
            createPermission(CATEGORY_USERS, ACTION_MANAGE),
            createPermission(CATEGORY_SETTINGS, ACTION_MANAGE),
            createPermission(CATEGORY_BACKUP, ACTION_MANAGE),
            createPermission(CATEGORY_AUDIT, ACTION_READ),
            createPermission(CATEGORY_INVENTORY, ACTION_MANAGE),
            createPermission(CATEGORY_INVOICES, ACTION_CREATE),
            createPermission(CATEGORY_INVOICES, ACTION_READ),
            createPermission(CATEGORY_INVOICES, ACTION_UPDATE),
            createPermission(CATEGORY_INVOICES, ACTION_DELETE),
            createPermission(CATEGORY_CATEGORIES, ACTION_MANAGE)
        ));
        
        // دور المدير
        Set<String> adminPermissions = new HashSet<>(Arrays.asList(
            PERMISSION_VIEW_ALL_ACCOUNTS,
            PERMISSION_ACCESS_FINANCIAL_REPORTS,
            PERMISSION_PERFORM_BACKUP,
            PERMISSION_VIEW_AUDIT_LOGS,
            PERMISSION_APPROVE_TRANSACTIONS,
            PERMISSION_EXPORT_DATA,
            PERMISSION_ACCESS_AI_CHAT,
            PERMISSION_MANAGE_INVENTORY,
            PERMISSION_CREATE_INVOICES,
            createPermission(CATEGORY_ACCOUNTS, ACTION_CREATE),
            createPermission(CATEGORY_ACCOUNTS, ACTION_READ),
            createPermission(CATEGORY_ACCOUNTS, ACTION_UPDATE),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_CREATE),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_READ),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_UPDATE),
            createPermission(CATEGORY_REPORTS, ACTION_READ),
            createPermission(CATEGORY_REPORTS, ACTION_EXPORT),
            createPermission(CATEGORY_BACKUP, ACTION_CREATE),
            createPermission(CATEGORY_AUDIT, ACTION_READ),
            createPermission(CATEGORY_INVENTORY, ACTION_MANAGE),
            createPermission(CATEGORY_INVOICES, ACTION_CREATE),
            createPermission(CATEGORY_INVOICES, ACTION_READ),
            createPermission(CATEGORY_INVOICES, ACTION_UPDATE),
            createPermission(CATEGORY_CATEGORIES, ACTION_CREATE),
            createPermission(CATEGORY_CATEGORIES, ACTION_READ),
            createPermission(CATEGORY_CATEGORIES, ACTION_UPDATE)
        ));
        
        // دور المحاسب
        Set<String> accountantPermissions = new HashSet<>(Arrays.asList(
            PERMISSION_ACCESS_FINANCIAL_REPORTS,
            PERMISSION_EXPORT_DATA,
            PERMISSION_CREATE_INVOICES,
            createPermission(CATEGORY_ACCOUNTS, ACTION_CREATE),
            createPermission(CATEGORY_ACCOUNTS, ACTION_READ),
            createPermission(CATEGORY_ACCOUNTS, ACTION_UPDATE),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_CREATE),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_READ),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_UPDATE),
            createPermission(CATEGORY_REPORTS, ACTION_READ),
            createPermission(CATEGORY_REPORTS, ACTION_EXPORT),
            createPermission(CATEGORY_INVOICES, ACTION_CREATE),
            createPermission(CATEGORY_INVOICES, ACTION_READ),
            createPermission(CATEGORY_INVOICES, ACTION_UPDATE),
            createPermission(CATEGORY_CATEGORIES, ACTION_READ)
        ));
        
        // دور الصراف
        Set<String> cashierPermissions = new HashSet<>(Arrays.asList(
            PERMISSION_CREATE_INVOICES,
            createPermission(CATEGORY_TRANSACTIONS, ACTION_CREATE),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_READ),
            createPermission(CATEGORY_ACCOUNTS, ACTION_READ),
            createPermission(CATEGORY_INVOICES, ACTION_CREATE),
            createPermission(CATEGORY_INVOICES, ACTION_READ),
            createPermission(CATEGORY_INVENTORY, ACTION_READ),
            createPermission(CATEGORY_CATEGORIES, ACTION_READ)
        ));
        
        // دور المدقق
        Set<String> auditorPermissions = new HashSet<>(Arrays.asList(
            PERMISSION_VIEW_ALL_ACCOUNTS,
            PERMISSION_ACCESS_FINANCIAL_REPORTS,
            PERMISSION_VIEW_AUDIT_LOGS,
            PERMISSION_EXPORT_DATA,
            PERMISSION_VIEW_SENSITIVE_DATA,
            createPermission(CATEGORY_ACCOUNTS, ACTION_READ),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_READ),
            createPermission(CATEGORY_REPORTS, ACTION_READ),
            createPermission(CATEGORY_REPORTS, ACTION_EXPORT),
            createPermission(CATEGORY_AUDIT, ACTION_READ),
            createPermission(CATEGORY_INVOICES, ACTION_READ)
        ));
        
        // دور المشاهد
        Set<String> viewerPermissions = new HashSet<>(Arrays.asList(
            createPermission(CATEGORY_ACCOUNTS, ACTION_READ),
            createPermission(CATEGORY_TRANSACTIONS, ACTION_READ),
            createPermission(CATEGORY_REPORTS, ACTION_READ),
            createPermission(CATEGORY_INVOICES, ACTION_READ),
            createPermission(CATEGORY_CATEGORIES, ACTION_READ)
        ));
        
        // دور العميل
        Set<String> customerPermissions = new HashSet<>(Arrays.asList(
            createPermission(CATEGORY_INVOICES, ACTION_READ),
            createPermission(CATEGORY_ACCOUNTS, ACTION_READ) // فقط حسابه الشخصي
        ));
        
        // حفظ تعريفات الأدوار
        rolePermissions.put(ROLE_SUPER_ADMIN, superAdminPermissions);
        rolePermissions.put(ROLE_ADMIN, adminPermissions);
        rolePermissions.put(ROLE_ACCOUNTANT, accountantPermissions);
        rolePermissions.put(ROLE_CASHIER, cashierPermissions);
        rolePermissions.put(ROLE_AUDITOR, auditorPermissions);
        rolePermissions.put(ROLE_VIEWER, viewerPermissions);
        rolePermissions.put(ROLE_CUSTOMER, customerPermissions);
        
        // حفظ في التخزين المحلي
        saveRoleDefinitions();
    }
    
    /**
     * إنشاء صلاحية مركبة
     * Create composite permission
     */
    private String createPermission(String category, String action) {
        return category + ":" + action;
    }
    
    /**
     * تعيين دور لمستخدم
     * Assign role to user
     */
    public void assignRole(String userId, String role) {
        executorService.execute(() -> {
            try {
                Set<String> roles = userRoles.getOrDefault(userId, new HashSet<>());
                roles.add(role);
                userRoles.put(userId, roles);
                
                saveUserRoles();
                logRoleChange(userId, "ASSIGN_ROLE", role);
                
                Log.d(TAG, String.format("Role %s assigned to user %s", role, userId));
            } catch (Exception e) {
                Log.e(TAG, "Error assigning role", e);
            }
        });
    }
    
    /**
     * إزالة دور من مستخدم
     * Remove role from user
     */
    public void removeRole(String userId, String role) {
        executorService.execute(() -> {
            try {
                Set<String> roles = userRoles.getOrDefault(userId, new HashSet<>());
                roles.remove(role);
                userRoles.put(userId, roles);
                
                saveUserRoles();
                logRoleChange(userId, "REMOVE_ROLE", role);
                
                Log.d(TAG, String.format("Role %s removed from user %s", role, userId));
            } catch (Exception e) {
                Log.e(TAG, "Error removing role", e);
            }
        });
    }
    
    /**
     * إضافة صلاحية مخصصة لمستخدم
     * Add custom permission to user
     */
    public void addCustomPermission(String userId, String permission) {
        executorService.execute(() -> {
            try {
                Set<String> customPerms = userCustomPermissions.getOrDefault(userId, new HashSet<>());
                customPerms.add(permission);
                userCustomPermissions.put(userId, customPerms);
                
                saveCustomPermissions();
                logRoleChange(userId, "ADD_CUSTOM_PERMISSION", permission);
                
                Log.d(TAG, String.format("Custom permission %s added to user %s", permission, userId));
            } catch (Exception e) {
                Log.e(TAG, "Error adding custom permission", e);
            }
        });
    }
    
    /**
     * إزالة صلاحية مخصصة من مستخدم
     * Remove custom permission from user
     */
    public void removeCustomPermission(String userId, String permission) {
        executorService.execute(() -> {
            try {
                Set<String> customPerms = userCustomPermissions.getOrDefault(userId, new HashSet<>());
                customPerms.remove(permission);
                userCustomPermissions.put(userId, customPerms);
                
                saveCustomPermissions();
                logRoleChange(userId, "REMOVE_CUSTOM_PERMISSION", permission);
                
                Log.d(TAG, String.format("Custom permission %s removed from user %s", permission, userId));
            } catch (Exception e) {
                Log.e(TAG, "Error removing custom permission", e);
            }
        });
    }
    
    /**
     * فحص صلاحية مستخدم
     * Check user permission
     */
    public boolean hasPermission(String userId, String permission) {
        try {
            // فحص الصلاحيات المخصصة أولاً
            Set<String> customPerms = userCustomPermissions.getOrDefault(userId, new HashSet<>());
            if (customPerms.contains(permission)) {
                return true;
            }
            
            // فحص صلاحيات الأدوار
            Set<String> userRoleSet = userRoles.getOrDefault(userId, new HashSet<>());
            for (String role : userRoleSet) {
                Set<String> rolePerms = rolePermissions.getOrDefault(role, new HashSet<>());
                if (rolePerms.contains(permission)) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking permission", e);
            return false;
        }
    }
    
    /**
     * فحص صلاحية معقدة (فئة + عملية)
     * Check complex permission (category + action)
     */
    public boolean hasPermission(String userId, String category, String action) {
        String permission = createPermission(category, action);
        return hasPermission(userId, permission);
    }
    
    /**
     * فحص دور مستخدم
     * Check user role
     */
    public boolean hasRole(String userId, String role) {
        Set<String> roles = userRoles.getOrDefault(userId, new HashSet<>());
        return roles.contains(role);
    }
    
    /**
     * الحصول على جميع أدوار المستخدم
     * Get all user roles
     */
    public Set<String> getUserRoles(String userId) {
        return new HashSet<>(userRoles.getOrDefault(userId, new HashSet<>()));
    }
    
    /**
     * الحصول على جميع صلاحيات المستخدم
     * Get all user permissions
     */
    public Set<String> getUserPermissions(String userId) {
        Set<String> allPermissions = new HashSet<>();
        
        // إضافة الصلاحيات المخصصة
        allPermissions.addAll(userCustomPermissions.getOrDefault(userId, new HashSet<>()));
        
        // إضافة صلاحيات الأدوار
        Set<String> userRoleSet = userRoles.getOrDefault(userId, new HashSet<>());
        for (String role : userRoleSet) {
            allPermissions.addAll(rolePermissions.getOrDefault(role, new HashSet<>()));
        }
        
        return allPermissions;
    }
    
    /**
     * إنشاء دور مخصص
     * Create custom role
     */
    public void createCustomRole(String roleName, Set<String> permissions) {
        executorService.execute(() -> {
            try {
                rolePermissions.put(roleName, new HashSet<>(permissions));
                saveRoleDefinitions();
                
                logRoleChange("SYSTEM", "CREATE_CUSTOM_ROLE", roleName);
                Log.d(TAG, "Custom role created: " + roleName);
            } catch (Exception e) {
                Log.e(TAG, "Error creating custom role", e);
            }
        });
    }
    
    /**
     * تعديل دور موجود
     * Modify existing role
     */
    public void modifyRole(String roleName, Set<String> permissions) {
        executorService.execute(() -> {
            try {
                if (rolePermissions.containsKey(roleName)) {
                    rolePermissions.put(roleName, new HashSet<>(permissions));
                    saveRoleDefinitions();
                    
                    logRoleChange("SYSTEM", "MODIFY_ROLE", roleName);
                    Log.d(TAG, "Role modified: " + roleName);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error modifying role", e);
            }
        });
    }
    
    /**
     * حذف دور مخصص
     * Delete custom role
     */
    public void deleteCustomRole(String roleName) {
        executorService.execute(() -> {
            try {
                // التأكد من أنه ليس دور افتراضي
                if (!isDefaultRole(roleName)) {
                    rolePermissions.remove(roleName);
                    
                    // إزالة الدور من جميع المستخدمين
                    for (String userId : userRoles.keySet()) {
                        userRoles.get(userId).remove(roleName);
                    }
                    
                    saveRoleDefinitions();
                    saveUserRoles();
                    
                    logRoleChange("SYSTEM", "DELETE_CUSTOM_ROLE", roleName);
                    Log.d(TAG, "Custom role deleted: " + roleName);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deleting custom role", e);
            }
        });
    }
    
    /**
     * فحص ما إذا كان الدور افتراضي
     * Check if role is default
     */
    private boolean isDefaultRole(String roleName) {
        return Arrays.asList(
            ROLE_SUPER_ADMIN, ROLE_ADMIN, ROLE_MANAGER, ROLE_ACCOUNTANT, 
            ROLE_CASHIER, ROLE_AUDITOR, ROLE_VIEWER, ROLE_CUSTOMER, ROLE_SUPPLIER
        ).contains(roleName);
    }
    
    /**
     * الحصول على جميع الأدوار المتاحة
     * Get all available roles
     */
    public Set<String> getAllRoles() {
        return new HashSet<>(rolePermissions.keySet());
    }
    
    /**
     * الحصول على صلاحيات دور معين
     * Get permissions for specific role
     */
    public Set<String> getRolePermissions(String roleName) {
        return new HashSet<>(rolePermissions.getOrDefault(roleName, new HashSet<>()));
    }
    
    /**
     * فحص صلاحية إدارية عالية
     * Check high-level administrative permission
     */
    public boolean isAdmin(String userId) {
        return hasRole(userId, ROLE_SUPER_ADMIN) || hasRole(userId, ROLE_ADMIN);
    }
    
    /**
     * فحص صلاحية المدير العام
     * Check super admin permission
     */
    public boolean isSuperAdmin(String userId) {
        return hasRole(userId, ROLE_SUPER_ADMIN);
    }
    
    /**
     * تصفية البيانات حسب صلاحيات المستخدم
     * Filter data based on user permissions
     */
    public <T> List<T> filterByPermissions(String userId, List<T> data, String requiredPermission) {
        if (hasPermission(userId, requiredPermission)) {
            return data;
        }
        return new ArrayList<>(); // قائمة فارغة إذا لم تكن هناك صلاحية
    }
    
    /**
     * حفظ أدوار المستخدمين
     * Save user roles
     */
    private void saveUserRoles() {
        String json = gson.toJson(userRoles);
        prefs.edit().putString(USER_ROLES_KEY, json).apply();
    }
    
    /**
     * تحميل أدوار المستخدمين
     * Load user roles
     */
    private void loadUserRoles() {
        String json = prefs.getString(USER_ROLES_KEY, "{}");
        Type type = new TypeToken<Map<String, Set<String>>>(){}.getType();
        userRoles = gson.fromJson(json, type);
        if (userRoles == null) {
            userRoles = new HashMap<>();
        }
    }
    
    /**
     * حفظ الصلاحيات المخصصة
     * Save custom permissions
     */
    private void saveCustomPermissions() {
        String json = gson.toJson(userCustomPermissions);
        prefs.edit().putString(CUSTOM_PERMISSIONS_KEY, json).apply();
    }
    
    /**
     * حفظ تعريفات الأدوار
     * Save role definitions
     */
    private void saveRoleDefinitions() {
        String json = gson.toJson(rolePermissions);
        prefs.edit().putString(ROLE_DEFINITIONS_KEY, json).apply();
    }
    
    /**
     * تسجيل تغييرات الأدوار في سجل التدقيق
     * Log role changes in audit log
     */
    private void logRoleChange(String userId, String action, String details) {
        executorService.execute(() -> {
            try {
                AuditLog auditLog = new AuditLog();
                auditLog.userId = userId;
                auditLog.action = action;
                auditLog.details = details;
                auditLog.timestamp = System.currentTimeMillis();
                
                database.auditLogDao().insert(auditLog);
            } catch (Exception e) {
                Log.e(TAG, "Error logging role change", e);
            }
        });
    }
    
    /**
     * إنشاء تقرير صلاحيات المستخدمين
     * Generate user permissions report
     */
    public Map<String, Object> generatePermissionsReport() {
        Map<String, Object> report = new HashMap<>();
        
        try {
            Map<String, Set<String>> userPermissionsMap = new HashMap<>();
            
            for (String userId : userRoles.keySet()) {
                userPermissionsMap.put(userId, getUserPermissions(userId));
            }
            
            report.put("userPermissions", userPermissionsMap);
            report.put("roleDefinitions", rolePermissions);
            report.put("customPermissions", userCustomPermissions);
            report.put("generatedAt", System.currentTimeMillis());
            
        } catch (Exception e) {
            Log.e(TAG, "Error generating permissions report", e);
        }
        
        return report;
    }
    
    /**
     * تنظيف الموارد
     * Cleanup resources
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    /**
     * فئة معلومات الصلاحية
     * Permission info class
     */
    public static class PermissionInfo {
        public String permission;
        public String category;
        public String action;
        public String description;
        
        public PermissionInfo(String permission, String category, String action, String description) {
            this.permission = permission;
            this.category = category;
            this.action = action;
            this.description = description;
        }
    }
    
    /**
     * فئة معلومات الدور
     * Role info class
     */
    public static class RoleInfo {
        public String roleName;
        public String description;
        public Set<String> permissions;
        public boolean isCustom;
        
        public RoleInfo(String roleName, String description, Set<String> permissions, boolean isCustom) {
            this.roleName = roleName;
            this.description = description;
            this.permissions = permissions;
            this.isCustom = isCustom;
        }
    }
}