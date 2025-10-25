package com.example.androidapp.utils;

import android.content.Context;
import android.util.Log;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.ChangeLog;
import com.example.androidapp.data.dao.ChangeLogDao;
import com.example.androidapp.services.RealTimeNotificationService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * مدير تتبع التغييرات في النظام
 * يوفر آليات شاملة لتسجيل ومراقبة جميع التغييرات على البيانات
 * 
 * @author MiniMax Agent
 * @version 1.0
 * @since 2025-10-20
 */
public class ChangeTrackingManager {
    
    private static final String TAG = "ChangeTrackingManager";
    private static ChangeTrackingManager instance;
    
    private final Context context;
    private final AppDatabase database;
    private final ChangeLogDao changeLogDao;
    private final SessionManager sessionManager;
    private final Gson gson;
    private final ExecutorService executorService;
    
    // معرفات المستخدم والشركة الحالية
    private String currentUserId;
    private String currentUserName;
    private String currentUserRole;
    private String currentCompanyId;
    private String currentSessionId;
    
    // معرف المعاملة الحالية لربط التغييرات المتعددة
    private String currentTransactionId;
    
    // خدمة الإشعارات الفورية
    private RealTimeNotificationService notificationService;
    
    /**
     * Constructor خاص للنمط Singleton
     */
    private ChangeTrackingManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getDatabase(context);
        this.changeLogDao = database.changeLogDao();
        this.sessionManager = new SessionManager(context);
        this.gson = new Gson();
        this.executorService = Executors.newFixedThreadPool(2);
        
        // تهيئة معلومات المستخدم الحالي
        initializeCurrentUser();
        
