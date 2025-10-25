package com.example.androidapp.utils;

import android.content.Context;
import android.util.Log;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.RecordLock;
import com.example.androidapp.data.entities.ChangeLog;
import com.example.androidapp.data.dao.RecordLockDao;
import com.example.androidapp.data.dao.ChangeLogDao;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * مدير الجلسات المتزامنة لإدارة تعدد المستخدمين في الوقت الفعلي
 * يوفر آليات لمنع تضارب البيانات وإدارة الأقفال والتغييرات
 * 
 * @author MiniMax Agent
 * @version 1.0
 * @since 2025-10-20
 */
public class ConcurrentSessionManager {
    
    private static final String TAG = "ConcurrentSessionManager";
    private static ConcurrentSessionManager instance;
    
    // إعدادات افتراضية
    private static final int DEFAULT_LOCK_TIMEOUT_MINUTES = 30;
    private static final int CLEANUP_INTERVAL_MINUTES = 5;
    private static final int MAX_CONCURRENT_LOCKS_PER_USER = 10;
    
    private final Context context;
    private final AppDatabase database;
    private final RecordLockDao recordLockDao;
    private final ChangeLogDao changeLogDao;
    private final SessionManager sessionManager;
    
    // خدمات التنفيذ
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;
    
    // خرائط للتتبع المحلي
    private final ConcurrentHashMap<String, RecordLock> activeLocks;
    private final ConcurrentHashMap<String, String> userSessions;
    private final ConcurrentHashMap<String, Long> lastActivity;
    
    // معرفات للجلسة الحالية
    private String currentSessionId;
    private String currentUserId;
    private String currentUserName;
    private String currentCompanyId;
    
    /**
     * Constructor خاص للنمط Singleton
     */
    private ConcurrentSessionManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getDatabase(context);
        this.recordLockDao = database.recordLockDao();
        this.changeLogDao = database.changeLogDao();
        this.sessionManager = new SessionManager(context);
        
        // تهيئة خدمات التنفيذ
        this.executorService = Executors.newFixedThreadPool(4);
        this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
        
        // تهيئة الخرائط
        this.activeLocks = new ConcurrentHashMap<>();
        this.userSessions = new ConcurrentHashMap<>();
        this.lastActivity = new ConcurrentHashMap<>();
        
        // بدء خدمة التنظيف الدورية
        startCleanupService();
        
        // تهيئة الجلسة الحالية
        initializeCurrentSession();
        
