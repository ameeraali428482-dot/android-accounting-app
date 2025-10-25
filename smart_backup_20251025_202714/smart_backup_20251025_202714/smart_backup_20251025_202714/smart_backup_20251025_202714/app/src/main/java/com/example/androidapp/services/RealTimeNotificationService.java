package com.example.androidapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.ChangeLog;
import com.example.androidapp.data.entities.RecordLock;
import com.example.androidapp.utils.SessionManager;
import com.example.androidapp.utils.NotificationHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * خدمة الإشعارات الفورية للتحديثات المتزامنة
 * تدير الإشعارات بين المستخدمين في الوقت الفعلي عبر Firebase
 * 
 * @author MiniMax Agent
 * @version 1.0
 * @since 2025-10-20
 */
public class RealTimeNotificationService extends Service {
    
    private static final String TAG = "RealTimeNotificationService";
    
    // قنوات الإشعارات
    private static final String CHANNEL_LOCK_NOTIFICATIONS = "lock_notifications";
    private static final String CHANNEL_CHANGE_NOTIFICATIONS = "change_notifications";
    private static final String CHANNEL_CONFLICT_NOTIFICATIONS = "conflict_notifications";
    
    // أنواع الإشعارات
    public static final String NOTIFICATION_TYPE_RECORD_LOCKED = "record_locked";
    public static final String NOTIFICATION_TYPE_RECORD_UNLOCKED = "record_unlocked";
    public static final String NOTIFICATION_TYPE_RECORD_CHANGED = "record_changed";
    public static final String NOTIFICATION_TYPE_CONFLICT_DETECTED = "conflict_detected";
    public static final String NOTIFICATION_TYPE_USER_JOINED = "user_joined";
    public static final String NOTIFICATION_TYPE_USER_LEFT = "user_left";
    
    private FirebaseFirestore firestore;
    private FirebaseMessaging messaging;
    private AppDatabase database;
    private SessionManager sessionManager;
    private NotificationHelper notificationHelper;
    private ExecutorService executorService;
    
    // معرفات الجلسة والمستخدم
    private String currentUserId;
    private String currentUserName;
    private String currentCompanyId;
    private String currentSessionId;
    
    // مستمعي Firestore
    private final Map<String, ListenerRegistration> firestoreListeners = new ConcurrentHashMap<>();
    
