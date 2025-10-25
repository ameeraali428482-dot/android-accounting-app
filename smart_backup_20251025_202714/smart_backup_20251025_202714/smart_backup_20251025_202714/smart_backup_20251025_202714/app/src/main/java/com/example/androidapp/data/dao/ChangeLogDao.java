package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.ChangeLog;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object لكيان ChangeLog
 * يوفر العمليات المطلوبة لإدارة سجل التغييرات والمراجعة في النظام
 * 
 * @author MiniMax Agent
 * @version 1.0
 * @since 2025-10-20
 */
@Dao
public interface ChangeLogDao {
    
    /**
     * إدراج سجل تغيير جديد
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertChangeLog(ChangeLog changeLog);
    
    /**
     * إدراج عدة سجلات تغيير
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertChangeLogs(List<ChangeLog> changeLogs);
    
    /**
     * تحديث سجل تغيير موجود
     */
    @Update
    int updateChangeLog(ChangeLog changeLog);
    
    /**
     * حذف سجل تغيير
     */
    @Delete
    int deleteChangeLog(ChangeLog changeLog);
    
    /**
     * البحث عن سجل تغيير بمعرف السجل
     */
    @Query("SELECT * FROM change_logs WHERE log_id = :logId")
    ChangeLog getChangeLogById(long logId);
    
    /**
     * البحث عن جميع تغييرات سجل معين
     */
    @Query("SELECT * FROM change_logs WHERE record_id = :recordId AND record_type = :recordType " +
           "AND company_id = :companyId ORDER BY changed_at DESC")
    List<ChangeLog> getChangeLogsForRecord(String recordId, String recordType, String companyId);
    
    /**
     * البحث عن تغييرات مستخدم معين
     */
    @Query("SELECT * FROM change_logs WHERE user_id = :userId AND company_id = :companyId " +
           "ORDER BY changed_at DESC LIMIT :limit")
    List<ChangeLog> getChangeLogsByUser(String userId, String companyId, int limit);
    
    /**
     * البحث عن التغييرات بنوع معين
     */
    @Query("SELECT * FROM change_logs WHERE change_type = :changeType AND company_id = :companyId " +
           "ORDER BY changed_at DESC LIMIT :limit")
    List<ChangeLog> getChangeLogsByType(String changeType, String companyId, int limit);
    
    /**
     * البحث عن التغييرات بفترة زمنية
     */
    @Query("SELECT * FROM change_logs WHERE company_id = :companyId AND " +
           "changed_at BETWEEN :startDate AND :endDate ORDER BY changed_at DESC")
    List<ChangeLog> getChangeLogsByDateRange(String companyId, Date startDate, Date endDate);
    
    /**
     * البحث عن التغييرات بمستوى خطورة معين
     */
    @Query("SELECT * FROM change_logs WHERE severity_level = :severityLevel AND company_id = :companyId " +
           "ORDER BY changed_at DESC LIMIT :limit")
    List<ChangeLog> getChangeLogsBySeverity(String severityLevel, String companyId, int limit);
    
    /**
     * البحث عن التغييرات بفئة معينة
     */
    @Query("SELECT * FROM change_logs WHERE change_category = :category AND company_id = :companyId " +
           "ORDER BY changed_at DESC LIMIT :limit")
    List<ChangeLog> getChangeLogsByCategory(String category, String companyId, int limit);
    
    /**
     * البحث عن التغييرات بحالة مراجعة معينة
     */
    @Query("SELECT * FROM change_logs WHERE review_status = :reviewStatus AND company_id = :companyId " +
           "ORDER BY changed_at DESC")
    List<ChangeLog> getChangeLogsByReviewStatus(String reviewStatus, String companyId);
    
    /**
     * البحث عن التغييرات المالية الحرجة
     */
    @Query("SELECT * FROM change_logs WHERE change_category = 'FINANCIAL' AND " +
           "severity_level IN ('HIGH', 'CRITICAL') AND company_id = :companyId " +
           "ORDER BY changed_at DESC LIMIT :limit")
    List<ChangeLog> getCriticalFinancialChanges(String companyId, int limit);
    
    /**
     * البحث عن التغييرات غير المراجعة
     */
    @Query("SELECT * FROM change_logs WHERE review_status = 'PENDING' AND company_id = :companyId " +
           "AND (severity_level IN ('HIGH', 'CRITICAL') OR change_category = 'FINANCIAL') " +
           "ORDER BY changed_at DESC")
    List<ChangeLog> getPendingReviewChanges(String companyId);
    
    /**
     * البحث عن التغييرات بمعاملة معينة
     */
    @Query("SELECT * FROM change_logs WHERE transaction_id = :transactionId AND company_id = :companyId " +
           "ORDER BY changed_at ASC")
    List<ChangeLog> getChangeLogsByTransaction(String transactionId, String companyId);
    
    /**
     * البحث عن التغييرات المرتبطة
     */
    @Query("SELECT * FROM change_logs WHERE related_change_id = :relatedChangeId AND company_id = :companyId " +
           "ORDER BY changed_at DESC")
    List<ChangeLog> getRelatedChangeLogs(Long relatedChangeId, String companyId);
    
    /**
     * البحث عن تغييرات حقل معين
     */
    @Query("SELECT * FROM change_logs WHERE field_name = :fieldName AND record_type = :recordType " +
           "AND company_id = :companyId ORDER BY changed_at DESC LIMIT :limit")
    List<ChangeLog> getChangeLogsByField(String fieldName, String recordType, String companyId, int limit);
    
    /**
     * البحث عن آخر تغيير لسجل معين
     */
    @Query("SELECT * FROM change_logs WHERE record_id = :recordId AND record_type = :recordType " +
           "AND company_id = :companyId ORDER BY changed_at DESC LIMIT 1")
    ChangeLog getLastChangeForRecord(String recordId, String recordType, String companyId);
    
