package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.RecordLock;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object لكيان RecordLock
 * يوفر العمليات المطلوبة لإدارة أقفال السجلات في النظام
 * 
 * @author MiniMax Agent
 * @version 1.0
 * @since 2025-10-20
 */
@Dao
public interface RecordLockDao {
    
    /**
     * إدراج قفل جديد
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLock(RecordLock recordLock);
    
    /**
     * إدراج عدة أقفال
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertLocks(List<RecordLock> recordLocks);
    
    /**
     * تحديث قفل موجود
     */
    @Update
    int updateLock(RecordLock recordLock);
    
    /**
     * حذف قفل
     */
    @Delete
    int deleteLock(RecordLock recordLock);
    
    /**
     * البحث عن قفل بمعرف القفل
     */
    @Query("SELECT * FROM record_locks WHERE lock_id = :lockId")
    RecordLock getLockById(long lockId);
    
    /**
     * البحث عن قفل نشط لسجل معين
     */
    @Query("SELECT * FROM record_locks WHERE record_id = :recordId AND record_type = :recordType " +
           "AND company_id = :companyId AND lock_status = 'ACTIVE' " +
           "AND expires_at > :currentTime LIMIT 1")
    RecordLock getActiveLockForRecord(String recordId, String recordType, String companyId, Date currentTime);
    
    /**
     * البحث عن جميع الأقفال النشطة لمستخدم معين
     */
    @Query("SELECT * FROM record_locks WHERE user_id = :userId AND company_id = :companyId " +
           "AND lock_status = 'ACTIVE' AND expires_at > :currentTime ORDER BY locked_at DESC")
    List<RecordLock> getActiveLocksForUser(String userId, String companyId, Date currentTime);
    
    /**
     * البحث عن جميع الأقفال لسجل معين (نشطة ومنتهية)
     */
    @Query("SELECT * FROM record_locks WHERE record_id = :recordId AND record_type = :recordType " +
           "AND company_id = :companyId ORDER BY locked_at DESC")
    List<RecordLock> getAllLocksForRecord(String recordId, String recordType, String companyId);
    
    /**
     * البحث عن الأقفال المنتهية الصلاحية
     */
    @Query("SELECT * FROM record_locks WHERE lock_status = 'ACTIVE' AND expires_at <= :currentTime")
    List<RecordLock> getExpiredLocks(Date currentTime);
    
    /**
     * البحث عن الأقفال النشطة لشركة معينة
     */
    @Query("SELECT * FROM record_locks WHERE company_id = :companyId AND lock_status = 'ACTIVE' " +
           "AND expires_at > :currentTime ORDER BY locked_at DESC")
    List<RecordLock> getActiveLocksForCompany(String companyId, Date currentTime);
    
    /**
     * البحث عن الأقفال بنوع سجل معين
     */
    @Query("SELECT * FROM record_locks WHERE record_type = :recordType AND company_id = :companyId " +
           "AND lock_status = 'ACTIVE' AND expires_at > :currentTime ORDER BY locked_at DESC")
    List<RecordLock> getActiveLocksByRecordType(String recordType, String companyId, Date currentTime);
    
    /**
     * البحث عن قفل بمعرف الجلسة
     */
    @Query("SELECT * FROM record_locks WHERE session_id = :sessionId AND lock_status = 'ACTIVE' " +
           "AND expires_at > :currentTime")
    List<RecordLock> getActiveLocksBySession(String sessionId, Date currentTime);
    
    /**
     * فحص ما إذا كان السجل مقفلاً من مستخدم آخر
     */
    @Query("SELECT COUNT(*) FROM record_locks WHERE record_id = :recordId AND record_type = :recordType " +
           "AND company_id = :companyId AND user_id != :currentUserId AND lock_status = 'ACTIVE' " +
           "AND expires_at > :currentTime")
    int isRecordLockedByOtherUser(String recordId, String recordType, String companyId, 
                                  String currentUserId, Date currentTime);
    
    /**
     * تحرير جميع أقفال مستخدم معين
     */
    @Query("UPDATE record_locks SET lock_status = 'RELEASED', updated_at = :currentTime " +
           "WHERE user_id = :userId AND company_id = :companyId AND lock_status = 'ACTIVE'")
    int releaseAllUserLocks(String userId, String companyId, Date currentTime);
    
