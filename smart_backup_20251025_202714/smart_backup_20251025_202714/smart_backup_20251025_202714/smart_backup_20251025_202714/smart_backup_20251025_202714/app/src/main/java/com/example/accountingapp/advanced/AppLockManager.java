package com.example.accountingapp.advanced;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppLockManager {
    private static final String TAG = "AppLockManager";
    private static final String PREFS_NAME = "app_lock_prefs";
    private static final String KEY_LOCK_ENABLED = "lock_enabled";
    private static final String KEY_LOCK_PASSWORD = "lock_password";
    private static final String KEY_LOCK_ATTEMPTS = "lock_attempts";
    private static final String KEY_LAST_LOCK_TIME = "last_lock_time";
    private static final String KEY_AUTO_LOCK_TIMEOUT = "auto_lock_timeout";
    private static final String KEY_FINGERPRINT_ENABLED = "fingerprint_enabled";
    
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_COOLDOWN = 5 * 60 * 1000; // 5 دقائق
    
    private static AppLockManager instance;
    private SharedPreferences prefs;
    private Context context;
    private boolean isAppLocked = false;
    
    private AppLockManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized AppLockManager getInstance(Context context) {
        if (instance == null) {
            instance = new AppLockManager(context);
        }
        return instance;
    }
    
    // تفعيل قفل التطبيق
    public void enableAppLock(String password) {
        String hashedPassword = hashPassword(password);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_LOCK_ENABLED, true);
        editor.putString(KEY_LOCK_PASSWORD, hashedPassword);
        editor.putInt(KEY_LOCK_ATTEMPTS, 0);
        editor.apply();
        
        Log.d(TAG, "تم تفعيل قفل التطبيق");
    }
    
    // إلغاء قفل التطبيق
    public void disableAppLock() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_LOCK_ENABLED, false);
        editor.remove(KEY_LOCK_PASSWORD);
        editor.putInt(KEY_LOCK_ATTEMPTS, 0);
        editor.apply();
        
        isAppLocked = false;
        Log.d(TAG, "تم إلغاء قفل التطبيق");
    }
    
    // فحص ما إذا كان القفل مفعل
    public boolean isLockEnabled() {
        return prefs.getBoolean(KEY_LOCK_ENABLED, false);
    }
    
    // فحص ما إذا كان التطبيق مقفل حالياً
    public boolean isAppLocked() {
        return isAppLocked || (isLockEnabled() && shouldAutoLock());
    }
    
    // محاولة فتح القفل
    public boolean unlockApp(String password) {
        if (!isLockEnabled()) {
            return true;
        }
        
        // فحص فترة التهدئة
        if (isInCooldown()) {
            return false;
        }
        
        String hashedPassword = hashPassword(password);
        String storedPassword = prefs.getString(KEY_LOCK_PASSWORD, "");
        
        if (hashedPassword.equals(storedPassword)) {
            // نجح فتح القفل
            isAppLocked = false;
            resetAttempts();
            updateLastUnlockTime();
            Log.d(TAG, "تم فتح قفل التطبيق بنجاح");
            return true;
        } else {
            // فشل فتح القفل
            incrementAttempts();
            Log.d(TAG, "فشل في فتح قفل التطبيق");
            return false;
        }
    }
    
    // قفل التطبيق فوراً
    public void lockApp() {
        if (isLockEnabled()) {
            isAppLocked = true;
            Log.d(TAG, "تم قفل التطبيق");
        }
    }
    
    // تغيير كلمة مرور القفل
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!unlockApp(oldPassword)) {
            return false;
        }
        
        enableAppLock(newPassword);
        Log.d(TAG, "تم تغيير كلمة مرور القفل");
        return true;
    }
    
    // تعيين مهلة القفل التلقائي
    public void setAutoLockTimeout(long timeoutMinutes) {
        prefs.edit().putLong(KEY_AUTO_LOCK_TIMEOUT, timeoutMinutes).apply();
    }
    
    // فحص ما إذا كان يجب قفل التطبيق تلقائياً
    private boolean shouldAutoLock() {
        long timeout = prefs.getLong(KEY_AUTO_LOCK_TIMEOUT, 0);
        if (timeout == 0) return false;
        
        long lastUnlock = prefs.getLong(KEY_LAST_LOCK_TIME, System.currentTimeMillis());
        long currentTime = System.currentTimeMillis();
        
        return (currentTime - lastUnlock) > (timeout * 60 * 1000);
    }
    
    // تحديث وقت آخر فتح قفل
    private void updateLastUnlockTime() {
        prefs.edit().putLong(KEY_LAST_LOCK_TIME, System.currentTimeMillis()).apply();
    }
    
    // زيادة عدد المحاولات الفاشلة
    private void incrementAttempts() {
        int attempts = prefs.getInt(KEY_LOCK_ATTEMPTS, 0) + 1;
        prefs.edit().putInt(KEY_LOCK_ATTEMPTS, attempts).apply();
        
        if (attempts >= MAX_ATTEMPTS) {
            // بدء فترة التهدئة
            prefs.edit().putLong(KEY_LAST_LOCK_TIME, System.currentTimeMillis()).apply();
        }
    }
    
    // إعادة تعيين المحاولات
    private void resetAttempts() {
        prefs.edit().putInt(KEY_LOCK_ATTEMPTS, 0).apply();
    }
    
    // فحص فترة التهدئة
    private boolean isInCooldown() {
        int attempts = prefs.getInt(KEY_LOCK_ATTEMPTS, 0);
        if (attempts < MAX_ATTEMPTS) return false;
        
        long lastAttempt = prefs.getLong(KEY_LAST_LOCK_TIME, 0);
        long currentTime = System.currentTimeMillis();
        
        return (currentTime - lastAttempt) < LOCK_COOLDOWN;
    }
    
    // الحصول على الوقت المتبقي للتهدئة
    public long getCooldownRemainingTime() {
        if (!isInCooldown()) return 0;
        
        long lastAttempt = prefs.getLong(KEY_LAST_LOCK_TIME, 0);
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastAttempt;
        
        return Math.max(0, LOCK_COOLDOWN - elapsed);
    }
    
    // تشفير كلمة المرور
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "خطأ في تشفير كلمة المرور", e);
            return password; // fallback (غير آمن)
        }
    }
    
    // فحص ما إذا كان يجب عرض شاشة القفل
    public boolean shouldShowLockScreen(Activity activity) {
        if (!isLockEnabled()) return false;
        if (isAppLocked()) return true;
        
        // فحص القفل التلقائي
        if (shouldAutoLock()) {
            lockApp();
            return true;
        }
        
        return false;
    }
}
