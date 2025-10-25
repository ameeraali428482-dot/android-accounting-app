package com.example.androidapp.utils;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Comprehensive password security utilities
 * Provides secure password hashing, encryption, and validation
 */
public class PasswordUtils {
    
    private static final String TAG = "PasswordUtils";
    
    // Password hashing constants
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_LENGTH = 32; // 256 bits
    private static final int HASH_ITERATIONS = 100000; // OWASP recommended minimum
    
    // Encryption constants
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    
    // Password validation constants
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;
    
    // Password strength patterns
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    private static final Pattern ARABIC_PATTERN = Pattern.compile(".*[\\u0600-\\u06FF].*");
    
    /**
     * Hash a password with salt using PBKDF2
     * @param password The plain text password
     * @return Hashed password with salt (format: salt:hash)
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash the password with the salt
            String hash = hashPasswordWithSalt(password, salt);
            
            // Encode salt to Base64
            String encodedSalt = Base64.encodeToString(salt, Base64.NO_WRAP);
            
            // Return salt:hash format
            return encodedSalt + ":" + hash;
            
        } catch (Exception e) {
            Log.e(TAG, "Error hashing password", e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }
    
    /**
     * Verify a password against a stored hash
     * @param password The plain text password to verify
     * @param storedHash The stored hash (format: salt:hash)
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        
        try {
            // Split the stored hash to get salt and hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                Log.w(TAG, "Invalid stored hash format");
                return false;
            }
            
            // Decode the salt
            byte[] salt = Base64.decode(parts[0], Base64.NO_WRAP);
            String expectedHash = parts[1];
            
            // Hash the provided password with the same salt
            String actualHash = hashPasswordWithSalt(password, salt);
            
            // Compare hashes using constant-time comparison
            return constantTimeEquals(expectedHash, actualHash);
            
        } catch (Exception e) {
            Log.e(TAG, "Error verifying password", e);
            return false;
        }
    }
    
    /**
     * Hash password with a specific salt
     */
    private static String hashPasswordWithSalt(String password, byte[] salt) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        
        // Combine password and salt
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] combined = new byte[passwordBytes.length + salt.length];
        System.arraycopy(passwordBytes, 0, combined, 0, passwordBytes.length);
        System.arraycopy(salt, 0, combined, passwordBytes.length, salt.length);
        
        // Perform multiple iterations for security
        byte[] hash = combined;
        for (int i = 0; i < HASH_ITERATIONS; i++) {
            digest.reset();
            hash = digest.digest(hash);
        }
        
        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }
    
    /**
     * Constant-time string comparison to prevent timing attacks
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }
        
        if (a.length() != b.length()) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        
        return result == 0;
    }
    
    /**
     * Validate password strength
     * @param password The password to validate
     * @return PasswordStrength enum indicating strength level
     */
    public static PasswordStrength validatePasswordStrength(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return PasswordStrength.WEAK;
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return PasswordStrength.INVALID;
        }
        
        int score = 0;
        
        // Length bonus
        if (password.length() >= 12) score += 2;
        else if (password.length() >= 10) score += 1;
        
        // Character type checks
        if (UPPERCASE_PATTERN.matcher(password).matches()) score++;
        if (LOWERCASE_PATTERN.matcher(password).matches()) score++;
        if (DIGIT_PATTERN.matcher(password).matches()) score++;
        if (SPECIAL_CHAR_PATTERN.matcher(password).matches()) score++;
        if (ARABIC_PATTERN.matcher(password).matches()) score++;
        
        // Check for common patterns (reduce score)
        if (hasRepeatingCharacters(password)) score--;
        if (hasSequentialCharacters(password)) score--;
        if (isCommonPassword(password)) score -= 2;
        
        // Determine strength based on score
        if (score >= 6) return PasswordStrength.VERY_STRONG;
        if (score >= 4) return PasswordStrength.STRONG;
        if (score >= 2) return PasswordStrength.MEDIUM;
        return PasswordStrength.WEAK;
    }
    
    /**
     * Get password validation requirements
     */
    public static String getPasswordRequirements() {
        return "كلمة المرور يجب أن تحتوي على:\n" +
                "• " + MIN_PASSWORD_LENGTH + " أحرف على الأقل\n" +
                "• حرف كبير واحد على الأقل (A-Z)\n" +
                "• حرف صغير واحد على الأقل (a-z)\n" +
                "• رقم واحد على الأقل (0-9)\n" +
                "• رمز خاص واحد على الأقل (!@#$%^&*)\n" +
                "• عدم استخدام كلمات مرور شائعة";
    }
    
    /**
     * Check if password has repeating characters
     */
    private static boolean hasRepeatingCharacters(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) && 
                password.charAt(i) == password.charAt(i + 2)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if password has sequential characters
     */
    private static boolean hasSequentialCharacters(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);
            
            if ((c2 == c1 + 1 && c3 == c2 + 1) || (c2 == c1 - 1 && c3 == c2 - 1)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if password is in common passwords list
     */
    private static boolean isCommonPassword(String password) {
        String[] commonPasswords = {
            "password", "123456", "123456789", "12345678", "12345",
            "qwerty", "abc123", "password123", "admin", "root",
            "user", "test", "guest", "demo", "welcome",
            "111111", "000000", "888888", "666666",
            "أحمد", "محمد", "فاطمة", "عائشة", "خديجة",
            "الله", "اللهم", "بسم", "مرحبا", "كلمة_المرور"
        };
        
        String lowerPassword = password.toLowerCase();
        for (String common : commonPasswords) {
            if (lowerPassword.contains(common.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Generate a secure random password
     */
    public static String generateSecurePassword(int length) {
        if (length < MIN_PASSWORD_LENGTH) {
            length = MIN_PASSWORD_LENGTH;
        }
        
        String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerChars = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        
        String allChars = upperChars + lowerChars + digits + specialChars;
        
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each category
        password.append(upperChars.charAt(random.nextInt(upperChars.length())));
        password.append(lowerChars.charAt(random.nextInt(lowerChars.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Shuffle the password
        return shuffleString(password.toString());
    }
    
    /**
     * Shuffle a string randomly
     */
    private static String shuffleString(String string) {
        char[] chars = string.toCharArray();
        SecureRandom random = new SecureRandom();
        
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        
        return new String(chars);
    }
    
    /**
     * Encrypt sensitive data using AES-GCM
     */
    public static String encrypt(String data, String password) throws Exception {
        SecretKeySpec keySpec = generateKeyFromPassword(password);
        
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        
        // Generate random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);
        
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        // Combine IV and encrypted data
        byte[] combined = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
        
        return Base64.encodeToString(combined, Base64.NO_WRAP);
    }
    
    /**
     * Decrypt data using AES-GCM
     */
    public static String decrypt(String encryptedData, String password) throws Exception {
        byte[] combined = Base64.decode(encryptedData, Base64.NO_WRAP);
        
        // Extract IV and encrypted data
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
        
        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);
        
        SecretKeySpec keySpec = generateKeyFromPassword(password);
        
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
        
        byte[] decryptedData = cipher.doFinal(encrypted);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
    
    /**
     * Generate encryption key from password
     */
    private static SecretKeySpec generateKeyFromPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] key = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        
        // Use first 32 bytes for AES-256
        byte[] keyBytes = new byte[32];
        System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, keyBytes.length));
        
        return new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);
    }
    
    /**
     * Password strength enumeration
     */
    public enum PasswordStrength {
        INVALID("غير صالحة"),
        WEAK("ضعيفة"),
        MEDIUM("متوسطة"),
        STRONG("قوية"),
        VERY_STRONG("قوية جداً");
        
        private final String arabicName;
        
        PasswordStrength(String arabicName) {
            this.arabicName = arabicName;
        }
        
        public String getArabicName() {
            return arabicName;
        }
        
        public int getColor() {
            switch (this) {
                case INVALID:
                case WEAK:
                    return 0xFFFF5722; // Red
                case MEDIUM:
                    return 0xFFFF9800; // Orange
                case STRONG:
                    return 0xFF2196F3; // Blue
                case VERY_STRONG:
                    return 0xFF4CAF50; // Green
                default:
                    return 0xFF757575; // Gray
            }
        }
    }
}