package com.example.accountingapp.advanced;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationManager {
    private static final String TAG = "NotificationManager";
    private static final String PREFS_NAME = "notification_prefs";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
    private static final String KEY_ADMIN_NOTIFICATIONS_ENABLED = "admin_notifications_enabled";
    
    private static final String CHANNEL_ID = "accounting_app_channel";
    private static final String ADMIN_CHANNEL_ID = "admin_notifications_channel";
    
    private static NotificationManager instance;
    private SharedPreferences prefs;
    private Context context;
    private android.app.NotificationManager systemNotificationManager;
    
    private NotificationManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.systemNotificationManager = (android.app.NotificationManager) 
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        createNotificationChannels();
    }
    
    public static synchronized NotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationManager(context);
        }
        return instance;
    }
    
    // إنشاء قنوات الإشعارات
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // قناة الإشعارات العامة
            NotificationChannel generalChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "إشعارات التطبيق",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
            );
            generalChannel.setDescription("إشعارات عامة للتطبيق");
            
            // قناة إشعارات الإداريين
            NotificationChannel adminChannel = new NotificationChannel(
                    ADMIN_CHANNEL_ID,
                    "إشعارات الإداريين",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            adminChannel.setDescription("إشعارات خاصة بالمستخدمين الإداريين");
            
            systemNotificationManager.createNotificationChannel(generalChannel);
            systemNotificationManager.createNotificationChannel(adminChannel);
        }
    }
    
    // إرسال إشعار إداري
    public void sendAdminNotification(ActivityLogManager.ActivityType activityType, 
                                    String description, String userId, String username) {
        if (!isAdminNotificationsEnabled()) {
            return;
        }
        
        try {
            // إنشاء الإشعار
            AppNotification notification = new AppNotification();
            notification.id = System.currentTimeMillis() + "_admin";
            notification.type = NotificationType.ADMIN_ACTIVITY;
            notification.title = "نشاط إداري جديد";
            notification.message = username + " قام بـ " + activityType.getArabicName() + ": " + description;
            notification.userId = userId;
            notification.username = username;
            notification.timestamp = System.currentTimeMillis();
            notification.isRead = false;
            notification.priority = NotificationPriority.HIGH;
            
            // حفظ الإشعار
            saveNotification(notification);
            
            // عرض الإشعار في النظام
            showSystemNotification(notification);
            
            Log.d(TAG, "تم إرسال إشعار إداري: " + description);
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إرسال الإشعار الإداري", e);
        }
    }
    
    // إرسال إشعار عام
    public void sendNotification(NotificationType type, String title, String message) {
        String currentUserId = OfflineSessionManager.getInstance(context).getCurrentUserId();
        sendNotification(type, title, message, currentUserId, NotificationPriority.NORMAL);
    }
    
    // إرسال إشعار مع تفاصيل
    public void sendNotification(NotificationType type, String title, String message, 
                               String targetUserId, NotificationPriority priority) {
        if (!isNotificationsEnabled()) {
            return;
        }
        
        try {
            AppNotification notification = new AppNotification();
            notification.id = System.currentTimeMillis() + "_" + targetUserId;
            notification.type = type;
            notification.title = title;
            notification.message = message;
            notification.userId = targetUserId;
            notification.timestamp = System.currentTimeMillis();
            notification.isRead = false;
            notification.priority = priority;
            
            saveNotification(notification);
            
            // عرض الإشعار في النظام إذا كان للمستخدم الحالي
            String currentUserId = OfflineSessionManager.getInstance(context).getCurrentUserId();
            if (targetUserId.equals(currentUserId)) {
                showSystemNotification(notification);
            }
            
            Log.d(TAG, "تم إرسال إشعار: " + title);
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إرسال الإشعار", e);
        }
    }
    
    // حفظ الإشعار
    private void saveNotification(AppNotification notification) throws JSONException {
        JSONArray notifications = getNotifications();
        
        JSONObject notificationObj = new JSONObject();
        notificationObj.put("id", notification.id);
        notificationObj.put("type", notification.type.name());
        notificationObj.put("title", notification.title);
        notificationObj.put("message", notification.message);
        notificationObj.put("user_id", notification.userId);
        notificationObj.put("username", notification.username);
        notificationObj.put("timestamp", notification.timestamp);
        notificationObj.put("is_read", notification.isRead);
        notificationObj.put("priority", notification.priority.name());
        
        notifications.put(notificationObj);
        
        // الاحتفاظ بآخر 500 إشعار فقط
        if (notifications.length() > 500) {
            JSONArray newNotifications = new JSONArray();
            int startIndex = notifications.length() - 500;
            for (int i = startIndex; i < notifications.length(); i++) {
                newNotifications.put(notifications.get(i));
            }
            notifications = newNotifications;
        }
        
        prefs.edit().putString(KEY_NOTIFICATIONS, notifications.toString()).apply();
    }
    
    // عرض الإشعار في النظام
    private void showSystemNotification(AppNotification notification) {
        if (systemNotificationManager == null) {
            return;
        }
        
        try {
            // تحديد القناة والأيقونة
            String channelId = notification.type == NotificationType.ADMIN_ACTIVITY ? 
                    ADMIN_CHANNEL_ID : CHANNEL_ID;
            
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle(notification.title)
                    .setContentText(notification.message)
                    .setAutoCancel(true);
            
            // تعيين الأولوية
            switch (notification.priority) {
                case HIGH:
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                    break;
                case LOW:
                    builder.setPriority(NotificationCompat.PRIORITY_LOW);
                    break;
                default:
                    builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    break;
            }
            
            // عرض الإشعار
            int notificationId = (int) (notification.timestamp % Integer.MAX_VALUE);
            systemNotificationManager.notify(notificationId, builder.build());
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في عرض إشعار النظام", e);
        }
    }
    
    // الحصول على قائمة الإشعارات
    public JSONArray getNotifications() {
        String notificationsString = prefs.getString(KEY_NOTIFICATIONS, "[]");
        try {
            return new JSONArray(notificationsString);
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في قراءة الإشعارات", e);
            return new JSONArray();
        }
    }
    
    // الحصول على إشعارات المستخدم الحالي
    public List<AppNotification> getUserNotifications() {
        String currentUserId = OfflineSessionManager.getInstance(context).getCurrentUserId();
        return getUserNotifications(currentUserId);
    }
    
    // الحصول على إشعارات مستخدم معين
    public List<AppNotification> getUserNotifications(String userId) {
        List<AppNotification> userNotifications = new ArrayList<>();
        JSONArray notifications = getNotifications();
        
        try {
            for (int i = notifications.length() - 1; i >= 0; i--) { // الأحدث أولاً
                JSONObject notificationObj = notifications.getJSONObject(i);
                
                if (notificationObj.getString("user_id").equals(userId)) {
                    AppNotification notification = new AppNotification();
                    notification.id = notificationObj.getString("id");
                    notification.type = NotificationType.valueOf(notificationObj.getString("type"));
                    notification.title = notificationObj.getString("title");
                    notification.message = notificationObj.getString("message");
                    notification.userId = notificationObj.getString("user_id");
                    notification.username = notificationObj.optString("username", "");
                    notification.timestamp = notificationObj.getLong("timestamp");
                    notification.isRead = notificationObj.getBoolean("is_read");
                    notification.priority = NotificationPriority.valueOf(
                            notificationObj.optString("priority", "NORMAL"));
                    
                    userNotifications.add(notification);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في تحويل الإشعارات", e);
        }
        
        return userNotifications;
    }
    
    // تمييز الإشعار كمقروء
    public void markAsRead(String notificationId) {
        try {
            JSONArray notifications = getNotifications();
            boolean updated = false;
            
            for (int i = 0; i < notifications.length(); i++) {
                JSONObject notification = notifications.getJSONObject(i);
                if (notification.getString("id").equals(notificationId)) {
                    notification.put("is_read", true);
                    updated = true;
                    break;
                }
            }
            
            if (updated) {
                prefs.edit().putString(KEY_NOTIFICATIONS, notifications.toString()).apply();
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في تمييز الإشعار كمقروء", e);
        }
    }
    
    // حذف إشعار
    public void deleteNotification(String notificationId) {
        try {
            JSONArray notifications = getNotifications();
            JSONArray newNotifications = new JSONArray();
            
            for (int i = 0; i < notifications.length(); i++) {
                JSONObject notification = notifications.getJSONObject(i);
                if (!notification.getString("id").equals(notificationId)) {
                    newNotifications.put(notification);
                }
            }
            
            prefs.edit().putString(KEY_NOTIFICATIONS, newNotifications.toString()).apply();
            
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في حذف الإشعار", e);
        }
    }
    
    // عدد الإشعارات غير المقروءة
    public int getUnreadNotificationsCount() {
        String currentUserId = OfflineSessionManager.getInstance(context).getCurrentUserId();
        return getUnreadNotificationsCount(currentUserId);
    }
    
    // عدد الإشعارات غير المقروءة لمستخدم معين
    public int getUnreadNotificationsCount(String userId) {
        int count = 0;
        List<AppNotification> notifications = getUserNotifications(userId);
        
        for (AppNotification notification : notifications) {
            if (!notification.isRead) {
                count++;
            }
        }
        
        return count;
    }
    
    // تمكين/تعطيل الإشعارات
    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply();
    }
    
    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true);
    }
    
    // تمكين/تعطيل إشعارات الإداريين
    public void setAdminNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ADMIN_NOTIFICATIONS_ENABLED, enabled).apply();
    }
    
    public boolean isAdminNotificationsEnabled() {
        return prefs.getBoolean(KEY_ADMIN_NOTIFICATIONS_ENABLED, true);
    }
    
    // مسح جميع الإشعارات
    public void clearAllNotifications() {
        prefs.edit().remove(KEY_NOTIFICATIONS).apply();
        Log.d(TAG, "تم مسح جميع الإشعارات");
    }
    
    // مسح الإشعارات المقروءة
    public void clearReadNotifications() {
        try {
            JSONArray notifications = getNotifications();
            JSONArray newNotifications = new JSONArray();
            
            for (int i = 0; i < notifications.length(); i++) {
                JSONObject notification = notifications.getJSONObject(i);
                if (!notification.getBoolean("is_read")) {
                    newNotifications.put(notification);
                }
            }
            
            prefs.edit().putString(KEY_NOTIFICATIONS, newNotifications.toString()).apply();
            
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في مسح الإشعارات المقروءة", e);
        }
    }
    
    // أنواع الإشعارات
    public enum NotificationType {
        ADMIN_ACTIVITY("نشاط إداري"),
        BACKUP_REMINDER("تذكير نسخة احتياطية"),
        SYSTEM_UPDATE("تحديث النظام"),
        FINANCIAL_ALERT("تنبيه مالي"),
        USER_ACTION("إجراء مستخدم"),
        GENERAL("عام");
        
        private final String arabicName;
        
        NotificationType(String arabicName) {
            this.arabicName = arabicName;
        }
        
        public String getArabicName() {
            return arabicName;
        }
    }
    
    // أولويات الإشعارات
    public enum NotificationPriority {
        LOW, NORMAL, HIGH
    }
    
    // كلاس الإشعار
    public static class AppNotification {
        public String id;
        public NotificationType type;
        public String title;
        public String message;
        public String userId;
        public String username;
        public long timestamp;
        public boolean isRead;
        public NotificationPriority priority;
        
        public String getFormattedDate() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(new Date(timestamp));
        }
        
        public String getTypeDisplayName() {
            return type.getArabicName();
        }
    }
}
