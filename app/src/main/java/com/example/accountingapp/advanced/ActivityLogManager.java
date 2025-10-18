package com.example.accountingapp.advanced;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.*;

public class ActivityLogManager {
    private static final String TAG = "ActivityLogManager";
    private static final String PREFS_NAME = "activity_log_prefs";
    private static final String KEY_LOG_ENABLED = "log_enabled";
    private static final String KEY_MAX_LOG_ENTRIES = "max_log_entries";
    private static final String KEY_LOG_RETENTION_DAYS = "log_retention_days";
    
    // أولويات الأنشطة
    public static final int PRIORITY_LOW = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_HIGH = 3;
    public static final int PRIORITY_CRITICAL = 4;
    
    // أنواع الأنشطة
    public static final String TYPE_LOGIN = "LOGIN";
    public static final String TYPE_LOGOUT = "LOGOUT";
    public static final String TYPE_CREATE_ACCOUNT = "CREATE_ACCOUNT";
    public static final String TYPE_UPDATE_ACCOUNT = "UPDATE_ACCOUNT";
    public static final String TYPE_DELETE_ACCOUNT = "DELETE_ACCOUNT";
    public static final String TYPE_TRANSACTION = "TRANSACTION";
    public static final String TYPE_REPORT = "REPORT";
    public static final String TYPE_BACKUP = "BACKUP";
    public static final String TYPE_RESTORE = "RESTORE";
    public static final String TYPE_ADMIN_ACTION = "ADMIN_ACTION";
    public static final String TYPE_SECURITY = "SECURITY";
    public static final String TYPE_ERROR = "ERROR";
    
    private static ActivityLogManager instance;
    private SharedPreferences prefs;
    private Context context;
    private SimpleDateFormat dateFormat;
    private List<ActivityEntry> activityCache;
    private final Object cacheLock = new Object();
    
    private ActivityLogManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.activityCache = new ArrayList<>();
        
        // تحميل الإعدادات الافتراضية
        if (!prefs.contains(KEY_LOG_ENABLED)) {
            prefs.edit()
                 .putBoolean(KEY_LOG_ENABLED, true)
                 .putInt(KEY_MAX_LOG_ENTRIES, 1000)
                 .putInt(KEY_LOG_RETENTION_DAYS, 90)
                 .apply();
        }
        
