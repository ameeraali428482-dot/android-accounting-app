package com.example.accountingapp.advanced;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityLogManager {
    private static final String TAG = "ActivityLogManager";
    private static final String PREFS_NAME = "activity_log_prefs";
    private static final String KEY_ACTIVITY_LOG = "activity_log";
    private static final String KEY_MAX_LOG_ENTRIES = "max_log_entries";
    
    private static ActivityLogManager instance;
    private SharedPreferences prefs;
    private Context context;
    
    private ActivityLogManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized ActivityLogManager getInstance(Context context) {
        if (instance == null) {
            instance = new ActivityLogManager(context);
        }
        return instance;
    }
    
    // تسجيل نشاط
    public void logActivity(ActivityType type, String description, String details) {
        String userId = OfflineSessionManager.getInstance(context).getCurrentUserId();
        String username = OfflineSessionManager.getInstance(context).getCurrentUsername();
        
        logActivity(type, description, details, userId, username);
    }
    
    // تسجيل نشاط مع معلومات المستخدم
    public void logActivity(ActivityType type, String description, String details, String userId, String username) {
        try {
            JSONObject activity = new JSONObject();
            activity.put("id", System.currentTimeMillis() + "_" + userId);
            activity.put("type", type.name());
            activity.put("description", description);
            activity.put("details", details);
            activity.put("user_id", userId);
            activity.put("username", username);
            activity.put("timestamp", System.currentTimeMillis());
            activity.put("date", getCurrentDateString());
            
            addActivityToLog(activity);
            
            // إرسال إشعار للإداريين إذا لزم الأمر
            if (shouldNotifyAdmins(type)) {
                NotificationManager.getInstance(context)
                    .sendAdminNotification(type, description, userId, username);
            }
            
            Log.d(TAG, "تم تسجيل النشاط: " + description);
            
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في تسجيل النشاط", e);
        }
    }
    
    // إضافة النشاط إلى السجل
    private void addActivityToLog(JSONObject activity) throws JSONException {
        JSONArray activityLog = getActivityLog();
        activityLog.put(activity);
        
        // تحديد عدد الإدخالات القصوى
        int maxEntries = prefs.getInt(KEY_MAX_LOG_ENTRIES, 1000);
        if (activityLog.length() > maxEntries) {
            // حذف الإدخالات القديمة
            JSONArray newLog = new JSONArray();
            int startIndex = activityLog.length() - maxEntries;
            for (int i = startIndex; i < activityLog.length(); i++) {
                newLog.put(activityLog.get(i));
            }
            activityLog = newLog;
        }
        
        // حفظ السجل المحدث
        prefs.edit().putString(KEY_ACTIVITY_LOG, activityLog.toString()).apply();
    }
    
    // الحصول على سجل الأنشطة
    public JSONArray getActivityLog() {
        String logString = prefs.getString(KEY_ACTIVITY_LOG, "[]");
        try {
            return new JSONArray(logString);
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في قراءة سجل الأنشطة", e);
            return new JSONArray();
        }
    }
    
    // الحصول على الأنشطة كقائمة
    public List<ActivityEntry> getActivityEntries() {
        List<ActivityEntry> entries = new ArrayList<>();
        JSONArray activityLog = getActivityLog();
        
        try {
            for (int i = activityLog.length() - 1; i >= 0; i--) { // الأحدث أولاً
                JSONObject activity = activityLog.getJSONObject(i);
                ActivityEntry entry = new ActivityEntry();
                
                entry.id = activity.getString("id");
                entry.type = ActivityType.valueOf(activity.getString("type"));
                entry.description = activity.getString("description");
                entry.details = activity.optString("details", "");
                entry.userId = activity.getString("user_id");
                entry.username = activity.getString("username");
                entry.timestamp = activity.getLong("timestamp");
                entry.date = activity.optString("date", "");
                
                entries.add(entry);
            }
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في تحويل سجل الأنشطة", e);
        }
        
        return entries;
    }
    
    // الحصول على أنشطة مستخدم معين
    public List<ActivityEntry> getUserActivities(String userId) {
        List<ActivityEntry> userActivities = new ArrayList<>();
        List<ActivityEntry> allActivities = getActivityEntries();
        
        for (ActivityEntry entry : allActivities) {
            if (entry.userId.equals(userId)) {
                userActivities.add(entry);
            }
        }
        
        return userActivities;
    }
    
    // الحصول على أنشطة نوع معين
    public List<ActivityEntry> getActivitiesByType(ActivityType type) {
        List<ActivityEntry> typeActivities = new ArrayList<>();
        List<ActivityEntry> allActivities = getActivityEntries();
        
        for (ActivityEntry entry : allActivities) {
            if (entry.type == type) {
                typeActivities.add(entry);
            }
        }
        
        return typeActivities;
    }
    
    // فحص ما إذا كان يجب إشعار الإداريين
    private boolean shouldNotifyAdmins(ActivityType type) {
        switch (type) {
            case CREATE_INVOICE:
            case UPDATE_INVOICE:
            case DELETE_INVOICE:
            case CREATE_ACCOUNT:
            case UPDATE_ACCOUNT:
            case DELETE_ACCOUNT:
            case FINANCIAL_TRANSACTION:
                return true;
            default:
                return false;
        }
    }
    
    // مسح سجل الأنشطة
    public void clearActivityLog() {
        prefs.edit().remove(KEY_ACTIVITY_LOG).apply();
        Log.d(TAG, "تم مسح سجل الأنشطة");
    }
    
    // تصدير سجل الأنشطة
    public String exportActivityLog() {
        JSONArray activityLog = getActivityLog();
        return activityLog.toString();
    }
    
    // تعيين عدد الإدخالات القصوى
    public void setMaxLogEntries(int maxEntries) {
        prefs.edit().putInt(KEY_MAX_LOG_ENTRIES, maxEntries).apply();
    }
    
    // الحصول على تاريخ اليوم كنص
    private String getCurrentDateString() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }
    
    // أنواع الأنشطة
    public enum ActivityType {
        LOGIN("تسجيل دخول"),
        LOGOUT("تسجيل خروج"),
        CREATE_INVOICE("إنشاء فاتورة"),
        UPDATE_INVOICE("تحديث فاتورة"),
        DELETE_INVOICE("حذف فاتورة"),
        CREATE_ACCOUNT("إنشاء حساب"),
        UPDATE_ACCOUNT("تحديث حساب"),
        DELETE_ACCOUNT("حذف حساب"),
        FINANCIAL_TRANSACTION("معاملة مالية"),
        BACKUP_CREATED("إنشاء نسخة احتياطية"),
        BACKUP_RESTORED("استعادة نسخة احتياطية"),
        DATA_EXPORT("تصدير بيانات"),
        DATA_IMPORT("استيراد بيانات"),
        SETTINGS_CHANGED("تغيير إعدادات"),
        USER_CREATED("إنشاء مستخدم"),
        USER_UPDATED("تحديث مستخدم"),
        USER_DELETED("حذف مستخدم"),
        PERMISSION_CHANGED("تغيير صلاحيات"),
        SYSTEM_ERROR("خطأ في النظام"),
        OTHER("أخرى");
        
        private final String arabicName;
        
        ActivityType(String arabicName) {
            this.arabicName = arabicName;
        }
        
        public String getArabicName() {
            return arabicName;
        }
    }
    
    // كلاس إدخال النشاط
    public static class ActivityEntry {
        public String id;
        public ActivityType type;
        public String description;
        public String details;
        public String userId;
        public String username;
        public long timestamp;
        public String date;
        
        public String getFormattedDate() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(new Date(timestamp));
        }
        
        public String getTypeDisplayName() {
            return type.getArabicName();
        }
    }
}
