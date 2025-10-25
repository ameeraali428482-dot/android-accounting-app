package com.example.androidapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.OfflineTransactionDao;
import com.example.androidapp.data.entities.OfflineTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * مدير العمل بدون إنترنت - لإدارة العمليات بدون اتصال بالإنترنت
 */
public class OfflineManager {

    private static final String TAG = "OfflineManager";
    
    private Context context;
    private OfflineTransactionDao offlineTransactionDao;
    private ConnectivityManager connectivityManager;
    
    public OfflineManager(Context context) {
        this.context = context;
        
        AppDatabase database = AppDatabase.getDatabase(context);
        this.offlineTransactionDao = database.offlineTransactionDao();
        
        this.connectivityManager = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * تسجيل عملية إنشاء عند العمل بدون إنترنت
     */
    public void recordCreateOperation(String userId, String entityType, String entityId,
                                     Object entityData, boolean isCritical) {
        try {
            OfflineTransaction transaction = new OfflineTransaction();
            transaction.setId(UUID.randomUUID().toString());
            transaction.setUserId(userId);
            transaction.setTransactionType("CREATE");
            transaction.setEntityType(entityType);
            transaction.setEntityId(entityId);
            transaction.setOperation("create_" + entityType.toLowerCase());
            transaction.setDataAfter(convertToJson(entityData));
            transaction.setCritical(isCritical);
            transaction.setSyncPriority(isCritical ? 10 : 5);
            transaction.setDeviceId(getDeviceId());
            transaction.setAppVersion(getAppVersion());
            transaction.setConnectionType(getConnectionType());
            
            offlineTransactionDao.insert(transaction);
            
            Log.d(TAG, "Recorded CREATE operation for " + entityType + " ID: " + entityId);
            
        } catch (Exception e) {
            Log.e(TAG, "Error recording CREATE operation", e);
        }
    }

    /**
     * تسجيل عملية تحديث عند العمل بدون إنترنت
     */
    public void recordUpdateOperation(String userId, String entityType, String entityId,
                                     Object oldData, Object newData, boolean isCritical) {
        try {
            OfflineTransaction transaction = new OfflineTransaction();
            transaction.setId(UUID.randomUUID().toString());
            transaction.setUserId(userId);
            transaction.setTransactionType("UPDATE");
            transaction.setEntityType(entityType);
            transaction.setEntityId(entityId);
            transaction.setOperation("update_" + entityType.toLowerCase());
            transaction.setDataBefore(convertToJson(oldData));
            transaction.setDataAfter(convertToJson(newData));
            transaction.setChanges(calculateChanges(oldData, newData));
            transaction.setCritical(isCritical);
            transaction.setSyncPriority(isCritical ? 10 : 5);
            transaction.setDeviceId(getDeviceId());
            transaction.setAppVersion(getAppVersion());
            transaction.setConnectionType(getConnectionType());
            
            offlineTransactionDao.insert(transaction);
            
            Log.d(TAG, "Recorded UPDATE operation for " + entityType + " ID: " + entityId);
            
        } catch (Exception e) {
            Log.e(TAG, "Error recording UPDATE operation", e);
        }
    }

    /**
     * تسجيل عملية حذف عند العمل بدون إنترنت
     */
    public void recordDeleteOperation(String userId, String entityType, String entityId,
                                     Object entityData, boolean isCritical) {
        try {
            OfflineTransaction transaction = new OfflineTransaction();
            transaction.setId(UUID.randomUUID().toString());
            transaction.setUserId(userId);
            transaction.setTransactionType("DELETE");
            transaction.setEntityType(entityType);
            transaction.setEntityId(entityId);
            transaction.setOperation("delete_" + entityType.toLowerCase());
            transaction.setDataBefore(convertToJson(entityData));
            transaction.setCritical(isCritical);
            transaction.setSyncPriority(isCritical ? 10 : 5);
            transaction.setDeviceId(getDeviceId());
            transaction.setAppVersion(getAppVersion());
            transaction.setConnectionType(getConnectionType());
            
            offlineTransactionDao.insert(transaction);
            
            Log.d(TAG, "Recorded DELETE operation for " + entityType + " ID: " + entityId);
            
        } catch (Exception e) {
            Log.e(TAG, "Error recording DELETE operation", e);
        }
    }

    /**
     * فحص حالة الاتصال بالإنترنت
     */
    public boolean isOnline() {
        if (connectivityManager == null) {
            return false;
        }
        
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * فحص عدد المعاملات المعلقة
     */
    public int getPendingTransactionCount() {
        try {
            return offlineTransactionDao.getPendingTransactions().size();
        } catch (Exception e) {
            Log.e(TAG, "Error getting pending transaction count", e);
            return 0;
        }
    }

    /**
     * فحص عدد المعاملات الحرجة
     */
    public int getCriticalPendingCount() {
        try {
            return offlineTransactionDao.getCriticalPendingTransactions().size();
        } catch (Exception e) {
            Log.e(TAG, "Error getting critical pending count", e);
            return 0;
        }
    }

    /**
     * تعيين أولوية المزامنة لمعاملة
     */
    public void setSyncPriority(String transactionId, int priority) {
        try {
            offlineTransactionDao.updatePriority(transactionId, priority, new Date());
            Log.d(TAG, "Updated sync priority for transaction " + transactionId + " to " + priority);
        } catch (Exception e) {
            Log.e(TAG, "Error updating sync priority", e);
        }
    }

    /**
     * تأكيد معاملة تحتاج إلى موافقة
     */
    public void confirmTransaction(String transactionId) {
        try {
            offlineTransactionDao.confirmTransaction(transactionId, new Date());
            Log.d(TAG, "Confirmed transaction " + transactionId);
        } catch (Exception e) {
            Log.e(TAG, "Error confirming transaction", e);
        }
    }

    /**
     * حذل معاملة معلقة
     */
    public void cancelPendingTransaction(String transactionId) {
        try {
            offlineTransactionDao.deleteById(transactionId);
            Log.d(TAG, "Cancelled pending transaction " + transactionId);
        } catch (Exception e) {
            Log.e(TAG, "Error cancelling transaction", e);
        }
    }

    /**
     * تنظيف المعاملات القديمة المزامنة
     */
    public void cleanupOldSyncedTransactions() {
        try {
            // حذف المعاملات المزامنة الأقدم من 30 يوم
            Date cutoffDate = new Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000));
            offlineTransactionDao.deleteOldSyncedTransactions(cutoffDate);
            
            Log.d(TAG, "Cleaned up old synced transactions");
        } catch (Exception e) {
            Log.e(TAG, "Error cleaning up old transactions", e);
        }
    }

