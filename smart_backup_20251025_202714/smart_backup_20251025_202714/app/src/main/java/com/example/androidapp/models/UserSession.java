package com.example.androidapp.models;

/**
 * نموذج جلسة المستخدم
 * يمثل معلومات جلسة مستخدم نشط في النظام
 */
public class UserSession {
    private String sessionId;
    private String userId;
    private String deviceInfo;
    private long loginTime;
    private long lastActivityTime;
    private String ipAddress;
    private String appVersion;
    private boolean isActive;
    
    /**
     * البناء الافتراضي
     */
    public UserSession() {}
    
    /**
     * البناء مع المعاملات الأساسية
     */
    public UserSession(String sessionId, String userId, String deviceInfo, 
                      long loginTime, long lastActivityTime, String ipAddress) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.deviceInfo = deviceInfo;
        this.loginTime = loginTime;
        this.lastActivityTime = lastActivityTime;
        this.ipAddress = ipAddress;
        this.isActive = true;
    }
    
    /**
     * البناء الكامل
     */
    public UserSession(String sessionId, String userId, String deviceInfo, 
                      long loginTime, long lastActivityTime, String ipAddress,
                      String appVersion, boolean isActive) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.deviceInfo = deviceInfo;
        this.loginTime = loginTime;
        this.lastActivityTime = lastActivityTime;
        this.ipAddress = ipAddress;
        this.appVersion = appVersion;
        this.isActive = isActive;
    }
    
    // Getters
    public String getSessionId() { 
        return sessionId; 
    }
    
    public String getUserId() { 
        return userId; 
    }
    
    public String getDeviceInfo() { 
        return deviceInfo; 
    }
    
    public long getLoginTime() { 
        return loginTime; 
    }
    
    public long getLastActivityTime() { 
        return lastActivityTime; 
    }
    
    public String getIpAddress() { 
        return ipAddress; 
    }
    
    public String getAppVersion() { 
        return appVersion; 
    }
    
    public boolean isActive() { 
        return isActive; 
    }
    
    // Setters
    public void setSessionId(String sessionId) { 
        this.sessionId = sessionId; 
    }
    
    public void setUserId(String userId) { 
        this.userId = userId; 
    }
    
    public void setDeviceInfo(String deviceInfo) { 
        this.deviceInfo = deviceInfo; 
    }
    
    public void setLoginTime(long loginTime) { 
        this.loginTime = loginTime; 
    }
    
    public void setLastActivityTime(long lastActivityTime) { 
        this.lastActivityTime = lastActivityTime; 
    }
    
    public void setIpAddress(String ipAddress) { 
        this.ipAddress = ipAddress; 
    }
    
    public void setAppVersion(String appVersion) { 
        this.appVersion = appVersion; 
    }
    
    public void setActive(boolean active) { 
        this.isActive = active; 
    }
    
    /**
     * حساب مدة الجلسة بالمللي ثانية
     */
    public long getSessionDuration() {
        return System.currentTimeMillis() - loginTime;
    }
    
    /**
     * حساب مدة عدم النشاط بالمللي ثانية
     */
    public long getInactivityDuration() {
        return System.currentTimeMillis() - lastActivityTime;
    }
    
    /**
     * التحقق من كون الجلسة نشطة (آخر نشاط خلال 5 دقائق)
     */
    public boolean isOnline() {
        return isActive && (getInactivityDuration() < 300000); // 5 دقائق
    }
    
    /**
     * تحديث وقت آخر نشاط
     */
    public void updateLastActivity() {
        this.lastActivityTime = System.currentTimeMillis();
    }
    
    /**
     * إنهاء الجلسة
     */
    public void terminate() {
        this.isActive = false;
    }
    
    @Override
    public String toString() {
        return "UserSession{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", loginTime=" + loginTime +
                ", lastActivityTime=" + lastActivityTime +
                ", ipAddress='" + ipAddress + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", isActive=" + isActive +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserSession that = (UserSession) o;
        
        return sessionId != null ? sessionId.equals(that.sessionId) : that.sessionId == null;
    }
    
    @Override
    public int hashCode() {
        return sessionId != null ? sessionId.hashCode() : 0;
    }
}