    /**
     * البحث النصي في سجلات التغيير
     */
    @Query("SELECT * FROM change_logs WHERE company_id = :companyId AND " +
           "(user_name LIKE :searchQuery OR record_id LIKE :searchQuery OR " +
           "change_reason LIKE :searchQuery OR old_value LIKE :searchQuery OR " +
           "new_value LIKE :searchQuery) ORDER BY changed_at DESC LIMIT 100")
    List<ChangeLog> searchChangeLogs(String companyId, String searchQuery);
    
    /**
     * إحصائيات التغييرات لشركة معينة
     */
    @Query("SELECT COUNT(*) FROM change_logs WHERE company_id = :companyId")
    int getTotalChangeLogsCount(String companyId);
    
    /**
     * إحصائيات التغييرات لمستخدم معين
     */
    @Query("SELECT COUNT(*) FROM change_logs WHERE user_id = :userId AND company_id = :companyId")
    int getUserChangeLogsCount(String userId, String companyId);
    
    /**
     * إحصائيات التغييرات بنوع معين
     */
    @Query("SELECT COUNT(*) FROM change_logs WHERE change_type = :changeType AND company_id = :companyId")
    int getChangeLogsCountByType(String changeType, String companyId);
    
    /**
     * إحصائيات التغييرات المالية اليومية
     */
    @Query("SELECT COUNT(*) FROM change_logs WHERE change_category = 'FINANCIAL' AND " +
           "company_id = :companyId AND DATE(changed_at) = DATE(:date)")
    int getDailyFinancialChangesCount(String companyId, Date date);
    
    /**
     * البحث عن التغييرات بـ IP معين
     */
    @Query("SELECT * FROM change_logs WHERE ip_address = :ipAddress AND company_id = :companyId " +
           "ORDER BY changed_at DESC LIMIT 50")
    List<ChangeLog> getChangeLogsByIpAddress(String ipAddress, String companyId);
    
    /**
     * البحث عن التغييرات بجلسة معينة
     */
    @Query("SELECT * FROM change_logs WHERE session_id = :sessionId AND company_id = :companyId " +
           "ORDER BY changed_at DESC")
    List<ChangeLog> getChangeLogsBySession(String sessionId, String companyId);
    
    /**
     * موافقة على التغيير
     */
    @Query("UPDATE change_logs SET review_status = 'APPROVED', reviewed_by = :reviewerId, " +
           "reviewed_at = :reviewDate, review_notes = :notes WHERE log_id = :logId")
    int approveChangeLog(long logId, String reviewerId, Date reviewDate, String notes);
    
    /**
     * رفض التغيير
     */
    @Query("UPDATE change_logs SET review_status = 'REJECTED', reviewed_by = :reviewerId, " +
           "reviewed_at = :reviewDate, review_notes = :notes WHERE log_id = :logId")
    int rejectChangeLog(long logId, String reviewerId, Date reviewDate, String notes);
    
    /**
     * تحديث حالة المراجعة
     */
    @Query("UPDATE change_logs SET review_status = :newStatus WHERE log_id = :logId")
    int updateReviewStatus(long logId, String newStatus);
    
    /**
     * حذف السجلات القديمة (للتنظيف)
     */
    @Query("DELETE FROM change_logs WHERE created_at < :cutoffDate AND " +
           "severity_level NOT IN ('HIGH', 'CRITICAL')")
    int deleteOldChangeLogs(Date cutoffDate);
    
    /**
     * أرشفة السجلات القديمة
     */
    @Query("UPDATE change_logs SET review_status = 'ARCHIVED' WHERE created_at < :cutoffDate " +
           "AND review_status = 'APPROVED'")
    int archiveOldChangeLogs(Date cutoffDate);
    
    /**
     * البحث عن التغييرات المشبوهة (نفس المستخدم، تغييرات متكررة)
     */
    @Query("SELECT * FROM change_logs WHERE user_id = :userId AND company_id = :companyId " +
           "AND changed_at > :since GROUP BY record_id HAVING COUNT(*) > :threshold " +
           "ORDER BY changed_at DESC")
    List<ChangeLog> getSuspiciousChanges(String userId, String companyId, Date since, int threshold);
    
    /**
     * تقرير نشاط المستخدمين
     */
    @Query("SELECT user_id, user_name, COUNT(*) as change_count FROM change_logs " +
           "WHERE company_id = :companyId AND changed_at BETWEEN :startDate AND :endDate " +
           "GROUP BY user_id, user_name ORDER BY change_count DESC")
    List<UserActivity> getUserActivityReport(String companyId, Date startDate, Date endDate);
    
    /**
     * تقرير أنواع التغييرات
     */
    @Query("SELECT change_type, COUNT(*) as count FROM change_logs " +
           "WHERE company_id = :companyId AND changed_at BETWEEN :startDate AND :endDate " +
           "GROUP BY change_type ORDER BY count DESC")
    List<ChangeTypeCount> getChangeTypeReport(String companyId, Date startDate, Date endDate);
    
    /**
     * البحث عن التغييرات القابلة للتراجع
     */
    @Query("SELECT * FROM change_logs WHERE is_reversible = 1 AND company_id = :companyId " +
           "AND record_id = :recordId AND record_type = :recordType ORDER BY changed_at DESC")
    List<ChangeLog> getReversibleChanges(String companyId, String recordId, String recordType);
    
    // Inner classes for query results
    public static class UserActivity {
        public String user_id;
        public String user_name;
        public int change_count;
    }
    
    public static class ChangeTypeCount {
        public String change_type;
        public int count;
    }
}