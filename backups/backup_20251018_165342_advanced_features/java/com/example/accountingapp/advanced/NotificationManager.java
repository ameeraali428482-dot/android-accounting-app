package com.example.accountingapp.advanced;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.*;

public class NotificationManager {
    private static final String TAG = "NotificationManager";
    private static final String PREFS_NAME = "notification_prefs";
    
    // قنوات الإشعارات
    private static final String CHANNEL_ADMIN = "admin_notifications";
    private static final String CHANNEL_BACKUP = "backup_notifications";
    private static final String CHANNEL_SECURITY = "security_notifications";
    private static final String CHANNEL_ACTIVITY = "activity_notifications";
    private static final String CHANNEL_GENERAL = "general_notifications";
    
    // أنواع الإشعارات
    public static final String TYPE_ADMIN_ACTIVITY = "admin_activity";
    public static final String TYPE_BACKUP_RESTORE = "backup_restore";
    public static final String TYPE_SECURITY_ALERT = "security_alert";
    public static final String TYPE_LOGIN_ALERT = "login_alert";
    public static final String TYPE_DATA_CHANGE = "data_change";
    public static final String TYPE_SYSTEM_UPDATE = "system_update";
    
    private static NotificationManager instance;
    private Context context;
    private NotificationManagerCompat notificationManager;
    private SharedPreferences prefs;
    private SimpleDateFormat dateFormat;
    private List<NotificationEntry> notificationHistory;
    
    private NotificationManager(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = NotificationManagerCompat.from(context);
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.notificationHistory = new ArrayList<>();
        
        createNotificationChannels();
        loadNotificationHistory();
    }
    
