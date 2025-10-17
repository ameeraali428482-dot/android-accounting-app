
package com.example.accountingapp.advanced;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

/**
 * نظام إدارة الجلسات المتقدم مع دعم العمل دون اتصال
 * والذاكرة الذكية للمستخدمين
 * 
 * @author MiniMax Agent
 * @version 3.0
 * @since 2025-10-17
 */
public class OfflineSessionManager {
    private static final String TAG = "OfflineSessionManager";
    private static final String PREFS_NAME = "offline_session_prefs_v3";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_LAST_LOGIN = "last_login";
    private static final String KEY_SESSION_TIMEOUT = "session_timeout";
    private static final String KEY_AUTO_LOGIN_ENABLED = "auto_login_enabled";
    private static final String KEY_REMEMBERED_ACCOUNTS = "remembered_accounts";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    private static final String KEY_SESSION_TOKEN = "session_token";
    private static final String KEY_OFFLINE_MODE = "offline_mode";
    
    private static OfflineSessionManager instance;
    private SharedPreferences prefs;
    private Context context;
    private boolean isOfflineMode = false;
    
    private OfflineSessionManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.isOfflineMode = prefs.getBoolean(KEY_OFFLINE_MODE, false);
    }
    
    public static synchronized OfflineSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new OfflineSessionManager(context);
        }
        return instance;
    }
    
    /**
     * تسجيل دخول ذكي مع تذكر بيانات المستخدم وإدارة الجلسات
     */
    public void loginUser(String userId, String username, String role, String sessionToken) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_SESSION_TOKEN, sessionToken);
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis());
        editor.putBoolean(KEY_AUTO_LOGIN_ENABLED, true);
        
        // إضافة الحساب للحسابات المتذكرة
        Set<String> rememberedAccounts = getRememberedAccounts();
        String accountInfo = userId + ":" + username + ":" + role;
        rememberedAccounts.add(accountInfo);
        editor.putStringSet(KEY_REMEMBERED_ACCOUNTS, rememberedAccounts);
        
        editor.apply();
        
        Log.d(TAG, "تم تسجيل دخول المستخدم: " + username);
        
        // فحص النسخ الاحتياطية عند تسجيل الدخول
        checkForBackupsOnLogin();
        
        // إشعار نظام تسجيل الأنشطة
        ActivityLogManager.getInstance(context).logActivity(
            "USER_LOGIN", 
            "تسجيل دخول المستخدم: " + username,
            userId
        );
    }
    
    /**
     * تسجيل خروج مع خيارات مرنة للاحتفاظ بالبيانات
     */
    public void logoutUser(boolean clearAllData, boolean keepRememberedAccounts) {
        String currentUserId = getCurrentUserId();
        String currentUsername = getCurrentUsername();
        
        SharedPreferences.Editor editor = prefs.edit();
        
        if (clearAllData) {
            if (keepRememberedAccounts) {
                // الاحتفاظ بالحسابات المتذكرة فقط
                Set<String> rememberedAccounts = getRememberedAccounts();
                editor.clear();
                editor.putStringSet(KEY_REMEMBERED_ACCOUNTS, rememberedAccounts);
            } else {
                editor.clear();
            }
        } else {
            // الاحتفاظ ببيانات التذكر والإعدادات
            editor.putBoolean(KEY_IS_LOGGED_IN, false);
            editor.putBoolean(KEY_AUTO_LOGIN_ENABLED, false);
            editor.remove(KEY_SESSION_TOKEN);
        }
        
        editor.apply();
        
        // تسجيل عملية تسجيل الخروج
        ActivityLogManager.getInstance(context).logActivity(
            "USER_LOGOUT", 
            "تسجيل خروج المستخدم: " + currentUsername,
            currentUserId
        );
        
        Log.d(TAG, "تم تسجيل خروج المستخدم");
    }
    
    /**
     * فحص حالة تسجيل الدخول مع التحقق من صحة الجلسة
     */
    public boolean isUserLoggedIn() {
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        
        if (isLoggedIn && isSessionExpired()) {
            // انتهاء الجلسة تلقائياً
            logoutUser(false, true);
            return false;
        }
        
        return isLoggedIn;
    }
    
    /**
     * تمكين الدخول التلقائي مع فحص الأمان
     */
    public boolean isAutoLoginEnabled() {
        return prefs.getBoolean(KEY_AUTO_LOGIN_ENABLED, false) && 
               isUserLoggedIn() && 
               !isSessionExpired();
    }
    
    /**
     * تفعيل/إلغاء الوضع غير المتصل
     */
    public void setOfflineMode(boolean enabled) {
        this.isOfflineMode = enabled;
        prefs.edit().putBoolean(KEY_OFFLINE_MODE, enabled).apply();
        
        ActivityLogManager.getInstance(context).logActivity(
            "OFFLINE_MODE_CHANGE",
            "تغيير الوضع غير المتصل: " + (enabled ? "مفعل" : "معطل"),
            getCurrentUserId()
        );
        
        Log.d(TAG, "تم تغيير الوضع غير المتصل: " + enabled);
    }
    
    public boolean isOfflineMode() {
        return isOfflineMode;
    }
    
    // ميثودات الحصول على معلومات المستخدم
    public String getCurrentUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }
    
    public String getCurrentUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }
    
    public String getCurrentUserRole() {
        return prefs.getString(KEY_USER_ROLE, "user");
    }
    
    public String getSessionToken() {
        return prefs.getString(KEY_SESSION_TOKEN, "");
    }
    
    public long getLastLoginTime() {
        return prefs.getLong(KEY_LAST_LOGIN, 0);
    }
    
    /**
     * الحصول على الحسابات المتذكرة
     */
    public Set<String> getRememberedAccounts() {
        return new HashSet<>(prefs.getStringSet(KEY_REMEMBERED_ACCOUNTS, new HashSet<>()));
    }
    
    /**
     * فحص النسخ الاحتياطية عند تسجيل الدخول
     */
    private void checkForBackupsOnLogin() {
        try {
            BackupManager backupManager = BackupManager.getInstance(context);
            backupManager.checkForAvailableBackupsAndNotify();
        } catch (Exception e) {
            Log.e(TAG, "فشل في فحص النسخ الاحتياطية", e);
        }
    }
    
    /**
     * تحديد مهلة انتهاء الجلسة (بالدقائق)
     */
    public void setSessionTimeout(long timeoutMinutes) {
        prefs.edit().putLong(KEY_SESSION_TIMEOUT, timeoutMinutes).apply();
        Log.d(TAG, "تم تحديد مهلة انتهاء الجلسة: " + timeoutMinutes + " دقيقة");
    }
    
    /**
     * فحص انتهاء الجلسة
     */
    public boolean isSessionExpired() {
        long timeout = prefs.getLong(KEY_SESSION_TIMEOUT, 0);
        if (timeout == 0) return false; // لا توجد مهلة محددة
        
        long lastActivity = prefs.getLong(KEY_LAST_LOGIN, 0);
        long currentTime = System.currentTimeMillis();
        
        return (currentTime - lastActivity) > (timeout * 60 * 1000);
    }
    
    /**
     * تحديث وقت النشاط الأخير لإعادة تعيين مهلة الجلسة
     */
    public void updateLastActivity() {
        prefs.edit().putLong(KEY_LAST_LOGIN, System.currentTimeMillis()).apply();
    }
    
    /**
     * مسح حساب من الحسابات المتذكرة
     */
    public void removeRememberedAccount(String accountInfo) {
        Set<String> accounts = getRememberedAccounts();
        accounts.remove(accountInfo);
        prefs.edit().putStringSet(KEY_REMEMBERED_ACCOUNTS, accounts).apply();
        
        Log.d(TAG, "تم مسح الحساب من الحسابات المتذكرة: " + accountInfo);
    }
    
    /**
     * تفعيل/إلغاء المصادقة البيومترية
     */
    public void setBiometricEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply();
        
        ActivityLogManager.getInstance(context).logActivity(
            "BIOMETRIC_SETTING_CHANGE",
            "تغيير إعداد المصادقة البيومترية: " + (enabled ? "مفعل" : "معطل"),
            getCurrentUserId()
        );
        
        Log.d(TAG, "تم تغيير إعداد المصادقة البيومترية: " + enabled);
    }
    
    public boolean isBiometricEnabled() {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);
    }
    
    /**
     * مسح جميع بيانات الجلسة (للطوارئ فقط)
     */
    public void clearAllSessionData() {
        String currentUserId = getCurrentUserId();
        prefs.edit().clear().apply();
        
        ActivityLogManager.getInstance(context).logActivity(
            "EMERGENCY_DATA_CLEAR",
            "مسح طارئ لجميع بيانات الجلسة",
            currentUserId
        );
        
        Log.w(TAG, "تم مسح جميع بيانات الجلسة (طارئ)");
    }
    
    /**
     * الحصول على إحصائيات الجلسة
     */
    public String getSessionStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("إحصائيات الجلسة:\n");
        stats.append("المستخدم الحالي: ").append(getCurrentUsername()).append("\n");
        stats.append("الدور: ").append(getCurrentUserRole()).append("\n");
        stats.append("وقت آخر دخول: ").append(new java.util.Date(getLastLoginTime())).append("\n");
        stats.append("عدد الحسابات المتذكرة: ").append(getRememberedAccounts().size()).append("\n");
        stats.append("الوضع غير المتصل: ").append(isOfflineMode() ? "مفعل" : "معطل").append("\n");
        stats.append("المصادقة البيومترية: ").append(isBiometricEnabled() ? "مفعل" : "معطل");
        
        return stats.toString();
    }
}

