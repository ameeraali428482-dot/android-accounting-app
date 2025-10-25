package com.example.androidapp.utils;

import android.content.Context;
import android.util.Log;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.ExternalNotificationDao;
import com.example.androidapp.data.entities.ExternalNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.*;

/**
 * مدير الإشعارات الخارجية - لإرسال الإشعارات عبر واتساب/تلجرام/SMS
 */
public class ExternalNotificationManager {

    private static final String TAG = "ExternalNotificationManager";
    
    // APIs URLs (يجب تعيينها حسب الخدمة المستخدمة)
    private static final String WHATSAPP_API_URL = "https://api.whatsapp.business/v1/messages";
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot{BOT_TOKEN}/sendMessage";
    private static final String SMS_API_URL = "https://api.twilio.com/2010-04-01/Accounts/{ACCOUNT_SID}/Messages.json";
    
    private Context context;
    private ExternalNotificationDao externalNotificationDao;
    private OkHttpClient httpClient;
    private ExecutorService executorService;
    
    public ExternalNotificationManager(Context context) {
        this.context = context;
        
        AppDatabase database = AppDatabase.getDatabase(context);
        this.externalNotificationDao = database.externalNotificationDao();
        
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();
                
        this.executorService = Executors.newFixedThreadPool(3);
    }

