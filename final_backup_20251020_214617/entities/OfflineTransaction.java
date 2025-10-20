package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.androidapp.data.DateConverter;

import java.util.Date;

/**
 * كيان المعاملات بدون إنترنت - لحفظ المعاملات التي تمت بدون اتصال
 */
@Entity(tableName = "offline_transactions")
@TypeConverters({DateConverter.class})
public class OfflineTransaction {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "transaction_type")
    private String transactionType; // CREATE, UPDATE, DELETE

    @ColumnInfo(name = "entity_type")
    private String entityType; // INVOICE, PAYMENT, CUSTOMER, ITEM, etc.

    @ColumnInfo(name = "entity_id")
    private String entityId; // ID of the affected entity

    @ColumnInfo(name = "operation")
    private String operation; // Specific operation performed

    @ColumnInfo(name = "data_before")
    private String dataBefore; // JSON of entity state before change

    @ColumnInfo(name = "data_after")
    private String dataAfter; // JSON of entity state after change

    @ColumnInfo(name = "changes")
    private String changes; // JSON of specific fields changed

    @ColumnInfo(name = "sync_status")
    private String syncStatus; // PENDING, SYNCING, SYNCED, FAILED, CONFLICT

    @ColumnInfo(name = "sync_priority")
    private int syncPriority; // 1-10, higher number = higher priority

    @ColumnInfo(name = "requires_confirmation")
    private boolean requiresConfirmation; // Whether sync needs user confirmation

    @ColumnInfo(name = "conflict_resolution")
    private String conflictResolution; // AUTO, MANUAL, USER_CHOICE

    @ColumnInfo(name = "local_timestamp")
    private Date localTimestamp; // When transaction was created locally

    @ColumnInfo(name = "sync_timestamp")
    private Date syncTimestamp; // When it was synced to server

    @ColumnInfo(name = "server_timestamp")
    private Date serverTimestamp; // Server timestamp from sync response

    @ColumnInfo(name = "retry_count")
    private int retryCount;

    @ColumnInfo(name = "max_retries")
    private int maxRetries;

    @ColumnInfo(name = "last_retry")
    private Date lastRetry;

    @ColumnInfo(name = "next_retry")
    private Date nextRetry;

    @ColumnInfo(name = "error_message")
    private String errorMessage;

    @ColumnInfo(name = "server_response")
    private String serverResponse; // Full server response

    @ColumnInfo(name = "dependency_ids")
    private String dependencyIds; // JSON array of transaction IDs this depends on

    @ColumnInfo(name = "checksum")
    private String checksum; // Data integrity check

    @ColumnInfo(name = "device_id")
    private String deviceId; // ID of device that created this transaction

    @ColumnInfo(name = "app_version")
    private String appVersion; // App version when transaction was created

    @ColumnInfo(name = "connection_type")
    private String connectionType; // OFFLINE, WIFI, MOBILE when created

    @ColumnInfo(name = "metadata")
    private String metadata; // Additional JSON metadata

    @ColumnInfo(name = "is_critical")
    private boolean isCritical; // Whether this transaction is critical for business

    @ColumnInfo(name = "expiry_date")
    private Date expiryDate; // When this transaction becomes invalid

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // Constructors
    public OfflineTransaction() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.localTimestamp = new Date();
        this.syncStatus = "PENDING";
        this.syncPriority = 5;
        this.retryCount = 0;
        this.maxRetries = 3;
    }

    @Ignore
    public OfflineTransaction(@NonNull String id, String userId, String transactionType,
                             String entityType, String entityId) {
        this.id = id;
        this.userId = userId;
        this.transactionType = transactionType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.localTimestamp = new Date();
        this.syncStatus = "PENDING";
        this.syncPriority = 5;
        this.retryCount = 0;
        this.maxRetries = 3;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getDataBefore() { return dataBefore; }
    public void setDataBefore(String dataBefore) { this.dataBefore = dataBefore; }

    public String getDataAfter() { return dataAfter; }
    public void setDataAfter(String dataAfter) { this.dataAfter = dataAfter; }

    public String getChanges() { return changes; }
    public void setChanges(String changes) { this.changes = changes; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public int getSyncPriority() { return syncPriority; }
    public void setSyncPriority(int syncPriority) { this.syncPriority = syncPriority; }

    public boolean isRequiresConfirmation() { return requiresConfirmation; }
    public void setRequiresConfirmation(boolean requiresConfirmation) { this.requiresConfirmation = requiresConfirmation; }

    public String getConflictResolution() { return conflictResolution; }
    public void setConflictResolution(String conflictResolution) { this.conflictResolution = conflictResolution; }

    public Date getLocalTimestamp() { return localTimestamp; }
    public void setLocalTimestamp(Date localTimestamp) { this.localTimestamp = localTimestamp; }

    public Date getSyncTimestamp() { return syncTimestamp; }
    public void setSyncTimestamp(Date syncTimestamp) { this.syncTimestamp = syncTimestamp; }

    public Date getServerTimestamp() { return serverTimestamp; }
    public void setServerTimestamp(Date serverTimestamp) { this.serverTimestamp = serverTimestamp; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

    public Date getLastRetry() { return lastRetry; }
    public void setLastRetry(Date lastRetry) { this.lastRetry = lastRetry; }

    public Date getNextRetry() { return nextRetry; }
    public void setNextRetry(Date nextRetry) { this.nextRetry = nextRetry; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getServerResponse() { return serverResponse; }
    public void setServerResponse(String serverResponse) { this.serverResponse = serverResponse; }

    public String getDependencyIds() { return dependencyIds; }
    public void setDependencyIds(String dependencyIds) { this.dependencyIds = dependencyIds; }

    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }

    public String getConnectionType() { return connectionType; }
    public void setConnectionType(String connectionType) { this.connectionType = connectionType; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public boolean isCritical() { return isCritical; }
    public void setCritical(boolean critical) { isCritical = critical; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
