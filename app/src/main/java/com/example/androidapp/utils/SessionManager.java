package com.example.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * مدير الجلسة
 */
public class SessionManager {
    
    private SharedPreferences preferences;
    private static final String PREF_NAME = "AppSession";
    private static final String KEY_COMPANY_ID = "company_id";
    
    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public String getCompanyId() {
        return preferences.getString(KEY_COMPANY_ID, "default_company");
    }
    
    public void setCompanyId(String companyId) {
        preferences.edit().putString(KEY_COMPANY_ID, companyId).apply();
    }
}
