package com.example.androidapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.room.Room;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.*;
import com.example.androidapp.utils.NetworkUtils;
import com.example.androidapp.utils.SecurityUtils;
import com.example.androidapp.utils.NotificationHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * خدمة المزامنة الحية المتقدمة - مزامنة البيانات في الوقت الفعلي مع حل التعارضات الذكي
 * Advanced Live Sync Service - real-time data synchronization with intelligent conflict resolution
 */
public class SyncService extends Service {
    
    private static final String TAG = "SyncService";
    private static final String PREFS_NAME = "sync_service_prefs";
    
    // إعدادات المزامنة
    private static final String SYNC_ENABLED_KEY = "sync_enabled";
    private static final String SYNC_INTERVAL_KEY = "sync_interval"; // in minutes
    private static final String LAST_SYNC_TIMESTAMP_KEY = "last_sync_timestamp";
    private static final String AUTO_SYNC_KEY = "auto_sync_enabled";
    private static final String REAL_TIME_SYNC_KEY = "real_time_sync_enabled";
    private static final String CONFLICT_RESOLUTION_MODE_KEY = "conflict_resolution_mode";
    
    // أوضاع حل التعارضات
    public static final String RESOLUTION_MANUAL = "MANUAL";
    public static final String RESOLUTION_LATEST_WINS = "LATEST_WINS";
    public static final String RESOLUTION_LOCAL_WINS = "LOCAL_WINS";
    public static final String RESOLUTION_REMOTE_WINS = "REMOTE_WINS";
    public static final String RESOLUTION_MERGE = "MERGE";
    
    // حالات المزامنة
    public static final String STATUS_IDLE = "IDLE";
    public static final String STATUS_SYNCING = "SYNCING";
    public static final String STATUS_ERROR = "ERROR";
    public static final String STATUS_CONFLICT = "CONFLICT";
    
    // أنواع التغييرات
    private static final String CHANGE_INSERT = "INSERT";
    private static final String CHANGE_UPDATE = "UPDATE";
    private static final String CHANGE_DELETE = "DELETE";
    
    private AppDatabase database;
    private SharedPreferences prefs;
    private NetworkUtils networkUtils;
    private SecurityUtils securityUtils;
    private NotificationHelper notificationHelper;
    private ExecutorService syncExecutor;
    private ScheduledExecutorService scheduledExecutor;
    private Gson gson;
    
    // متتبع التغييرات المحلية
    private ChangeTracker changeTracker;
    
    // حالة المزامنة الحالية
    private String currentSyncStatus = STATUS_IDLE;
    private boolean isSyncInProgress = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration()
                .build();
        
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        networkUtils = new NetworkUtils();
        securityUtils = new SecurityUtils(this);
        notificationHelper = new NotificationHelper(this);
        syncExecutor = Executors.newCachedThreadPool();
        scheduledExecutor = Executors.newScheduledThreadPool(2);
        gson = new Gson();
        
        changeTracker = new ChangeTracker();
        
        // بدء المزامنة التلقائية إذا كانت مفعلة
        if (isAutoSyncEnabled()) {
            startAutoSync();
        }
        
