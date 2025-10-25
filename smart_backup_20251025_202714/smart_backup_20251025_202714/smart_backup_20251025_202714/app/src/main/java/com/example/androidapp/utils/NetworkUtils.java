package com.example.androidapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Utility class for network operations and connectivity checks
 */
public class NetworkUtils {
    
    private static final String TAG = "NetworkUtils";
    
    private final Context context;
    private final ConnectivityManager connectivityManager;
    
    public NetworkUtils(Context context) {
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    
    /**
     * Check if network is available
     */
    public boolean isNetworkAvailable() {
        if (connectivityManager == null) {
            return false;
        }
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork == null) {
                    return false;
                }
                
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                return capabilities != null && 
                       (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            } else {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking network availability", e);
            return false;
        }
    }
    
    /**
     * Check if connected to WiFi
     */
    public boolean isWifiConnected() {
        if (connectivityManager == null) {
            return false;
        }
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork == null) {
                    return false;
                }
                
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            } else {
                NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                return wifiInfo != null && wifiInfo.isConnected();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking WiFi connectivity", e);
            return false;
        }
    }
    
    /**
     * Check if connected to mobile network
     */
    public boolean isMobileConnected() {
        if (connectivityManager == null) {
            return false;
        }
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork == null) {
                    return false;
                }
                
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            } else {
                NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                return mobileInfo != null && mobileInfo.isConnected();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking mobile connectivity", e);
            return false;
        }
    }
    
    /**
     * Check if network is metered (has data limits)
     */
    public boolean isNetworkMetered() {
        if (connectivityManager == null) {
            return true; // Assume metered if unknown
        }
        
        try {
            return connectivityManager.isActiveNetworkMetered();
        } catch (Exception e) {
            Log.e(TAG, "Error checking if network is metered", e);
            return true; // Assume metered if error
        }
    }
    
    /**
     * Get network type as string
     */
    public String getNetworkType() {
        if (!isNetworkAvailable()) {
            return "غير متصل";
        }
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork == null) {
                    return "غير معروف";
                }
                
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                if (capabilities == null) {
                    return "غير معروف";
                }
                
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return "واي فاي";
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return getMobileNetworkType();
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return "إيثرنت";
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
                    return "بلوتوث";
                } else {
                    return "آخر";
                }
            } else {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo == null) {
                    return "غير معروف";
                }
                
                switch (activeNetworkInfo.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        return "واي فاي";
                    case ConnectivityManager.TYPE_MOBILE:
                        return getMobileNetworkType();
                    case ConnectivityManager.TYPE_ETHERNET:
                        return "إيثرنت";
                    case ConnectivityManager.TYPE_BLUETOOTH:
                        return "بلوتوث";
                    default:
                        return activeNetworkInfo.getTypeName();
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting network type", e);
            return "غير معروف";
        }
    }
    
    /**
     * Get mobile network type (2G, 3G, 4G, 5G)
     */
    public String getMobileNetworkType() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return "موبايل";
            }
            
            int networkType = telephonyManager.getNetworkType();
            
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                case TelephonyManager.NETWORK_TYPE_GSM:
                    return "2G";
                    
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    return "3G";
                    
                case TelephonyManager.NETWORK_TYPE_LTE:
                case TelephonyManager.NETWORK_TYPE_IWLAN:
                    return "4G";
                    
                // 5G (API 29+)
                case 20: // TelephonyManager.NETWORK_TYPE_NR
                    return "5G";
                    
                default:
                    return "موبايل";
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting mobile network type", e);
            return "موبايل";
        }
    }
    
    /**
     * Get WiFi signal strength
     */
    public int getWifiSignalStrength() {
        if (!isWifiConnected()) {
            return -1;
        }
        
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    return wifiInfo.getRssi();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting WiFi signal strength", e);
        }
        
        return -1;
    }
    
    /**
     * Get WiFi signal strength as percentage
     */
    public int getWifiSignalStrengthPercentage() {
        int rssi = getWifiSignalStrength();
        if (rssi == -1) {
            return 0;
        }
        
        // Convert RSSI to percentage (rough approximation)
        // RSSI typically ranges from -100 (worst) to -50 (best)
        if (rssi >= -50) {
            return 100;
        } else if (rssi <= -100) {
            return 0;
        } else {
            return (rssi + 100) * 2; // Convert to 0-100 scale
        }
    }
    
    /**
     * Get network speed category
     */
    public String getNetworkSpeedCategory() {
        if (!isNetworkAvailable()) {
            return "غير متصل";
        }
        
        if (isWifiConnected()) {
            int signalStrength = getWifiSignalStrengthPercentage();
            if (signalStrength >= 80) {
                return "سريع جداً";
            } else if (signalStrength >= 60) {
                return "سريع";
            } else if (signalStrength >= 40) {
                return "متوسط";
            } else if (signalStrength >= 20) {
                return "بطيء";
            } else {
                return "بطيء جداً";
            }
        }
        
        if (isMobileConnected()) {
            String mobileType = getMobileNetworkType();
            switch (mobileType) {
                case "5G":
                    return "سريع جداً";
                case "4G":
                    return "سريع";
                case "3G":
                    return "متوسط";
                case "2G":
                    return "بطيء";
                default:
                    return "متوسط";
            }
        }
        
        return "غير معروف";
    }
    
    /**
     * Check if network is suitable for large downloads
     */
    public boolean isNetworkSuitableForLargeDownloads() {
        if (!isNetworkAvailable()) {
            return false;
        }
        
        // WiFi is generally suitable
        if (isWifiConnected()) {
            return getWifiSignalStrengthPercentage() >= 30;
        }
        
        // Mobile network - check if it's fast enough and not metered
        if (isMobileConnected()) {
            String mobileType = getMobileNetworkType();
            boolean isFastEnough = "4G".equals(mobileType) || "5G".equals(mobileType);
            boolean isUnlimited = !isNetworkMetered();
            
            return isFastEnough && isUnlimited;
        }
        
        return false;
    }
    
    /**
     * Check if network is suitable for real-time sync
     */
    public boolean isNetworkSuitableForRealTimeSync() {
        if (!isNetworkAvailable()) {
            return false;
        }
        
        // WiFi with good signal
        if (isWifiConnected()) {
            return getWifiSignalStrengthPercentage() >= 50;
        }
        
        // 4G or 5G mobile network
        if (isMobileConnected()) {
            String mobileType = getMobileNetworkType();
            return "4G".equals(mobileType) || "5G".equals(mobileType);
        }
        
        return false;
    }
    
    /**
     * Get connection quality description
     */
    public String getConnectionQuality() {
        if (!isNetworkAvailable()) {
            return "غير متصل";
        }
        
        String networkType = getNetworkType();
        String speedCategory = getNetworkSpeedCategory();
        boolean isMetered = isNetworkMetered();
        
        StringBuilder quality = new StringBuilder();
        quality.append(networkType);
        quality.append(" - ").append(speedCategory);
        
        if (isMetered) {
            quality.append(" (محدود البيانات)");
        }
        
        return quality.toString();
    }
    
    /**
     * Get network information for debugging
     */
    public String getNetworkDebugInfo() {
        StringBuilder info = new StringBuilder();
        
        info.append("متاح: ").append(isNetworkAvailable()).append("\n");
        info.append("النوع: ").append(getNetworkType()).append("\n");
        info.append("السرعة: ").append(getNetworkSpeedCategory()).append("\n");
        info.append("محدود: ").append(isNetworkMetered()).append("\n");
        
        if (isWifiConnected()) {
            info.append("قوة الواي فاي: ").append(getWifiSignalStrengthPercentage()).append("%\n");
        }
        
        info.append("مناسب للتنزيلات الكبيرة: ").append(isNetworkSuitableForLargeDownloads()).append("\n");
        info.append("مناسب للمزامنة الفورية: ").append(isNetworkSuitableForRealTimeSync()).append("\n");
        
        return info.toString();
    }
    
    /**
     * Wait for network to become available
     */
    public boolean waitForNetwork(int timeoutMillis) {
        long startTime = System.currentTimeMillis();
        
        while (!isNetworkAvailable() && (System.currentTimeMillis() - startTime) < timeoutMillis) {
            try {
                Thread.sleep(1000); // Check every second
            } catch (InterruptedException e) {
                break;
            }
        }
        
        return isNetworkAvailable();
    }
    
    /**
     * Create a singleton instance
     */
    private static NetworkUtils instance;
    
    public static NetworkUtils getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkUtils(context.getApplicationContext());
        }
        return instance;
    }
}