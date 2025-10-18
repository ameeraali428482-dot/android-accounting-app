package com.example.accountingapp.advanced;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AppLockManager {
    private static final String TAG = "AppLockManager";
    private static final String PREFS_NAME = "app_lock_prefs";
    private static final String KEY_LOCK_ENABLED = "lock_enabled";
    private static final String KEY_PASSWORD_HASH = "password_hash";
    private static final String KEY_LOCK_TIMEOUT = "lock_timeout";
    private static final String KEY_LAST_UNLOCK_TIME = "last_unlock_time";
    private static final String KEY_FAILED_ATTEMPTS = "failed_attempts";
    private static final String KEY_LOCK_TIME = "lock_time";
    
    private static AppLockManager instance;
    private SharedPreferences prefs;
    private Context context;
    
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
    
    // تمكين قفل التطبيق
    public void enableAppLock(String password) {
        String hashedPassword = hashPassword(password);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_LOCK_ENABLED, true);
        editor.putString(KEY_PASSWORD_HASH, hashedPassword);
        editor.putLong(KEY_LOCK_TIMEOUT, 5 * 60 * 1000); // 5 دقائق افتراضياً
        editor.apply();
        
        Log.d(TAG, "تم تمكين قفل التطبيق");
    }
    
    // تعطيل قفل التطبيق
    public void disableAppLock() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_LOCK_ENABLED, false);
        editor.remove(KEY_PASSWORD_HASH);
        editor.remove(KEY_FAILED_ATTEMPTS);
        editor.remove(KEY_LOCK_TIME);
        editor.apply();
        
        Log.d(TAG, "تم تعطيل قفل التطبيق");
    }
    
    // فحص حالة القفل
    public boolean isLockEnabled() {
        return prefs.getBoolean(KEY_LOCK_ENABLED, false);
    }
    
    // فحص ما إذا كان التطبيق مقفل
    public boolean isAppLocked() {
        if (!isLockEnabled()) {
            return false;
        }
        
        long lastUnlockTime = prefs.getLong(KEY_LAST_UNLOCK_TIME, 0);
        long lockTimeout = prefs.getLong(KEY_LOCK_TIMEOUT, 5 * 60 * 1000);
        
        return (System.currentTimeMillis() - lastUnlockTime) > lockTimeout;
    }
    
    // فحص كلمة المرور
    public boolean verifyPassword(String password) {
        String storedHash = prefs.getString(KEY_PASSWORD_HASH, "");
        String inputHash = hashPassword(password);
        
        if (storedHash.equals(inputHash)) {
            // كلمة مرور صحيحة
            prefs.edit().putLong(KEY_LAST_UNLOCK_TIME, System.currentTimeMillis()).apply();
            resetFailedAttempts();
            return true;
        } else {
            // كلمة مرور خاطئة
            incrementFailedAttempts();
            return false;
        }
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
            return password; // fallback (ليس آمن)
        }
    }
    
    // زيادة عدد المحاولات الفاشلة
    private void incrementFailedAttempts() {
        int failedAttempts = prefs.getInt(KEY_FAILED_ATTEMPTS, 0) + 1;
        prefs.edit().putInt(KEY_FAILED_ATTEMPTS, failedAttempts).apply();
        
        // قفل التطبيق لفترة بعد 3 محاولات فاشلة
        if (failedAttempts >= 3) {
            long lockTime = System.currentTimeMillis() + (30 * 60 * 1000); // 30 دقيقة
            prefs.edit().putLong(KEY_LOCK_TIME, lockTime).apply();
        }
    }
    
    // إعادة تعيين المحاولات الفاشلة
    private void resetFailedAttempts() {
        prefs.edit().remove(KEY_FAILED_ATTEMPTS).remove(KEY_LOCK_TIME).apply();
    }
    
    // فحص ما إذا كان التطبيق مقفل بسبب المحاولات الفاشلة
    public boolean isTemporarilyLocked() {
        long lockTime = prefs.getLong(KEY_LOCK_TIME, 0);
        return lockTime > System.currentTimeMillis();
    }
    
    // الحصول على وقت انتهاء القفل المؤقت
    public long getLockEndTime() {
        return prefs.getLong(KEY_LOCK_TIME, 0);
    }
    
    // تحديث آخر وقت إلغاء قفل
    public void updateLastUnlockTime() {
        prefs.edit().putLong(KEY_LAST_UNLOCK_TIME, System.currentTimeMillis()).apply();
    }
    
    // تغيير كلمة المرور
    public boolean changePassword(String oldPassword, String newPassword) {
        if (verifyPassword(oldPassword)) {
            enableAppLock(newPassword);
            return true;
        }
        return false;
    }
    
    // تعيين مهلة القفل
    public void setLockTimeout(long timeoutInMillis) {
        prefs.edit().putLong(KEY_LOCK_TIMEOUT, timeoutInMillis).apply();
    }
    
    // الحصول على مهلة القفل
    public long getLockTimeout() {
        return prefs.getLong(KEY_LOCK_TIMEOUT, 5 * 60 * 1000);
    }
}
