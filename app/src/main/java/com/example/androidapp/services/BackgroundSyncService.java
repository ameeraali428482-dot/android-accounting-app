package com.example.androidapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.sync.SyncManager;
import com.example.androidapp.ui.main.MainActivity;
import com.example.androidapp.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Background sync service for data synchronization
 */
public class BackgroundSyncService extends Service {
    
    private static final String TAG = "BackgroundSyncService";
    
    // Actions
    public static final String ACTION_START_SYNC = "com.example.androidapp.START_SYNC";
    public static final String ACTION_STOP_SYNC = "com.example.androidapp.STOP_SYNC";
    public static final String ACTION_SYNC_ON_NETWORK = "com.example.androidapp.SYNC_ON_NETWORK";
    public static final String ACTION_STOP_NETWORK_SYNC = "com.example.androidapp.STOP_NETWORK_SYNC";
    public static final String ACTION_FORCE_SYNC = "com.example.androidapp.FORCE_SYNC";
    
    // Notification
    private static final String CHANNEL_ID = "sync_service_channel";
    private static final int NOTIFICATION_ID = 1001;
    
    // Sync components
    private SyncManager syncManager;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private AppDatabase database;
    
    // State
    private boolean isSyncRunning = false;
    private boolean isNetworkSyncEnabled = true;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Background sync service created");
        
        // Initialize components
        syncManager = new SyncManager(this);
        sessionManager = new SessionManager(this);
        database = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Create notification channel
        createNotificationChannel();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started with action: " + (intent != null ? intent.getAction() : "null"));
        
        if (intent != null) {
            String action = intent.getAction();
            
            switch (action != null ? action : "") {
                case ACTION_START_SYNC:
                    startBackgroundSync();
                    break;
                case ACTION_STOP_SYNC:
                    stopBackgroundSync();
                    break;
                case ACTION_SYNC_ON_NETWORK:
                    syncOnNetworkAvailable();
                    break;
                case ACTION_STOP_NETWORK_SYNC:
                    stopNetworkSync();
                    break;
                case ACTION_FORCE_SYNC:
                    forceSync();
                    break;
                default:
                    startBackgroundSync();
                    break;
            }
        } else {
            startBackgroundSync();
        }
        