    public static synchronized NotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationManager(context);
        }
        return instance;
    }
    
    // إشعار بنشاط إداري
    public void showAdminNotification(String title, String message, int priority) {
        if (!isNotificationEnabled(TYPE_ADMIN_ACTIVITY)) return;
        
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ADMIN)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(getPriorityLevel(priority))
                .setAutoCancel(true);
            
            // إضافة أيقونة حسب الأولوية
            if (priority >= ActivityLogManager.PRIORITY_HIGH) {
                builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
            } else {
                builder.setSmallIcon(android.R.drawable.ic_dialog_info);
            }
            
            int notificationId = generateNotificationId();
            notificationManager.notify(notificationId, builder.build());
            
            // حفظ في التاريخ
            saveNotificationToHistory(TYPE_ADMIN_ACTIVITY, title, message, priority);
            
            Log.d(TAG, "تم عرض إشعار إداري: " + title);
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في عرض الإشعار الإداري", e);
        }
    }
    
    // إشعار استرجاع النسخة الاحتياطية
    public void showBackupRestoreNotification(BackupManager.BackupInfo backup, String message) {
        if (!isNotificationEnabled(TYPE_BACKUP_RESTORE)) return;
        
        try {
            // إنشاء أزرار الإجراءات
            Intent restoreIntent = new Intent(context, BackupRestoreActivity.class);
            restoreIntent.putExtra("backup_path", backup.filePath);
            restoreIntent.putExtra("action", "restore");
            PendingIntent restorePendingIntent = PendingIntent.getActivity(
                context, 0, restoreIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            
            Intent mergeIntent = new Intent(context, BackupRestoreActivity.class);
            mergeIntent.putExtra("backup_path", backup.filePath);
            mergeIntent.putExtra("action", "merge");
            PendingIntent mergePendingIntent = PendingIntent.getActivity(
                context, 1, mergeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_BACKUP)
                .setContentTitle("نسخة احتياطية متاحة")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(android.R.drawable.ic_menu_save)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .addAction(android.R.drawable.ic_menu_revert, "استرجاع", restorePendingIntent)
                .addAction(android.R.drawable.ic_menu_add, "دمج", mergePendingIntent);
            
            int notificationId = generateNotificationId();
            notificationManager.notify(notificationId, builder.build());
            
            saveNotificationToHistory(TYPE_BACKUP_RESTORE, "نسخة احتياطية متاحة", message, 
                                    ActivityLogManager.PRIORITY_HIGH);
            
            Log.d(TAG, "تم عرض إشعار النسخة الاحتياطية");
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في عرض إشعار النسخة الاحتياطية", e);
        }
    }
    
    // إشعار أمني
    public void showSecurityAlert(String alertType, String details) {
        if (!isNotificationEnabled(TYPE_SECURITY_ALERT)) return;
        
        try {
            String title = getSecurityAlertTitle(alertType);
            
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_SECURITY)
                .setContentTitle(title)
                .setContentText(details)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(details))
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 250, 500});
            
            int notificationId = generateNotificationId();
            notificationManager.notify(notificationId, builder.build());
            
            saveNotificationToHistory(TYPE_SECURITY_ALERT, title, details, 
                                    ActivityLogManager.PRIORITY_CRITICAL);
            
            // تسجيل في سجل الأنشطة
            ActivityLogManager.getInstance(context).logActivity(
                ActivityLogManager.TYPE_SECURITY,
                "تنبيه أمني: " + alertType + " - " + details,
                ActivityLogManager.PRIORITY_CRITICAL
            );
            
            Log.d(TAG, "تم عرض تنبيه أمني: " + alertType);
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في عرض التنبيه الأمني", e);
        }
    }
    
    // إشعار تسجيل دخول
    public void showLoginAlert(String username, String deviceInfo, boolean suspicious) {
        if (!isNotificationEnabled(TYPE_LOGIN_ALERT)) return;
        
        try {
            String title = suspicious ? "محاولة دخول مشبوهة" : "تسجيل دخول جديد";
            String message = String.format("المستخدم: %s\nالجهاز: %s\nالوقت: %s",
                username, deviceInfo, dateFormat.format(new Date()));
            
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_SECURITY)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(suspicious ? android.R.drawable.ic_dialog_alert : 
                             android.R.drawable.ic_dialog_info)
                .setPriority(suspicious ? NotificationCompat.PRIORITY_HIGH : 
                           NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
            
            if (suspicious) {
                builder.setVibrate(new long[]{0, 300, 150, 300});
            }
            
            int notificationId = generateNotificationId();
            notificationManager.notify(notificationId, builder.build());
            
            saveNotificationToHistory(TYPE_LOGIN_ALERT, title, message,
                suspicious ? ActivityLogManager.PRIORITY_HIGH : ActivityLogManager.PRIORITY_MEDIUM);
            
            Log.d(TAG, "تم عرض إشعار تسجيل الدخول");
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في عرض إشعار تسجيل الدخول", e);
        }
    }
    
    // إشعار تغيير البيانات
    public void showDataChangeNotification(String changeType, String details, String userId) {
        if (!isNotificationEnabled(TYPE_DATA_CHANGE)) return;
        
        // فحص ما إذا كان المستخدم الحالي هو المدير
        String currentUserId = OfflineSessionManager.getInstance(context).getCurrentUserId();
        String currentRole = OfflineSessionManager.getInstance(context).getCurrentUserRole();
        
        // إشعار المدير فقط إذا لم يكن هو من قام بالتغيير
        if ("admin".equals(currentRole) && !currentUserId.equals(userId)) {
            try {
                String username = getUsernameById(userId);
                String title = "تم تعديل البيانات";
                String message = String.format("النوع: %s\nبواسطة: %s\nالتفاصيل: %s",
                    changeType, username, details);
                
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ACTIVITY)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSmallIcon(android.R.drawable.ic_menu_edit)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
                
                int notificationId = generateNotificationId();
                notificationManager.notify(notificationId, builder.build());
                
                saveNotificationToHistory(TYPE_DATA_CHANGE, title, message,
                                        ActivityLogManager.PRIORITY_MEDIUM);
                
                Log.d(TAG, "تم عرض إشعار تغيير البيانات");
                
            } catch (Exception e) {
                Log.e(TAG, "خطأ في عرض إشعار تغيير البيانات", e);
            }
        }
    }
    
    // إشعار عام
    public void showGeneralNotification(String title, String message) {
        showGeneralNotification(title, message, ActivityLogManager.PRIORITY_LOW);
    }
    
    public void showGeneralNotification(String title, String message, int priority) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_GENERAL)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(getPriorityLevel(priority))
                .setAutoCancel(true);
            
            int notificationId = generateNotificationId();
            notificationManager.notify(notificationId, builder.build());
            
            saveNotificationToHistory(TYPE_SYSTEM_UPDATE, title, message, priority);
            
            Log.d(TAG, "تم عرض إشعار عام: " + title);
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في عرض الإشعار العام", e);
        }
    }
    
    // الحصول على تاريخ الإشعارات
    public List<NotificationEntry> getNotificationHistory(int limit) {
        synchronized (notificationHistory) {
            List<NotificationEntry> result = new ArrayList<>(notificationHistory);
            
            // ترتيب حسب الوقت (الأحدث أولاً)
            Collections.sort(result, (a, b) -> Long.compare(b.timestamp, a.timestamp));
            
            if (limit > 0 && result.size() > limit) {
                result = result.subList(0, limit);
            }
            
            return result;
        }
    }
    
    // البحث في تاريخ الإشعارات
    public List<NotificationEntry> searchNotifications(String searchTerm, int limit) {
        List<NotificationEntry> results = new ArrayList<>();
        String searchLower = searchTerm.toLowerCase();
        
        synchronized (notificationHistory) {
            for (NotificationEntry entry : notificationHistory) {
                if (entry.title.toLowerCase().contains(searchLower) ||
                    entry.message.toLowerCase().contains(searchLower) ||
                    entry.type.toLowerCase().contains(searchLower)) {
                    
                    results.add(entry);
                    
                    if (limit > 0 && results.size() >= limit) {
                        break;
                    }
                }
            }
        }
        
        return results;
    }
    
    // تفعيل/إلغاء أنواع الإشعارات
    public void setNotificationEnabled(String type, boolean enabled) {
        prefs.edit().putBoolean("notification_" + type, enabled).apply();
    }
    
    public boolean isNotificationEnabled(String type) {
        return prefs.getBoolean("notification_" + type, true);
    }
    
    // مسح الإشعارات القديمة
    public void clearOldNotifications(int daysToKeep) {
        long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L);
        
        synchronized (notificationHistory) {
            Iterator<NotificationEntry> iterator = notificationHistory.iterator();
            int removedCount = 0;
            
            while (iterator.hasNext()) {
                NotificationEntry entry = iterator.next();
                if (entry.timestamp < cutoffTime) {
                    iterator.remove();
                    removedCount++;
                }
            }
            
            if (removedCount > 0) {
                saveNotificationHistory();
                Log.d(TAG, "تم مسح " + removedCount + " إشعار قديم");
            }
        }
    }
    
    // إنشاء قنوات الإشعارات
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // قناة الإشعارات الإدارية
            NotificationChannel adminChannel = new NotificationChannel(
                CHANNEL_ADMIN,
                "الإشعارات الإدارية",
                android.app.NotificationManager.IMPORTANCE_HIGH
            );
            adminChannel.setDescription("إشعارات الأنشطة الإدارية المهمة");
            
            // قناة النسخ الاحتياطية
            NotificationChannel backupChannel = new NotificationChannel(
                CHANNEL_BACKUP,
                "النسخ الاحتياطية",
                android.app.NotificationManager.IMPORTANCE_HIGH
            );
            backupChannel.setDescription("إشعارات النسخ الاحتياطية والاسترجاع");
            
            // قناة الأمان
            NotificationChannel securityChannel = new NotificationChannel(
                CHANNEL_SECURITY,
                "التنبيهات الأمنية",
                android.app.NotificationManager.IMPORTANCE_MAX
            );
            securityChannel.setDescription("تنبيهات الأمان ومحاولات الدخول");
            
            // قناة الأنشطة
            NotificationChannel activityChannel = new NotificationChannel(
                CHANNEL_ACTIVITY,
                "أنشطة النظام",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            );
            activityChannel.setDescription("إشعارات أنشطة المستخدمين");
            
            // قناة عامة
            NotificationChannel generalChannel = new NotificationChannel(
                CHANNEL_GENERAL,
                "إشعارات عامة",
                android.app.NotificationManager.IMPORTANCE_LOW
            );
            generalChannel.setDescription("إشعارات عامة ومعلومات النظام");
            
            // تسجيل القنوات
            android.app.NotificationManager manager = 
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            
            if (manager != null) {
                manager.createNotificationChannel(adminChannel);
                manager.createNotificationChannel(backupChannel);
                manager.createNotificationChannel(securityChannel);
                manager.createNotificationChannel(activityChannel);
                manager.createNotificationChannel(generalChannel);
            }
        }
    }
    
    // الحصول على مستوى الأولوية
    private int getPriorityLevel(int priority) {
        switch (priority) {
            case ActivityLogManager.PRIORITY_CRITICAL:
                return NotificationCompat.PRIORITY_MAX;
            case ActivityLogManager.PRIORITY_HIGH:
                return NotificationCompat.PRIORITY_HIGH;
            case ActivityLogManager.PRIORITY_MEDIUM:
                return NotificationCompat.PRIORITY_DEFAULT;
            case ActivityLogManager.PRIORITY_LOW:
            default:
                return NotificationCompat.PRIORITY_LOW;
        }
    }
    
    // الحصول على عنوان التنبيه الأمني
    private String getSecurityAlertTitle(String alertType) {
        switch (alertType) {
            case "FAILED_LOGIN":
                return "فشل تسجيل الدخول";
            case "SUSPICIOUS_ACTIVITY":
                return "نشاط مشبوه";
            case "UNAUTHORIZED_ACCESS":
                return "محاولة وصول غير مصرح";
            case "DATA_BREACH":
                return "اختراق محتمل للبيانات";
            default:
                return "تنبيه أمني";
        }
    }
    
    // الحصول على اسم المستخدم بالمعرف
    private String getUsernameById(String userId) {
        // هنا يجب البحث في قاعدة البيانات عن اسم المستخدم
        return "مستخدم_" + userId;
    }
    
    // توليد معرف إشعار فريد
    private int generateNotificationId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }
    
    // حفظ الإشعار في التاريخ
    private void saveNotificationToHistory(String type, String title, String message, int priority) {
        synchronized (notificationHistory) {
            NotificationEntry entry = new NotificationEntry();
            entry.type = type;
            entry.title = title;
            entry.message = message;
            entry.priority = priority;
            entry.timestamp = System.currentTimeMillis();
            
            notificationHistory.add(0, entry); // إضافة في المقدمة
            
            // تحديد العدد (الاحتفاظ بآخر 500 إشعار)
            while (notificationHistory.size() > 500) {
                notificationHistory.remove(notificationHistory.size() - 1);
            }
            
            saveNotificationHistory();
        }
    }
    
    // تحميل تاريخ الإشعارات
    private void loadNotificationHistory() {
        // هنا يجب تحميل البيانات من التخزين الدائم
    }
    
    // حفظ تاريخ الإشعارات
    private void saveNotificationHistory() {
        // هنا يجب حفظ البيانات في التخزين الدائم
    }
    
    // فئة إدخال الإشعار
    public static class NotificationEntry {
        public String type;
        public String title;
        public String message;
        public int priority;
        public long timestamp;
        
        public String getFormattedDate() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return formatter.format(new Date(timestamp));
        }
        
        public String getPriorityText() {
            switch (priority) {
                case ActivityLogManager.PRIORITY_LOW: return "منخفض";
                case ActivityLogManager.PRIORITY_MEDIUM: return "متوسط";
                case ActivityLogManager.PRIORITY_HIGH: return "عالي";
                case ActivityLogManager.PRIORITY_CRITICAL: return "حرج";
                default: return "غير محدد";
            }
        }
    }
}
