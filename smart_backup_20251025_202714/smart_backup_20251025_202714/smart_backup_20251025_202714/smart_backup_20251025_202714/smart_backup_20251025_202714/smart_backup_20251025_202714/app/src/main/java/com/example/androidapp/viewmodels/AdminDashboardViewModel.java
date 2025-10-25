package com.example.androidapp.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.androidapp.data.entities.ChangeLog;
import com.example.androidapp.data.entities.RecordLock;
import com.example.androidapp.models.UserActivity;
import com.example.androidapp.models.UserSession;
import com.example.androidapp.utils.ConcurrentSessionManager;
import com.example.androidapp.utils.ChangeTrackingManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * نموذج العرض للوحة التحكم الإدارية
 * يدير البيانات والحالة للواجهات الإدارية
 */
public class AdminDashboardViewModel extends AndroidViewModel {
    
    private static final String TAG = "AdminDashboardViewModel";
    
    private final ConcurrentSessionManager sessionManager;
    private final ChangeTrackingManager changeManager;
    private final ExecutorService executorService;
    
    // البيانات المباشرة للإحصائيات
    private final MutableLiveData<Integer> activeLocksCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> activeSessionsCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> todayChangesCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> conflictsCount = new MutableLiveData<>();
    
    // البيانات المباشرة للقوائم
    private final MutableLiveData<List<UserSession>> activeSessions = new MutableLiveData<>();
    private final MutableLiveData<List<UserActivity>> userActivities = new MutableLiveData<>();
    private final MutableLiveData<List<RecordLock>> activeLocks = new MutableLiveData<>();
    private final MutableLiveData<List<ChangeLog>> recentChanges = new MutableLiveData<>();
    
    // حالة التحميل والأخطاء
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public AdminDashboardViewModel(@NonNull Application application) {
        super(application);
        
        sessionManager = new ConcurrentSessionManager(application);
        changeManager = new ChangeTrackingManager(application);
        executorService = Executors.newFixedThreadPool(3);
        
        // تحميل البيانات الأولية
        refreshData();
    }
    
    // ================= الحصول على البيانات المباشرة =================
    
    public LiveData<Integer> getActiveLocksCount() {
        return activeLocksCount;
    }
    
    public LiveData<Integer> getActiveSessionsCount() {
        return activeSessionsCount;
    }
    
    public LiveData<Integer> getTodayChangesCount() {
        return todayChangesCount;
    }
    
    public LiveData<Integer> getConflictsCount() {
        return conflictsCount;
    }
    
    public LiveData<List<UserSession>> getActiveSessions() {
        return activeSessions;
    }
    
    public LiveData<List<UserActivity>> getUserActivities() {
        return userActivities;
    }
    
    public LiveData<List<RecordLock>> getActiveLocks() {
        return activeLocks;
    }
    
