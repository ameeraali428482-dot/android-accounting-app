package com.example.androidapp.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.androidapp.data.DateConverter;
import java.util.Date;

/**
 * كيان RecordLock لإدارة أقفال السجلات في النظام
 * يمنع تضارب البيانات عند تعديل سجل واحد من عدة مستخدمين
 * 
 * @author MiniMax Agent
 * @version 1.0
 * @since 2025-10-20
 */
@Entity(tableName = "record_locks")
@TypeConverters({DateConverter.class})
public class RecordLock {
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "lock_id")
    private long lockId;
    
    /**
     * معرف السجل المقفل (يمكن أن يكون ID للفاتورة، العميل، المنتج، إلخ)
     */
    @ColumnInfo(name = "record_id")
    private String recordId;
    
    /**
     * نوع السجل (invoice, customer, product, account, etc.)
     */
    @ColumnInfo(name = "record_type")
    private String recordType;
    
    /**
     * معرف المستخدم الذي قفل السجل
     */
    @ColumnInfo(name = "user_id")
    private String userId;
    
    /**
     * اسم المستخدم الذي قفل السجل (للعرض)
     */
    @ColumnInfo(name = "user_name")
    private String userName;
    
    /**
     * معرف الشركة (Multi-tenant support)
     */
    @ColumnInfo(name = "company_id")
    private String companyId;
    
    /**
     * وقت بداية القفل
     */
    @ColumnInfo(name = "locked_at")
    private Date lockedAt;
    
    /**
     * وقت انتهاء صلاحية القفل (تلقائياً بعد مدة محددة)
     */
    @ColumnInfo(name = "expires_at")
    private Date expiresAt;
    
    /**
     * حالة القفل (ACTIVE, EXPIRED, RELEASED)
     */
    @ColumnInfo(name = "lock_status")
    private String lockStatus;
    
    /**
     * معرف الجلسة للمستخدم
     */
    @ColumnInfo(name = "session_id")
    private String sessionId;
    
    /**
     * عنوان IP الخاص بالمستخدم
     */
    @ColumnInfo(name = "ip_address")
    private String ipAddress;
    
    /**
     * معلومات إضافية عن الجهاز
     */
    @ColumnInfo(name = "device_info")
    private String deviceInfo;
    
    /**
     * ملاحظات اختيارية عن سبب القفل
     */
    @ColumnInfo(name = "lock_reason")
    private String lockReason;
    
    /**
     * تاريخ إنشاء السجل
     */
    @ColumnInfo(name = "created_at")
    private Date createdAt;
    
    /**
     * تاريخ آخر تحديث
     */
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;
    
    // Constructors
    public RecordLock() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.lockStatus = "ACTIVE";
        this.lockedAt = new Date();
        // انتهاء صلاحية القفل بعد 30 دقيقة افتراضياً
        this.expiresAt = new Date(System.currentTimeMillis() + (30 * 60 * 1000));
    }
    
    public RecordLock(String recordId, String recordType, String userId, 
                     String userName, String companyId, String sessionId) {
        this();
        this.recordId = recordId;
        this.recordType = recordType;
        this.userId = userId;
        this.userName = userName;
        this.companyId = companyId;
        this.sessionId = sessionId;
    }
    
    // Getters and Setters
    public long getLockId() {
        return lockId;
    }
    
    public void setLockId(long lockId) {
        this.lockId = lockId;
    }
    
    public String getRecordId() {
        return recordId;
    }
    
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
    
    public String getRecordType() {
        return recordType;
    }
    
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getCompanyId() {
        return companyId;
    }
    
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
    
    public Date getLockedAt() {
        return lockedAt;
    }
    
    public void setLockedAt(Date lockedAt) {
        this.lockedAt = lockedAt;
    }
    
    public Date getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public String getLockStatus() {
        return lockStatus;
    }
    
    public void setLockStatus(String lockStatus) {
        this.lockStatus = lockStatus;
        this.updatedAt = new Date();
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getDeviceInfo() {
        return deviceInfo;
    }
    
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
    
    public String getLockReason() {
        return lockReason;
    }
    
    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * فحص ما إذا كان القفل لا يزال ساري المفعول
     */
    public boolean isActive() {
        return "ACTIVE".equals(lockStatus) && 
               expiresAt != null && 
               expiresAt.after(new Date());
    }
    
    /**
     * فحص ما إذا كان القفل منتهي الصلاحية
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.before(new Date());
    }
    
    /**
     * تمديد مدة القفل
     */
    public void extendLock(int additionalMinutes) {
        if (this.expiresAt != null) {
            long extensionMillis = additionalMinutes * 60 * 1000L;
            this.expiresAt = new Date(this.expiresAt.getTime() + extensionMillis);
            this.updatedAt = new Date();
        }
    }
    
    /**
     * تحرير القفل
     */
    public void releaseLock() {
        this.lockStatus = "RELEASED";
        this.updatedAt = new Date();
    }
    
    /**
     * تعيين القفل كمنتهي الصلاحية
     */
    public void expireLock() {
        this.lockStatus = "EXPIRED";
        this.updatedAt = new Date();
    }
    
    @Override
    public String toString() {
        return "RecordLock{" +
                "lockId=" + lockId +
                ", recordId='" + recordId + '\'' +
                ", recordType='" + recordType + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", companyId='" + companyId + '\'' +
                ", lockStatus='" + lockStatus + '\'' +
                ", lockedAt=" + lockedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
    
    /**
     * ثوابت حالات القفل
     */
    public static class LockStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String EXPIRED = "EXPIRED";
        public static final String RELEASED = "RELEASED";
    }
    
    /**
     * ثوابت أنواع السجلات
     */
    public static class RecordType {
        public static final String INVOICE = "invoice";
        public static final String CUSTOMER = "customer";
        public static final String SUPPLIER = "supplier";
        public static final String PRODUCT = "product";
        public static final String ACCOUNT = "account";
        public static final String PAYMENT = "payment";
        public static final String ORDER = "order";
        public static final String EMPLOYEE = "employee";
        public static final String COMPANY_SETTINGS = "company_settings";
    }
}