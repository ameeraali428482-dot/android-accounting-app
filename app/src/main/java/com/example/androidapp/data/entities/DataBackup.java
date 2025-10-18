package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.androidapp.data.DateConverter;

import java.util.Date;

/**
 * كيان النسخ الاحتياطية - لإدارة عمليات النسخ الاحتياطي والاستعادة
 */
@Entity(tableName = "data_backups")
@TypeConverters({DateConverter.class})
public class DataBackup {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "backup_name")
    private String backupName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "backup_type")
    private String backupType; // FULL, INCREMENTAL, PARTIAL, SCHEDULED

    @ColumnInfo(name = "data_types")
    private String dataTypes; // JSON array of included data types

    @ColumnInfo(name = "file_path")
    private String filePath; // Local file path

    @ColumnInfo(name = "cloud_path")
    private String cloudPath; // Cloud storage path

    @ColumnInfo(name = "file_size")
    private long fileSize; // Size in bytes

    @ColumnInfo(name = "compressed_size")
    private long compressedSize; // Compressed size in bytes

    @ColumnInfo(name = "compression_format")
    private String compressionFormat; // ZIP, 7Z, etc.

    @ColumnInfo(name = "encryption_algorithm")
    private String encryptionAlgorithm; // AES256, etc.

    @ColumnInfo(name = "is_encrypted")
    private boolean isEncrypted;

    @ColumnInfo(name = "checksum")
    private String checksum; // MD5 or SHA256 checksum

    @ColumnInfo(name = "version")
    private int version; // Backup version number

    @ColumnInfo(name = "status")
    private String status; // CREATING, COMPLETED, FAILED, UPLOADING, UPLOADED

    @ColumnInfo(name = "progress")
    private int progress; // 0-100 percentage

    @ColumnInfo(name = "start_time")
    private Date startTime;

    @ColumnInfo(name = "end_time")
    private Date endTime;

    @ColumnInfo(name = "duration_ms")
    private long durationMs;

    @ColumnInfo(name = "records_count")
    private int recordsCount; // Total number of records backed up

    @ColumnInfo(name = "tables_included")
    private String tablesIncluded; // JSON array of table names

    @ColumnInfo(name = "date_range_from")
    private Date dateRangeFrom; // For partial backups

    @ColumnInfo(name = "date_range_to")
    private Date dateRangeTo; // For partial backups

    @ColumnInfo(name = "is_auto_backup")
    private boolean isAutoBackup; // Automatic vs manual backup

    @ColumnInfo(name = "retention_days")
    private int retentionDays; // How long to keep this backup

    @ColumnInfo(name = "expiry_date")
    private Date expiryDate;

    @ColumnInfo(name = "storage_location")
    private String storageLocation; // LOCAL, GOOGLE_DRIVE, DROPBOX, FIREBASE, etc.

    @ColumnInfo(name = "error_message")
    private String errorMessage;

    @ColumnInfo(name = "metadata")
    private String metadata; // Additional JSON metadata

    @ColumnInfo(name = "restore_count")
    private int restoreCount; // Number of times this backup was restored

    @ColumnInfo(name = "last_restored")
    private Date lastRestored;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // Constructors
    public DataBackup() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = "CREATING";
        this.progress = 0;
        this.version = 1;
        this.restoreCount = 0;
    }

    public DataBackup(@NonNull String id, String userId, String backupName, String backupType) {
        this.id = id;
        this.userId = userId;
        this.backupName = backupName;
        this.backupType = backupType;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = "CREATING";
        this.progress = 0;
        this.version = 1;
        this.restoreCount = 0;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBackupName() { return backupName; }
    public void setBackupName(String backupName) { this.backupName = backupName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBackupType() { return backupType; }
    public void setBackupType(String backupType) { this.backupType = backupType; }

    public String getDataTypes() { return dataTypes; }
    public void setDataTypes(String dataTypes) { this.dataTypes = dataTypes; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getCloudPath() { return cloudPath; }
    public void setCloudPath(String cloudPath) { this.cloudPath = cloudPath; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public long getCompressedSize() { return compressedSize; }
    public void setCompressedSize(long compressedSize) { this.compressedSize = compressedSize; }

    public String getCompressionFormat() { return compressionFormat; }
    public void setCompressionFormat(String compressionFormat) { this.compressionFormat = compressionFormat; }

    public String getEncryptionAlgorithm() { return encryptionAlgorithm; }
    public void setEncryptionAlgorithm(String encryptionAlgorithm) { this.encryptionAlgorithm = encryptionAlgorithm; }

    public boolean isEncrypted() { return isEncrypted; }
    public void setEncrypted(boolean encrypted) { isEncrypted = encrypted; }

    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }

    public int getRecordsCount() { return recordsCount; }
    public void setRecordsCount(int recordsCount) { this.recordsCount = recordsCount; }

    public String getTablesIncluded() { return tablesIncluded; }
    public void setTablesIncluded(String tablesIncluded) { this.tablesIncluded = tablesIncluded; }

    public Date getDateRangeFrom() { return dateRangeFrom; }
    public void setDateRangeFrom(Date dateRangeFrom) { this.dateRangeFrom = dateRangeFrom; }

    public Date getDateRangeTo() { return dateRangeTo; }
    public void setDateRangeTo(Date dateRangeTo) { this.dateRangeTo = dateRangeTo; }

    public boolean isAutoBackup() { return isAutoBackup; }
    public void setAutoBackup(boolean autoBackup) { isAutoBackup = autoBackup; }

    public int getRetentionDays() { return retentionDays; }
    public void setRetentionDays(int retentionDays) { this.retentionDays = retentionDays; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public int getRestoreCount() { return restoreCount; }
    public void setRestoreCount(int restoreCount) { this.restoreCount = restoreCount; }

    public Date getLastRestored() { return lastRestored; }
    public void setLastRestored(Date lastRestored) { this.lastRestored = lastRestored; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