    public LiveData<List<ChangeLog>> getRecentChanges() {
        return recentChanges;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    // ================= تحديث البيانات =================
    
    /**
     * تحديث جميع البيانات
     */
    public void refreshData() {
        isLoading.postValue(true);
        
        executorService.execute(() -> {
            try {
                loadStatistics();
                loadActiveSessions();
                loadUserActivities();
                loadActiveLocks();
                loadRecentChanges();
                
                isLoading.postValue(false);
                
            } catch (Exception e) {
                errorMessage.postValue("خطأ في تحميل البيانات: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }
    
    /**
     * تحميل الإحصائيات فقط
     */
    public void refreshStatistics() {
        executorService.execute(this::loadStatistics);
    }
    
    /**
     * تحميل الجلسات النشطة فقط
     */
    public void refreshActiveSessions() {
        executorService.execute(this::loadActiveSessions);
    }
    
    // ================= تحميل البيانات الداخلية =================
    
    private void loadStatistics() {
        try {
            int locks = sessionManager.getActiveLocksCount();
            int sessions = sessionManager.getActiveSessionsCount();
            int changes = changeManager.getTodayChangesCount();
            int conflicts = sessionManager.getTodayConflictsCount();
            
            activeLocksCount.postValue(locks);
            activeSessionsCount.postValue(sessions);
            todayChangesCount.postValue(changes);
            conflictsCount.postValue(conflicts);
            
        } catch (Exception e) {
            errorMessage.postValue("خطأ في تحميل الإحصائيات: " + e.getMessage());
        }
    }
    
    private void loadActiveSessions() {
        try {
            List<UserSession> sessions = sessionManager.getAllActiveSessions();
            activeSessions.postValue(sessions);
        } catch (Exception e) {
            errorMessage.postValue("خطأ في تحميل الجلسات النشطة: " + e.getMessage());
        }
    }
    
    private void loadUserActivities() {
        try {
            // TODO: تنفيذ تحميل أنشطة المستخدمين من قاعدة البيانات
            // هنا يمكن إنشاء بيانات تجريبية أو ربطها بنظام فعلي
            java.util.List<UserActivity> activities = new java.util.ArrayList<>();
            userActivities.postValue(activities);
        } catch (Exception e) {
            errorMessage.postValue("خطأ في تحميل أنشطة المستخدمين: " + e.getMessage());
        }
    }
    
    private void loadActiveLocks() {
        try {
            List<RecordLock> locks = sessionManager.getAllActiveLocks();
            activeLocks.postValue(locks);
        } catch (Exception e) {
            errorMessage.postValue("خطأ في تحميل الأقفال النشطة: " + e.getMessage());
        }
    }
    
    private void loadRecentChanges() {
        try {
            // تحميل آخر 50 تغيير
            List<ChangeLog> changes = changeManager.getAllChangeLogs();
            
            // ترتيب حسب التاريخ (الأحدث أولاً) وأخذ أول 50
            if (changes.size() > 50) {
                changes = changes.subList(0, 50);
            }
            
            recentChanges.postValue(changes);
        } catch (Exception e) {
            errorMessage.postValue("خطأ في تحميل التغييرات الحديثة: " + e.getMessage());
        }
    }
    
    // ================= إجراءات إدارية =================
    
    /**
     * إنهاء جلسة مستخدم
     */
    public void terminateUserSession(String sessionId) {
        executorService.execute(() -> {
            try {
                boolean success = sessionManager.terminateSession(sessionId);
                if (success) {
                    loadActiveSessions();
                    loadStatistics();
                } else {
                    errorMessage.postValue("فشل في إنهاء الجلسة");
                }
            } catch (Exception e) {
                errorMessage.postValue("خطأ في إنهاء الجلسة: " + e.getMessage());
            }
        });
    }
    
    /**
     * إلغاء قفل
     */
    public void releaseLock(String recordId, String entityType) {
        executorService.execute(() -> {
            try {
                boolean success = sessionManager.forcefullyReleaseLock(recordId, entityType);
                if (success) {
                    loadActiveLocks();
                    loadStatistics();
                } else {
                    errorMessage.postValue("فشل في إلغاء القفل");
                }
            } catch (Exception e) {
                errorMessage.postValue("خطأ في إلغاء القفل: " + e.getMessage());
            }
        });
    }
    
    /**
     * تنظيف البيانات القديمة
     */
    public void cleanupOldData(int daysToKeep) {
        executorService.execute(() -> {
            try {
                int deletedChanges = changeManager.deleteOldChangeLogs(daysToKeep);
                int releasedLocks = sessionManager.releaseExpiredLocks();
                
                refreshData();
                
                // يمكن إضافة callback للإشعار بنتيجة التنظيف
                
            } catch (Exception e) {
                errorMessage.postValue("خطأ في تنظيف البيانات: " + e.getMessage());
            }
        });
    }
    
    /**
     * الحصول على إحصائيات مفصلة
     */
    public void getDetailedStatistics(StatisticsCallback callback) {
        executorService.execute(() -> {
            try {
                ChangeTrackingManager.ChangeStatistics changeStats = changeManager.getChangeStatistics();
                
                DetailedStatistics stats = new DetailedStatistics(
                    sessionManager.getActiveLocksCount(),
                    sessionManager.getActiveSessionsCount(),
                    changeStats.getTodayChanges(),
                    changeStats.getWeekChanges(),
                    changeStats.getMonthChanges(),
                    changeStats.getTotalChanges(),
                    sessionManager.getTodayConflictsCount()
                );
                
                callback.onSuccess(stats);
                
            } catch (Exception e) {
                callback.onError("خطأ في الحصول على الإحصائيات: " + e.getMessage());
            }
        });
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    // ================= الكلاسات المساعدة =================
    
    /**
     * إحصائيات مفصلة
     */
    public static class DetailedStatistics {
        private final int activeLocks;
        private final int activeSessions;
        private final int todayChanges;
        private final int weekChanges;
        private final int monthChanges;
        private final int totalChanges;
        private final int todayConflicts;
        
        public DetailedStatistics(int activeLocks, int activeSessions, int todayChanges,
                                int weekChanges, int monthChanges, int totalChanges, int todayConflicts) {
            this.activeLocks = activeLocks;
            this.activeSessions = activeSessions;
            this.todayChanges = todayChanges;
            this.weekChanges = weekChanges;
            this.monthChanges = monthChanges;
            this.totalChanges = totalChanges;
            this.todayConflicts = todayConflicts;
        }
        
        // Getters
        public int getActiveLocks() { return activeLocks; }
        public int getActiveSessions() { return activeSessions; }
        public int getTodayChanges() { return todayChanges; }
        public int getWeekChanges() { return weekChanges; }
        public int getMonthChanges() { return monthChanges; }
        public int getTotalChanges() { return totalChanges; }
        public int getTodayConflicts() { return todayConflicts; }
    }
    
    /**
     * واجهة callback للإحصائيات
     */
    public interface StatisticsCallback {
        void onSuccess(DetailedStatistics statistics);
        void onError(String error);
    }
}