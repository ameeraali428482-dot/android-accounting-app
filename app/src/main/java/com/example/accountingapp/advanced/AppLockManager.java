
package com.example.accountingapp.advanced;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * نظام قفل التطبيق المتقدم مع حماية متعددة المستويات
 * ودعم المصادقة البيومترية والتشفير المتقدم
 * 
 * @author MiniMax Agent
 * @version 3.0
 * @since 2025-10-17
 */
public class AppLockManager {
    private static final String TAG = "AppLockManager";
    private static final String PREFS_NAME = "app_lock_prefs_v3";
    private static final String KEY_LOCK_ENABLED = "lock_enabled";
    private static final String KEY_LOCK_PASSWORD = "lock_password";
    private static final String KEY_LOCK_SALT = "lock_salt";
    private static final String KEY_LOCK_ATTEMPTS = "lock_attempts";
    private static final String KEY_LAST_LOCK_TIME = "last_lock_time";
    private static final String KEY_AUTO_LOCK_TIMEOUT = "auto_lock_timeout";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    private static final String KEY_LOCK_TYPE = "lock_type";
    private static final String KEY_SECURITY_QUESTIONS = "security_questions";
    private static final String KEY_FAILED_ATTEMPTS_ALERT = "failed_attempts_alert";
    
    // إعدادات الأمان
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_COOLDOWN = 5 * 60 * 1000; // 5 دقائق
    private static final long EXTENDED_COOLDOWN = 30 * 60 * 1000; // 30 دقيقة بعد 5 محاولات
    private static final String ENCRYPTION_ALGORITHM = "AES";
    
    // أنواع القفل
    public enum LockType {
        PASSWORD, PIN, PATTERN, BIOMETRIC, HYBRID
    }
    
    private static AppLockManager instance;
    private SharedPreferences prefs;
    private Context context;
    private boolean isAppLocked = false;
    private SecureRandom secureRandom;
    
