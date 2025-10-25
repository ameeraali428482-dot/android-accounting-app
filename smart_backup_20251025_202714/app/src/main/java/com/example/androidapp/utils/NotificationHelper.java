package com.example.androidapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.Notification;
import com.example.androidapp.data.entities.Transaction;
import com.example.androidapp.data.entities.User;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.telephony.SmsManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;

/**
 * مساعد الإشعارات المتقدم - يدير جميع أنواع الإشعارات والتذكيرات الذكية
 * Advanced Notification Helper - manages all types of notifications and smart reminders
 */
public class NotificationHelper {
    
    private static final String TAG = "NotificationHelper";
    private static final String PREFS_NAME = "notification_prefs";
    
    // قنوات الإشعارات
    private static final String CHANNEL_TRANSACTIONS = "transactions_channel";
    private static final String CHANNEL_REMINDERS = "reminders_channel";
    private static final String CHANNEL_ALERTS = "alerts_channel";
    private static final String CHANNEL_SYSTEM = "system_channel";
    private static final String CHANNEL_BACKUP = "backup_channel";
    
    // أنواع الإشعارات
    public static final int TYPE_TRANSACTION = 1;
    public static final int TYPE_REMINDER = 2;
    public static final int TYPE_ALERT = 3;
    public static final int TYPE_SYSTEM = 4;
    public static final int TYPE_BACKUP = 5;
    public static final int TYPE_INVENTORY = 6;
    public static final int TYPE_ACCOUNT_ACTIVITY = 7;
    
    // طرق الإرسال
    public static final int METHOD_APP = 1;
    public static final int METHOD_SMS = 2;
    public static final int METHOD_WHATSAPP = 3;
    public static final int METHOD_TELEGRAM = 4;
    public static final int METHOD_EMAIL = 5;
    
    private Context context;
    private NotificationManager notificationManager;
    private AppDatabase database;
    private SharedPreferences prefs;
    private ExecutorService executorService;
    
    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.database = Room.databaseBuilder(context, AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration()
                .build();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.executorService = Executors.newCachedThreadPool();
        
        createNotificationChannels();
    }
    
