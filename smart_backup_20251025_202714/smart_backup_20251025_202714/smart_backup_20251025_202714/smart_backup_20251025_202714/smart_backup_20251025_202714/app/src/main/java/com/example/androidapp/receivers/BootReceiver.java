package com.example.androidapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.androidapp.services.BackgroundSyncService;
import com.example.androidapp.utils.SessionManager;

/**
 * Boot receiver to restart services and sync after device boot
 */
public class BootReceiver extends BroadcastReceiver {
    
    private static final String TAG = "BootReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Boot receiver triggered: " + intent.getAction());
        
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
            Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction()) ||
            Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            
            try {
                // Check if user is logged in
                SessionManager sessionManager = new SessionManager(context);
                if (sessionManager.isLoggedIn()) {
                    Log.d(TAG, "User is logged in, starting background services");
                    
                    // Start background sync service
                    Intent syncServiceIntent = new Intent(context, BackgroundSyncService.class);
                    syncServiceIntent.setAction(BackgroundSyncService.ACTION_START_SYNC);
                    context.startForegroundService(syncServiceIntent);
                    
                    Log.d(TAG, "Background services started successfully");
                } else {
                    Log.d(TAG, "User not logged in, skipping service start");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error starting services on boot", e);
            }
        }
    }
}