    /**
     * تنظيف المعاملات المنتهية الصلاحية
     */
    public void cleanupExpiredTransactions() {
        try {
            Date currentDate = new Date();
            offlineTransactionDao.deleteExpiredTransactions(currentDate);
            
            Log.d(TAG, "Cleaned up expired transactions");
        } catch (Exception e) {
            Log.e(TAG, "Error cleaning up expired transactions", e);
        }
    }

    private String convertToJson(Object data) {
        try {
            if (data == null) {
                return null;
            }
            
            // يمكن استخدام Gson أو Jackson لتحويل أفضل
            // هنا مثال بسيط
            return data.toString();
            
        } catch (Exception e) {
            Log.e(TAG, "Error converting object to JSON", e);
            return "{}";
        }
    }

    private String calculateChanges(Object oldData, Object newData) {
        try {
            JSONObject changes = new JSONObject();
            changes.put("hasChanges", true);
            changes.put("timestamp", System.currentTimeMillis());
            
            // هنا يمكن إضافة منطق لحساب التغييرات الدقيقة
            
            return changes.toString();
            
        } catch (JSONException e) {
            Log.e(TAG, "Error calculating changes", e);
            return "{}";
        }
    }

    private String getDeviceId() {
        // يمكن استخدام Android ID أو معرف فريد آخر
        try {
            return android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID
            );
        } catch (Exception e) {
            return "unknown_device";
        }
    }

    private String getAppVersion() {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String getConnectionType() {
        if (!isOnline()) {
            return "OFFLINE";
        }
        
        try {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        return "WIFI";
                    case ConnectivityManager.TYPE_MOBILE:
                        return "MOBILE";
                    default:
                        return "OTHER";
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting connection type", e);
        }
        
        return "UNKNOWN";
    }
}
