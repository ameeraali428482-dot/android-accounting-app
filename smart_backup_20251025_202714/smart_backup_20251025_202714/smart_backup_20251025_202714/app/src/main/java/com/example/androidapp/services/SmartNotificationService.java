package com.example.androidapp.services;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.SmartNotificationDao;
import com.example.androidapp.data.dao.PeriodicReminderDao;
import com.example.androidapp.data.entities.SmartNotification;
import com.example.androidapp.data.entities.PeriodicReminder;
import com.example.androidapp.ui.main.MainActivity;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * خدمة التنبيهات الذكية - لإدارة وإرسال التنبيهات التلقائية والذكية
 */
public class SmartNotificationService extends Service {

    private static final String CHANNEL_ID = "smart_notifications";
    private static final String CHANNEL_NAME = "Smart Notifications";
    private static final int NOTIFICATION_ID = 1001;
    
    private SmartNotificationDao smartNotificationDao;
    private PeriodicReminderDao periodicReminderDao;
    private ScheduledExecutorService scheduler;
    private NotificationManagerCompat notificationManager;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        AppDatabase database = AppDatabase.getDatabase(this);
        smartNotificationDao = database.smartNotificationDao();
        periodicReminderDao = database.periodicReminderDao();
        
        notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
        
        scheduler = Executors.newScheduledThreadPool(2);
        
        // بدء مراقبة التنبيهات كل دقيقة
        startNotificationMonitoring();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // إنشاء notification للخدمة foreground
        Notification notification = createForegroundNotification();
        startForeground(NOTIFICATION_ID, notification);
        
        return START_STICKY; // إعادة تشغيل الخدمة تلقائياً إذا توقفت
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("قناة التنبيهات الذكية والتذكيرات");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createForegroundNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, 
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("خدمة التنبيهات الذكية")
                .setContentText("تعمل في الخلفية لمراقبة التنبيهات")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private void startNotificationMonitoring() {
        // فحص التنبيهات المستحقة كل دقيقة
        scheduler.scheduleAtFixedRate(this::checkPendingNotifications, 0, 1, TimeUnit.MINUTES);
        
        // فحص التذكيرات الدورية كل 5 دقائق
        scheduler.scheduleAtFixedRate(this::checkPeriodicReminders, 0, 5, TimeUnit.MINUTES);
    }

    private void checkPendingNotifications() {
        try {
            Date currentDate = new Date();
            List<SmartNotification> pendingNotifications = 
                smartNotificationDao.getPendingNotifications(currentDate);
                
            for (SmartNotification notification : pendingNotifications) {
                sendSmartNotification(notification);
                
                // تحديث حالة الإشعار
                smartNotificationDao.markAsRead(notification.getId(), new Date());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPeriodicReminders() {
        try {
            Date currentTime = new Date();
            List<PeriodicReminder> dueReminders = 
                periodicReminderDao.getDueReminders(currentTime);
                
            for (PeriodicReminder reminder : dueReminders) {
                processPeriodicReminder(reminder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSmartNotification(SmartNotification notification) {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            if (notification.getActionUrl() != null) {
                intent.putExtra("action_url", notification.getActionUrl());
            }
            
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 
                notification.getId().hashCode(), intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(notification.getTitle())
                    .setContentText(notification.getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(notification.getMessage()))
                    .setPriority(getPriorityLevel(notification.getPriority()))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            // إضافة أيقونة خاصة حسب نوع التنبيه
            int iconRes = getNotificationIcon(notification.getNotificationType());
            if (iconRes != 0) {
                builder.setSmallIcon(iconRes);
            }

            notificationManager.notify(notification.getId().hashCode(), builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processPeriodicReminder(PeriodicReminder reminder) {
        try {
            // إنشاء تنبيه ذكي من التذكير الدوري
            SmartNotification smartNotification = new SmartNotification();
            smartNotification.setId("reminder_" + reminder.getId() + "_" + System.currentTimeMillis());
            smartNotification.setUserId(reminder.getUserId());
            smartNotification.setTitle(reminder.getTitle());
            smartNotification.setMessage(reminder.getDescription());
            smartNotification.setNotificationType(reminder.getReminderType());
            smartNotification.setPriority("MEDIUM");
            smartNotification.setTriggerDate(new Date());
            
            // حفظ التنبيه الذكي
            smartNotificationDao.insert(smartNotification);
            
            // إرسال التنبيه فوراً
            sendSmartNotification(smartNotification);
            
            // تحديث التذكير الدوري للمرة القادمة
            updatePeriodicReminder(reminder);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePeriodicReminder(PeriodicReminder reminder) {
        try {
            Date nextTrigger = calculateNextTriggerDate(reminder);
            Date now = new Date();
            
            periodicReminderDao.updateTriggerDates(
                reminder.getId(), 
                now, 
                nextTrigger, 
                now
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Date calculateNextTriggerDate(PeriodicReminder reminder) {
        Date currentTime = new Date();
        long intervalMs = 0;
        
        switch (reminder.getFrequency()) {
            case "DAILY":
                intervalMs = 24 * 60 * 60 * 1000L; // يوم واحد
                break;
            case "WEEKLY":
                intervalMs = 7 * 24 * 60 * 60 * 1000L; // أسبوع
                break;
            case "MONTHLY":
                intervalMs = 30L * 24 * 60 * 60 * 1000L; // شهر تقريبي
                break;
            case "YEARLY":
                intervalMs = 365L * 24 * 60 * 60 * 1000L; // سنة
                break;
            case "CUSTOM":
                intervalMs = reminder.getFrequencyValue() * 24 * 60 * 60 * 1000L; // أيام مخصصة
                break;
        }
        
        return new Date(currentTime.getTime() + intervalMs);
    }

    private int getPriorityLevel(String priority) {
        if (priority == null) return NotificationCompat.PRIORITY_DEFAULT;
        
        switch (priority) {
            case "HIGH":
                return NotificationCompat.PRIORITY_HIGH;
            case "LOW":
                return NotificationCompat.PRIORITY_LOW;
            default:
                return NotificationCompat.PRIORITY_DEFAULT;
        }
    }

    private int getNotificationIcon(String notificationType) {
        if (notificationType == null) return R.drawable.ic_notification;
        
        switch (notificationType) {
            case "ORDER_REMINDER":
                return R.drawable.ic_shopping_cart;
            case "EXPIRY_WARNING":
                return R.drawable.ic_warning;
            case "STOCK_ALERT":
                return R.drawable.ic_inventory;
            case "PAYMENT_DUE":
                return R.drawable.ic_payment;
            default:
                return R.drawable.ic_notification;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
