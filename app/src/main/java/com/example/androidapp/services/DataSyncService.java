package com.example.androidapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.OfflineTransactionDao;
import com.example.androidapp.data.entities.OfflineTransaction;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * خدمة مزامنة البيانات - لمزامنة البيانات المحلية مع الخادم
 */
public class DataSyncService extends Service {

    private OfflineTransactionDao offlineTransactionDao;
    private ScheduledExecutorService scheduler;
    private boolean isOnline = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        AppDatabase database = AppDatabase.getDatabase(this);
        offlineTransactionDao = database.offlineTransactionDao();
        
        scheduler = Executors.newScheduledThreadPool(2);
        
        // بدء مراقبة المزامنة كل 30 ثانية
        startSyncMonitoring();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startSyncMonitoring() {
        scheduler.scheduleAtFixedRate(this::syncPendingTransactions, 0, 30, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::retryFailedTransactions, 60, 300, TimeUnit.SECONDS); // كل 5 دقائق
    }

    private void syncPendingTransactions() {
        try {
            if (!isNetworkAvailable()) {
                return;
            }
            
            List<OfflineTransaction> pendingTransactions = 
                offlineTransactionDao.getPendingTransactions();
                
            for (OfflineTransaction transaction : pendingTransactions) {
                syncTransaction(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryFailedTransactions() {
        try {
            if (!isNetworkAvailable()) {
                return;
            }
            
            Date currentTime = new Date();
            List<OfflineTransaction> retryTransactions = 
                offlineTransactionDao.getTransactionsDueForRetry(currentTime);
                
            for (OfflineTransaction transaction : retryTransactions) {
                syncTransaction(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncTransaction(OfflineTransaction transaction) {
        try {
            // تحديث حالة المعاملة إلى "جاري المزامنة"
            offlineTransactionDao.markAsSyncing(transaction.getId(), new Date());
            
            // محاولة إرسال البيانات إلى الخادم
            boolean success = sendToServer(transaction);
            
            if (success) {
                // نجحت المزامنة
                Date now = new Date();
                offlineTransactionDao.markAsSynced(
                    transaction.getId(),
                    "SYNCED",
                    now,
                    now,
                    "Success",
                    now
                );
            } else {
                // فشلت المزامنة
                handleSyncFailure(transaction, "Server error");
            }
            
        } catch (Exception e) {
            handleSyncFailure(transaction, e.getMessage());
        }
    }

    private boolean sendToServer(OfflineTransaction transaction) {
        // هنا يتم إرسال البيانات إلى الخادم
        // يمكن استخدام Retrofit أو OkHttp
        
        try {
            // مثال على إرسال البيانات
            // إنشاء الطلب HTTP المناسب حسب نوع المعاملة
            
            switch (transaction.getTransactionType()) {
                case "CREATE":
                    return handleCreateTransaction(transaction);
                case "UPDATE":
                    return handleUpdateTransaction(transaction);
                case "DELETE":
                    return handleDeleteTransaction(transaction);
                default:
                    return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean handleCreateTransaction(OfflineTransaction transaction) {
        // تنفيذ إنشاء الكيان في الخادم
        // POST request
        return true; // مثال
    }

    private boolean handleUpdateTransaction(OfflineTransaction transaction) {
        // تنفيذ تحديث الكيان في الخادم
        // PUT request
        return true; // مثال
    }

    private boolean handleDeleteTransaction(OfflineTransaction transaction) {
        // تنفيذ حذف الكيان في الخادم
        // DELETE request
        return true; // مثال
    }

    private void handleSyncFailure(OfflineTransaction transaction, String errorMessage) {
        try {
            Date now = new Date();
            Date nextRetry = calculateNextRetryTime(transaction.getRetryCount() + 1);
            
            offlineTransactionDao.markAsFailed(
                transaction.getId(),
                errorMessage,
                now,
                nextRetry,
                now
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Date calculateNextRetryTime(int retryCount) {
        // زيادة فترة الانتظار مع كل محاولة (exponential backoff)
        long delayMs = Math.min(300000, 30000 * (long) Math.pow(2, retryCount)); // حد أقصى 5 دقائق
        return new Date(System.currentTimeMillis() + delayMs);
    }

    private boolean isNetworkAvailable() {
        // فحص توفر الإنترنت
        // يمكن استخدام ConnectivityManager
        return true; // مثال
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
