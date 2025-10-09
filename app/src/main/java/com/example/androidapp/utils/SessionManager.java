package com.example.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "AndroidAppPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_FIREBASE_UID = "firebaseUid";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_CURRENT_ORG_ID = "currentOrgId";
    public static final String KEY_CURRENT_BRANCH_ID = "currentBranchId";
    public static final String KEY_ASSOCIATED_ORGS = "associatedOrgs";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String firebaseUid, String username, String userId, String currentOrgId, String currentBranchId, Map<String, UserOrgAssociation> associatedOrgs) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_FIREBASE_UID, firebaseUid);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_CURRENT_ORG_ID, currentOrgId);
        editor.putString(KEY_CURRENT_BRANCH_ID, currentBranchId);

        Gson gson = new Gson();
        String json = gson.toJson(associatedOrgs);
        editor.putString(KEY_ASSOCIATED_ORGS, json);

        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FIREBASE_UID, pref.getString(KEY_FIREBASE_UID, null));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_CURRENT_ORG_ID, pref.getString(KEY_CURRENT_ORG_ID, null));
        user.put(KEY_CURRENT_BRANCH_ID, pref.getString(KEY_CURRENT_BRANCH_ID, null));
        return user;
    }

    public String getCurrentUserId() {
        return pref.getString(KEY_USER_ID, null);
    }
    
    public String getCurrentCompanyId() {
        return pref.getString(KEY_CURRENT_ORG_ID, null);
    }

    public Map<String, UserOrgAssociation> getAssociatedOrgs() {
        Gson gson = new Gson();
        String json = pref.getString(KEY_ASSOCIATED_ORGS, null);
        if (json == null) {
            return new HashMap<>();
        }
        Type type = new TypeToken<Map<String, UserOrgAssociation>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void setCurrentOrg(String orgId, String branchId) {
        editor.putString(KEY_CURRENT_ORG_ID, orgId);
        editor.putString(KEY_CURRENT_BRANCH_ID, branchId);
        editor.commit();
    }

    public static class UserOrgAssociation {
        public String orgId;
        public String orgName;
        public String roleId;
        public String roleName;
        public String branchId;
        public String branchName;

        public UserOrgAssociation(String orgId, String orgName, String roleId, String roleName, String branchId, String branchName) {
            this.orgId = orgId;
            this.orgName = orgName;
            this.roleId = roleId;
            this.roleName = roleName;
            this.branchId = branchId;
            this.branchName = branchName;
        }
    }
}