    // cache للمستخدمين النشطين
    private final Map<String, UserSession> activeUsers = new ConcurrentHashMap<>();
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // تهيئة المكونات
        firestore = FirebaseFirestore.getInstance();
        messaging = FirebaseMessaging.getInstance();
        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);
        notificationHelper = new NotificationHelper(this);
        executorService = Executors.newFixedThreadPool(3);
        
        // تهيئة معلومات المستخدم الحالي
        initializeCurrentUser();
        
        // بدء الاستماع للتحديثات
        startListeningForUpdates();
        
        Log.i(TAG, "RealTimeNotificationService created and initialized");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "RealTimeNotificationService started");
        return START_STICKY; // إعادة تشغيل الخدمة تلقائياً إذا توقفت
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // خدمة غير مرتبطة
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // إيقاف جميع المستمعين
        stopAllListeners();
        
        // إنهاء ExecutorService
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        
        // إرسال إشعار مغادرة المستخدم
        notifyUserLeft();
        
        Log.i(TAG, "RealTimeNotificationService destroyed");
    }
    
    /**
     * تهيئة معلومات المستخدم الحالي
     */
    private void initializeCurrentUser() {
        currentUserId = sessionManager.getUserId();
        currentUserName = sessionManager.getUserName();
        currentCompanyId = sessionManager.getCompanyId();
        currentSessionId = sessionManager.getSessionId();
        
        Log.d(TAG, "Current user initialized: " + currentUserName + " (" + currentUserId + ")");
    }
    
    /**
     * بدء الاستماع للتحديثات من Firestore
     */
    private void startListeningForUpdates() {
        if (currentCompanyId == null || currentUserId == null) {
            Log.w(TAG, "Cannot start listeners - user or company not initialized");
            return;
        }
        
        // الاستماع لأقفال الشركة
        listenToCompanyLocks();
        
        // الاستماع لتغييرات الشركة
        listenToCompanyChanges();
        
        // الاستماع للمستخدمين النشطين
        listenToActiveUsers();
        
        // تسجيل المستخدم كنشط
        registerUserAsActive();
        
        Log.d(TAG, "Started listening for real-time updates");
    }
    
    /**
     * الاستماع لأقفال الشركة
     */
    private void listenToCompanyLocks() {
        String lockCollectionPath = "companies/" + currentCompanyId + "/locks";
        
        ListenerRegistration lockListener = firestore.collection(lockCollectionPath)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error listening to locks", error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        snapshots.getDocumentChanges().forEach(change -> {
                            try {
                                handleLockChange(change.getDocument(), change.getType().name());
                            } catch (Exception e) {
                                Log.e(TAG, "Error handling lock change", e);
                            }
                        });
                    }
                });
        
        firestoreListeners.put("company_locks", lockListener);
        Log.d(TAG, "Listening to company locks at: " + lockCollectionPath);
    }
    
    /**
     * الاستماع لتغييرات الشركة
     */
    private void listenToCompanyChanges() {
        String changeCollectionPath = "companies/" + currentCompanyId + "/changes";
        
        ListenerRegistration changeListener = firestore.collection(changeCollectionPath)
                .whereGreaterThan("changed_at", new Date())
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error listening to changes", error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        snapshots.getDocumentChanges().forEach(change -> {
                            try {
                                handleChangeNotification(change.getDocument());
                            } catch (Exception e) {
                                Log.e(TAG, "Error handling change notification", e);
                            }
                        });
                    }
                });
        
        firestoreListeners.put("company_changes", changeListener);
        Log.d(TAG, "Listening to company changes at: " + changeCollectionPath);
    }
    
    /**
     * الاستماع للمستخدمين النشطين
     */
    private void listenToActiveUsers() {
        String usersCollectionPath = "companies/" + currentCompanyId + "/active_users";
        
        ListenerRegistration usersListener = firestore.collection(usersCollectionPath)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error listening to active users", error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        snapshots.getDocumentChanges().forEach(change -> {
                            try {
                                handleActiveUserChange(change.getDocument(), change.getType().name());
                            } catch (Exception e) {
                                Log.e(TAG, "Error handling active user change", e);
                            }
                        });
                    }
                });
        
        firestoreListeners.put("active_users", usersListener);
        Log.d(TAG, "Listening to active users at: " + usersCollectionPath);
    }
    
    /**
     * معالجة تغيير في الأقفال
     */
    private void handleLockChange(DocumentSnapshot document, String changeType) {
        try {
            String lockUserId = document.getString("user_id");
            String recordId = document.getString("record_id");
            String recordType = document.getString("record_type");
            String userName = document.getString("user_name");
            String lockStatus = document.getString("lock_status");
            
            // تجاهل التغييرات الخاصة بالمستخدم الحالي
            if (currentUserId.equals(lockUserId)) {
                return;
            }
            
            switch (changeType) {
                case "ADDED":
                case "MODIFIED":
                    if ("ACTIVE".equals(lockStatus)) {
                        handleRecordLocked(recordId, recordType, userName);
                    } else {
                        handleRecordUnlocked(recordId, recordType, userName);
                    }
                    break;
                    
                case "REMOVED":
                    handleRecordUnlocked(recordId, recordType, userName);
                    break;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing lock change", e);
        }
    }
    
    /**
     * معالجة تغيير في البيانات
     */
    private void handleChangeNotification(DocumentSnapshot document) {
        try {
            String changeUserId = document.getString("user_id");
            String recordId = document.getString("record_id");
            String recordType = document.getString("record_type");
            String userName = document.getString("user_name");
            String changeType = document.getString("change_type");
            String fieldName = document.getString("field_name");
            
            // تجاهل التغييرات الخاصة بالمستخدم الحالي
            if (currentUserId.equals(changeUserId)) {
                return;
            }
            
            handleRecordChanged(recordId, recordType, userName, changeType, fieldName);
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing change notification", e);
        }
    }
    
    /**
     * معالجة تغيير في المستخدمين النشطين
     */
    private void handleActiveUserChange(DocumentSnapshot document, String changeType) {
        try {
            String userId = document.getId();
            String userName = document.getString("user_name");
            
            // تجاهل التغييرات الخاصة بالمستخدم الحالي
            if (currentUserId.equals(userId)) {
                return;
            }
            
            switch (changeType) {
                case "ADDED":
                    handleUserJoined(userId, userName);
                    break;
                    
                case "REMOVED":
                    handleUserLeft(userId, userName);
                    break;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing active user change", e);
        }
    }
    
    /**
     * معالجة قفل السجل
     */
    private void handleRecordLocked(String recordId, String recordType, String userName) {
        String title = "سجل مقفل";
        String message = "تم قفل " + getRecordTypeArabic(recordType) + " من قبل " + userName;
        
        notificationHelper.showNotification(
            CHANNEL_LOCK_NOTIFICATIONS,
            title,
            message,
            createNotificationData(NOTIFICATION_TYPE_RECORD_LOCKED, recordId, recordType, userName)
        );
        
        Log.d(TAG, "Record locked notification: " + recordType + ":" + recordId + " by " + userName);
    }
    
    /**
     * معالجة تحرير قفل السجل
     */
    private void handleRecordUnlocked(String recordId, String recordType, String userName) {
        String title = "تم تحرير السجل";
        String message = "تم تحرير " + getRecordTypeArabic(recordType) + " من قبل " + userName;
        
        notificationHelper.showNotification(
            CHANNEL_LOCK_NOTIFICATIONS,
            title,
            message,
            createNotificationData(NOTIFICATION_TYPE_RECORD_UNLOCKED, recordId, recordType, userName)
        );
        
        Log.d(TAG, "Record unlocked notification: " + recordType + ":" + recordId + " by " + userName);
    }
    
    /**
     * معالجة تغيير السجل
     */
    private void handleRecordChanged(String recordId, String recordType, String userName, 
                                   String changeType, String fieldName) {
        String title = "تم تحديث السجل";
        String message = userName + " قام بـ" + getChangeTypeArabic(changeType) + 
                        " " + getRecordTypeArabic(recordType);
        
        if (fieldName != null && !fieldName.isEmpty()) {
            message += " - الحقل: " + fieldName;
        }
        
        notificationHelper.showNotification(
            CHANNEL_CHANGE_NOTIFICATIONS,
            title,
            message,
            createNotificationData(NOTIFICATION_TYPE_RECORD_CHANGED, recordId, recordType, userName)
        );
        
        Log.d(TAG, "Record changed notification: " + recordType + ":" + recordId + 
                  " by " + userName + " (" + changeType + ")");
    }
    
    /**
     * معالجة انضمام مستخدم
     */
    private void handleUserJoined(String userId, String userName) {
        activeUsers.put(userId, new UserSession(userId, userName, new Date()));
        
        String title = "مستخدم جديد";
        String message = userName + " انضم إلى النظام";
        
        notificationHelper.showNotification(
            CHANNEL_CONFLICT_NOTIFICATIONS,
            title,
            message,
            createNotificationData(NOTIFICATION_TYPE_USER_JOINED, userId, "user", userName)
        );
        
        Log.d(TAG, "User joined notification: " + userName);
    }
    
    /**
     * معالجة مغادرة مستخدم
     */
    private void handleUserLeft(String userId, String userName) {
        activeUsers.remove(userId);
        
        String title = "مستخدم غادر";
        String message = userName + " غادر النظام";
        
        notificationHelper.showNotification(
            CHANNEL_CONFLICT_NOTIFICATIONS,
            title,
            message,
            createNotificationData(NOTIFICATION_TYPE_USER_LEFT, userId, "user", userName)
        );
        
        Log.d(TAG, "User left notification: " + userName);
    }
    
    /**
     * تسجيل المستخدم كنشط في Firestore
     */
    private void registerUserAsActive() {
        String userDocPath = "companies/" + currentCompanyId + "/active_users/" + currentUserId;
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("user_id", currentUserId);
        userData.put("user_name", currentUserName);
        userData.put("session_id", currentSessionId);
        userData.put("joined_at", new Date());
        userData.put("last_activity", new Date());
        userData.put("device_info", getDeviceInfo());
        
        firestore.document(userDocPath)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User registered as active in Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to register user as active", e);
                });
    }
    
    /**
     * إشعار مغادرة المستخدم
     */
    private void notifyUserLeft() {
        String userDocPath = "companies/" + currentCompanyId + "/active_users/" + currentUserId;
        
        firestore.document(userDocPath)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User removed from active users in Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to remove user from active users", e);
                });
    }
    
    /**
     * إرسال إشعار قفل سجل إلى المستخدمين الآخرين
     */
    public void notifyRecordLocked(String recordId, String recordType, String reason) {
        executorService.execute(() -> {
            try {
                String lockDocPath = "companies/" + currentCompanyId + "/locks/" + 
                                   recordType + "_" + recordId;
                
                Map<String, Object> lockData = new HashMap<>();
                lockData.put("record_id", recordId);
                lockData.put("record_type", recordType);
                lockData.put("user_id", currentUserId);
                lockData.put("user_name", currentUserName);
                lockData.put("lock_status", "ACTIVE");
                lockData.put("locked_at", new Date());
                lockData.put("reason", reason);
                
                firestore.document(lockDocPath).set(lockData);
                
            } catch (Exception e) {
                Log.e(TAG, "Error notifying record locked", e);
            }
        });
    }
    
    /**
     * إرسال إشعار تحرير قفل سجل
     */
    public void notifyRecordUnlocked(String recordId, String recordType) {
        executorService.execute(() -> {
            try {
                String lockDocPath = "companies/" + currentCompanyId + "/locks/" + 
                                   recordType + "_" + recordId;
                
                firestore.document(lockDocPath).delete();
                
            } catch (Exception e) {
                Log.e(TAG, "Error notifying record unlocked", e);
            }
        });
    }
    
    /**
     * إرسال إشعار تغيير سجل
     */
    public void notifyRecordChanged(String recordId, String recordType, String changeType, 
                                  String fieldName, String oldValue, String newValue) {
        executorService.execute(() -> {
            try {
                String changeDocPath = "companies/" + currentCompanyId + "/changes/" + 
                                     System.currentTimeMillis() + "_" + recordType + "_" + recordId;
                
                Map<String, Object> changeData = new HashMap<>();
                changeData.put("record_id", recordId);
                changeData.put("record_type", recordType);
                changeData.put("change_type", changeType);
                changeData.put("field_name", fieldName);
                changeData.put("old_value", oldValue);
                changeData.put("new_value", newValue);
                changeData.put("user_id", currentUserId);
                changeData.put("user_name", currentUserName);
                changeData.put("changed_at", new Date());
                
                firestore.document(changeDocPath).set(changeData);
                
                // حذف الإشعار بعد دقيقة (لتجنب تراكم البيانات)
                executorService.execute(() -> {
                    try {
                        Thread.sleep(60000); // 60 seconds
                        firestore.document(changeDocPath).delete();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error notifying record changed", e);
            }
        });
    }
    
    /**
     * إيقاف جميع المستمعين
     */
    private void stopAllListeners() {
        for (ListenerRegistration listener : firestoreListeners.values()) {
            if (listener != null) {
                listener.remove();
            }
        }
        firestoreListeners.clear();
        Log.d(TAG, "All Firestore listeners stopped");
    }
    
    /**
     * إنشاء بيانات الإشعار
     */
    private Map<String, String> createNotificationData(String type, String recordId, 
                                                      String recordType, String userName) {
        Map<String, String> data = new HashMap<>();
        data.put("type", type);
        data.put("record_id", recordId);
        data.put("record_type", recordType);
        data.put("user_name", userName);
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return data;
    }
    
    /**
     * ترجمة نوع السجل للعربية
     */
    private String getRecordTypeArabic(String recordType) {
        switch (recordType) {
            case "invoice": return "الفاتورة";
            case "customer": return "العميل";
            case "supplier": return "المورد";
            case "product": return "المنتج";
            case "account": return "الحساب";
            case "payment": return "الدفعة";
            case "order": return "الطلب";
            case "employee": return "الموظف";
            default: return "السجل";
        }
    }
    
    /**
     * ترجمة نوع التغيير للعربية
     */
    private String getChangeTypeArabic(String changeType) {
        switch (changeType) {
            case "CREATE": return "إنشاء";
            case "UPDATE": return "تحديث";
            case "DELETE": return "حذف";
            default: return "تعديل";
        }
    }
    
    /**
     * الحصول على معلومات الجهاز
     */
    private String getDeviceInfo() {
        return android.os.Build.MANUFACTURER + " " + 
               android.os.Build.MODEL + " (Android " + 
               android.os.Build.VERSION.RELEASE + ")";
    }
    
    // Inner Classes
    
    /**
     * معلومات جلسة المستخدم
     */
    private static class UserSession {
        final String userId;
        final String userName;
        final Date joinedAt;
        
        UserSession(String userId, String userName, Date joinedAt) {
            this.userId = userId;
            this.userName = userName;
            this.joinedAt = joinedAt;
        }
    }
}