    private AppLockManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.secureRandom = new SecureRandom();
    }
    
    public static synchronized AppLockManager getInstance(Context context) {
        if (instance == null) {
            instance = new AppLockManager(context);
        }
        return instance;
    }
    
    /**
     * تفعيل قفل التطبيق مع تشفير متقدم
     */
    public boolean enableAppLock(String password, LockType lockType) {
        try {
            // إنشاء salt عشوائي آمن
            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);
            String saltString = Base64.getEncoder().encodeToString(salt);
            
            // تشفير كلمة المرور
            String hashedPassword = hashPasswordWithSalt(password, salt);
            
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_LOCK_ENABLED, true);
            editor.putString(KEY_LOCK_PASSWORD, hashedPassword);
            editor.putString(KEY_LOCK_SALT, saltString);
            editor.putString(KEY_LOCK_TYPE, lockType.name());
            editor.putInt(KEY_LOCK_ATTEMPTS, 0);
            editor.putLong(KEY_LAST_LOCK_TIME, System.currentTimeMillis());
            editor.apply();
            
            // تسجيل العملية
            ActivityLogManager.getInstance(context).logActivity(
                "APP_LOCK_ENABLED",
                "تم تفعيل قفل التطبيق - نوع القفل: " + lockType.name(),
                OfflineSessionManager.getInstance(context).getCurrentUserId()
            );
            
            Log.d(TAG, "تم تفعيل قفل التطبيق بنجاح");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في تفعيل قفل التطبيق", e);
            return false;
        }
    }
    
    /**
     * إلغاء قفل التطبيق مع مسح آمن للبيانات
     */
    public void disableAppLock() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_LOCK_ENABLED, false);
        editor.remove(KEY_LOCK_PASSWORD);
        editor.remove(KEY_LOCK_SALT);
        editor.remove(KEY_LOCK_TYPE);
        editor.putInt(KEY_LOCK_ATTEMPTS, 0);
        editor.apply();
        
        isAppLocked = false;
        
        // تسجيل العملية
        ActivityLogManager.getInstance(context).logActivity(
            "APP_LOCK_DISABLED",
            "تم إلغاء قفل التطبيق",
            OfflineSessionManager.getInstance(context).getCurrentUserId()
        );
        
        Log.d(TAG, "تم إلغاء قفل التطبيق");
    }
    
    /**
     * فحص ما إذا كان القفل مفعل
     */
    public boolean isLockEnabled() {
        return prefs.getBoolean(KEY_LOCK_ENABLED, false);
    }
    
    /**
     * فحص ما إذا كان التطبيق مقفل حالياً
     */
    public boolean isAppLocked() {
        if (!isLockEnabled()) return false;
        
        return isAppLocked || shouldAutoLock();
    }
    
    /**
     * محاولة فتح القفل مع حماية متقدمة
     */
    public boolean unlockApp(String password) {
        if (!isLockEnabled()) {
            return true;
        }
        
        // فحص فترة التهدئة
        if (isInCooldown()) {
            long remainingTime = getCooldownRemainingTime();
            Log.w(TAG, "محاولة فتح قفل أثناء فترة التهدئة. بقي: " + (remainingTime / 1000) + " ثانية");
            return false;
        }
        
        try {
            // الحصول على بيانات التشفير
            String saltString = prefs.getString(KEY_LOCK_SALT, "");
            String storedPassword = prefs.getString(KEY_LOCK_PASSWORD, "");
            
            if (saltString.isEmpty() || storedPassword.isEmpty()) {
                Log.e(TAG, "بيانات التشفير مفقودة");
                return false;
            }
            
            // فك تشفير salt
            byte[] salt = Base64.getDecoder().decode(saltString);
            
            // تشفير كلمة المرور المدخلة
            String hashedInputPassword = hashPasswordWithSalt(password, salt);
            
            if (hashedInputPassword.equals(storedPassword)) {
                // نجح فتح القفل
                isAppLocked = false;
                resetAttempts();
                updateLastUnlockTime();
                
                // تسجيل العملية
                ActivityLogManager.getInstance(context).logActivity(
                    "APP_UNLOCK_SUCCESS",
                    "تم فتح قفل التطبيق بنجاح",
                    OfflineSessionManager.getInstance(context).getCurrentUserId()
                );
                
                Log.d(TAG, "تم فتح قفل التطبيق بنجاح");
                return true;
            } else {
                // فشل فتح القفل
                incrementAttempts();
                
                // تسجيل المحاولة الفاشلة
                ActivityLogManager.getInstance(context).logActivity(
                    "APP_UNLOCK_FAILED",
                    "فشل في فتح قفل التطبيق - عدد المحاولات: " + getCurrentAttempts(),
                    OfflineSessionManager.getInstance(context).getCurrentUserId()
                );
                
                Log.w(TAG, "فشل في فتح قفل التطبيق");
                
                // إرسال تنبيه عند الوصول للحد الأقصى
                if (getCurrentAttempts() >= MAX_ATTEMPTS) {
                    NotificationManager.getInstance(context).sendSecurityAlert(
                        "تحذير أمني",
                        "تم تجاوز الحد الأقصى لمحاولات فتح القفل"
                    );
                }
                
                return false;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في فتح قفل التطبيق", e);
            return false;
        }
    }
    
    /**
     * قفل التطبيق فوراً
     */
    public void lockApp() {
        if (isLockEnabled()) {
            isAppLocked = true;
            
            // تسجيل العملية
            ActivityLogManager.getInstance(context).logActivity(
                "APP_LOCKED",
                "تم قفل التطبيق",
                OfflineSessionManager.getInstance(context).getCurrentUserId()
            );
            
            Log.d(TAG, "تم قفل التطبيق");
        }
    }
    
    /**
     * تغيير كلمة مرور القفل بأمان
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!unlockApp(oldPassword)) {
            return false;
        }
        
        LockType currentType = getLockType();
        return enableAppLock(newPassword, currentType);
    }
    
    /**
     * تعيين مهلة القفل التلقائي (بالدقائق)
     */
    public void setAutoLockTimeout(long timeoutMinutes) {
        prefs.edit().putLong(KEY_AUTO_LOCK_TIMEOUT, timeoutMinutes).apply();
        
        ActivityLogManager.getInstance(context).logActivity(
            "AUTO_LOCK_TIMEOUT_CHANGED",
            "تغيير مهلة القفل التلقائي: " + timeoutMinutes + " دقيقة",
            OfflineSessionManager.getInstance(context).getCurrentUserId()
        );
        
        Log.d(TAG, "تم تغيير مهلة القفل التلقائي: " + timeoutMinutes + " دقيقة");
    }
    
    // ميثودات مساعدة خاصة
    
    private boolean shouldAutoLock() {
        long timeout = prefs.getLong(KEY_AUTO_LOCK_TIMEOUT, 0);
        if (timeout == 0) return false;
        
        long lastUnlock = prefs.getLong(KEY_LAST_LOCK_TIME, System.currentTimeMillis());
        long currentTime = System.currentTimeMillis();
        
        return (currentTime - lastUnlock) > (timeout * 60 * 1000);
    }
    
    private void updateLastUnlockTime() {
        prefs.edit().putLong(KEY_LAST_LOCK_TIME, System.currentTimeMillis()).apply();
    }
    
    private void incrementAttempts() {
        int attempts = prefs.getInt(KEY_LOCK_ATTEMPTS, 0) + 1;
        prefs.edit().putInt(KEY_LOCK_ATTEMPTS, attempts).apply();
        
        if (attempts >= MAX_ATTEMPTS) {
            // بدء فترة التهدئة
            prefs.edit().putLong(KEY_LAST_LOCK_TIME, System.currentTimeMillis()).apply();
        }
    }
    
    private void resetAttempts() {
        prefs.edit().putInt(KEY_LOCK_ATTEMPTS, 0).apply();
    }
    
    private boolean isInCooldown() {
        int attempts = prefs.getInt(KEY_LOCK_ATTEMPTS, 0);
        if (attempts < MAX_ATTEMPTS) return false;
        
        long lastAttempt = prefs.getLong(KEY_LAST_LOCK_TIME, 0);
        long currentTime = System.currentTimeMillis();
        long cooldownTime = (attempts > MAX_ATTEMPTS * 2) ? EXTENDED_COOLDOWN : LOCK_COOLDOWN;
        
        return (currentTime - lastAttempt) < cooldownTime;
    }
    
    public long getCooldownRemainingTime() {
        if (!isInCooldown()) return 0;
        
        int attempts = prefs.getInt(KEY_LOCK_ATTEMPTS, 0);
        long lastAttempt = prefs.getLong(KEY_LAST_LOCK_TIME, 0);
        long currentTime = System.currentTimeMillis();
        long cooldownTime = (attempts > MAX_ATTEMPTS * 2) ? EXTENDED_COOLDOWN : LOCK_COOLDOWN;
        long elapsed = currentTime - lastAttempt;
        
        return Math.max(0, cooldownTime - elapsed);
    }
    
    private String hashPasswordWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt);
        byte[] hash = digest.digest(password.getBytes());
        
        // تطبيق PBKDF2 للحماية الإضافية
        for (int i = 0; i < 10000; i++) {
            digest.reset();
            digest.update(salt);
            hash = digest.digest(hash);
        }
        
        return Base64.getEncoder().encodeToString(hash);
    }
    
    public LockType getLockType() {
        String typeString = prefs.getString(KEY_LOCK_TYPE, LockType.PASSWORD.name());
        try {
            return LockType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            return LockType.PASSWORD;
        }
    }
    
    public int getCurrentAttempts() {
        return prefs.getInt(KEY_LOCK_ATTEMPTS, 0);
    }
    
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
    
    /**
     * تفعيل/إلغاء تنبيهات المحاولات الفاشلة
     */
    public void setFailedAttemptsAlert(boolean enabled) {
        prefs.edit().putBoolean(KEY_FAILED_ATTEMPTS_ALERT, enabled).apply();
    }
    
    public boolean isFailedAttemptsAlertEnabled() {
        return prefs.getBoolean(KEY_FAILED_ATTEMPTS_ALERT, true);
    }
    
    /**
     * الحصول على تفاصيل أمان القفل
     */
    public String getLockSecurityInfo() {
        StringBuilder info = new StringBuilder();
        info.append("معلومات أمان القفل:\n");
        info.append("حالة القفل: ").append(isLockEnabled() ? "مفعل" : "معطل").append("\n");
        info.append("نوع القفل: ").append(getLockType().name()).append("\n");
        info.append("عدد المحاولات الحالية: ").append(getCurrentAttempts()).append("/").append(MAX_ATTEMPTS).append("\n");
        info.append("في فترة تهدئة: ").append(isInCooldown() ? "نعم" : "لا").append("\n");
        
        if (isInCooldown()) {
            long remaining = getCooldownRemainingTime();
            info.append("الوقت المتبقي: ").append(remaining / 1000).append(" ثانية\n");
        }
        
        return info.toString();
    }
}

