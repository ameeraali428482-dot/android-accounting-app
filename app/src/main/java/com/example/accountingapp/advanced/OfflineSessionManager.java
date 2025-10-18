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
    
    // تسجيل دخول ذكي
    public void loginUser(String userId, String username, String role) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_ROLE, role);
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis());
        editor.apply();
        
        // حفظ الحساب في قائمة الحسابات المحفوظة
        saveRememberedAccount(userId, username);
        
        Log.d(TAG, "تم تسجيل دخول المستخدم: " + username);
    }
    
    // حفظ الحساب المتذكر
    private void saveRememberedAccount(String userId, String username) {
        Set<String> rememberedAccounts = getRememberedAccounts();
        rememberedAccounts.add(userId + ":" + username);
        prefs.edit().putStringSet(KEY_REMEMBERED_ACCOUNTS, rememberedAccounts).apply();
    }
    
    // الحصول على الحسابات المحفوظة
    public Set<String> getRememberedAccounts() {
        return prefs.getStringSet(KEY_REMEMBERED_ACCOUNTS, new HashSet<>());
    }
    
    // فحص حالة تسجيل الدخول
    public boolean isLoggedIn() {
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        
        // فحص انتهاء صلاحية الجلسة (24 ساعة افتراضياً)
        if (isLoggedIn) {
            long lastLogin = prefs.getLong(KEY_LAST_LOGIN, 0);
            long sessionTimeout = prefs.getLong(KEY_SESSION_TIMEOUT, 24 * 60 * 60 * 1000); // 24 ساعة
            
            if (System.currentTimeMillis() - lastLogin > sessionTimeout) {
                // انتهت صلاحية الجلسة
                if (!isAutoLoginEnabled()) {
                    logout();
                    return false;
                }
            }
        }
        
        return isLoggedIn;
    }
    
    // تمكين/تعطيل الدخول التلقائي
    public void setAutoLoginEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_LOGIN_ENABLED, enabled).apply();
    }
    
    public boolean isAutoLoginEnabled() {
        return prefs.getBoolean(KEY_AUTO_LOGIN_ENABLED, true);
    }
    
    // تحديث وقت آخر نشاط
    public void updateLastActivity() {
        prefs.edit().putLong(KEY_LAST_LOGIN, System.currentTimeMillis()).apply();
    }
    
    // الحصول على معلومات المستخدم
    public String getCurrentUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }
    
    public String getCurrentUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }
    
    public String getCurrentUserRole() {
        return prefs.getString(KEY_USER_ROLE, "");
    }
    
    // تسجيل خروج
    public void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_USER_ROLE);
        editor.apply();
        
        Log.d(TAG, "تم تسجيل خروج المستخدم");
    }
    
    // مسح كل البيانات
    public void clearAllData() {
        prefs.edit().clear().apply();
        Log.d(TAG, "تم مسح جميع بيانات الجلسة");
    }
}
