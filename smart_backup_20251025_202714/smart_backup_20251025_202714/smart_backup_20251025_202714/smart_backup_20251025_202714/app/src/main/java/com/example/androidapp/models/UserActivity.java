package com.example.androidapp.models;

/**
 * نموذج نشاط المستخدم
 * يمثل نشاط معين قام به المستخدم في النظام
 */
public class UserActivity {
    private String activityId;
    private String userId;
    private String activityType;
    private String description;
    private long timestamp;
    private String entityType;
    private String entityId;
    private String ipAddress;
    private String deviceInfo;
    private String activityLevel; // LOW, MEDIUM, HIGH
    private String sessionId;
    
    /**
     * البناء الافتراضي
     */
    public UserActivity() {}
    
    /**
     * البناء الأساسي
     */
    public UserActivity(String activityId, String userId, String activityType, 
                       String description, long timestamp) {
        this.activityId = activityId;
        this.userId = userId;
        this.activityType = activityType;
        this.description = description;
        this.timestamp = timestamp;
        this.activityLevel = "LOW"; // افتراضي
    }
    
    /**
     * البناء الكامل
     */
    public UserActivity(String activityId, String userId, String activityType, 
                       String description, long timestamp, String entityType, 
                       String entityId, String ipAddress, String deviceInfo, 
                       String activityLevel, String sessionId) {
        this.activityId = activityId;
        this.userId = userId;
        this.activityType = activityType;
        this.description = description;
        this.timestamp = timestamp;
        this.entityType = entityType;
        this.entityId = entityId;
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;
        this.activityLevel = activityLevel;
        this.sessionId = sessionId;
    }
    
    // Getters
    public String getActivityId() { 
        return activityId; 
    }
    
    public String getUserId() { 
        return userId; 
    }
    
    public String getActivityType() { 
        return activityType; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public long getTimestamp() { 
        return timestamp; 
    }
    
    public String getEntityType() { 
        return entityType; 
    }
    
    public String getEntityId() { 
        return entityId; 
    }
    
    public String getIpAddress() { 
        return ipAddress; 
    }
    
    public String getDeviceInfo() { 
        return deviceInfo; 
    }
    
    public String getActivityLevel() { 
        return activityLevel; 
    }
    
    public String getSessionId() { 
        return sessionId; 
    }
    
    // Setters
    public void setActivityId(String activityId) { 
        this.activityId = activityId; 
    }
    
    public void setUserId(String userId) { 
        this.userId = userId; 
    }
    
    public void setActivityType(String activityType) { 
        this.activityType = activityType; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public void setTimestamp(long timestamp) { 
        this.timestamp = timestamp; 
    }
    
    public void setEntityType(String entityType) { 
        this.entityType = entityType; 
    }
    
    public void setEntityId(String entityId) { 
        this.entityId = entityId; 
    }
    
    public void setIpAddress(String ipAddress) { 
        this.ipAddress = ipAddress; 
    }
    
    public void setDeviceInfo(String deviceInfo) { 
        this.deviceInfo = deviceInfo; 
    }
    
    public void setActivityLevel(String activityLevel) { 
        this.activityLevel = activityLevel; 
    }
    
    public void setSessionId(String sessionId) { 
        this.sessionId = sessionId; 
    }
    
    /**
     * التحقق من كون النشاط حديث (خلال آخر ساعة)
     */
    public boolean isRecent() {
        long oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000);
        return timestamp > oneHourAgo;
    }
    
    /**
     * التحقق من كون النشاط مهم (مستوى عالي أو متوسط)
     */
    public boolean isImportant() {
        return "HIGH".equals(activityLevel) || "MEDIUM".equals(activityLevel);
    }
    
    /**
     * إنشاء نشاط تسجيل دخول
     */
    public static UserActivity createLoginActivity(String userId, String sessionId, 
                                                  String deviceInfo, String ipAddress) {
        return new UserActivity(
            generateActivityId(),
            userId,
            "LOGIN",
            "تسجيل دخول المستخدم",
            System.currentTimeMillis(),
            "Session",
            sessionId,
            ipAddress,
            deviceInfo,
            "MEDIUM",
            sessionId
        );
    }
    
    /**
     * إنشاء نشاط تسجيل خروج
     */
    public static UserActivity createLogoutActivity(String userId, String sessionId) {
        return new UserActivity(
            generateActivityId(),
            userId,
            "LOGOUT",
            "تسجيل خروج المستخدم",
            System.currentTimeMillis(),
            "Session",
            sessionId,
            null,
            null,
            "MEDIUM",
            sessionId
        );
    }
    
    /**
     * إنشاء نشاط تعديل البيانات
     */
    public static UserActivity createUpdateActivity(String userId, String entityType, 
                                                   String entityId, String description) {
        return new UserActivity(
            generateActivityId(),
            userId,
            "UPDATE",
            description,
            System.currentTimeMillis(),
            entityType,
            entityId,
            null,
            null,
            "HIGH",
            null
        );
    }
    
    /**
     * إنشاء نشاط إنشاء البيانات
     */
    public static UserActivity createCreateActivity(String userId, String entityType, 
                                                   String entityId, String description) {
        return new UserActivity(
            generateActivityId(),
            userId,
            "CREATE",
            description,
            System.currentTimeMillis(),
            entityType,
            entityId,
            null,
            null,
            "HIGH",
            null
        );
    }
    
    /**
     * إنشاء نشاط حذف البيانات
     */
    public static UserActivity createDeleteActivity(String userId, String entityType, 
                                                   String entityId, String description) {
        return new UserActivity(
            generateActivityId(),
            userId,
            "DELETE",
            description,
            System.currentTimeMillis(),
            entityType,
            entityId,
            null,
            null,
            "HIGH",
            null
        );
    }
    
    /**
     * توليد معرف فريد للنشاط
     */
    private static String generateActivityId() {
        return "ACT_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    @Override
    public String toString() {
        return "UserActivity{" +
                "activityId='" + activityId + '\'' +
                ", userId='" + userId + '\'' +
                ", activityType='" + activityType + '\'' +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", activityLevel='" + activityLevel + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserActivity that = (UserActivity) o;
        
        return activityId != null ? activityId.equals(that.activityId) : that.activityId == null;
    }
    
    @Override
    public int hashCode() {
        return activityId != null ? activityId.hashCode() : 0;
    }
}