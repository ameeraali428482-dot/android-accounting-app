package com.example.androidapp.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.androidapp.data.DateConverter;
import java.util.Date;

/**
 * كيان ChangeLog لتتبع جميع التغييرات في النظام
 * يسجل كل تغيير يتم على البيانات مع تفاصيل كاملة للمراجعة والتدقيق
 * 
 * @author MiniMax Agent
 * @version 1.0
 * @since 2025-10-20
 */
@Entity(tableName = "change_logs")
@TypeConverters({DateConverter.class})
public class ChangeLog {
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "log_id")
    private long logId;
    
    /**
     * معرف السجل الذي تم تغييره
     */
    @ColumnInfo(name = "record_id")
    private String recordId;
    
    /**
     * نوع السجل (invoice, customer, product, account, etc.)
     */
    @ColumnInfo(name = "record_type")
    private String recordType;
    
    /**
     * اسم الجدول في قاعدة البيانات
     */
    @ColumnInfo(name = "table_name")
    private String tableName;
    
    /**
     * نوع التغيير (CREATE, UPDATE, DELETE, RESTORE)
     */
    @ColumnInfo(name = "change_type")
    private String changeType;
    
    /**
     * اسم الحقل الذي تم تغييره (للتحديثات المحددة)
     */
    @ColumnInfo(name = "field_name")
    private String fieldName;
    
    /**
     * القيمة القديمة (قبل التغيير)
     */
    @ColumnInfo(name = "old_value")
    private String oldValue;
    
    /**
     * القيمة الجديدة (بعد التغيير)
     */
    @ColumnInfo(name = "new_value")
    private String newValue;
    
    /**
     * معرف المستخدم الذي قام بالتغيير
     */
    @ColumnInfo(name = "user_id")
    private String userId;
    
    /**
     * اسم المستخدم الذي قام بالتغيير
     */
    @ColumnInfo(name = "user_name")
    private String userName;
    
    /**
     * دور المستخدم وقت التغيير
     */
    @ColumnInfo(name = "user_role")
    private String userRole;
    
    /**
     * معرف الشركة (Multi-tenant support)
     */
    @ColumnInfo(name = "company_id")
    private String companyId;
    
    /**
     * وقت حدوث التغيير
     */
    @ColumnInfo(name = "changed_at")
    private Date changedAt;
    
    /**
     * معرف الجلسة
     */
    @ColumnInfo(name = "session_id")
    private String sessionId;
    
    /**
     * عنوان IP الخاص بالمستخدم
     */
    @ColumnInfo(name = "ip_address")
    private String ipAddress;
    
    /**
     * معلومات الجهاز
     */
    @ColumnInfo(name = "device_info")
    private String deviceInfo;
    
    /**
     * تطبيق أو واجهة التي تم التغيير منها
     */
    @ColumnInfo(name = "source_application")
    private String sourceApplication;
    
    /**
     * سبب التغيير أو ملاحظات
     */
    @ColumnInfo(name = "change_reason")
    private String changeReason;
    
    /**
     * معرف المعاملة (Transaction ID) لربط التغييرات المتعددة
     */
    @ColumnInfo(name = "transaction_id")
    private String transactionId;
    
    /**
     * البيانات الكاملة قبل التغيير (JSON format)
     */
    @ColumnInfo(name = "before_data")
    private String beforeData;
    
    /**
     * البيانات الكاملة بعد التغيير (JSON format)
     */
    @ColumnInfo(name = "after_data")
    private String afterData;
    
    /**
     * مستوى الخطورة أو الأهمية (LOW, MEDIUM, HIGH, CRITICAL)
     */
    @ColumnInfo(name = "severity_level")
    private String severityLevel;
    
    /**
     * فئة التغيير (FINANCIAL, ADMINISTRATIVE, SYSTEM, USER_ACTION)
     */
    @ColumnInfo(name = "change_category")
    private String changeCategory;
    
    /**
     * حالة المراجعة (PENDING, REVIEWED, APPROVED, REJECTED)
     */
    @ColumnInfo(name = "review_status")
    private String reviewStatus;
    
    /**
     * معرف المراجع (إذا تم المراجعة)
     */
    @ColumnInfo(name = "reviewed_by")
    private String reviewedBy;
    
    /**
     * تاريخ المراجعة
     */
    @ColumnInfo(name = "reviewed_at")
    private Date reviewedAt;
    
    /**
     * ملاحظات المراجعة
     */
    @ColumnInfo(name = "review_notes")
    private String reviewNotes;
    
    /**
     * معرف التغيير المرتبط (لحالات التراجع أو التصحيح)
     */
    @ColumnInfo(name = "related_change_id")
    private Long relatedChangeId;
    
    /**
     * هل التغيير قابل للتراجع
     */
    @ColumnInfo(name = "is_reversible")
    private boolean isReversible;
    
    /**
     * تاريخ إنشاء السجل
     */
    @ColumnInfo(name = "created_at")
    private Date createdAt;
    
    // Constructors
    public ChangeLog() {
        this.createdAt = new Date();
        this.changedAt = new Date();
        this.severityLevel = "MEDIUM";
        this.reviewStatus = "PENDING";
        this.isReversible = true;
        this.sourceApplication = "OnSale Mobile App";
    }
    
    public ChangeLog(String recordId, String recordType, String changeType, 
                    String userId, String userName, String companyId) {
        this();
        this.recordId = recordId;
        this.recordType = recordType;
        this.changeType = changeType;
        this.userId = userId;
        this.userName = userName;
        this.companyId = companyId;
    }
    
    // Getters and Setters
    public long getLogId() {
        return logId;
    }
    
    public void setLogId(long logId) {
        this.logId = logId;
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
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getChangeType() {
        return changeType;
    }
    
    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getOldValue() {
        return oldValue;
    }
    
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
    
    public String getNewValue() {
        return newValue;
    }
    
    public void setNewValue(String newValue) {
        this.newValue = newValue;
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
    
    public String getUserRole() {
        return userRole;
    }
    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    
    public String getCompanyId() {
        return companyId;
    }
    
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
    
    public Date getChangedAt() {
        return changedAt;
    }
    
    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
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
    
    public String getSourceApplication() {
        return sourceApplication;
    }
    
    public void setSourceApplication(String sourceApplication) {
        this.sourceApplication = sourceApplication;
    }
    
    public String getChangeReason() {
        return changeReason;
    }
    
    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getBeforeData() {
        return beforeData;
    }
    
    public void setBeforeData(String beforeData) {
        this.beforeData = beforeData;
    }
    
    public String getAfterData() {
        return afterData;
    }
    
    public void setAfterData(String afterData) {
        this.afterData = afterData;
    }
    
    public String getSeverityLevel() {
        return severityLevel;
    }
    
    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }
    
    public String getChangeCategory() {
        return changeCategory;
    }
    
    public void setChangeCategory(String changeCategory) {
        this.changeCategory = changeCategory;
    }
    
    public String getReviewStatus() {
        return reviewStatus;
    }
    
    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
    
    public String getReviewedBy() {
        return reviewedBy;
    }
    
    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    
    public Date getReviewedAt() {
        return reviewedAt;
    }
    
    public void setReviewedAt(Date reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    
    public String getReviewNotes() {
        return reviewNotes;
    }
    
    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }
    
    public Long getRelatedChangeId() {
        return relatedChangeId;
    }
    
    public void setRelatedChangeId(Long relatedChangeId) {
        this.relatedChangeId = relatedChangeId;
    }
    
    public boolean isReversible() {
        return isReversible;
    }
    
    public void setReversible(boolean reversible) {
        isReversible = reversible;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * تحديد ما إذا كان التغيير يحتاج مراجعة
     */
    public boolean needsReview() {
        return "HIGH".equals(severityLevel) || 
               "CRITICAL".equals(severityLevel) ||
               "FINANCIAL".equals(changeCategory);
    }
    
    /**
     * الموافقة على التغيير
     */
    public void approve(String reviewerId, String notes) {
        this.reviewStatus = "APPROVED";
        this.reviewedBy = reviewerId;
        this.reviewedAt = new Date();
        this.reviewNotes = notes;
    }
    
    /**
     * رفض التغيير
     */
    public void reject(String reviewerId, String notes) {
        this.reviewStatus = "REJECTED";
        this.reviewedBy = reviewerId;
        this.reviewedAt = new Date();
        this.reviewNotes = notes;
    }
    
    @Override
    public String toString() {
        return "ChangeLog{" +
                "logId=" + logId +
                ", recordId='" + recordId + '\'' +
                ", recordType='" + recordType + '\'' +
                ", changeType='" + changeType + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", companyId='" + companyId + '\'' +
                ", changedAt=" + changedAt +
                ", severityLevel='" + severityLevel + '\'' +
                '}';
    }
    
    /**
     * ثوابت أنواع التغيير
     */
    public static class ChangeType {
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String RESTORE = "RESTORE";
        public static final String BULK_UPDATE = "BULK_UPDATE";
        public static final String MERGE = "MERGE";
        public static final String SPLIT = "SPLIT";
    }
    
    /**
     * ثوابت مستويات الخطورة
     */
    public static class SeverityLevel {
        public static final String LOW = "LOW";
        public static final String MEDIUM = "MEDIUM";
        public static final String HIGH = "HIGH";
        public static final String CRITICAL = "CRITICAL";
    }
    
    /**
     * ثوابت فئات التغيير
     */
    public static class ChangeCategory {
        public static final String FINANCIAL = "FINANCIAL";
        public static final String ADMINISTRATIVE = "ADMINISTRATIVE";
        public static final String SYSTEM = "SYSTEM";
        public static final String USER_ACTION = "USER_ACTION";
        public static final String SECURITY = "SECURITY";
        public static final String CONFIGURATION = "CONFIGURATION";
    }
    
    /**
     * ثوابت حالات المراجعة
     */
    public static class ReviewStatus {
        public static final String PENDING = "PENDING";
        public static final String REVIEWED = "REVIEWED";
        public static final String APPROVED = "APPROVED";
        public static final String REJECTED = "REJECTED";
        public static final String AUTO_APPROVED = "AUTO_APPROVED";
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
        public static final String USER = "user";
        public static final String ROLE = "role";
        public static final String PERMISSION = "permission";
    }
}