package com.example.accountingapp.advanced;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public class OfflineSessionManager {
    private static final String TAG = "OfflineSessionManager";
    private static final String PREFS_NAME = "offline_session_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_LAST_LOGIN = "last_login";
    private static final String KEY_SESSION_TIMEOUT = "session_timeout";
    private static final String KEY_AUTO_LOGIN_ENABLED = "auto_login_enabled";
    private static final String KEY_REMEMBERED_ACCOUNTS = "remembered_accounts";
    
    private static OfflineSessionManager instance;
    private SharedPreferences prefs;
    private Context context;
    
    private OfflineSessionManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized OfflineSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new OfflineSessionManager(context);
        }
        return instance;
    }
    
    // تسجيل دخول ذكي مع تذكر البيانات
    public void loginUser(String userId, String username, String role) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_ROLE, role);
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis());
        editor.putBoolean(KEY_AUTO_LOGIN_ENABLED, true);
        
        // إضافة الحساب للحسابات المتذكرة
        Set<String> rememberedAccounts = getRememberedAccounts();
        rememberedAccounts.add(userId + ":" + username + ":" + role);
        editor.putStringSet(KEY_REMEMBERED_ACCOUNTS, rememberedAccounts);
        
        editor.apply();
        
        Log.d(TAG, "تم تسجيل دخول المستخدم: " + username);
        
        // فحص النسخ الاحتياطية عند تسجيل الدخول
        checkForBackupsOnLogin();
    }
    
    // تسجيل خروج مع الاحتفاظ بالبيانات للتذكر
    public void logoutUser(boolean clearAllData) {
        SharedPreferences.Editor editor = prefs.edit();
        
        if (clearAllData) {
            // مسح جميع البيانات
            editor.clear();
        } else {
            // الاحتفاظ بالبيانات للتذكر
            editor.putBoolean(KEY_IS_LOGGED_IN, false);
            editor.putBoolean(KEY_AUTO_LOGIN_ENABLED, false);
        }
        
        editor.apply();
        Log.d(TAG, "تم تسجيل خروج المستخدم");
    }
    
    // فحص حالة تسجيل الدخول
    public boolean isUserLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    // تمكين الدخول التلقائي
    public boolean isAutoLoginEnabled() {
        return prefs.getBoolean(KEY_AUTO_LOGIN_ENABLED, false) && isUserLoggedIn();
    }
    
    // الحصول على معلومات المستخدم
    public String getCurrentUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }
    
    public String getCurrentUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }
    
    public String getCurrentUserRole() {
        return prefs.getString(KEY_USER_ROLE, "user");
    }
    
    public long getLastLoginTime() {
        return prefs.getLong(KEY_LAST_LOGIN, 0);
    }
    
    // الحصول على الحسابات المتذكرة
    public Set<String> getRememberedAccounts() {
        return new HashSet<>(prefs.getStringSet(KEY_REMEMBERED_ACCOUNTS, new HashSet<>()));
    }
    
    // فحص النسخ الاحتياطية عند تسجيل الدخول
    private void checkForBackupsOnLogin() {
        BackupManager backupManager = BackupManager.getInstance(context);
        backupManager.checkForAvailableBackupsAndNotify();
    }
    
    // تحديد مهلة الجلسة
    public void setSessionTimeout(long timeoutMinutes) {
        prefs.edit().putLong(KEY_SESSION_TIMEOUT, timeoutMinutes).apply();
    }
    
    // فحص انتهاء الجلسة
    public boolean isSessionExpired() {
        long timeout = prefs.getLong(KEY_SESSION_TIMEOUT, 0);
        if (timeout == 0) return false; // لا توجد مهلة محددة
        
        long lastActivity = prefs.getLong(KEY_LAST_LOGIN, 0);
        long currentTime = System.currentTimeMillis();
        
        return (currentTime - lastActivity) > (timeout * 60 * 1000);
    }
    
    // تحديث وقت النشاط الأخير
    public void updateLastActivity() {
        prefs.edit().putLong(KEY_LAST_LOGIN, System.currentTimeMillis()).apply();
    }
    
    // مسح حساب من الحسابات المتذكرة
    public void removeRememberedAccount(String accountInfo) {
        Set<String> accounts = getRememberedAccounts();
        accounts.remove(accountInfo);
        prefs.edit().putStringSet(KEY_REMEMBERED_ACCOUNTS, accounts).apply();
    }
}