        Log.d(TAG, "SyncService created");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case "START_SYNC":
                        performSync(false);
                        break;
                    case "FORCE_SYNC":
                        performSync(true);
                        break;
                    case "ENABLE_AUTO_SYNC":
                        enableAutoSync(true);
                        break;
                    case "DISABLE_AUTO_SYNC":
                        enableAutoSync(false);
                        break;
                    case "RESOLVE_CONFLICTS":
                        String resolutionMode = intent.getStringExtra("resolution_mode");
                        resolveConflicts(resolutionMode);
                        break;
                }
            }
        }
        return START_STICKY;
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    /**
     * تنفيذ المزامنة
     * Perform synchronization
     */
    private void performSync(boolean force) {
        if (isSyncInProgress && !force) {
            Log.d(TAG, "Sync already in progress");
            return;
        }
        
        syncExecutor.execute(() -> {
            try {
                isSyncInProgress = true;
                currentSyncStatus = STATUS_SYNCING;
                
                Log.d(TAG, "Starting synchronization...");
                
                // فحص الاتصال بالشبكة
                if (!NetworkUtils.isNetworkAvailable(this)) {
                    Log.w(TAG, "No network connection available");
                    currentSyncStatus = STATUS_ERROR;
                    return;
                }
                
                // الحصول على التغييرات المحلية
                List<ChangeRecord> localChanges = changeTracker.getPendingChanges();
                
                // إرسال التغييرات المحلية للخادم
                SyncResponse uploadResponse = uploadLocalChanges(localChanges);
                
                if (uploadResponse.success) {
                    // تنزيل التغييرات من الخادم
                    SyncResponse downloadResponse = downloadRemoteChanges();
                    
                    if (downloadResponse.success) {
                        // تطبيق التغييرات البعيدة
                        ApplyResult applyResult = applyRemoteChanges(downloadResponse.changes);
                        
                        if (applyResult.hasConflicts()) {
                            currentSyncStatus = STATUS_CONFLICT;
                            handleConflicts(applyResult.conflicts);
                        } else {
                            // مزامنة ناجحة
                            markChangesAsSynced(localChanges);
                            updateLastSyncTimestamp();
                            currentSyncStatus = STATUS_IDLE;
                            
                            notificationHelper.sendCriticalAlert("SYNC_SUCCESS", 
                                "تمت المزامنة بنجاح", 0);
                        }
                    } else {
                        currentSyncStatus = STATUS_ERROR;
                        Log.e(TAG, "Failed to download remote changes: " + downloadResponse.error);
                    }
                } else {
                    currentSyncStatus = STATUS_ERROR;
                    Log.e(TAG, "Failed to upload local changes: " + uploadResponse.error);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error during synchronization", e);
                currentSyncStatus = STATUS_ERROR;
                notificationHelper.sendCriticalAlert("SYNC_ERROR", 
                    "خطأ في المزامنة: " + e.getMessage(), 0);
            } finally {
                isSyncInProgress = false;
                Log.d(TAG, "Synchronization completed with status: " + currentSyncStatus);
            }
        });
    }
    
    /**
     * رفع التغييرات المحلية للخادم
     * Upload local changes to server
     */
    private SyncResponse uploadLocalChanges(List<ChangeRecord> changes) {
        try {
            if (changes.isEmpty()) {
                return new SyncResponse(true, "No local changes to upload", null);
            }
            
            // إنشاء payload للرفع
            JsonObject payload = new JsonObject();
            payload.addProperty("deviceId", getDeviceId());
            payload.addProperty("timestamp", System.currentTimeMillis());
            payload.add("changes", gson.toJsonTree(changes));
            
            // تشفير البيانات إذا لزم الأمر
            String data = gson.toJson(payload);
            if (isEncryptionEnabled()) {
                data = securityUtils.encryptData(data);
            }
            
            // إرسال الطلب
            String serverUrl = getServerUrl() + "/api/sync/upload";
            Map<String, String> headers = createAuthHeaders();
            
            NetworkUtils.ApiResponse response = NetworkUtils.sendPostRequest(serverUrl, data, headers).get();
            
            if (response.isSuccess()) {
                SyncUploadResponse uploadResponse = gson.fromJson(response.getData(), SyncUploadResponse.class);
                return new SyncResponse(true, "Upload successful", null);
            } else {
                return new SyncResponse(false, "Upload failed: " + response.getData(), null);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error uploading local changes", e);
            return new SyncResponse(false, "Upload error: " + e.getMessage(), null);
        }
    }
    
    /**
     * تنزيل التغييرات من الخادم
     * Download remote changes from server
     */
    private SyncResponse downloadRemoteChanges() {
        try {
            // إنشاء طلب التنزيل
            JsonObject request = new JsonObject();
            request.addProperty("deviceId", getDeviceId());
            request.addProperty("lastSyncTimestamp", getLastSyncTimestamp());
            
            String data = gson.toJson(request);
            String serverUrl = getServerUrl() + "/api/sync/download";
            Map<String, String> headers = createAuthHeaders();
            
            NetworkUtils.ApiResponse response = NetworkUtils.sendPostRequest(serverUrl, data, headers).get();
            
            if (response.isSuccess()) {
                String responseData = response.getData();
                
                // فك التشفير إذا لزم الأمر
                if (isEncryptionEnabled()) {
                    responseData = securityUtils.decryptData(responseData);
                }
                
                SyncDownloadResponse downloadResponse = gson.fromJson(responseData, SyncDownloadResponse.class);
                
                return new SyncResponse(true, "Download successful", downloadResponse.changes);
            } else {
                return new SyncResponse(false, "Download failed: " + response.getData(), null);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error downloading remote changes", e);
            return new SyncResponse(false, "Download error: " + e.getMessage(), null);
        }
    }
    
    /**
     * تطبيق التغييرات البعيدة
     * Apply remote changes
     */
    private ApplyResult applyRemoteChanges(List<ChangeRecord> remoteChanges) {
        ApplyResult result = new ApplyResult();
        
        if (remoteChanges == null || remoteChanges.isEmpty()) {
            return result;
        }
        
        try {
            for (ChangeRecord change : remoteChanges) {
                try {
                    // فحص التعارضات
                    ConflictInfo conflict = detectConflict(change);
                    
                    if (conflict != null) {
                        result.addConflict(conflict);
                        continue;
                    }
                    
                    // تطبيق التغيير
                    boolean applied = applyChange(change);
                    
                    if (applied) {
                        result.appliedChanges++;
                    } else {
                        result.failedChanges++;
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error applying change: " + change.id, e);
                    result.failedChanges++;
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error applying remote changes", e);
        }
        
        return result;
    }
    
    /**
     * كشف التعارضات
     * Detect conflicts
     */
    private ConflictInfo detectConflict(ChangeRecord remoteChange) {
        try {
            // البحث عن تغيير محلي متعارض
            ChangeRecord localChange = changeTracker.findConflictingChange(remoteChange);
            
            if (localChange != null) {
                // فحص التوقيت
                if (localChange.timestamp > remoteChange.timestamp) {
                    return new ConflictInfo(localChange, remoteChange, "Local change is newer");
                } else if (localChange.timestamp < remoteChange.timestamp) {
                    return new ConflictInfo(localChange, remoteChange, "Remote change is newer");
                } else {
                    // نفس التوقيت - فحص المحتوى
                    if (!localChange.data.equals(remoteChange.data)) {
                        return new ConflictInfo(localChange, remoteChange, "Same timestamp, different data");
                    }
                }
            }
            
            return null;
            
        } catch (Exception e) {
            Log.e(TAG, "Error detecting conflict", e);
            return null;
        }
    }
    
    /**
     * تطبيق تغيير واحد
     * Apply single change
     */
    private boolean applyChange(ChangeRecord change) {
        try {
            switch (change.entityType) {
                case "Account":
                    return applyAccountChange(change);
                case "Transaction":
                    return applyTransactionChange(change);
                case "Category":
                    return applyCategoryChange(change);
                case "User":
                    return applyUserChange(change);
                default:
                    Log.w(TAG, "Unknown entity type: " + change.entityType);
                    return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error applying change for entity: " + change.entityType, e);
            return false;
        }
    }
    
    /**
     * تطبيق تغيير حساب
     * Apply account change
     */
    private boolean applyAccountChange(ChangeRecord change) {
        try {
            Account account = gson.fromJson(change.data, Account.class);
            
            switch (change.operation) {
                case CHANGE_INSERT:
                    database.accountDao().insert(account);
                    break;
                case CHANGE_UPDATE:
                    database.accountDao().update(account);
                    break;
                case CHANGE_DELETE:
                    database.accountDao().delete(account);
                    break;
                default:
                    return false;
            }
            
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error applying account change", e);
            return false;
        }
    }
    
    /**
     * تطبيق تغيير معاملة
     * Apply transaction change
     */
    private boolean applyTransactionChange(ChangeRecord change) {
        try {
            Transaction transaction = gson.fromJson(change.data, Transaction.class);
            
            switch (change.operation) {
                case CHANGE_INSERT:
                    database.transactionDao().insert(transaction);
                    break;
                case CHANGE_UPDATE:
                    database.transactionDao().update(transaction);
                    break;
                case CHANGE_DELETE:
                    database.transactionDao().delete(transaction);
                    break;
                default:
                    return false;
            }
            
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error applying transaction change", e);
            return false;
        }
    }
    
    /**
     * معالجة التعارضات
     * Handle conflicts
     */
    private void handleConflicts(List<ConflictInfo> conflicts) {
        String resolutionMode = getConflictResolutionMode();
        
        switch (resolutionMode) {
            case RESOLUTION_LATEST_WINS:
                resolveConflictsLatestWins(conflicts);
                break;
            case RESOLUTION_LOCAL_WINS:
                resolveConflictsLocalWins(conflicts);
                break;
            case RESOLUTION_REMOTE_WINS:
                resolveConflictsRemoteWins(conflicts);
                break;
            case RESOLUTION_MERGE:
                resolveConflictsMerge(conflicts);
                break;
            case RESOLUTION_MANUAL:
            default:
                notifyUserOfConflicts(conflicts);
                break;
        }
    }
    
    /**
     * حل التعارضات - آخر تعديل يفوز
     * Resolve conflicts - latest wins
     */
    private void resolveConflictsLatestWins(List<ConflictInfo> conflicts) {
        for (ConflictInfo conflict : conflicts) {
            if (conflict.localChange.timestamp > conflict.remoteChange.timestamp) {
                // الاحتفاظ بالتغيير المحلي
                markChangeAsResolved(conflict.localChange);
            } else {
                // تطبيق التغيير البعيد
                applyChange(conflict.remoteChange);
                markChangeAsResolved(conflict.localChange);
            }
        }
    }
    
    /**
     * حل التعارضات - المحلي يفوز
     * Resolve conflicts - local wins
     */
    private void resolveConflictsLocalWins(List<ConflictInfo> conflicts) {
        for (ConflictInfo conflict : conflicts) {
            // الاحتفاظ بالتغيير المحلي وتجاهل البعيد
            markChangeAsResolved(conflict.localChange);
        }
    }
    
    /**
     * حل التعارضات - البعيد يفوز
     * Resolve conflicts - remote wins
     */
    private void resolveConflictsRemoteWins(List<ConflictInfo> conflicts) {
        for (ConflictInfo conflict : conflicts) {
            // تطبيق التغيير البعيد وحذف المحلي
            applyChange(conflict.remoteChange);
            markChangeAsResolved(conflict.localChange);
        }
    }
    
    /**
     * حل التعارضات - دمج ذكي
     * Resolve conflicts - smart merge
     */
    private void resolveConflictsMerge(List<ConflictInfo> conflicts) {
        for (ConflictInfo conflict : conflicts) {
            try {
                // محاولة دمج التغييرات
                ChangeRecord mergedChange = mergeChanges(conflict.localChange, conflict.remoteChange);
                
                if (mergedChange != null) {
                    applyChange(mergedChange);
                    markChangeAsResolved(conflict.localChange);
                } else {
                    // إذا فشل الدمج، استخدم آخر تعديل
                    resolveConflictsLatestWins(Arrays.asList(conflict));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error merging changes", e);
                // في حالة الفشل، استخدم آخر تعديل
                resolveConflictsLatestWins(Arrays.asList(conflict));
            }
        }
    }
    
    /**
     * دمج التغييرات
     * Merge changes
     */
    private ChangeRecord mergeChanges(ChangeRecord localChange, ChangeRecord remoteChange) {
        // تنفيذ منطق الدمج حسب نوع الكيان
        // هذا مثال مبسط
        
        if (!localChange.entityType.equals(remoteChange.entityType)) {
            return null; // لا يمكن دمج أنواع مختلفة
        }
        
        // استخدام التوقيت الأحدث
        ChangeRecord merged = new ChangeRecord();
        merged.id = localChange.id;
        merged.entityType = localChange.entityType;
        merged.entityId = localChange.entityId;
        merged.operation = localChange.operation;
        merged.timestamp = Math.max(localChange.timestamp, remoteChange.timestamp);
        
        // دمج البيانات (منطق مبسط)
        merged.data = localChange.timestamp > remoteChange.timestamp ? 
            localChange.data : remoteChange.data;
        
        return merged;
    }
    
    /**
     * إشعار المستخدم بالتعارضات
     * Notify user of conflicts
     */
    private void notifyUserOfConflicts(List<ConflictInfo> conflicts) {
        String message = String.format("توجد %d تعارضات في المزامنة تحتاج لتدخل يدوي", conflicts.size());
        
        notificationHelper.sendCriticalAlert("SYNC_CONFLICTS", message, 0);
        
        // حفظ التعارضات للمراجعة اليدوية
        saveConflictsForManualReview(conflicts);
    }
    
    /**
     * بدء المزامنة التلقائية
     * Start automatic sync
     */
    private void startAutoSync() {
        if (scheduledExecutor.isShutdown()) {
            scheduledExecutor = Executors.newScheduledThreadPool(2);
        }
        
        int intervalMinutes = getSyncInterval();
        
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                performSync(false);
            }
        }, intervalMinutes, intervalMinutes, TimeUnit.MINUTES);
        
        Log.d(TAG, "Auto sync started with interval: " + intervalMinutes + " minutes");
    }
    
    /**
     * إيقاف المزامنة التلقائية
     * Stop automatic sync
     */
    private void stopAutoSync() {
        if (scheduledExecutor != null && !scheduledExecutor.isShutdown()) {
            scheduledExecutor.shutdown();
        }
        
        Log.d(TAG, "Auto sync stopped");
    }
    
    /**
     * تفعيل/تعطيل المزامنة التلقائية
     * Enable/disable automatic sync
     */
    public void enableAutoSync(boolean enabled) {
        prefs.edit().putBoolean(AUTO_SYNC_KEY, enabled).apply();
        
        if (enabled) {
            startAutoSync();
        } else {
            stopAutoSync();
        }
    }
    
    /**
     * حل التعارضات بطريقة محددة
     * Resolve conflicts with specified method
     */
    private void resolveConflicts(String resolutionMode) {
        syncExecutor.execute(() -> {
            try {
                List<ConflictInfo> conflicts = loadPendingConflicts();
                
                if (conflicts.isEmpty()) {
                    Log.d(TAG, "No conflicts to resolve");
                    return;
                }
                
                switch (resolutionMode) {
                    case RESOLUTION_LATEST_WINS:
                        resolveConflictsLatestWins(conflicts);
                        break;
                    case RESOLUTION_LOCAL_WINS:
                        resolveConflictsLocalWins(conflicts);
                        break;
                    case RESOLUTION_REMOTE_WINS:
                        resolveConflictsRemoteWins(conflicts);
                        break;
                    case RESOLUTION_MERGE:
                        resolveConflictsMerge(conflicts);
                        break;
                    default:
                        Log.w(TAG, "Unknown resolution mode: " + resolutionMode);
                        return;
                }
                
                // مسح التعارضات المحلولة
                clearResolvedConflicts();
                
                // تحديث حالة المزامنة
                currentSyncStatus = STATUS_IDLE;
                
                notificationHelper.sendCriticalAlert("CONFLICTS_RESOLVED", 
                    "تم حل التعارضات بنجاح", 0);
                
            } catch (Exception e) {
                Log.e(TAG, "Error resolving conflicts", e);
            }
        });
    }
    
    // Helper methods
    
    private String getDeviceId() {
        return prefs.getString("device_id", "default_device");
    }
    
    private String getServerUrl() {
        return prefs.getString("server_url", "");
    }
    
    private boolean isEncryptionEnabled() {
        return prefs.getBoolean("encryption_enabled", true);
    }
    
    private Map<String, String> createAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + getAuthToken());
        headers.put("X-Device-ID", getDeviceId());
        return headers;
    }
    
    private String getAuthToken() {
        return prefs.getString("auth_token", "");
    }
    
    private boolean isAutoSyncEnabled() {
        return prefs.getBoolean(AUTO_SYNC_KEY, false);
    }
    
    private int getSyncInterval() {
        return prefs.getInt(SYNC_INTERVAL_KEY, 30); // default 30 minutes
    }
    
    private long getLastSyncTimestamp() {
        return prefs.getLong(LAST_SYNC_TIMESTAMP_KEY, 0);
    }
    
    private void updateLastSyncTimestamp() {
        prefs.edit().putLong(LAST_SYNC_TIMESTAMP_KEY, System.currentTimeMillis()).apply();
    }
    
    private String getConflictResolutionMode() {
        return prefs.getString(CONFLICT_RESOLUTION_MODE_KEY, RESOLUTION_MANUAL);
    }
    
    private void markChangesAsSynced(List<ChangeRecord> changes) {
        changeTracker.markAsSynced(changes);
    }
    
    private void markChangeAsResolved(ChangeRecord change) {
        changeTracker.markAsResolved(change);
    }
    
    private void saveConflictsForManualReview(List<ConflictInfo> conflicts) {
        // حفظ التعارضات في قاعدة البيانات للمراجعة اليدوية
        String conflictsJson = gson.toJson(conflicts);
        prefs.edit().putString("pending_conflicts", conflictsJson).apply();
    }
    
    private List<ConflictInfo> loadPendingConflicts() {
        String conflictsJson = prefs.getString("pending_conflicts", "[]");
        return gson.fromJson(conflictsJson, List.class);
    }
    
    private void clearResolvedConflicts() {
        prefs.edit().remove("pending_conflicts").apply();
    }
    
    // تطبيق تغييرات أخرى
    
    private boolean applyCategoryChange(ChangeRecord change) {
        try {
            Category category = gson.fromJson(change.data, Category.class);
            
            switch (change.operation) {
                case CHANGE_INSERT:
                    database.categoryDao().insert(category);
                    break;
                case CHANGE_UPDATE:
                    database.categoryDao().update(category);
                    break;
                case CHANGE_DELETE:
                    database.categoryDao().delete(category);
                    break;
                default:
                    return false;
            }
            
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error applying category change", e);
            return false;
        }
    }
    
    private boolean applyUserChange(ChangeRecord change) {
        try {
            User user = gson.fromJson(change.data, User.class);
            
            switch (change.operation) {
                case CHANGE_INSERT:
                    database.userDao().insert(user);
                    break;
                case CHANGE_UPDATE:
                    database.userDao().update(user);
                    break;
                case CHANGE_DELETE:
                    database.userDao().delete(user);
                    break;
                default:
                    return false;
            }
            
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error applying user change", e);
            return false;
        }
    }
    
    /**
     * الحصول على حالة المزامنة الحالية
     * Get current sync status
     */
    public String getCurrentSyncStatus() {
        return currentSyncStatus;
    }
    
    /**
     * فحص ما إذا كانت المزامنة جارية
     * Check if sync is in progress
     */
    public boolean isSyncInProgress() {
        return isSyncInProgress;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // إيقاف جميع المنفذين
        if (syncExecutor != null && !syncExecutor.isShutdown()) {
            syncExecutor.shutdown();
        }
        
        if (scheduledExecutor != null && !scheduledExecutor.isShutdown()) {
            scheduledExecutor.shutdown();
        }
        
        Log.d(TAG, "SyncService destroyed");
    }
    
    // Classes مساعدة
    
    public static class ChangeRecord {
        public String id;
        public String entityType;
        public long entityId;
        public String operation;
        public String data;
        public long timestamp;
        public boolean synced;
    }
    
    public static class ConflictInfo {
        public ChangeRecord localChange;
        public ChangeRecord remoteChange;
        public String description;
        
        public ConflictInfo(ChangeRecord localChange, ChangeRecord remoteChange, String description) {
            this.localChange = localChange;
            this.remoteChange = remoteChange;
            this.description = description;
        }
    }
    
    public static class SyncResponse {
        public boolean success;
        public String error;
        public List<ChangeRecord> changes;
        
        public SyncResponse(boolean success, String error, List<ChangeRecord> changes) {
            this.success = success;
            this.error = error;
            this.changes = changes;
        }
    }
    
    public static class ApplyResult {
        public int appliedChanges = 0;
        public int failedChanges = 0;
        public List<ConflictInfo> conflicts = new ArrayList<>();
        
        public void addConflict(ConflictInfo conflict) {
            conflicts.add(conflict);
        }
        
        public boolean hasConflicts() {
            return !conflicts.isEmpty();
        }
    }
    
    public static class SyncUploadResponse {
        public boolean success;
        public String message;
    }
    
    public static class SyncDownloadResponse {
        public boolean success;
        public List<ChangeRecord> changes;
    }
    
    /**
     * متتبع التغييرات
     * Change tracker
     */
    private static class ChangeTracker {
        private List<ChangeRecord> pendingChanges = new ArrayList<>();
        
        public List<ChangeRecord> getPendingChanges() {
            return new ArrayList<>(pendingChanges);
        }
        
        public ChangeRecord findConflictingChange(ChangeRecord remoteChange) {
            for (ChangeRecord localChange : pendingChanges) {
                if (localChange.entityType.equals(remoteChange.entityType) &&
                    localChange.entityId == remoteChange.entityId) {
                    return localChange;
                }
            }
            return null;
        }
        
        public void markAsSynced(List<ChangeRecord> changes) {
            for (ChangeRecord change : changes) {
                change.synced = true;
            }
            pendingChanges.removeAll(changes);
        }
        
        public void markAsResolved(ChangeRecord change) {
            pendingChanges.remove(change);
        }
        
        public void addChange(ChangeRecord change) {
            pendingChanges.add(change);
        }
    }
}