        Log.i(TAG, "ChangeTrackingManager initialized successfully");
    }
    
    /**
     * الحصول على مثيل المدير (Singleton)
     */
    public static synchronized ChangeTrackingManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChangeTrackingManager(context);
        }
        return instance;
    }
    
    /**
     * تهيئة معلومات المستخدم الحالي
     */
    private void initializeCurrentUser() {
        currentUserId = sessionManager.getUserId();
        currentUserName = sessionManager.getUserName();
        currentUserRole = sessionManager.getUserRole();
        currentCompanyId = sessionManager.getCompanyId();
        currentSessionId = sessionManager.getSessionId();
        
        Log.d(TAG, "Current user initialized for change tracking: " + currentUserName);
    }
    
    /**
     * تعيين خدمة الإشعارات الفورية
     */
    public void setNotificationService(RealTimeNotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * بدء معاملة جديدة لربط التغييرات المتعددة
     */
    public String startTransaction(String description) {
        currentTransactionId = "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString();
        Log.d(TAG, "Started transaction: " + currentTransactionId + " - " + description);
        return currentTransactionId;
    }
    
    /**
     * إنهاء المعاملة الحالية
     */
    public void endTransaction() {
        if (currentTransactionId != null) {
            Log.d(TAG, "Ended transaction: " + currentTransactionId);
            currentTransactionId = null;
        }
    }
    
    /**
     * تسجيل تغيير عام في النظام
     */
    public void logChange(String recordId, String recordType, String changeType, 
                         String reason) {
        logChange(recordId, recordType, changeType, null, null, null, reason, 
                 ChangeLog.SeverityLevel.MEDIUM, ChangeLog.ChangeCategory.USER_ACTION);
    }
    
    /**
     * تسجيل تغيير في حقل محدد
     */
    public void logFieldChange(String recordId, String recordType, String fieldName, 
                              Object oldValue, Object newValue, String reason) {
        String oldValueStr = oldValue != null ? oldValue.toString() : null;
        String newValueStr = newValue != null ? newValue.toString() : null;
        
        logChange(recordId, recordType, ChangeLog.ChangeType.UPDATE, fieldName, 
                 oldValueStr, newValueStr, reason, 
                 ChangeLog.SeverityLevel.LOW, ChangeLog.ChangeCategory.USER_ACTION);
    }
    
    /**
     * تسجيل تغيير مالي حرج
     */
    public void logFinancialChange(String recordId, String recordType, String changeType,
                                  String fieldName, Object oldValue, Object newValue, String reason) {
        String oldValueStr = oldValue != null ? oldValue.toString() : null;
        String newValueStr = newValue != null ? newValue.toString() : null;
        
        logChange(recordId, recordType, changeType, fieldName, oldValueStr, newValueStr, reason,
                 ChangeLog.SeverityLevel.HIGH, ChangeLog.ChangeCategory.FINANCIAL);
    }
    
    /**
     * تسجيل تغيير أمني حرج
     */
    public void logSecurityChange(String recordId, String recordType, String changeType,
                                 String fieldName, Object oldValue, Object newValue, String reason) {
        String oldValueStr = oldValue != null ? oldValue.toString() : null;
        String newValueStr = newValue != null ? newValue.toString() : null;
        
        logChange(recordId, recordType, changeType, fieldName, oldValueStr, newValueStr, reason,
                 ChangeLog.SeverityLevel.CRITICAL, ChangeLog.ChangeCategory.SECURITY);
    }
    
    /**
     * تسجيل تغيير شامل مع جميع الخيارات
     */
    public void logChange(String recordId, String recordType, String changeType, 
                         String fieldName, String oldValue, String newValue, String reason,
                         String severityLevel, String category) {
        
        executorService.execute(() -> {
            try {
                // إنشاء سجل التغيير
                ChangeLog changeLog = new ChangeLog(recordId, recordType, changeType, 
                                                   currentUserId, currentUserName, currentCompanyId);
                
                // تعيين التفاصيل
                changeLog.setFieldName(fieldName);
                changeLog.setOldValue(oldValue);
                changeLog.setNewValue(newValue);
                changeLog.setChangeReason(reason);
                changeLog.setSeverityLevel(severityLevel);
                changeLog.setChangeCategory(category);
                
                // معلومات الجلسة والنظام
                changeLog.setSessionId(currentSessionId);
                changeLog.setUserRole(currentUserRole);
                changeLog.setTransactionId(currentTransactionId);
                changeLog.setIpAddress(NetworkUtils.getLocalIpAddress(context));
                changeLog.setDeviceInfo(getDeviceInfo());
                
                // تحديد اسم الجدول
                changeLog.setTableName(getTableNameForRecordType(recordType));
                
                // تعيين حالة المراجعة
                if (changeLog.needsReview()) {
                    changeLog.setReviewStatus(ChangeLog.ReviewStatus.PENDING);
                } else {
                    changeLog.setReviewStatus(ChangeLog.ReviewStatus.AUTO_APPROVED);
                }
                
                // حفظ في قاعدة البيانات
                long logId = changeLogDao.insertChangeLog(changeLog);
                changeLog.setLogId(logId);
                
                // إرسال إشعار فوري إذا كانت الخدمة متاحة
                if (notificationService != null && shouldNotifyOthers(changeLog)) {
                    notificationService.notifyRecordChanged(recordId, recordType, changeType, 
                                                          fieldName, oldValue, newValue);
                }
                
                Log.d(TAG, "Change logged successfully - Record: " + recordType + ":" + recordId + 
                          ", Change: " + changeType + ", Severity: " + severityLevel);
                
            } catch (Exception e) {
                Log.e(TAG, "Error logging change for record: " + recordType + ":" + recordId, e);
            }
        });
    }
    
    /**
     * تسجيل تغيير كائن كامل (مقارنة القيم القديمة والجديدة)
     */
    public <T> void logObjectChange(String recordId, T oldObject, T newObject, String reason) {
        if (oldObject == null && newObject == null) {
            return;
        }
        
        String recordType = getRecordTypeFromObject(oldObject != null ? oldObject : newObject);
        String changeType = determineChangeType(oldObject, newObject);
        
        executorService.execute(() -> {
            try {
                // إنشاء معرف معاملة للتغييرات المتعددة
                String transactionId = startTransaction("Object change: " + recordType);
                
                if (changeType.equals(ChangeLog.ChangeType.CREATE)) {
                    // كائن جديد - تسجيل إنشاء
                    logChange(recordId, recordType, ChangeLog.ChangeType.CREATE, null, null, null, reason,
                             ChangeLog.SeverityLevel.MEDIUM, ChangeLog.ChangeCategory.USER_ACTION);
                    
                    // حفظ البيانات الكاملة
                    logCompleteObjectData(recordId, recordType, null, newObject, reason);
                    
                } else if (changeType.equals(ChangeLog.ChangeType.DELETE)) {
                    // حذف كائن
                    logChange(recordId, recordType, ChangeLog.ChangeType.DELETE, null, null, null, reason,
                             ChangeLog.SeverityLevel.HIGH, ChangeLog.ChangeCategory.USER_ACTION);
                    
                    // حفظ البيانات المحذوفة
                    logCompleteObjectData(recordId, recordType, oldObject, null, reason);
                    
                } else if (changeType.equals(ChangeLog.ChangeType.UPDATE)) {
                    // مقارنة الحقول وتسجيل التغييرات
                    compareAndLogFieldChanges(recordId, recordType, oldObject, newObject, reason);
                }
                
                endTransaction();
                
            } catch (Exception e) {
                Log.e(TAG, "Error logging object change", e);
                endTransaction();
            }
        });
    }
    
    /**
     * مقارنة كائنين وتسجيل التغييرات في الحقول
     */
    private <T> void compareAndLogFieldChanges(String recordId, String recordType, 
                                              T oldObject, T newObject, String reason) {
        try {
            Class<?> clazz = oldObject.getClass();
            Field[] fields = clazz.getDeclaredFields();
            
            for (Field field : fields) {
                field.setAccessible(true);
                
                Object oldValue = field.get(oldObject);
                Object newValue = field.get(newObject);
                
                // فحص التغيير
                if (!isEqual(oldValue, newValue)) {
                    String fieldName = field.getName();
                    String severityLevel = getFieldSeverityLevel(fieldName, recordType);
                    String category = getFieldCategory(fieldName, recordType);
                    
                    logChange(recordId, recordType, ChangeLog.ChangeType.UPDATE, fieldName,
                             oldValue != null ? oldValue.toString() : null,
                             newValue != null ? newValue.toString() : null,
                             reason, severityLevel, category);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error comparing objects for changes", e);
        }
    }
    
    /**
     * تسجيل البيانات الكاملة للكائن
     */
    private <T> void logCompleteObjectData(String recordId, String recordType, 
                                          T beforeObject, T afterObject, String reason) {
        try {
            String beforeData = beforeObject != null ? gson.toJson(beforeObject) : null;
            String afterData = afterObject != null ? gson.toJson(afterObject) : null;
            
            ChangeLog changeLog = new ChangeLog(recordId, recordType, 
                                              beforeObject == null ? ChangeLog.ChangeType.CREATE : ChangeLog.ChangeType.DELETE,
                                              currentUserId, currentUserName, currentCompanyId);
            
            changeLog.setBeforeData(beforeData);
            changeLog.setAfterData(afterData);
            changeLog.setChangeReason(reason);
            changeLog.setTransactionId(currentTransactionId);
            
            changeLogDao.insertChangeLog(changeLog);
            
        } catch (Exception e) {
            Log.e(TAG, "Error logging complete object data", e);
        }
    }
    
    /**
     * الحصول على تاريخ التغييرات لسجل معين
     */
    public void getChangeHistory(String recordId, String recordType, ChangeHistoryCallback callback) {
        executorService.execute(() -> {
            try {
                List<ChangeLog> changeLogs = changeLogDao.getChangeLogsForRecord(
                    recordId, recordType, currentCompanyId
                );
                
                // إعادة النتيجة للـ UI thread
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(changeLogs);
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error getting change history", e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError(e.getMessage());
                });
            }
        });
    }
    
    /**
     * البحث في التغييرات
     */
    public void searchChanges(String searchQuery, ChangeSearchCallback callback) {
        executorService.execute(() -> {
            try {
                List<ChangeLog> changeLogs = changeLogDao.searchChangeLogs(
                    currentCompanyId, "%" + searchQuery + "%"
                );
                
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(changeLogs);
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error searching changes", e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError(e.getMessage());
                });
            }
        });
    }
    
    /**
     * الحصول على التغييرات المعلقة للمراجعة
     */
    public void getPendingReviewChanges(PendingChangesCallback callback) {
        executorService.execute(() -> {
            try {
                List<ChangeLog> pendingChanges = changeLogDao.getPendingReviewChanges(currentCompanyId);
                
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(pendingChanges);
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error getting pending review changes", e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError(e.getMessage());
                });
            }
        });
    }
    
    /**
     * الموافقة على تغيير
     */
    public void approveChange(long logId, String notes, ApprovalCallback callback) {
        executorService.execute(() -> {
            try {
                int updated = changeLogDao.approveChangeLog(logId, currentUserId, new Date(), notes);
                
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    if (updated > 0) {
                        callback.onSuccess("تم اعتماد التغيير بنجاح");
                    } else {
                        callback.onError("فشل في اعتماد التغيير");
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error approving change", e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError(e.getMessage());
                });
            }
        });
    }
    
    /**
     * رفض تغيير
     */
    public void rejectChange(long logId, String notes, ApprovalCallback callback) {
        executorService.execute(() -> {
            try {
                int updated = changeLogDao.rejectChangeLog(logId, currentUserId, new Date(), notes);
                
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    if (updated > 0) {
                        callback.onSuccess("تم رفض التغيير");
                    } else {
                        callback.onError("فشل في رفض التغيير");
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error rejecting change", e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError(e.getMessage());
                });
            }
        });
    }
    
    // Helper Methods
    
    private String getRecordTypeFromObject(Object object) {
        if (object == null) return "unknown";
        
        String className = object.getClass().getSimpleName().toLowerCase();
        
        // إزالة كلمات غير ضرورية
        if (className.endsWith("entity")) {
            className = className.substring(0, className.length() - 6);
        }
        
        return className;
    }
    
    private String determineChangeType(Object oldObject, Object newObject) {
        if (oldObject == null && newObject != null) {
            return ChangeLog.ChangeType.CREATE;
        } else if (oldObject != null && newObject == null) {
            return ChangeLog.ChangeType.DELETE;
        } else {
            return ChangeLog.ChangeType.UPDATE;
        }
    }
    
    private boolean isEqual(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) return true;
        if (obj1 == null || obj2 == null) return false;
        return obj1.equals(obj2);
    }
    
    private String getFieldSeverityLevel(String fieldName, String recordType) {
        // حقول مالية حرجة
        if (fieldName.toLowerCase().contains("amount") || 
            fieldName.toLowerCase().contains("price") ||
            fieldName.toLowerCase().contains("total") ||
            fieldName.toLowerCase().contains("balance")) {
            return ChangeLog.SeverityLevel.HIGH;
        }
        
        // حقول أمنية
        if (fieldName.toLowerCase().contains("password") ||
            fieldName.toLowerCase().contains("permission") ||
            fieldName.toLowerCase().contains("role")) {
            return ChangeLog.SeverityLevel.CRITICAL;
        }
        
        return ChangeLog.SeverityLevel.MEDIUM;
    }
    
    private String getFieldCategory(String fieldName, String recordType) {
        if (fieldName.toLowerCase().contains("amount") || 
            fieldName.toLowerCase().contains("price") ||
            fieldName.toLowerCase().contains("total") ||
            fieldName.toLowerCase().contains("balance")) {
            return ChangeLog.ChangeCategory.FINANCIAL;
        }
        
        if (fieldName.toLowerCase().contains("password") ||
            fieldName.toLowerCase().contains("permission") ||
            fieldName.toLowerCase().contains("role")) {
            return ChangeLog.ChangeCategory.SECURITY;
        }
        
        return ChangeLog.ChangeCategory.USER_ACTION;
    }
    
    private String getTableNameForRecordType(String recordType) {
        // تحويل نوع السجل إلى اسم الجدول
        switch (recordType) {
            case "invoice": return "invoices";
            case "customer": return "customers";
            case "supplier": return "suppliers";
            case "product": return "items";
            case "account": return "accounts";
            case "payment": return "payments";
            case "order": return "orders";
            case "employee": return "employees";
            default: return recordType + "s";
        }
    }
    
    private boolean shouldNotifyOthers(ChangeLog changeLog) {
        // إرسال إشعارات للتغييرات المهمة فقط
        return ChangeLog.SeverityLevel.HIGH.equals(changeLog.getSeverityLevel()) ||
               ChangeLog.SeverityLevel.CRITICAL.equals(changeLog.getSeverityLevel()) ||
               ChangeLog.ChangeCategory.FINANCIAL.equals(changeLog.getChangeCategory());
    }
    
    private String getDeviceInfo() {
        return android.os.Build.MANUFACTURER + " " + 
               android.os.Build.MODEL + " (Android " + 
               android.os.Build.VERSION.RELEASE + ")";
    }
    
    /**
     * إنهاء المدير وتحرير الموارد
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        Log.i(TAG, "ChangeTrackingManager shutdown completed");
    }
    
    // Callback Interfaces
    
    public interface ChangeHistoryCallback {
        void onSuccess(java.util.List<ChangeLog> changeLogs);
        void onError(String error);
    }
    
    public interface ChangeSearchCallback {
        void onSuccess(java.util.List<ChangeLog> changeLogs);
        void onError(String error);
    }
    
    public interface PendingChangesCallback {
        void onSuccess(java.util.List<ChangeLog> pendingChanges);
        void onError(String error);
    }
    
    public interface ApprovalCallback {
        void onSuccess(String message);
        void onError(String error);
    }
    
    // ================= وظائف جديدة للواجهات الإدارية =================
    
    /**
     * الحصول على عدد التغييرات اليومية
     */
    public int getTodayChangesCount() {
        try {
            long startOfDay = getTodayStartTime();
            return changeLogDao.getChangesCountSince(startOfDay);
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على عدد التغييرات اليومية", e);
            return 0;
        }
    }
    
    /**
     * الحصول على جميع سجلات التغيير
     */
    public java.util.List<ChangeLog> getAllChangeLogs() {
        try {
            return changeLogDao.getAllChangeLogs();
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على سجلات التغيير", e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * الحصول على سجلات التغيير حسب التاريخ
     */
    public java.util.List<ChangeLog> getChangeLogsByDateRange(long startTime, long endTime) {
        try {
            return changeLogDao.getChangeLogsByDateRange(startTime, endTime);
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على سجلات التغيير حسب التاريخ", e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * الحصول على سجلات التغيير حسب المستخدم
     */
    public java.util.List<ChangeLog> getChangeLogsByUser(String userId) {
        try {
            return changeLogDao.getChangeLogsByUser(userId);
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على سجلات التغيير للمستخدم", e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * الحصول على سجلات التغيير حسب نوع الكيان
     */
    public java.util.List<ChangeLog> getChangeLogsByEntityType(String entityType) {
        try {
            return changeLogDao.getChangeLogsByEntityType(entityType);
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على سجلات التغيير للكيان", e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * الحصول على سجلات التغيير حسب نوع التغيير
     */
    public java.util.List<ChangeLog> getChangeLogsByChangeType(String changeType) {
        try {
            return changeLogDao.getChangeLogsByChangeType(changeType);
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على سجلات التغيير حسب النوع", e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * البحث في سجلات التغيير
     */
    public java.util.List<ChangeLog> searchChangeLogs(String searchTerm) {
        try {
            return changeLogDao.searchChangeLogs("%" + searchTerm + "%");
        } catch (Exception e) {
            Log.e(TAG, "خطأ في البحث في سجلات التغيير", e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * حذف سجلات التغيير القديمة
     */
    public int deleteOldChangeLogs(int daysToKeep) {
        try {
            long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L);
            int deletedCount = changeLogDao.deleteChangeLogsOlderThan(cutoffTime);
            Log.i(TAG, "تم حذف " + deletedCount + " سجل تغيير قديم");
            return deletedCount;
        } catch (Exception e) {
            Log.e(TAG, "خطأ في حذف سجلات التغيير القديمة", e);
            return 0;
        }
    }
    
    /**
     * الحصول على إحصائيات التغييرات
     */
    public ChangeStatistics getChangeStatistics() {
        try {
            long todayStart = getTodayStartTime();
            long weekStart = todayStart - (7 * 24 * 60 * 60 * 1000L);
            long monthStart = todayStart - (30 * 24 * 60 * 60 * 1000L);
            
            int totalChanges = changeLogDao.getTotalChangesCount();
            int todayChanges = changeLogDao.getChangesCountSince(todayStart);
            int weekChanges = changeLogDao.getChangesCountSince(weekStart);
            int monthChanges = changeLogDao.getChangesCountSince(monthStart);
            
            return new ChangeStatistics(totalChanges, todayChanges, weekChanges, monthChanges);
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على إحصائيات التغييرات", e);
            return new ChangeStatistics(0, 0, 0, 0);
        }
    }
    
    /**
     * الحصول على أكثر المستخدمين نشاطاً
     */
    public java.util.List<UserActivitySummary> getMostActiveUsers(int limit) {
        try {
            return changeLogDao.getMostActiveUsers(limit);
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على أكثر المستخدمين نشاطاً", e);
            return java.util.Collections.emptyList();
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
    
    /**
     * إحصائيات التغييرات
     */
    public static class ChangeStatistics {
        private final int totalChanges;
        private final int todayChanges;
        private final int weekChanges;
        private final int monthChanges;
        
        public ChangeStatistics(int totalChanges, int todayChanges, int weekChanges, int monthChanges) {
            this.totalChanges = totalChanges;
            this.todayChanges = todayChanges;
            this.weekChanges = weekChanges;
            this.monthChanges = monthChanges;
        }
        
        public int getTotalChanges() { return totalChanges; }
        public int getTodayChanges() { return todayChanges; }
        public int getWeekChanges() { return weekChanges; }
        public int getMonthChanges() { return monthChanges; }
    }
    
    /**
     * ملخص نشاط المستخدم
     */
    public static class UserActivitySummary {
        private final String userId;
        private final int changeCount;
        private final long lastActivityTime;
        
        public UserActivitySummary(String userId, int changeCount, long lastActivityTime) {
            this.userId = userId;
            this.changeCount = changeCount;
            this.lastActivityTime = lastActivityTime;
        }
        
        public String getUserId() { return userId; }
        public int getChangeCount() { return changeCount; }
        public long getLastActivityTime() { return lastActivityTime; }
    }
}