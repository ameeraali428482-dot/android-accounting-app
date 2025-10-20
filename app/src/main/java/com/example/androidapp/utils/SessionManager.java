package com.example.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * مدير الجلسة
 */
public class SessionManager {
    
    private Context context;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "AppSession";
    private static final String KEY_COMPANY_ID = "company_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public String getCompanyId() {
        return preferences.getString(KEY_COMPANY_ID, "default_company");
    }
    
    public void setCompanyId(String companyId) {
        preferences.edit().putString(KEY_COMPANY_ID, companyId).apply();
    }

    public String getCurrentUserId() {
        return preferences.getString(KEY_USER_ID, null);
    }

    public int getCurrentUserIdInt() {
        String userId = getCurrentUserId();
        try {
            return userId != null ? Integer.parseInt(userId) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setCurrentUserId(String userId) {
        preferences.edit().putString(KEY_USER_ID, userId).apply();
    }

    public void setCurrentUserId(int userId) {
        preferences.edit().putString(KEY_USER_ID, String.valueOf(userId)).apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }

    public void logout() {
        preferences.edit()
                .remove(KEY_USER_ID)
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .apply();
    }

    public void login(String userId) {
        preferences.edit()
                .putString(KEY_USER_ID, userId)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public void login(int userId) {
        login(String.valueOf(userId));
    }
}