        Log.i(TAG, "ConcurrentSessionManager initialized successfully");
    }
    
    /**
     * الحصول على مثيل المدير (Singleton)
     */
    public static synchronized ConcurrentSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new ConcurrentSessionManager(context);
        }
        return instance;
    }
    
    /**
     * تهيئة الجلسة الحالية
     */
    private void initializeCurrentSession() {
        // استخراج معلومات المستخدم من SessionManager
        this.currentUserId = sessionManager.getUserId();
        this.currentUserName = sessionManager.getUserName();
        this.currentCompanyId = sessionManager.getCompanyId();
        this.currentSessionId = generateSessionId();
        
        // تسجيل الجلسة
        userSessions.put(currentUserId, currentSessionId);
        updateLastActivity(currentUserId);
        
        Log.d(TAG, "Current session initialized - User: " + currentUserName + 
                  ", Session: " + currentSessionId);
    }
    
    /**
     * توليد معرف جلسة فريد
     */
    private String generateSessionId() {
        return "SESSION_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString();
    }
    
    /**
     * محاولة قفل سجل معين
     */
    public LockResult attemptLock(String recordId, String recordType, String reason) {
        return attemptLock(recordId, recordType, reason, DEFAULT_LOCK_TIMEOUT_MINUTES);
    }
    
    /**
     * محاولة قفل سجل معين مع مدة زمنية محددة
     */
    public LockResult attemptLock(String recordId, String recordType, String reason, int timeoutMinutes) {
        updateLastActivity(currentUserId);
        
        try {
            // فحص الأقفال الموجودة
            RecordLock existingLock = recordLockDao.getActiveLockForRecord(
                recordId, recordType, currentCompanyId, new Date()
            );
            
            if (existingLock != null && !existingLock.getUserId().equals(currentUserId)) {
                // السجل مقفل من مستخدم آخر
                return new LockResult(false, "السجل مقفل حالياً من المستخدم: " + 
                                    existingLock.getUserName(), existingLock);
            }
            
            // فحص عدد الأقفال الحالية للمستخدم
            int userLocksCount = recordLockDao.getUserActiveLocksCount(
                currentUserId, currentCompanyId, new Date()
            );
            
            if (userLocksCount >= MAX_CONCURRENT_LOCKS_PER_USER) {
                return new LockResult(false, "تم الوصول للحد الأقصى من الأقفال المسموح بها");
            }
            
            // إنشاء قفل جديد
            RecordLock newLock = new RecordLock(recordId, recordType, currentUserId, 
                                              currentUserName, currentCompanyId, currentSessionId);
            newLock.setLockReason(reason);
            newLock.setIpAddress(NetworkUtils.getLocalIpAddress(context));
            newLock.setDeviceInfo(getDeviceInfo());
            
            // تعيين وقت انتهاء الصلاحية
            long expiryTime = System.currentTimeMillis() + (timeoutMinutes * 60 * 1000L);
            newLock.setExpiresAt(new Date(expiryTime));
            
            // حفظ القفل في قاعدة البيانات
            long lockId = recordLockDao.insertLock(newLock);
            newLock.setLockId(lockId);
            
            // إضافة القفل للتتبع المحلي
            String lockKey = generateLockKey(recordId, recordType);
            activeLocks.put(lockKey, newLock);
            
            // تسجيل التغيير
            logChange(recordId, recordType, ChangeLog.ChangeType.UPDATE, 
                     "locked", "unlocked", "locked", "قفل السجل: " + reason);
            
            Log.d(TAG, "Lock acquired successfully for record: " + recordId + " by user: " + currentUserName);
            return new LockResult(true, "تم قفل السجل بنجاح", newLock);
            
        } catch (Exception e) {
            Log.e(TAG, "Error acquiring lock for record: " + recordId, e);
            return new LockResult(false, "خطأ في قفل السجل: " + e.getMessage());
        }
    }
    
    /**
     * تحرير قفل سجل معين
     */
    public boolean releaseLock(String recordId, String recordType) {
        updateLastActivity(currentUserId);
        
        try {
            // تحرير القفل في قاعدة البيانات
            int updated = recordLockDao.releaseLock(recordId, recordType, currentCompanyId, 
                                                   currentUserId, new Date());
            
            if (updated > 0) {
                // إزالة القفل من التتبع المحلي
                String lockKey = generateLockKey(recordId, recordType);
                activeLocks.remove(lockKey);
                
                // تسجيل التغيير
                logChange(recordId, recordType, ChangeLog.ChangeType.UPDATE, 
                         "unlocked", "locked", "unlocked", "تحرير قفل السجل");
                
                Log.d(TAG, "Lock released successfully for record: " + recordId);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            Log.e(TAG, "Error releasing lock for record: " + recordId, e);
            return false;
        }
    }
    
    /**
     * تمديد مدة قفل معين
     */
    public boolean extendLock(String recordId, String recordType, int additionalMinutes) {
        updateLastActivity(currentUserId);
        
        try {
            // البحث عن القفل النشط
            RecordLock activeLock = recordLockDao.getActiveLockForRecord(
                recordId, recordType, currentCompanyId, new Date()
            );
            
            if (activeLock == null || !activeLock.getUserId().equals(currentUserId)) {
                return false;
            }
            
            // حساب الوقت الجديد
            long extensionMillis = additionalMinutes * 60 * 1000L;
            Date newExpiryTime = new Date(activeLock.getExpiresAt().getTime() + extensionMillis);
            
            // تحديث القفل
            int updated = recordLockDao.extendLock(activeLock.getLockId(), newExpiryTime, new Date());
            
            if (updated > 0) {
                // تحديث التتبع المحلي
                String lockKey = generateLockKey(recordId, recordType);
                RecordLock localLock = activeLocks.get(lockKey);
                if (localLock != null) {
                    localLock.setExpiresAt(newExpiryTime);
                }
                
                Log.d(TAG, "Lock extended successfully for record: " + recordId);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            Log.e(TAG, "Error extending lock for record: " + recordId, e);
            return false;
        }
    }
    
    /**
     * فحص ما إذا كان السجل مقفلاً
     */
    public boolean isRecordLocked(String recordId, String recordType) {
        try {
            RecordLock activeLock = recordLockDao.getActiveLockForRecord(
                recordId, recordType, currentCompanyId, new Date()
            );
            return activeLock != null;
        } catch (Exception e) {
            Log.e(TAG, "Error checking lock status for record: " + recordId, e);
            return false;
        }
    }
    
    /**
     * فحص ما إذا كان السجل مقفلاً من مستخدم آخر
     */
    public boolean isRecordLockedByOtherUser(String recordId, String recordType) {
        try {
            int count = recordLockDao.isRecordLockedByOtherUser(
                recordId, recordType, currentCompanyId, currentUserId, new Date()
            );
            return count > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking other user lock for record: " + recordId, e);
            return false;
        }
    }
    
    /**
     * الحصول على معلومات القفل النشط لسجل معين
     */
    public RecordLock getActiveLock(String recordId, String recordType) {
        try {
            return recordLockDao.getActiveLockForRecord(
                recordId, recordType, currentCompanyId, new Date()
            );
        } catch (Exception e) {
            Log.e(TAG, "Error getting active lock for record: " + recordId, e);
            return null;
        }
    }
    
    /**
     * تسجيل تغيير في النظام
     */
    public void logChange(String recordId, String recordType, String changeType, 
                         String fieldName, String oldValue, String newValue, String reason) {
        logChange(recordId, recordType, changeType, fieldName, oldValue, newValue, reason, 
                 ChangeLog.SeverityLevel.MEDIUM, ChangeLog.ChangeCategory.USER_ACTION);
    }
    
    /**
     * تسجيل تغيير مع مستوى خطورة وفئة محددة
     */
    public void logChange(String recordId, String recordType, String changeType, 
                         String fieldName, String oldValue, String newValue, String reason,
                         String severityLevel, String category) {
        try {
            ChangeLog changeLog = new ChangeLog(recordId, recordType, changeType, 
                                              currentUserId, currentUserName, currentCompanyId);
            
            changeLog.setFieldName(fieldName);
            changeLog.setOldValue(oldValue);
            changeLog.setNewValue(newValue);
            changeLog.setChangeReason(reason);
            changeLog.setSeverityLevel(severityLevel);
            changeLog.setChangeCategory(category);
            changeLog.setSessionId(currentSessionId);
            changeLog.setIpAddress(NetworkUtils.getLocalIpAddress(context));
            changeLog.setDeviceInfo(getDeviceInfo());
            changeLog.setUserRole(sessionManager.getUserRole());
            
            // تعيين حالة المراجعة حسب مستوى الخطورة
            if (changeLog.needsReview()) {
                changeLog.setReviewStatus(ChangeLog.ReviewStatus.PENDING);
            } else {
                changeLog.setReviewStatus(ChangeLog.ReviewStatus.AUTO_APPROVED);
            }
            
            // حفظ في قاعدة البيانات
            changeLogDao.insertChangeLog(changeLog);
            
            Log.d(TAG, "Change logged successfully for record: " + recordId + 
                      ", change: " + changeType);
            
        } catch (Exception e) {
            Log.e(TAG, "Error logging change for record: " + recordId, e);
        }
    }
    
    /**
     * تحرير جميع أقفال المستخدم الحالي
     */
    public void releaseAllUserLocks() {
        try {
            int released = recordLockDao.releaseAllUserLocks(currentUserId, currentCompanyId, new Date());
            
            // مسح التتبع المحلي
            activeLocks.clear();
            
            Log.d(TAG, "Released " + released + " locks for user: " + currentUserName);
            
        } catch (Exception e) {
            Log.e(TAG, "Error releasing all user locks", e);
        }
    }
    
    /**
     * تحرير أقفال الجلسة عند إنهائها
     */
    public void releaseSessionLocks() {
        try {
            int released = recordLockDao.releaseSessionLocks(currentSessionId, new Date());
            
            // مسح التتبع المحلي
            activeLocks.clear();
            userSessions.remove(currentUserId);
            lastActivity.remove(currentUserId);
            
            Log.d(TAG, "Released " + released + " locks for session: " + currentSessionId);
            
        } catch (Exception e) {
            Log.e(TAG, "Error releasing session locks", e);
        }
    }
    
    /**
     * الحصول على جميع الأقفال النشطة للشركة
     */
    public List<RecordLock> getActiveCompanyLocks() {
        try {
            return recordLockDao.getActiveLocksForCompany(currentCompanyId, new Date());
        } catch (Exception e) {
            Log.e(TAG, "Error getting active company locks", e);
            return null;
        }
    }
    
    /**
     * الحصول على إحصائيات الأقفال
     */
    public LockStatistics getLockStatistics() {
        try {
            int totalActiveLocks = recordLockDao.getActiveLocksCount(currentCompanyId, new Date());
            int userActiveLocks = recordLockDao.getUserActiveLocksCount(
                currentUserId, currentCompanyId, new Date()
            );
            
            return new LockStatistics(totalActiveLocks, userActiveLocks);
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting lock statistics", e);
            return new LockStatistics(0, 0);
        }
    }
    
    /**
     * تحديث آخر نشاط للمستخدم
     */
    private void updateLastActivity(String userId) {
        lastActivity.put(userId, System.currentTimeMillis());
    }
    
    /**
     * توليد مفتاح القفل
     */
    private String generateLockKey(String recordId, String recordType) {
        return recordType + ":" + recordId + ":" + currentCompanyId;
    }
    
    /**
     * الحصول على معلومات الجهاز
     */
    private String getDeviceInfo() {
        return android.os.Build.MANUFACTURER + " " + 
               android.os.Build.MODEL + " (Android " + 
               android.os.Build.VERSION.RELEASE + ")";
    }
    
    /**
     * بدء خدمة التنظيف الدورية
     */
    private void startCleanupService() {
        scheduledExecutorService.scheduleWithFixedDelay(
            this::performCleanup,
            CLEANUP_INTERVAL_MINUTES,
            CLEANUP_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        );
        
        Log.d(TAG, "Cleanup service started with interval: " + CLEANUP_INTERVAL_MINUTES + " minutes");
    }
    
    /**
     * تنفيذ عملية التنظيف
     */
    private void performCleanup() {
        try {
            // تعليم الأقفال المنتهية الصلاحية
            int expiredMarked = recordLockDao.markExpiredLocks(new Date());
            
            // حذف السجلات القديمة (أقدم من 30 يوم)
            Date cutoffDate = new Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000));
            int oldLocksDeleted = recordLockDao.deleteOldLocks(cutoffDate);
            int oldLogsDeleted = changeLogDao.deleteOldChangeLogs(cutoffDate);
            
            Log.d(TAG, "Cleanup completed - Expired locks marked: " + expiredMarked + 
                      ", Old locks deleted: " + oldLocksDeleted + 
                      ", Old logs deleted: " + oldLogsDeleted);
            
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup", e);
        }
    }
    
    /**
     * إنهاء المدير وتحرير الموارد
     */
    public void shutdown() {
        releaseSessionLocks();
        
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
        
        Log.i(TAG, "ConcurrentSessionManager shutdown completed");
    }
    
    // Inner Classes
    
    /**
     * نتيجة محاولة القفل
     */
    public static class LockResult {
        private final boolean success;
        private final String message;
        private final RecordLock lock;
        
        public LockResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public LockResult(boolean success, String message, RecordLock lock) {
            this.success = success;
            this.message = message;
            this.lock = lock;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public RecordLock getLock() { return lock; }
    }
    
    /**
     * إحصائيات الأقفال
     */
    public static class LockStatistics {
        private final int totalActiveLocks;
        private final int userActiveLocks;
        
        public LockStatistics(int totalActiveLocks, int userActiveLocks) {
            this.totalActiveLocks = totalActiveLocks;
            this.userActiveLocks = userActiveLocks;
        }
        
        public int getTotalActiveLocks() { return totalActiveLocks; }
        public int getUserActiveLocks() { return userActiveLocks; }
    }
    
    // ================= وظائف جديدة للواجهات الإدارية =================
    
    /**
     * الحصول على عدد الأقفال النشطة
     */
    public int getActiveLocksCount() {
        try {
            return recordLockDao.getActiveLocksCount();
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على عدد الأقفال النشطة", e);
            return 0;
        }
    }
    
    /**
     * الحصول على عدد الجلسات النشطة
     */
    public int getActiveSessionsCount() {
        return userSessions.size();
    }
    
    /**
     * الحصول على عدد التعارضات اليومية
     */
    public int getTodayConflictsCount() {
        try {
            long startOfDay = getTodayStartTime();
            return changeLogDao.getConflictsCountSince(startOfDay);
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على عدد التعارضات", e);
            return 0;
        }
    }
    
    /**
     * الحصول على جميع الأقفال النشطة
     */
    public List<RecordLock> getAllActiveLocks() {
        try {
            return recordLockDao.getAllActiveLocks();
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على الأقفال النشطة", e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * الحصول على جميع الجلسات النشطة
     */
    public List<com.example.androidapp.models.UserSession> getAllActiveSessions() {
        try {
            List<com.example.androidapp.models.UserSession> sessions = new java.util.ArrayList<>();
            for (String userId : userSessions.keySet()) {
                String sessionId = userSessions.get(userId);
                com.example.androidapp.models.UserSession session = getUserSessionInfo(userId, sessionId);
                if (session != null) {
                    sessions.add(session);
                }
            }
            return sessions;
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على الجلسات النشطة", e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * إنهاء جلسة معينة بقوة
     */
    public boolean terminateSession(String sessionId) {
        try {
            // البحث عن المستخدم بمعرف الجلسة
            String userId = null;
            for (java.util.Map.Entry<String, String> entry : userSessions.entrySet()) {
                if (sessionId.equals(entry.getValue())) {
                    userId = entry.getKey();
                    break;
                }
            }
            
            if (userId != null) {
                // إلغاء جميع أقفال المستخدم
                releaseUserLocks(userId);
                // إزالة الجلسة
                userSessions.remove(userId);
                Log.i(TAG, "تم إنهاء الجلسة: " + sessionId + " للمستخدم: " + userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إنهاء الجلسة", e);
            return false;
        }
    }
    
    /**
     * إنهاء جميع الجلسات
     */
    public int terminateAllSessions() {
        try {
            int count = userSessions.size();
            
            // إلغاء جميع الأقفال
            releaseAllLocks();
            
            // إزالة جميع الجلسات
            userSessions.clear();
            
            Log.i(TAG, "تم إنهاء " + count + " جلسة");
            return count;
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إنهاء جميع الجلسات", e);
            return 0;
        }
    }
    
    /**
     * الحصول على عدد الأقفال النشطة لمستخدم معين
     */
    public int getUserActiveLocks(String userId) {
        try {
            return recordLockDao.getUserActiveLocksCount(userId);
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على أقفال المستخدم", e);
            return 0;
        }
    }
    
    /**
     * إلغاء قفل بقوة
     */
    public boolean forcefullyReleaseLock(String recordId, String entityType) {
        try {
            String lockKey = generateLockKey(recordId, entityType);
            
            // إزالة من الذاكرة المحلية
            activeLocks.remove(lockKey);
            
            // إزالة من قاعدة البيانات
            recordLockDao.releaseLock(recordId, entityType);
            
            Log.i(TAG, "تم إلغاء القفل بقوة: " + lockKey);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إلغاء القفل بقوة", e);
            return false;
        }
    }
    
    /**
     * إلغاء جميع الأقفال
     */
    public int releaseAllLocks() {
        try {
            int count = activeLocks.size();
            
            // إزالة من الذاكرة المحلية
            activeLocks.clear();
            
            // إزالة من قاعدة البيانات
            recordLockDao.releaseAllLocks();
            
            Log.i(TAG, "تم إلغاء " + count + " قفل");
            return count;
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إلغاء جميع الأقفال", e);
            return 0;
        }
    }
    
    /**
     * إلغاء الأقفال المنتهية الصلاحية
     */
    public int releaseExpiredLocks() {
        try {
            long currentTime = System.currentTimeMillis();
            int count = recordLockDao.releaseExpiredLocks(currentTime);
            
            // تنظيف الذاكرة المحلية
            cleanupExpiredLocks();
            
            Log.i(TAG, "تم إلغاء " + count + " قفل منتهي الصلاحية");
            return count;
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إلغاء الأقفال المنتهية", e);
            return 0;
        }
    }
    
    /**
     * الحصول على معلومات جلسة المستخدم
     */
    private com.example.androidapp.models.UserSession getUserSessionInfo(String userId, String sessionId) {
        try {
            // هذه دالة مؤقتة - يجب ربطها بنظام إدارة الجلسات الفعلي
            long loginTime = System.currentTimeMillis() - (2 * 60 * 60 * 1000); // منذ ساعتين
            long lastActivity = System.currentTimeMillis() - (5 * 60 * 1000); // منذ 5 دقائق
            
            return new com.example.androidapp.models.UserSession(
                sessionId,
                userId,
                "Android Device", // يجب الحصول على معلومات الجهاز الفعلية
                loginTime,
                lastActivity,
                "192.168.1.100", // يجب الحصول على IP الفعلي
                "1.0.0", // إصدار التطبيق
                true
            );
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على معلومات الجلسة", e);
            return null;
        }
    }
    
    /**
     * الحصول على وقت بداية اليوم
     */
    private long getTodayStartTime() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}