    /**
     * تحرير أقفال جلسة معينة
     */
    @Query("UPDATE record_locks SET lock_status = 'RELEASED', updated_at = :currentTime " +
           "WHERE session_id = :sessionId AND lock_status = 'ACTIVE'")
    int releaseSessionLocks(String sessionId, Date currentTime);
    
    /**
     * تحديد الأقفال المنتهية الصلاحية
     */
    @Query("UPDATE record_locks SET lock_status = 'EXPIRED', updated_at = :currentTime " +
           "WHERE lock_status = 'ACTIVE' AND expires_at <= :currentTime")
    int markExpiredLocks(Date currentTime);
    
    /**
     * تمديد مدة القفل
     */
    @Query("UPDATE record_locks SET expires_at = :newExpiryTime, updated_at = :currentTime " +
           "WHERE lock_id = :lockId AND lock_status = 'ACTIVE'")
    int extendLock(long lockId, Date newExpiryTime, Date currentTime);
    
    /**
     * تحرير قفل محدد
     */
    @Query("UPDATE record_locks SET lock_status = 'RELEASED', updated_at = :currentTime " +
           "WHERE record_id = :recordId AND record_type = :recordType AND company_id = :companyId " +
           "AND user_id = :userId AND lock_status = 'ACTIVE'")
    int releaseLock(String recordId, String recordType, String companyId, String userId, Date currentTime);
    
    /**
     * حذف الأقفال القديمة (للتنظيف)
     */
    @Query("DELETE FROM record_locks WHERE lock_status != 'ACTIVE' AND created_at < :cutoffDate")
    int deleteOldLocks(Date cutoffDate);
    
    /**
     * إحصائيات الأقفال النشطة لشركة
     */
    @Query("SELECT COUNT(*) FROM record_locks WHERE company_id = :companyId AND lock_status = 'ACTIVE' " +
           "AND expires_at > :currentTime")
    int getActiveLocksCount(String companyId, Date currentTime);
    
    /**
     * إحصائيات الأقفال لمستخدم معين
     */
    @Query("SELECT COUNT(*) FROM record_locks WHERE user_id = :userId AND company_id = :companyId " +
           "AND lock_status = 'ACTIVE' AND expires_at > :currentTime")
    int getUserActiveLocksCount(String userId, String companyId, Date currentTime);
    
    /**
     * البحث عن الأقفال بفترة زمنية معينة
     */
    @Query("SELECT * FROM record_locks WHERE company_id = :companyId AND locked_at BETWEEN :startDate AND :endDate " +
           "ORDER BY locked_at DESC")
    List<RecordLock> getLocksByDateRange(String companyId, Date startDate, Date endDate);
    
    /**
     * البحث عن الأقفال بـ IP معين
     */
    @Query("SELECT * FROM record_locks WHERE ip_address = :ipAddress AND company_id = :companyId " +
           "ORDER BY locked_at DESC LIMIT 50")
    List<RecordLock> getLocksByIpAddress(String ipAddress, String companyId);
    
    /**
     * البحث النصي في الأقفال
     */
    @Query("SELECT * FROM record_locks WHERE company_id = :companyId AND " +
           "(user_name LIKE :searchQuery OR record_id LIKE :searchQuery OR " +
           "record_type LIKE :searchQuery OR lock_reason LIKE :searchQuery) " +
           "ORDER BY locked_at DESC LIMIT 100")
    List<RecordLock> searchLocks(String companyId, String searchQuery);
    
    /**
     * الحصول على آخر قفل لسجل معين
     */
    @Query("SELECT * FROM record_locks WHERE record_id = :recordId AND record_type = :recordType " +
           "AND company_id = :companyId ORDER BY locked_at DESC LIMIT 1")
    RecordLock getLastLockForRecord(String recordId, String recordType, String companyId);
    
    /**
     * البحث عن التعارضات المحتملة
     */
    @Query("SELECT * FROM record_locks WHERE record_id = :recordId AND record_type = :recordType " +
           "AND company_id = :companyId AND lock_status = 'ACTIVE' AND expires_at > :currentTime " +
           "AND user_id != :currentUserId")
    List<RecordLock> getConflictingLocks(String recordId, String recordType, String companyId, 
                                        String currentUserId, Date currentTime);
    
    /**
     * تنظيف الأقفال المنتهية الصلاحية تلقائياً
     */
    @Query("DELETE FROM record_locks WHERE lock_status = 'EXPIRED' AND updated_at < :cutoffDate")
    int cleanupExpiredLocks(Date cutoffDate);
}