    /**
     * إنشاء قنوات الإشعارات
     * Create notification channels
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            
            // قناة المعاملات
            NotificationChannel transactionsChannel = new NotificationChannel(
                    CHANNEL_TRANSACTIONS,
                    "إشعارات المعاملات",
                    NotificationManager.IMPORTANCE_HIGH
            );
            transactionsChannel.setDescription("إشعارات المعاملات المالية الجديدة");
            transactionsChannel.enableVibration(true);
            transactionsChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
            
            // قناة التذكيرات
            NotificationChannel remindersChannel = new NotificationChannel(
                    CHANNEL_REMINDERS,
                    "التذكيرات الذكية",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            remindersChannel.setDescription("التذكيرات الدورية والذكية");
            remindersChannel.enableVibration(true);
            
            // قناة التنبيهات
            NotificationChannel alertsChannel = new NotificationChannel(
                    CHANNEL_ALERTS,
                    "تنبيهات النظام",
                    NotificationManager.IMPORTANCE_HIGH
            );
            alertsChannel.setDescription("تنبيهات مهمة حول النظام والحسابات");
            alertsChannel.enableVibration(true);
            alertsChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null);
            
            // قناة النظام
            NotificationChannel systemChannel = new NotificationChannel(
                    CHANNEL_SYSTEM,
                    "إشعارات النظام",
                    NotificationManager.IMPORTANCE_LOW
            );
            systemChannel.setDescription("إشعارات النظام العامة");
            
            // قناة النسخ الاحتياطي
            NotificationChannel backupChannel = new NotificationChannel(
                    CHANNEL_BACKUP,
                    "النسخ الاحتياطي",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            backupChannel.setDescription("إشعارات النسخ الاحتياطي والمزامنة");
            
            // تسجيل القنوات
            notificationManager.createNotificationChannel(transactionsChannel);
            notificationManager.createNotificationChannel(remindersChannel);
            notificationManager.createNotificationChannel(alertsChannel);
            notificationManager.createNotificationChannel(systemChannel);
            notificationManager.createNotificationChannel(backupChannel);
        }
    }
    
    /**
     * إرسال إشعار معاملة جديدة
     * Send new transaction notification
     */
    public void sendTransactionNotification(Transaction transaction, Account fromAccount, Account toAccount) {
        executorService.execute(() -> {
            try {
                // إنشاء محتوى الإشعار
                String title = "معاملة جديدة";
                String content = formatTransactionMessage(transaction, fromAccount, toAccount);
                
                // إرسال إشعار التطبيق
                sendAppNotification(TYPE_TRANSACTION, title, content, transaction.id);
                
                // حفظ الإشعار في قاعدة البيانات
                saveNotificationToDatabase(TYPE_TRANSACTION, title, content, transaction.id);
                
                // إرسال إشعارات خارجية إذا كانت مفعلة
                if (shouldSendExternalNotifications(TYPE_TRANSACTION)) {
                    sendExternalNotifications(transaction, fromAccount, toAccount);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error sending transaction notification", e);
            }
        });
    }
    
    /**
     * إرسال تذكير دوري ذكي
     * Send smart periodic reminder
     */
    public void sendSmartReminder(String reminderType, Map<String, Object> data) {
        executorService.execute(() -> {
            try {
                String title = "";
                String content = "";
                
                switch (reminderType) {
                    case "PAYMENT_DUE":
                        title = "تذكير بدفعة مستحقة";
                        content = "لديك دفعة مستحقة بقيمة " + data.get("amount") + " لحساب " + data.get("accountName");
                        break;
                    case "INVENTORY_LOW":
                        title = "تنبيه مخزون منخفض";
                        content = "المخزون منخفض للصنف: " + data.get("itemName") + " (المتبقي: " + data.get("quantity") + ")";
                        break;
                    case "ACCOUNT_INACTIVE":
                        title = "حساب غير نشط";
                        content = "لم يتم تسجيل معاملات لحساب " + data.get("accountName") + " منذ " + data.get("days") + " يوم";
                        break;
                    case "BACKUP_REMINDER":
                        title = "تذكير النسخ الاحتياطي";
                        content = "لم يتم إنشاء نسخة احتياطية منذ " + data.get("days") + " يوم";
                        break;
                }
                
                // إرسال التذكير
                sendAppNotification(TYPE_REMINDER, title, content, 0);
                saveNotificationToDatabase(TYPE_REMINDER, title, content, 0);
                
                // إرسال عبر قنوات خارجية إذا كانت مفعلة
                if (shouldSendExternalReminders()) {
                    sendExternalReminder(title, content);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error sending smart reminder", e);
            }
        });
    }
    
    /**
     * إرسال تنبيه هام
     * Send critical alert
     */
    public void sendCriticalAlert(String alertType, String message, long relatedId) {
        executorService.execute(() -> {
            try {
                String title = "";
                
                switch (alertType) {
                    case "SECURITY_BREACH":
                        title = "تنبيه أمني";
                        break;
                    case "DATA_CORRUPTION":
                        title = "تلف في البيانات";
                        break;
                    case "SYNC_FAILED":
                        title = "فشل المزامنة";
                        break;
                    case "UNAUTHORIZED_ACCESS":
                        title = "محاولة دخول غير مصرح";
                        break;
                    default:
                        title = "تنبيه هام";
                        break;
                }
                
                // إرسال التنبيه الفوري
                sendAppNotification(TYPE_ALERT, title, message, relatedId);
                saveNotificationToDatabase(TYPE_ALERT, title, message, relatedId);
                
                // إرسال عبر جميع القنوات للتنبيهات الحرجة
                sendCriticalExternalAlert(title, message);
                
            } catch (Exception e) {
                Log.e(TAG, "Error sending critical alert", e);
            }
        });
    }
    
    /**
     * إرسال إشعار نشاط الحساب
     * Send account activity notification
     */
    public void sendAccountActivityNotification(String accountName, String activityType, String details, String userId) {
        executorService.execute(() -> {
            try {
                String title = "نشاط حساب: " + accountName;
                String content = String.format("تم %s - %s", activityType, details);
                
                sendAppNotification(TYPE_ACCOUNT_ACTIVITY, title, content, 0);
                saveNotificationToDatabase(TYPE_ACCOUNT_ACTIVITY, title, content, 0);
                
                // إرسال للمستخدمين المرتبطين بالحساب
                sendToRelatedUsers(accountName, title, content);
                
            } catch (Exception e) {
                Log.e(TAG, "Error sending account activity notification", e);
            }
        });
    }
    
    /**
     * إرسال إشعار التطبيق
     * Send app notification
     */
    private void sendAppNotification(int type, String title, String content, long relatedId) {
        String channelId = getChannelIdForType(type);
        int notificationId = generateNotificationId(type, relatedId);
        
        Intent intent = createNotificationIntent(type, relatedId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                notificationId, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(getIconForType(type))
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(getPriorityForType(type))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis());
        
        // إضافة صوت وإضاءة للإشعارات المهمة
        if (type == TYPE_ALERT || type == TYPE_TRANSACTION) {
            builder.setLights(0xFF0000FF, 500, 500);
            builder.setVibrate(new long[]{0, 250, 250, 250});
        }
        
        notificationManager.notify(notificationId, builder.build());
    }
    
    /**
     * إرسال إشعارات خارجية للمعاملات
     * Send external notifications for transactions
     */
    private void sendExternalNotifications(Transaction transaction, Account fromAccount, Account toAccount) {
        String message = formatTransactionMessage(transaction, fromAccount, toAccount);
        
        // الحصول على طرق الإرسال المفعلة
        Set<Integer> enabledMethods = getEnabledNotificationMethods();
        
        for (int method : enabledMethods) {
            switch (method) {
                case METHOD_SMS:
                    sendSmsNotification(message, transaction.userId);
                    break;
                case METHOD_WHATSAPP:
                    sendWhatsAppNotification(message, transaction.userId);
                    break;
                case METHOD_TELEGRAM:
                    sendTelegramNotification(message, transaction.userId);
                    break;
                case METHOD_EMAIL:
                    sendEmailNotification("معاملة جديدة", message, transaction.userId);
                    break;
            }
        }
    }
    
    /**
     * إرسال رسالة نصية
     * Send SMS notification
     */
    private void sendSmsNotification(String message, String userId) {
        try {
            String phoneNumber = getUserPhoneNumber(userId);
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Log.d(TAG, "SMS sent successfully to " + phoneNumber);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error sending SMS", e);
        }
    }
    
    /**
     * إرسال رسالة واتساب
     * Send WhatsApp notification
     */
    private void sendWhatsAppNotification(String message, String userId) {
        try {
            String phoneNumber = getUserPhoneNumber(userId);
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                // استخدام WhatsApp Business API
                String whatsappApiUrl = "https://api.whatsapp.com/send";
                String url = whatsappApiUrl + "?phone=" + phoneNumber + "&text=" + 
                           java.net.URLEncoder.encode(message, "UTF-8");
                
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error sending WhatsApp message", e);
        }
    }
    
    /**
     * إرسال رسالة تيليجرام
     * Send Telegram notification
     */
    private void sendTelegramNotification(String message, String userId) {
        try {
            String telegramBotToken = getTelegramBotToken();
            String chatId = getUserTelegramChatId(userId);
            
            if (telegramBotToken != null && chatId != null) {
                String url = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage";
                
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                
                String payload = String.format(
                    "{\"chat_id\":\"%s\",\"text\":\"%s\"}",
                    chatId, message.replace("\"", "\\\"")
                );
                
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(payload);
                writer.flush();
                writer.close();
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Telegram API response: " + responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error sending Telegram message", e);
        }
    }
    
    /**
     * إرسال بريد إلكتروني
     * Send email notification
     */
    private void sendEmailNotification(String subject, String message, String userId) {
        try {
            String email = getUserEmail(userId);
            if (email != null && !email.isEmpty()) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(Intent.createChooser(emailIntent, "إرسال بريد إلكتروني"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error sending email", e);
        }
    }
    
    /**
     * تنسيق رسالة المعاملة
     * Format transaction message
     */
    private String formatTransactionMessage(Transaction transaction, Account fromAccount, Account toAccount) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String date = sdf.format(new Date(transaction.date));
        
        return String.format(
            "معاملة جديدة\n" +
            "المبلغ: %,.2f\n" +
            "من: %s\n" +
            "إلى: %s\n" +
            "التاريخ: %s\n" +
            "الوصف: %s",
            transaction.amount,
            fromAccount != null ? fromAccount.name : "غير محدد",
            toAccount != null ? toAccount.name : "غير محدد",
            date,
            transaction.description != null ? transaction.description : "لا يوجد وصف"
        );
    }
    
    /**
     * حفظ الإشعار في قاعدة البيانات
     * Save notification to database
     */
    private void saveNotificationToDatabase(int type, String title, String content, long relatedId) {
        Notification notification = new Notification();
        notification.type = type;
        notification.title = title;
        notification.content = content;
        notification.relatedId = relatedId;
        notification.timestamp = System.currentTimeMillis();
        notification.isRead = false;
        notification.userId = getCurrentUserId();
        
        database.notificationDao().insert(notification);
    }
    
    /**
     * إنشاء Intent للإشعار
     * Create notification intent
     */
    private Intent createNotificationIntent(int type, long relatedId) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        switch (type) {
            case TYPE_TRANSACTION:
                // فتح تفاصيل المعاملة
                intent.setAction("com.example.androidapp.VIEW_TRANSACTION");
                intent.putExtra("transaction_id", relatedId);
                break;
            case TYPE_REMINDER:
                // فتح شاشة التذكيرات
                intent.setAction("com.example.androidapp.VIEW_REMINDERS");
                break;
            case TYPE_ALERT:
                // فتح شاشة التنبيهات
                intent.setAction("com.example.androidapp.VIEW_ALERTS");
                break;
            default:
                // فتح الشاشة الرئيسية
                intent.setAction("com.example.androidapp.MAIN_ACTIVITY");
                break;
        }
        
        return intent;
    }
    
    // Helper methods
    
    private String getChannelIdForType(int type) {
        switch (type) {
            case TYPE_TRANSACTION:
            case TYPE_ACCOUNT_ACTIVITY:
                return CHANNEL_TRANSACTIONS;
            case TYPE_REMINDER:
                return CHANNEL_REMINDERS;
            case TYPE_ALERT:
                return CHANNEL_ALERTS;
            case TYPE_BACKUP:
                return CHANNEL_BACKUP;
            default:
                return CHANNEL_SYSTEM;
        }
    }
    
    private int getIconForType(int type) {
        switch (type) {
            case TYPE_TRANSACTION:
                return android.R.drawable.ic_dialog_info;
            case TYPE_REMINDER:
                return android.R.drawable.ic_popup_reminder;
            case TYPE_ALERT:
                return android.R.drawable.ic_dialog_alert;
            case TYPE_BACKUP:
                return android.R.drawable.ic_menu_save;
            default:
                return android.R.drawable.ic_dialog_info;
        }
    }
    
    private int getPriorityForType(int type) {
        switch (type) {
            case TYPE_ALERT:
                return NotificationCompat.PRIORITY_MAX;
            case TYPE_TRANSACTION:
                return NotificationCompat.PRIORITY_HIGH;
            case TYPE_REMINDER:
                return NotificationCompat.PRIORITY_DEFAULT;
            default:
                return NotificationCompat.PRIORITY_LOW;
        }
    }
    
    private int generateNotificationId(int type, long relatedId) {
        return (int) (type * 1000 + relatedId % 1000);
    }
    
    private boolean shouldSendExternalNotifications(int type) {
        return prefs.getBoolean("external_notifications_" + type, false);
    }
    
    private boolean shouldSendExternalReminders() {
        return prefs.getBoolean("external_reminders", false);
    }
    
    private Set<Integer> getEnabledNotificationMethods() {
        Set<Integer> methods = new HashSet<>();
        if (prefs.getBoolean("method_sms", false)) methods.add(METHOD_SMS);
        if (prefs.getBoolean("method_whatsapp", false)) methods.add(METHOD_WHATSAPP);
        if (prefs.getBoolean("method_telegram", false)) methods.add(METHOD_TELEGRAM);
        if (prefs.getBoolean("method_email", false)) methods.add(METHOD_EMAIL);
        return methods;
    }
    
    private String getCurrentUserId() {
        return prefs.getString("current_user_id", "default_user");
    }
    
    private String getUserPhoneNumber(String userId) {
        // الحصول على رقم الهاتف من قاعدة البيانات
        User user = database.userDao().getUserByIdSync(userId);
        return user != null ? user.phoneNumber : null;
    }
    
    private String getUserEmail(String userId) {
        User user = database.userDao().getUserByIdSync(userId);
        return user != null ? user.email : null;
    }
    
    private String getTelegramBotToken() {
        return prefs.getString("telegram_bot_token", null);
    }
    
    private String getUserTelegramChatId(String userId) {
        return prefs.getString("telegram_chat_id_" + userId, null);
    }
    
    private void sendExternalReminder(String title, String message) {
        // إرسال التذكيرات عبر القنوات الخارجية
        Set<Integer> methods = getEnabledNotificationMethods();
        String fullMessage = title + "\n" + message;
        
        for (int method : methods) {
            switch (method) {
                case METHOD_SMS:
                    sendSmsNotification(fullMessage, getCurrentUserId());
                    break;
                case METHOD_WHATSAPP:
                    sendWhatsAppNotification(fullMessage, getCurrentUserId());
                    break;
                case METHOD_TELEGRAM:
                    sendTelegramNotification(fullMessage, getCurrentUserId());
                    break;
            }
        }
    }
    
    private void sendCriticalExternalAlert(String title, String message) {
        // إرسال التنبيهات الحرجة عبر جميع القنوات المتاحة
        String fullMessage = "🚨 " + title + "\n" + message;
        String userId = getCurrentUserId();
        
        sendSmsNotification(fullMessage, userId);
        sendWhatsAppNotification(fullMessage, userId);
        sendTelegramNotification(fullMessage, userId);
        sendEmailNotification(title, fullMessage, userId);
    }
    
    private void sendToRelatedUsers(String accountName, String title, String content) {
        // إرسال للمستخدمين المرتبطين بالحساب
        // يمكن تنفيذ منطق معقد لتحديد المستخدمين المرتبطين
    }
    
    /**
     * تنظيف الإشعارات القديمة
     * Clean up old notifications
     */
    public void cleanupOldNotifications() {
        executorService.execute(() -> {
            try {
                long cutoffTime = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000); // 30 days
                database.notificationDao().deleteOldNotifications(cutoffTime);
                Log.d(TAG, "Old notifications cleaned up");
            } catch (Exception e) {
                Log.e(TAG, "Error cleaning up notifications", e);
            }
        });
    }
    
    /**
     * تحديث حالة قراءة الإشعار
     * Update notification read status
     */
    public void markNotificationAsRead(long notificationId) {
        executorService.execute(() -> {
            database.notificationDao().markAsRead(notificationId);
        });
    }
    
    /**
     * إغلاق المساعد وتنظيف الموارد
     * Close helper and cleanup resources
     */
    public void close() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}