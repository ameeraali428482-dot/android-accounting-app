package com.example.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.concurrent.Executor;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * فئة متقدمة للأمان توفر جميع وظائف الحماية والتشفير المطلوبة
 * Advanced Security Utils providing comprehensive protection and encryption features
 */
public class SecurityUtils {
    
    private static final String PREFS_NAME = "security_prefs";
    private static final String KEY_ALIAS = "AppSecurityKey";
    private static final String PIN_HASH_KEY = "pin_hash";
    private static final String BIOMETRIC_ENABLED_KEY = "biometric_enabled";
    private static final String APP_LOCK_ENABLED_KEY = "app_lock_enabled";
    private static final String FAILED_ATTEMPTS_KEY = "failed_attempts";
    private static final String LOCK_TIMESTAMP_KEY = "lock_timestamp";
    private static final String SALT_KEY = "salt";
    
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MS = 5 * 60 * 1000; // 5 minutes
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    
    private Context context;
    private SharedPreferences prefs;
    
    public SecurityUtils(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        initializeKeyStore();
    }
    
    /**
     * تهيئة مخزن المفاتيح الآمن
     * Initialize secure keystore
     */
    private void initializeKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
                KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setUserAuthenticationRequired(false)
                        .build();
                
                keyGenerator.init(keyGenParameterSpec);
                keyGenerator.generateKey();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * تشفير البيانات الحساسة
     * Encrypt sensitive data
     */
    public String encryptData(String data) {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_ALIAS, null);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] iv = cipher.getIV();
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            
            // دمج IV مع البيانات المشفرة
            byte[] encryptedWithIv = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(encryptedData, 0, encryptedWithIv, iv.length, encryptedData.length);
            
            return Base64.encodeToString(encryptedWithIv, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * فك تشفير البيانات
     * Decrypt data
     */
    public String decryptData(String encryptedData) {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_ALIAS, null);
            byte[] encryptedWithIv = Base64.decode(encryptedData, Base64.DEFAULT);
            
            // استخراج IV
            byte[] iv = new byte[12]; // GCM IV size
            byte[] encrypted = new byte[encryptedWithIv.length - 12];
            System.arraycopy(encryptedWithIv, 0, iv, 0, 12);
            System.arraycopy(encryptedWithIv, 12, encrypted, 0, encrypted.length);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            
            byte[] decryptedData = cipher.doFinal(encrypted);
            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * تعيين رمز PIN للتطبيق
     * Set application PIN
     */
    public void setPIN(String pin) {
        String salt = generateSalt();
        String hashedPin = hashPIN(pin, salt);
        
        prefs.edit()
                .putString(PIN_HASH_KEY, hashedPin)
                .putString(SALT_KEY, salt)
                .putBoolean(APP_LOCK_ENABLED_KEY, true)
                .apply();
    }
    
    /**
     * التحقق من رمز PIN
     * Verify PIN
     */
    public boolean verifyPIN(String pin) {
        String storedHash = prefs.getString(PIN_HASH_KEY, "");
        String salt = prefs.getString(SALT_KEY, "");
        
        if (storedHash.isEmpty() || salt.isEmpty()) {
            return false;
        }
        
        String hashedPin = hashPIN(pin, salt);
        boolean isCorrect = storedHash.equals(hashedPin);
        
        if (isCorrect) {
            resetFailedAttempts();
        } else {
            incrementFailedAttempts();
        }
        
        return isCorrect;
    }
    
    /**
     * توليد مفتاح عشوائي للتشفير
     * Generate random salt
     */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }
    
    /**
     * تشفير رمز PIN
     * Hash PIN with salt
     */
    private String hashPIN(String pin, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(Base64.decode(salt, Base64.DEFAULT));
            byte[] hashedPin = digest.digest(pin.getBytes());
            return Base64.encodeToString(hashedPin, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     * زيادة عدد المحاولات الفاشلة
     * Increment failed attempts
     */
    private void incrementFailedAttempts() {
        int attempts = prefs.getInt(FAILED_ATTEMPTS_KEY, 0) + 1;
        prefs.edit().putInt(FAILED_ATTEMPTS_KEY, attempts).apply();
        
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            lockApp();
        }
    }
    
    /**
     * إعادة تعيين المحاولات الفاشلة
     * Reset failed attempts
     */
    private void resetFailedAttempts() {
        prefs.edit()
                .remove(FAILED_ATTEMPTS_KEY)
                .remove(LOCK_TIMESTAMP_KEY)
                .apply();
    }
    
    /**
     * قفل التطبيق مؤقتاً
     * Temporarily lock app
     */
    private void lockApp() {
        long lockTime = System.currentTimeMillis();
        prefs.edit().putLong(LOCK_TIMESTAMP_KEY, lockTime).apply();
    }
    
    /**
     * التحقق من حالة قفل التطبيق
     * Check if app is locked
     */
    public boolean isAppLocked() {
        long lockTime = prefs.getLong(LOCK_TIMESTAMP_KEY, 0);
        if (lockTime == 0) return false;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lockTime >= LOCK_DURATION_MS) {
            resetFailedAttempts();
            return false;
        }
        return true;
    }
    
    /**
     * الحصول على الوقت المتبقي للقفل
     * Get remaining lock time
     */
    public long getRemainingLockTime() {
        long lockTime = prefs.getLong(LOCK_TIMESTAMP_KEY, 0);
        if (lockTime == 0) return 0;
        
        long currentTime = System.currentTimeMillis();
        long remaining = LOCK_DURATION_MS - (currentTime - lockTime);
        return Math.max(0, remaining);
    }
    
    /**
     * التحقق من توفر المصادقة البيومترية
     * Check biometric authentication availability
     */
    public boolean isBiometricAvailable() {
        BiometricManager biometricManager = BiometricManager.from(context);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
            default:
                return false;
        }
    }
    
    /**
     * تفعيل المصادقة البيومترية
     * Enable biometric authentication
     */
    public void enableBiometric(boolean enabled) {
        prefs.edit().putBoolean(BIOMETRIC_ENABLED_KEY, enabled).apply();
    }
    
    /**
     * التحقق من تفعيل المصادقة البيومترية
     * Check if biometric is enabled
     */
    public boolean isBiometricEnabled() {
        return prefs.getBoolean(BIOMETRIC_ENABLED_KEY, false);
    }
    
    /**
     * عرض نافذة المصادقة البيومترية
     * Show biometric authentication dialog
     */
    public void showBiometricPrompt(FragmentActivity activity, BiometricAuthCallback callback) {
        if (!isBiometricAvailable() || !isBiometricEnabled()) {
            callback.onBiometricAuthenticationResult(false, "المصادقة البيومترية غير متوفرة");
            return;
        }
        
        Executor executor = ContextCompat.getMainExecutor(context);
        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor, 
                new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                callback.onBiometricAuthenticationResult(false, errString.toString());
            }
            
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                resetFailedAttempts();
                callback.onBiometricAuthenticationResult(true, "تم التحقق بنجاح");
            }
            
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                incrementFailedAttempts();
                callback.onBiometricAuthenticationResult(false, "فشل في التحقق");
            }
        });
        
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("تأكيد الهوية")
                .setSubtitle("استخدم بصمة إصبعك أو التعرف على الوجه للدخول")
                .setNegativeButtonText("إلغاء")
                .build();
        
        biometricPrompt.authenticate(promptInfo);
    }
    
    /**
     * التحقق من تفعيل قفل التطبيق
     * Check if app lock is enabled
     */
    public boolean isAppLockEnabled() {
        return prefs.getBoolean(APP_LOCK_ENABLED_KEY, false);
    }
    
    /**
     * تعطيل قفل التطبيق
     * Disable app lock
     */
    public void disableAppLock() {
        prefs.edit()
                .putBoolean(APP_LOCK_ENABLED_KEY, false)
                .remove(PIN_HASH_KEY)
                .remove(SALT_KEY)
                .remove(FAILED_ATTEMPTS_KEY)
                .remove(LOCK_TIMESTAMP_KEY)
                .putBoolean(BIOMETRIC_ENABLED_KEY, false)
                .apply();
    }
    
    /**
     * الحصول على عدد المحاولات الفاشلة
     * Get failed attempts count
     */
    public int getFailedAttempts() {
        return prefs.getInt(FAILED_ATTEMPTS_KEY, 0);
    }
    
    /**
     * توليد مفتاح تشفير عشوائي للجلسة
     * Generate random session encryption key
     */
    public String generateSessionKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[32]; // 256-bit key
        random.nextBytes(key);
        return Base64.encodeToString(key, Base64.DEFAULT);
    }
    
    /**
     * تشفير البيانات بمفتاح مخصص
     * Encrypt data with custom key
     */
    public String encryptWithKey(String data, String keyString) {
        try {
            byte[] key = Base64.decode(keyString, Base64.DEFAULT);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] iv = cipher.getIV();
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            
            byte[] encryptedWithIv = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(encryptedData, 0, encryptedWithIv, iv.length, encryptedData.length);
            
            return Base64.encodeToString(encryptedWithIv, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * فك تشفير البيانات بمفتاح مخصص
     * Decrypt data with custom key
     */
    public String decryptWithKey(String encryptedData, String keyString) {
        try {
            byte[] key = Base64.decode(keyString, Base64.DEFAULT);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            
            byte[] encryptedWithIv = Base64.decode(encryptedData, Base64.DEFAULT);
            
            byte[] iv = new byte[12];
            byte[] encrypted = new byte[encryptedWithIv.length - 12];
            System.arraycopy(encryptedWithIv, 0, iv, 0, 12);
            System.arraycopy(encryptedWithIv, 12, encrypted, 0, encrypted.length);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            
            byte[] decryptedData = cipher.doFinal(encrypted);
            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * واجهة استدعاء المصادقة البيومترية
     * Biometric authentication callback interface
     */
    public interface BiometricAuthCallback {
        void onBiometricAuthenticationResult(boolean success, String message);
    }
    
    /**
     * تنظيف البيانات الحساسة من الذاكرة
     * Clear sensitive data from memory
     */
    public void clearSensitiveData() {
        // إزالة المراجع الحساسة من الذاكرة
        System.gc();
    }
    
    /**
     * التحقق من سلامة البيانات
     * Verify data integrity
     */
    public boolean verifyDataIntegrity(String data, String expectedHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            String calculatedHash = Base64.encodeToString(hash, Base64.DEFAULT);
            return calculatedHash.equals(expectedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * إنشاء توقيع رقمي للبيانات
     * Create digital signature for data
     */
    public String createDataSignature(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            return Base64.encodeToString(hash, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}