        // Return START_STICKY to restart service if killed
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null; // This is a started service, not bound
    }
    
    @Override
    public void onDestroy() {
        Log.d(TAG, "Background sync service destroyed");
        
        // Stop all sync operations
        stopBackgroundSync();
        
        // Shutdown executor
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        
        super.onDestroy();
    }
    
    /**
     * Start background synchronization
     */
    private void startBackgroundSync() {
        if (!sessionManager.isLoggedIn()) {
            Log.w(TAG, "User not logged in, cannot start sync");
            stopSelf();
            return;
        }
        
        if (isSyncRunning) {
            Log.d(TAG, "Sync already running");
            return;
        }
        
        Log.d(TAG, "Starting background sync");
        isSyncRunning = true;
        
        // Start foreground service
        startForeground(NOTIFICATION_ID, createSyncNotification("جاري المزامنة..."));
        
        // Start sync in background thread
        executorService.execute(this::performBackgroundSync);
    }
    
    /**
     * Stop background synchronization
     */
    private void stopBackgroundSync() {
        Log.d(TAG, "Stopping background sync");
        
        isSyncRunning = false;
        isNetworkSyncEnabled = false;
        
        // Cancel sync operations
        if (syncManager != null) {
            syncManager.cancelAllSyncs();
        }
        
        // Stop foreground service
        stopForeground(true);
        stopSelf();
    }
    
    /**
     * Sync when network becomes available
     */
    private void syncOnNetworkAvailable() {
        if (!sessionManager.isLoggedIn()) {
            Log.w(TAG, "User not logged in, cannot sync");
            return;
        }
        
        Log.d(TAG, "Network available, starting sync");
        isNetworkSyncEnabled = true;
        
        if (!isSyncRunning) {
            startBackgroundSync();
        } else {
            // Resume sync operations
            executorService.execute(this::performNetworkSync);
        }
    }
    
    /**
     * Stop network sync operations
     */
    private void stopNetworkSync() {
        Log.d(TAG, "Stopping network sync operations");
        isNetworkSyncEnabled = false;
        
        if (syncManager != null) {
            syncManager.pauseNetworkOperations();
        }
        
        // Update notification
        updateNotification("متصل بلا شبكة - تم إيقاف المزامنة");
    }
    
    /**
     * Force immediate sync
     */
    private void forceSync() {
        Log.d(TAG, "Force sync requested");
        
        if (!sessionManager.isLoggedIn()) {
            Log.w(TAG, "User not logged in, cannot force sync");
            return;
        }
        
        // Start foreground service if not running
        if (!isSyncRunning) {
            startForeground(NOTIFICATION_ID, createSyncNotification("بدء المزامنة الفورية..."));
        }
        
        executorService.execute(this::performForceSync);
    }
    
    /**
     * Perform background synchronization
     */
    private void performBackgroundSync() {
        try {
            Log.d(TAG, "Performing background sync");
            
            while (isSyncRunning && sessionManager.isLoggedIn()) {
                try {
                    // Update notification
                    updateNotification("جاري مزامنة البيانات...");
                    
                    // Perform sync operations
                    boolean syncSuccess = syncManager.performIncrementalSync();
                    
                    if (syncSuccess) {
                        updateNotification("تمت المزامنة بنجاح");
                        Log.d(TAG, "Sync completed successfully");
                    } else {
                        updateNotification("فشلت المزامنة - سيتم إعادة المحاولة");
                        Log.w(TAG, "Sync failed, will retry");
                    }
                    
                    // Wait before next sync (30 minutes)
                    Thread.sleep(30 * 60 * 1000);
                    
                } catch (InterruptedException e) {
                    Log.d(TAG, "Sync interrupted");
                    break;
                } catch (Exception e) {
                    Log.e(TAG, "Error during sync", e);
                    updateNotification("خطأ في المزامنة");
                    
                    // Wait before retry (5 minutes)
                    try {
                        Thread.sleep(5 * 60 * 1000);
                    } catch (InterruptedException ie) {
                        break;
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Fatal error in background sync", e);
        } finally {
            isSyncRunning = false;
            stopForeground(true);
        }
    }
    
    /**
     * Perform network sync
     */
    private void performNetworkSync() {
        try {
            Log.d(TAG, "Performing network sync");
            
            updateNotification("مزامنة البيانات عبر الشبكة...");
            
            // Sync pending uploads
            boolean uploadSuccess = syncManager.syncPendingUploads();
            
            // Download latest data
            boolean downloadSuccess = syncManager.downloadLatestData();
            
            if (uploadSuccess && downloadSuccess) {
                updateNotification("تمت مزامنة الشبكة بنجاح");
                Log.d(TAG, "Network sync completed successfully");
            } else {
                updateNotification("فشلت مزامنة الشبكة جزئياً");
                Log.w(TAG, "Network sync partially failed");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error during network sync", e);
            updateNotification("خطأ في مزامنة الشبكة");
        }
    }
    
    /**
     * Perform force sync
     */
    private void performForceSync() {
        try {
            Log.d(TAG, "Performing force sync");
            
            updateNotification("مزامنة فورية جارية...");
            
            // Perform complete sync
            boolean syncSuccess = syncManager.performCompleteSync();
            
            if (syncSuccess) {
                updateNotification("تمت المزامنة الفورية بنجاح");
                Log.d(TAG, "Force sync completed successfully");
            } else {
                updateNotification("فشلت المزامنة الفورية");
                Log.w(TAG, "Force sync failed");
            }
            
            // Stop service after force sync
            stopBackgroundSync();
            
        } catch (Exception e) {
            Log.e(TAG, "Error during force sync", e);
            updateNotification("خطأ في المزامنة الفورية");
        }
    }
    
    /**
     * Create notification channel for Android O+
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "خدمة المزامنة",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("إشعارات مزامنة البيانات في الخلفية");
            channel.setSound(null, null);
            channel.enableVibration(false);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    /**
     * Create sync notification
     */
    private Notification createSyncNotification(String message) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0
        );
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("مزامنة البيانات")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_sync)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
    
    /**
     * Update notification message
     */
    private void updateNotification(String message) {
        try {
            NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            
            if (notificationManager != null) {
                Notification notification = createSyncNotification(message);
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating notification", e);
        }
    }
    
    /**
     * Check if sync service is running
     */
    public static boolean isServiceRunning() {
        // This would typically check if the service is actually running
        // For now, we'll return false as a placeholder
        return false;
    }
    
    /**
     * Start sync service
     */
    public static void startSyncService(Context context) {
        Intent intent = new Intent(context, BackgroundSyncService.class);
        intent.setAction(ACTION_START_SYNC);
        context.startForegroundService(intent);
    }
    
    /**
     * Stop sync service
     */
    public static void stopSyncService(Context context) {
        Intent intent = new Intent(context, BackgroundSyncService.class);
        intent.setAction(ACTION_STOP_SYNC);
        context.startService(intent);
    }
    
    /**
     * Force sync now
     */
    public static void forceSyncNow(Context context) {
        Intent intent = new Intent(context, BackgroundSyncService.class);
        intent.setAction(ACTION_FORCE_SYNC);
        context.startForegroundService(intent);
    }
}