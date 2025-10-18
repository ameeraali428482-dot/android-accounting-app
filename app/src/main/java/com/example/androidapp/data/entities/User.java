package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

import com.example.androidapp.utils.PasswordUtils;

@Entity(tableName = "users",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "personalCompanyId",
                                  onDelete = ForeignKey.SET_NULL),
        indices = {@Index(value = "personalCompanyId"), @Index(value = "email", unique = true)})
public class User {
    @PrimaryKey
    @NonNull
    private String id;
    
    @NonNull
    private String email;
    
    @NonNull
    private String passwordHash; // Changed from password to passwordHash for security
    
    private String name;
    private String phone;
    private String phoneHash;
    private int points;
    private String createdAt;
    private String updatedAt;
    private String personalCompanyId;
    private boolean isOnline;
    private boolean isActive;
    private boolean isEmailVerified;
    private boolean isPhoneVerified;
    private String lastLoginAt;
    private int failedLoginAttempts;
    private String lockedUntil;
    private String profileImageUrl;
    private String language; // User preferred language
    private String timezone; // User timezone
    
    // Security fields
    private String twoFactorSecret;
    private boolean twoFactorEnabled;
    private String recoveryCode;
    
    public User(@NonNull String id, @NonNull String email, @NonNull String passwordHash, 
                String name, String phone, String phoneHash, int points, String createdAt, 
                String updatedAt, String personalCompanyId, boolean isOnline, boolean isActive) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.phone = phone;
        this.phoneHash = phoneHash;
        this.points = points;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.personalCompanyId = personalCompanyId;
        this.isOnline = isOnline;
        this.isActive = isActive;
        this.isEmailVerified = false;
        this.isPhoneVerified = false;
        this.failedLoginAttempts = 0;
        this.twoFactorEnabled = false;
        this.language = "ar"; // Default to Arabic
        this.timezone = "Asia/Riyadh"; // Default timezone
    }

    // Constructor for creating new user with plain password (will be hashed)
    @Ignore
    public User(@NonNull String id, @NonNull String email, @NonNull String plainPassword, 
                String name, String phone, boolean hashPassword) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.points = 0;
        this.createdAt = String.valueOf(System.currentTimeMillis());
        this.updatedAt = this.createdAt;
        this.isOnline = false;
        this.isActive = true;
        this.isEmailVerified = false;
        this.isPhoneVerified = false;
        this.failedLoginAttempts = 0;
        this.twoFactorEnabled = false;
        this.language = "ar";
        this.timezone = "Asia/Riyadh";
        
        // Hash the password if requested
        if (hashPassword) {
            this.passwordHash = PasswordUtils.hashPassword(plainPassword);
        } else {
            this.passwordHash = plainPassword; // Assume already hashed
        }
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    
    @NonNull
    public String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }
    
    @NonNull
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(@NonNull String passwordHash) { this.passwordHash = passwordHash; }
    
    // Deprecated: Use setPasswordPlain instead for security
    @Deprecated
    public String getPassword() { 
        return "***HIDDEN***"; // Never return actual password
    }
    
    // Deprecated: Use setPasswordPlain instead for security
    @Deprecated
    public void setPassword(String password) { 
        setPasswordPlain(password);
    }
    
    /**
     * Set password from plain text (will be hashed automatically)
     * @param plainPassword The plain text password
     */
    public void setPasswordPlain(String plainPassword) {
        if (plainPassword != null && !plainPassword.isEmpty()) {
            this.passwordHash = PasswordUtils.hashPassword(plainPassword);
            this.updatedAt = String.valueOf(System.currentTimeMillis());
        }
    }
    
    /**
     * Verify password against stored hash
     * @param plainPassword The plain text password to verify
     * @return true if password matches, false otherwise
     */
    @Ignore
    public boolean verifyPassword(String plainPassword) {
        if (plainPassword == null || passwordHash == null) {
            return false;
        }
        return PasswordUtils.verifyPassword(plainPassword, passwordHash);
    }
    
    public String getName() { return name; }
    public String getUsername() { return name; } // Alias for compatibility
    public void setName(String name) { 
        this.name = name; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { 
        this.phone = phone; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public String getPhoneHash() { return phoneHash; }
    public void setPhoneHash(String phoneHash) { this.phoneHash = phoneHash; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { 
        this.points = points; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public String getPersonalCompanyId() { return personalCompanyId; }
    public void setPersonalCompanyId(String personalCompanyId) { 
        this.personalCompanyId = personalCompanyId; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { 
        isOnline = online; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { 
        isActive = active; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public boolean isEmailVerified() { return isEmailVerified; }
    public void setEmailVerified(boolean emailVerified) { 
        isEmailVerified = emailVerified; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public boolean isPhoneVerified() { return isPhoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { 
        isPhoneVerified = phoneVerified; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public String getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(String lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { 
        this.failedLoginAttempts = failedLoginAttempts; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public String getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(String lockedUntil) { 
        this.lockedUntil = lockedUntil; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { 
        this.profileImageUrl = profileImageUrl; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { 
        this.language = language; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { 
        this.timezone = timezone; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public String getTwoFactorSecret() { return twoFactorSecret; }
    public void setTwoFactorSecret(String twoFactorSecret) { 
        this.twoFactorSecret = twoFactorSecret; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) { 
        this.twoFactorEnabled = twoFactorEnabled; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    public String getRecoveryCode() { return recoveryCode; }
    public void setRecoveryCode(String recoveryCode) { 
        this.recoveryCode = recoveryCode; 
        this.updatedAt = String.valueOf(System.currentTimeMillis());
    }
    
    /**
     * Check if user account is locked
     */
    @Ignore
    public boolean isLocked() {
        if (lockedUntil == null) return false;
        
        try {
            long lockTime = Long.parseLong(lockedUntil);
            return System.currentTimeMillis() < lockTime;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Increment failed login attempts and lock account if necessary
     */
    @Ignore
    public void incrementFailedAttempts() {
        failedLoginAttempts++;
        
        // Lock account for 30 minutes after 5 failed attempts
        if (failedLoginAttempts >= 5) {
            long lockUntil = System.currentTimeMillis() + (30 * 60 * 1000); // 30 minutes
            setLockedUntil(String.valueOf(lockUntil));
        }
        
        setUpdatedAt(String.valueOf(System.currentTimeMillis()));
    }
    
    /**
     * Reset failed login attempts (called after successful login)
     */
    @Ignore
    public void resetFailedAttempts() {
        failedLoginAttempts = 0;
        lockedUntil = null;
        lastLoginAt = String.valueOf(System.currentTimeMillis());
        setUpdatedAt(String.valueOf(System.currentTimeMillis()));
    }
    
    /**
     * Get display name for user
     */
    @Ignore
    public String getDisplayName() {
        if (name != null && !name.trim().isEmpty()) {
            return name.trim();
        }
        return email;
    }
    
    /**
     * Check if user profile is complete
     */
    @Ignore
    public boolean isProfileComplete() {
        return name != null && !name.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               isEmailVerified;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", points=" + points +
                ", isActive=" + isActive +
                ", isOnline=" + isOnline +
                ", isEmailVerified=" + isEmailVerified +
                ", isPhoneVerified=" + isPhoneVerified +
                ", language='" + language + '\'' +
                ", twoFactorEnabled=" + twoFactorEnabled +
                '}';
    }
}
