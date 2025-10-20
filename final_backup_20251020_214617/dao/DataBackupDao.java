package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.DataBackup;

import java.util.Date;
import java.util.List;

/**
 * DAO للنسخ الاحتياطية - لإدارة العمليات على قاعدة البيانات للنسخ الاحتياطية
 */
@Dao
public interface DataBackupDao {

    @Query("SELECT * FROM data_backups ORDER BY created_at DESC")
    LiveData<List<DataBackup>> getAllBackups();

    @Query("SELECT * FROM data_backups WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<DataBackup>> getBackupsByUser(String userId);

    @Query("SELECT * FROM data_backups WHERE status = :status ORDER BY created_at DESC")
    LiveData<List<DataBackup>> getBackupsByStatus(String status);

    @Query("SELECT * FROM data_backups WHERE backup_type = :type ORDER BY created_at DESC")
    LiveData<List<DataBackup>> getBackupsByType(String type);

    @Query("SELECT * FROM data_backups WHERE is_auto_backup = 1 ORDER BY created_at DESC")
    LiveData<List<DataBackup>> getAutoBackups();

    @Query("SELECT * FROM data_backups WHERE is_auto_backup = 0 ORDER BY created_at DESC")
    LiveData<List<DataBackup>> getManualBackups();

    @Query("SELECT * FROM data_backups WHERE status = 'COMPLETED' ORDER BY created_at DESC")
    LiveData<List<DataBackup>> getCompletedBackups();

    @Query("SELECT * FROM data_backups WHERE status = 'FAILED' ORDER BY created_at DESC")
    LiveData<List<DataBackup>> getFailedBackups();

    @Query("SELECT * FROM data_backups WHERE storage_location = :location ORDER BY created_at DESC")
    LiveData<List<DataBackup>> getBackupsByStorageLocation(String location);

    @Query("SELECT * FROM data_backups WHERE expiry_date IS NOT NULL AND expiry_date <= :currentDate")
    List<DataBackup> getExpiredBackups(Date currentDate);

    @Query("SELECT * FROM data_backups WHERE expiry_date IS NOT NULL AND expiry_date BETWEEN :currentDate AND :warningDate")
    List<DataBackup> getExpiringBackups(Date currentDate, Date warningDate);

    @Query("SELECT * FROM data_backups WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    LiveData<List<DataBackup>> getBackupsInDateRange(Date startDate, Date endDate);

    @Query("SELECT * FROM data_backups WHERE file_size >= :minSize ORDER BY file_size DESC")
    LiveData<List<DataBackup>> getLargeBackups(long minSize);

    @Query("SELECT * FROM data_backups WHERE id = :id")
    LiveData<DataBackup> getBackupById(String id);

    @Query("SELECT COUNT(*) FROM data_backups WHERE user_id = :userId")
    LiveData<Integer> getUserBackupCount(String userId);

    @Query("SELECT COUNT(*) FROM data_backups WHERE user_id = :userId AND status = 'COMPLETED'")
    LiveData<Integer> getUserCompletedBackupCount(String userId);

    @Query("SELECT COUNT(*) FROM data_backups WHERE status = :status")
    LiveData<Integer> getBackupCountByStatus(String status);

    @Query("SELECT SUM(file_size) FROM data_backups WHERE user_id = :userId AND status = 'COMPLETED'")
    LiveData<Long> getTotalBackupSizeByUser(String userId);

    @Query("SELECT SUM(compressed_size) FROM data_backups WHERE user_id = :userId AND status = 'COMPLETED'")
    LiveData<Long> getTotalCompressedSizeByUser(String userId);

    @Query("SELECT AVG(duration_ms) FROM data_backups WHERE status = 'COMPLETED' AND duration_ms > 0")
    LiveData<Long> getAverageBackupDuration();

    @Query("SELECT * FROM data_backups WHERE user_id = :userId AND status = 'COMPLETED' ORDER BY created_at DESC LIMIT 1")
    LiveData<DataBackup> getLatestCompletedBackup(String userId);

    @Query("UPDATE data_backups SET status = :status, progress = :progress, updated_at = :updatedAt WHERE id = :id")
    void updateProgress(String id, String status, int progress, Date updatedAt);

    @Query("UPDATE data_backups SET status = :status, end_time = :endTime, duration_ms = :durationMs, file_size = :fileSize, compressed_size = :compressedSize, checksum = :checksum, updated_at = :updatedAt WHERE id = :id")
    void markAsCompleted(String id, String status, Date endTime, long durationMs, long fileSize, long compressedSize, String checksum, Date updatedAt);

    @Query("UPDATE data_backups SET status = 'FAILED', error_message = :errorMessage, updated_at = :updatedAt WHERE id = :id")
    void markAsFailed(String id, String errorMessage, Date updatedAt);

    @Query("UPDATE data_backups SET cloud_path = :cloudPath, status = :status, updated_at = :updatedAt WHERE id = :id")
    void updateCloudPath(String id, String cloudPath, String status, Date updatedAt);

    @Query("UPDATE data_backups SET restore_count = restore_count + 1, last_restored = :restoredDate, updated_at = :updatedAt WHERE id = :id")
    void incrementRestoreCount(String id, Date restoredDate, Date updatedAt);

    @Query("DELETE FROM data_backups WHERE user_id = :userId")
    void deleteUserBackups(String userId);

    @Query("DELETE FROM data_backups WHERE expiry_date IS NOT NULL AND expiry_date <= :currentDate")
    void deleteExpiredBackups(Date currentDate);

    @Query("DELETE FROM data_backups WHERE status = 'FAILED' AND created_at < :cutoffDate")
    void deleteOldFailedBackups(Date cutoffDate);

    @Insert
    void insert(DataBackup backup);

    @Insert
    void insertAll(List<DataBackup> backups);

    @Update
    void update(DataBackup backup);

    @Delete
    void delete(DataBackup backup);

    @Query("DELETE FROM data_backups WHERE id = :id")
    void deleteById(String id);

    // Advanced analytics queries
    @Query("SELECT backup_type, COUNT(*) as count FROM data_backups WHERE status = 'COMPLETED' GROUP BY backup_type ORDER BY count DESC")
    LiveData<List<BackupTypeCount>> getBackupCountsByType();

    @Query("SELECT storage_location, COUNT(*) as count FROM data_backups WHERE status = 'COMPLETED' GROUP BY storage_location")
    LiveData<List<BackupStorageCount>> getBackupCountsByStorage();

    @Query("SELECT DATE(created_at) as date, COUNT(*) as count FROM data_backups WHERE created_at >= :startDate GROUP BY DATE(created_at) ORDER BY date DESC")
    LiveData<List<BackupDailyCount>> getDailyBackupCounts(Date startDate);

    @Query("SELECT DATE(created_at) as date, SUM(file_size) as total_size FROM data_backups WHERE status = 'COMPLETED' AND created_at >= :startDate GROUP BY DATE(created_at) ORDER BY date DESC")
    LiveData<List<BackupDailySize>> getDailyBackupSizes(Date startDate);

    @Query("SELECT user_id, COUNT(*) as backup_count, SUM(file_size) as total_size FROM data_backups WHERE status = 'COMPLETED' GROUP BY user_id ORDER BY backup_count DESC LIMIT 10")
    LiveData<List<BackupUserSummary>> getTopBackupUsers();

    // Helper classes for query results
    class BackupTypeCount {
        public String backup_type;
        public int count;
    }

    class BackupStorageCount {
        public String storage_location;
        public int count;
    }

    class BackupDailyCount {
        public String date;
        public int count;
    }

    class BackupDailySize {
        public String date;
        public long total_size;
    }

    class BackupUserSummary {
        public String user_id;
        public int backup_count;
        public long total_size;
    }
}