        // تحميل الأنشطة المحفوظة
        loadActivityCache();
    }
    
    public static synchronized ActivityLogManager getInstance(Context context) {
        if (instance == null) {
            instance = new ActivityLogManager(context);
        }
        return instance;
    }
    
    // تسجيل نشاط جديد
    public void logActivity(String type, String description, int priority) {
        logActivity(type, description, priority, null);
    }
    
    // تسجيل نشاط مع بيانات إضافية
    public void logActivity(String type, String description, int priority, JSONObject additionalData) {
        if (!isLoggingEnabled()) return;
        
        try {
            ActivityEntry entry = new ActivityEntry();
            entry.id = generateActivityId();
            entry.type = type;
            entry.description = description;
            entry.priority = priority;
            entry.timestamp = System.currentTimeMillis();
            entry.userId = OfflineSessionManager.getInstance(context).getCurrentUserId();
            entry.username = OfflineSessionManager.getInstance(context).getCurrentUsername();
            entry.userRole = OfflineSessionManager.getInstance(context).getCurrentUserRole();
            entry.additionalData = additionalData;
            
            // إضافة إلى الكاش
            synchronized (cacheLock) {
                activityCache.add(0, entry); // إضافة في المقدمة
                
                // تحديد عدد الإدخالات
                int maxEntries = prefs.getInt(KEY_MAX_LOG_ENTRIES, 1000);
                while (activityCache.size() > maxEntries) {
                    activityCache.remove(activityCache.size() - 1);
                }
            }
            
            // حفظ في التخزين الدائم
            saveActivityCache();
            
            // إشعار الإدارة إذا كان النشاط مهماً
            if (priority >= PRIORITY_HIGH) {
                notifyAdminOfActivity(entry);
            }
            
            Log.d(TAG, String.format("تم تسجيل نشاط: %s - %s", type, description));
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في تسجيل النشاط", e);
        }
    }
    
    // الحصول على سجل الأنشطة مع التصفية
    public List<ActivityEntry> getActivityLog(ActivityFilter filter) {
        synchronized (cacheLock) {
            List<ActivityEntry> filteredList = new ArrayList<>();
            
            for (ActivityEntry entry : activityCache) {
                if (matchesFilter(entry, filter)) {
                    filteredList.add(entry);
                }
            }
            
            // ترتيب حسب الوقت (الأحدث أولاً)
            Collections.sort(filteredList, (a, b) -> 
                Long.compare(b.timestamp, a.timestamp));
            
            // تحديد العدد المطلوب
            if (filter.limit > 0 && filteredList.size() > filter.limit) {
                filteredList = filteredList.subList(0, filter.limit);
            }
            
            return filteredList;
        }
    }
    
    // الحصول على أنشطة مستخدم معين
    public List<ActivityEntry> getUserActivities(String userId, int limit) {
        ActivityFilter filter = new ActivityFilter();
        filter.userId = userId;
        filter.limit = limit;
        
        return getActivityLog(filter);
    }
    
    // الحصول على أنشطة نوع معين
    public List<ActivityEntry> getActivitiesByType(String type, int limit) {
        ActivityFilter filter = new ActivityFilter();
        filter.type = type;
        filter.limit = limit;
        
        return getActivityLog(filter);
    }
    
    // الحصول على الأنشطة الأخيرة للحسابات الإدارية
    public List<ActivityEntry> getAdminActivities(int limit) {
        ActivityFilter filter = new ActivityFilter();
        filter.adminOnly = true;
        filter.limit = limit;
        
        return getActivityLog(filter);
    }
    
    // البحث في سجل الأنشطة
    public List<ActivityEntry> searchActivities(String searchTerm, int limit) {
        synchronized (cacheLock) {
            List<ActivityEntry> results = new ArrayList<>();
            String searchLower = searchTerm.toLowerCase();
            
            for (ActivityEntry entry : activityCache) {
                if (entry.description.toLowerCase().contains(searchLower) ||
                    entry.type.toLowerCase().contains(searchLower) ||
                    entry.username.toLowerCase().contains(searchLower)) {
                    
                    results.add(entry);
                    
                    if (limit > 0 && results.size() >= limit) {
                        break;
                    }
                }
            }
            
            return results;
        }
    }
    
    // تصدير سجل الأنشطة
    public JSONArray exportActivityLog() {
        JSONArray exported = new JSONArray();
        
        synchronized (cacheLock) {
            try {
                for (ActivityEntry entry : activityCache) {
                    JSONObject entryJson = new JSONObject();
                    entryJson.put("id", entry.id);
                    entryJson.put("type", entry.type);
                    entryJson.put("description", entry.description);
                    entryJson.put("priority", entry.priority);
                    entryJson.put("timestamp", entry.timestamp);
                    entryJson.put("userId", entry.userId);
                    entryJson.put("username", entry.username);
                    entryJson.put("userRole", entry.userRole);
                    
                    if (entry.additionalData != null) {
                        entryJson.put("additionalData", entry.additionalData);
                    }
                    
                    exported.put(entryJson);
                }
            } catch (JSONException e) {
                Log.e(TAG, "خطأ في تصدير سجل الأنشطة", e);
            }
        }
        
        return exported;
    }
    
    // استيراد نشاط من النسخة الاحتياطية
    public void importActivity(JSONObject activityJson) {
        try {
            ActivityEntry entry = new ActivityEntry();
            entry.id = activityJson.getString("id");
            entry.type = activityJson.getString("type");
            entry.description = activityJson.getString("description");
            entry.priority = activityJson.getInt("priority");
            entry.timestamp = activityJson.getLong("timestamp");
            entry.userId = activityJson.getString("userId");
            entry.username = activityJson.getString("username");
            entry.userRole = activityJson.optString("userRole", "user");
            
            if (activityJson.has("additionalData")) {
                entry.additionalData = activityJson.getJSONObject("additionalData");
            }
            
            synchronized (cacheLock) {
                // فحص التكرار
                boolean exists = activityCache.stream()
                    .anyMatch(existing -> existing.id.equals(entry.id));
                
                if (!exists) {
                    activityCache.add(entry);
                }
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في استيراد النشاط", e);
        }
    }
    
    // مسح الأنشطة القديمة
    public void cleanOldActivities() {
        int retentionDays = prefs.getInt(KEY_LOG_RETENTION_DAYS, 90);
        long cutoffTime = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000L);
        
        synchronized (cacheLock) {
            Iterator<ActivityEntry> iterator = activityCache.iterator();
            int removedCount = 0;
            
            while (iterator.hasNext()) {
                ActivityEntry entry = iterator.next();
                if (entry.timestamp < cutoffTime) {
                    iterator.remove();
                    removedCount++;
                }
            }
            
            if (removedCount > 0) {
                saveActivityCache();
                Log.d(TAG, "تم مسح " + removedCount + " نشاط قديم");
            }
        }
    }
    
    // إحصائيات الأنشطة
    public ActivityStats getActivityStats(long fromTime, long toTime) {
        ActivityStats stats = new ActivityStats();
        
        synchronized (cacheLock) {
            for (ActivityEntry entry : activityCache) {
                if (entry.timestamp >= fromTime && entry.timestamp <= toTime) {
                    stats.totalActivities++;
                    
                    switch (entry.priority) {
                        case PRIORITY_LOW:
                            stats.lowPriorityCount++;
                            break;
                        case PRIORITY_MEDIUM:
                            stats.mediumPriorityCount++;
                            break;
                        case PRIORITY_HIGH:
                            stats.highPriorityCount++;
                            break;
                        case PRIORITY_CRITICAL:
                            stats.criticalPriorityCount++;
                            break;
                    }
                    
                    // إحصائيات الأنواع
                    stats.typeStats.put(entry.type, 
                        stats.typeStats.getOrDefault(entry.type, 0) + 1);
                    
                    // إحصائيات المستخدمين
                    stats.userStats.put(entry.userId,
                        stats.userStats.getOrDefault(entry.userId, 0) + 1);
                }
            }
        }
        
        return stats;
    }
    
    // فحص ما إذا كان التسجيل مفعل
    private boolean isLoggingEnabled() {
        return prefs.getBoolean(KEY_LOG_ENABLED, true);
    }
    
    // توليد معرف نشاط
    private String generateActivityId() {
        return "activity_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 10000);
    }
    
    // فحص ما إذا كان النشاط يطابق المرشح
    private boolean matchesFilter(ActivityEntry entry, ActivityFilter filter) {
        // فحص النوع
        if (filter.type != null && !filter.type.equals(entry.type)) {
            return false;
        }
        
        // فحص المستخدم
        if (filter.userId != null && !filter.userId.equals(entry.userId)) {
            return false;
        }
        
        // فحص الأولوية
        if (filter.minPriority > 0 && entry.priority < filter.minPriority) {
            return false;
        }
        
        // فحص الوقت
        if (filter.fromTime > 0 && entry.timestamp < filter.fromTime) {
            return false;
        }
        
        if (filter.toTime > 0 && entry.timestamp > filter.toTime) {
            return false;
        }
        
        // فحص الأنشطة الإدارية فقط
        if (filter.adminOnly && !"admin".equals(entry.userRole)) {
            return false;
        }
        
        return true;
    }
    
    // إشعار الإدارة بالنشاط المهم
    private void notifyAdminOfActivity(ActivityEntry entry) {
        try {
            NotificationManager notificationManager = NotificationManager.getInstance(context);
            
            String message = String.format(
                "نشاط مهم: %s\nالمستخدم: %s\nالوقت: %s",
                entry.description,
                entry.username,
                dateFormat.format(new Date(entry.timestamp))
            );
            
            notificationManager.showAdminNotification(
                "نشاط مهم في النظام",
                message,
                entry.priority
            );
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إشعار الإدارة", e);
        }
    }
    
    // تحميل كاش الأنشطة
    private void loadActivityCache() {
        // هنا يجب تحميل البيانات من التخزين الدائم
        // يمكن استخدام SQLite أو SharedPreferences للتخزين
    }
    
    // حفظ كاش الأنشطة
    private void saveActivityCache() {
        // هنا يجب حفظ البيانات في التخزين الدائم
    }
    
    // فئة إدخال النشاط
    public static class ActivityEntry {
        public String id;
        public String type;
        public String description;
        public int priority;
        public long timestamp;
        public String userId;
        public String username;
        public String userRole;
        public JSONObject additionalData;
        
        public String getFormattedDate() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return formatter.format(new Date(timestamp));
        }
        
        public String getPriorityText() {
            switch (priority) {
                case PRIORITY_LOW: return "منخفض";
                case PRIORITY_MEDIUM: return "متوسط";
                case PRIORITY_HIGH: return "عالي";
                case PRIORITY_CRITICAL: return "حرج";
                default: return "غير محدد";
            }
        }
    }
    
    // فئة مرشح الأنشطة
    public static class ActivityFilter {
        public String type;
        public String userId;
        public int minPriority = 0;
        public long fromTime = 0;
        public long toTime = 0;
        public boolean adminOnly = false;
        public int limit = 0;
    }
    
    // فئة إحصائيات الأنشطة
    public static class ActivityStats {
        public int totalActivities = 0;
        public int lowPriorityCount = 0;
        public int mediumPriorityCount = 0;
        public int highPriorityCount = 0;
        public int criticalPriorityCount = 0;
        public Map<String, Integer> typeStats = new HashMap<>();
        public Map<String, Integer> userStats = new HashMap<>();
    }
}
