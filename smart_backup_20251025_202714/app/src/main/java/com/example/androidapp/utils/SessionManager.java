package com.example.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;

/**
 * مدير الجلسات للتطبيق
 * يدير تسجيل الدخول وحفظ بيانات المستخدم
 */
public class SessionManager {
    private static final String PREF_NAME = "AndroidAppPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_CURRENT_ORG_ID = "currentOrgId";
    public static final String KEY_COMPANY_ID = KEY_CURRENT_ORG_ID; // Alias for KEY_CURRENT_ORG_ID

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String userId, String currentOrgId) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_CURRENT_ORG_ID, currentOrgId);
        editor.apply(); // استخدام apply بدلاً من commit للأداء الأفضل
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_CURRENT_ORG_ID, pref.getString(KEY_CURRENT_ORG_ID, null));
        return user;
    }

    public String getCurrentUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getCurrentCompanyId() {
        return pref.getString(KEY_CURRENT_ORG_ID, null);
    }

    public String getCompanyId() {
        return getCurrentCompanyId(); // Alias for getCurrentCompanyId()
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
    
    /**
     * تحديث معرف الشركة الحالية
     */
    public void updateCurrentCompanyId(String companyId) {
        editor.putString(KEY_CURRENT_ORG_ID, companyId);
        editor.apply();
    }
    
    /**
     * تحديث معرف المستخدم
     */
    public void updateUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }
}
