package com.example.androidapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.androidapp.services.BackgroundSyncService;
import com.example.androidapp.utils.SessionManager;

/**
 * Network change receiver to handle connectivity changes
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    
    private static final String TAG = "NetworkChangeReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Network change detected: " + intent.getAction());
        
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean isConnected = isNetworkConnected(context);
            
            Log.d(TAG, "Network connected: " + isConnected);
            
            if (isConnected) {
                onNetworkConnected(context);
            } else {
                onNetworkDisconnected(context);
            }
        }
    }
    
    /**
     * Handle network connected event
     */
    private void onNetworkConnected(Context context) {
        try {
            // Check if user is logged in
            SessionManager sessionManager = new SessionManager(context);
            if (sessionManager.isLoggedIn()) {
                Log.d(TAG, "Network connected and user logged in, starting sync");
                
                // Start background sync when network is available
                Intent syncIntent = new Intent(context, BackgroundSyncService.class);
                syncIntent.setAction(BackgroundSyncService.ACTION_SYNC_ON_NETWORK);
                context.startForegroundService(syncIntent);
                
                // Notify other components about network availability
                Intent networkIntent = new Intent("com.example.androidapp.NETWORK_CONNECTED");
                context.sendBroadcast(networkIntent);
                
                Log.d(TAG, "Sync service started and broadcast sent");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling network connected event", e);
        }
    }
    
    /**
     * Handle network disconnected event
     */
    private void onNetworkDisconnected(Context context) {
        try {
            Log.d(TAG, "Network disconnected, switching to offline mode");
            
            // Notify components about network unavailability
            Intent networkIntent = new Intent("com.example.androidapp.NETWORK_DISCONNECTED");
            context.sendBroadcast(networkIntent);
            
            // Stop non-essential network operations
            Intent syncIntent = new Intent(context, BackgroundSyncService.class);
            syncIntent.setAction(BackgroundSyncService.ACTION_STOP_NETWORK_SYNC);
            context.startService(syncIntent);
            
            Log.d(TAG, "Offline mode activated");
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling network disconnected event", e);
        }
    }
    
    /**
     * Check if network is connected
     */
    private boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking network connectivity", e);
        }
        
        return false;
    }
    
    /**
     * Get network type
     */
    private String getNetworkType(Context context) {
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    switch (activeNetworkInfo.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            return "WiFi";
                        case ConnectivityManager.TYPE_MOBILE:
                            return "Mobile";
                        case ConnectivityManager.TYPE_ETHERNET:
                            return "Ethernet";
                        default:
                            return "Other";
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting network type", e);
        }
        
        return "None";
    }
    
    /**
     * Check if network is metered (limited data)
     */
    private boolean isNetworkMetered(Context context) {
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager != null) {
                return connectivityManager.isActiveNetworkMetered();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking if network is metered", e);
        }
        
        return true; // Assume metered if unknown
    }
}