    /**
     * إرسال إشعار واتساب
     */
    public void sendWhatsAppNotification(String userId, String recipientPhone, 
                                        String title, String message,
                                        NotificationCallback callback) {
        executorService.execute(() -> {
            try {
                ExternalNotification notification = createNotification(
                    userId, recipientPhone, "WHATSAPP", title, message
                );
                
                boolean success = sendWhatsAppMessage(recipientPhone, message);
                
                if (success) {
                    externalNotificationDao.markAsSent(
                        notification.getId(), "SENT", new Date(), 
                        notification.getExternalMessageId(), new Date()
                    );
                    if (callback != null) callback.onSuccess(notification);
                } else {
                    externalNotificationDao.markAsFailed(
                        notification.getId(), "Failed to send WhatsApp message", new Date()
                    );
                    if (callback != null) callback.onError("Failed to send WhatsApp message");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error sending WhatsApp notification", e);
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * إرسال إشعار تلجرام
     */
    public void sendTelegramNotification(String userId, String recipientUsername,
                                        String title, String message,
                                        NotificationCallback callback) {
        executorService.execute(() -> {
            try {
                ExternalNotification notification = createNotification(
                    userId, recipientUsername, "TELEGRAM", title, message
                );
                
                boolean success = sendTelegramMessage(recipientUsername, title + "\n" + message);
                
                if (success) {
                    externalNotificationDao.markAsSent(
                        notification.getId(), "SENT", new Date(),
                        notification.getExternalMessageId(), new Date()
                    );
                    if (callback != null) callback.onSuccess(notification);
                } else {
                    externalNotificationDao.markAsFailed(
                        notification.getId(), "Failed to send Telegram message", new Date()
                    );
                    if (callback != null) callback.onError("Failed to send Telegram message");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error sending Telegram notification", e);
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * إرسال إشعار SMS
     */
    public void sendSMSNotification(String userId, String recipientPhone,
                                   String title, String message,
                                   NotificationCallback callback) {
        executorService.execute(() -> {
            try {
                ExternalNotification notification = createNotification(
                    userId, recipientPhone, "SMS", title, message
                );
                
                boolean success = sendSMSMessage(recipientPhone, title + ": " + message);
                
                if (success) {
                    externalNotificationDao.markAsSent(
                        notification.getId(), "SENT", new Date(),
                        notification.getExternalMessageId(), new Date()
                    );
                    if (callback != null) callback.onSuccess(notification);
                } else {
                    externalNotificationDao.markAsFailed(
                        notification.getId(), "Failed to send SMS", new Date()
                    );
                    if (callback != null) callback.onError("Failed to send SMS");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error sending SMS notification", e);
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * إرسال إشعارات جماعية
     */
    public void sendBulkNotifications(String userId, List<String> recipients,
                                     String channel, String title, String message,
                                     BulkNotificationCallback callback) {
        executorService.execute(() -> {
            String bulkId = UUID.randomUUID().toString();
            int successCount = 0;
            int failureCount = 0;
            
            for (String recipient : recipients) {
                try {
                    ExternalNotification notification = createBulkNotification(
                        userId, recipient, channel, title, message, bulkId
                    );
                    
                    boolean success = false;
                    switch (channel) {
                        case "WHATSAPP":
                            success = sendWhatsAppMessage(recipient, message);
                            break;
                        case "TELEGRAM":
                            success = sendTelegramMessage(recipient, title + "\n" + message);
                            break;
                        case "SMS":
                            success = sendSMSMessage(recipient, title + ": " + message);
                            break;
                    }
                    
                    if (success) {
                        successCount++;
                        externalNotificationDao.markAsSent(
                            notification.getId(), "SENT", new Date(),
                            notification.getExternalMessageId(), new Date()
                        );
                    } else {
                        failureCount++;
                        externalNotificationDao.markAsFailed(
                            notification.getId(), "Failed to send bulk message", new Date()
                        );
                    }
                    
                } catch (Exception e) {
                    failureCount++;
                    Log.e(TAG, "Error in bulk notification for " + recipient, e);
                }
            }
            
            if (callback != null) {
                callback.onBulkComplete(bulkId, successCount, failureCount);
            }
        });
    }

    private ExternalNotification createNotification(String userId, String recipient,
                                                   String channel, String title, String message) {
        ExternalNotification notification = new ExternalNotification();
        notification.setId(UUID.randomUUID().toString());
        notification.setUserId(userId);
        notification.setRecipientContact(recipient);
        notification.setNotificationChannel(channel);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setPriority("MEDIUM");
        notification.setStatus("PENDING");
        
        externalNotificationDao.insert(notification);
        return notification;
    }

    private ExternalNotification createBulkNotification(String userId, String recipient,
                                                       String channel, String title, String message,
                                                       String bulkId) {
        ExternalNotification notification = createNotification(userId, recipient, channel, title, message);
        notification.setBulk(true);
        notification.setBulkId(bulkId);
        
        externalNotificationDao.update(notification);
        return notification;
    }

    private boolean sendWhatsAppMessage(String phone, String message) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("to", phone);
            requestBody.put("type", "text");
            
            JSONObject textObj = new JSONObject();
            textObj.put("body", message);
            requestBody.put("text", textObj);
            
            RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.get("application/json")
            );
            
            Request request = new Request.Builder()
                    .url(WHATSAPP_API_URL)
                    .header("Authorization", "Bearer YOUR_WHATSAPP_TOKEN")
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
            
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error sending WhatsApp message", e);
            return false;
        }
    }

    private boolean sendTelegramMessage(String username, String message) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("chat_id", username);
            requestBody.put("text", message);
            requestBody.put("parse_mode", "HTML");
            
            RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.get("application/json")
            );
            
            String url = TELEGRAM_API_URL.replace("{BOT_TOKEN}", "YOUR_BOT_TOKEN");
            
            Request request = new Request.Builder()
                    .url(url)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
            
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error sending Telegram message", e);
            return false;
        }
    }

    private boolean sendSMSMessage(String phone, String message) {
        try {
            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("To", phone)
                    .add("From", "YOUR_TWILIO_PHONE")
                    .add("Body", message);
            
            RequestBody body = formBuilder.build();
            
            String url = SMS_API_URL.replace("{ACCOUNT_SID}", "YOUR_ACCOUNT_SID");
            
            String credentials = Credentials.basic("YOUR_ACCOUNT_SID", "YOUR_AUTH_TOKEN");
            
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", credentials)
                    .post(body)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Error sending SMS message", e);
            return false;
        }
    }

    /**
     * معالجة الإشعارات المعلقة
     */
    public void processPendingNotifications() {
        executorService.execute(() -> {
            try {
                List<ExternalNotification> pendingNotifications = 
                    externalNotificationDao.getPendingNotifications();
                    
                for (ExternalNotification notification : pendingNotifications) {
                    processNotification(notification);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing pending notifications", e);
            }
        });
    }

    private void processNotification(ExternalNotification notification) {
        try {
            boolean success = false;
            
            switch (notification.getNotificationChannel()) {
                case "WHATSAPP":
                    success = sendWhatsAppMessage(
                        notification.getRecipientContact(), 
                        notification.getMessage()
                    );
                    break;
                case "TELEGRAM":
                    success = sendTelegramMessage(
                        notification.getRecipientContact(),
                        notification.getTitle() + "\n" + notification.getMessage()
                    );
                    break;
                case "SMS":
                    success = sendSMSMessage(
                        notification.getRecipientContact(),
                        notification.getTitle() + ": " + notification.getMessage()
                    );
                    break;
            }
            
            if (success) {
                externalNotificationDao.markAsSent(
                    notification.getId(), "SENT", new Date(),
                    "processed_" + System.currentTimeMillis(), new Date()
                );
            } else {
                externalNotificationDao.markAsFailed(
                    notification.getId(), "Processing failed", new Date()
                );
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing notification " + notification.getId(), e);
        }
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * واجهة للحصول على نتيجة الإشعار
     */
    public interface NotificationCallback {
        void onSuccess(ExternalNotification notification);
        void onError(String error);
    }

    /**
     * واجهة للحصول على نتيجة الإشعارات الجماعية
     */
    public interface BulkNotificationCallback {
        void onBulkComplete(String bulkId, int successCount, int failureCount);
    }
}
