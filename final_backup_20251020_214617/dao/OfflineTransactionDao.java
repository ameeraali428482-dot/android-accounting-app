package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.OfflineTransaction;

import java.util.Date;
import java.util.List;

/**
 * DAO للمعاملات بدون إنترنت - لإدارة العمليات على قاعدة البيانات للمعاملات بدون إنترنت
 */
@Dao
public interface OfflineTransactionDao {

    @Query("SELECT * FROM offline_transactions ORDER BY local_timestamp DESC")
    LiveData<List<OfflineTransaction>> getAllTransactions();

    @Query("SELECT * FROM offline_transactions WHERE user_id = :userId ORDER BY local_timestamp DESC")
    LiveData<List<OfflineTransaction>> getTransactionsByUser(String userId);

    @Query("SELECT * FROM offline_transactions WHERE sync_status = :status ORDER BY sync_priority DESC, local_timestamp ASC")
    List<OfflineTransaction> getTransactionsByStatus(String status);

    @Query("SELECT * FROM offline_transactions WHERE sync_status = 'PENDING' ORDER BY sync_priority DESC, local_timestamp ASC")
    List<OfflineTransaction> getPendingTransactions();

    @Query("SELECT * FROM offline_transactions WHERE sync_status = 'FAILED' AND retry_count < max_retries ORDER BY sync_priority DESC, local_timestamp ASC")
    List<OfflineTransaction> getFailedTransactionsForRetry();

    @Query("SELECT * FROM offline_transactions WHERE entity_type = :entityType ORDER BY local_timestamp DESC")
    LiveData<List<OfflineTransaction>> getTransactionsByEntityType(String entityType);

    @Query("SELECT * FROM offline_transactions WHERE entity_type = :entityType AND entity_id = :entityId ORDER BY local_timestamp DESC")
    LiveData<List<OfflineTransaction>> getTransactionsByEntity(String entityType, String entityId);

    @Query("SELECT * FROM offline_transactions WHERE transaction_type = :transactionType ORDER BY local_timestamp DESC")
    LiveData<List<OfflineTransaction>> getTransactionsByType(String transactionType);

    @Query("SELECT * FROM offline_transactions WHERE is_critical = 1 AND sync_status != 'SYNCED' ORDER BY sync_priority DESC, local_timestamp ASC")
    List<OfflineTransaction> getCriticalPendingTransactions();

    @Query("SELECT * FROM offline_transactions WHERE requires_confirmation = 1 AND sync_status = 'PENDING'")
    LiveData<List<OfflineTransaction>> getTransactionsRequiringConfirmation();

    @Query("SELECT * FROM offline_transactions WHERE device_id = :deviceId ORDER BY local_timestamp DESC")
    LiveData<List<OfflineTransaction>> getTransactionsByDevice(String deviceId);

    @Query("SELECT * FROM offline_transactions WHERE local_timestamp BETWEEN :startDate AND :endDate ORDER BY local_timestamp DESC")
    LiveData<List<OfflineTransaction>> getTransactionsInDateRange(Date startDate, Date endDate);

    @Query("SELECT * FROM offline_transactions WHERE expiry_date IS NOT NULL AND expiry_date <= :currentDate AND sync_status != 'SYNCED'")
    List<OfflineTransaction> getExpiredTransactions(Date currentDate);

    @Query("SELECT * FROM offline_transactions WHERE next_retry IS NOT NULL AND next_retry <= :currentTime AND sync_status = 'FAILED' AND retry_count < max_retries")
    List<OfflineTransaction> getTransactionsDueForRetry(Date currentTime);

    @Query("SELECT * FROM offline_transactions WHERE id = :id")
    LiveData<OfflineTransaction> getTransactionById(String id);

    @Query("SELECT COUNT(*) FROM offline_transactions WHERE user_id = :userId AND sync_status = :status")
    LiveData<Integer> getTransactionCountByStatus(String userId, String status);

    @Query("SELECT COUNT(*) FROM offline_transactions WHERE sync_status = 'PENDING'")
    LiveData<Integer> getPendingTransactionCount();

    @Query("SELECT COUNT(*) FROM offline_transactions WHERE sync_status = 'FAILED' AND retry_count < max_retries")
    LiveData<Integer> getFailedTransactionCount();

    @Query("SELECT COUNT(*) FROM offline_transactions WHERE is_critical = 1 AND sync_status != 'SYNCED'")
    LiveData<Integer> getCriticalPendingCount();

    @Query("SELECT COUNT(*) FROM offline_transactions WHERE requires_confirmation = 1 AND sync_status = 'PENDING'")
    LiveData<Integer> getConfirmationRequiredCount();

    @Query("UPDATE offline_transactions SET sync_status = :status, sync_timestamp = :syncTime, server_timestamp = :serverTime, server_response = :response, updated_at = :updatedAt WHERE id = :id")
    void markAsSynced(String id, String status, Date syncTime, Date serverTime, String response, Date updatedAt);

    @Query("UPDATE offline_transactions SET sync_status = 'FAILED', retry_count = retry_count + 1, error_message = :errorMessage, last_retry = :retryTime, next_retry = :nextRetry, updated_at = :updatedAt WHERE id = :id")
    void markAsFailed(String id, String errorMessage, Date retryTime, Date nextRetry, Date updatedAt);

    @Query("UPDATE offline_transactions SET sync_status = 'SYNCING', updated_at = :updatedAt WHERE id = :id")
    void markAsSyncing(String id, Date updatedAt);

    @Query("UPDATE offline_transactions SET sync_priority = :priority, updated_at = :updatedAt WHERE id = :id")
    void updatePriority(String id, int priority, Date updatedAt);

    @Query("UPDATE offline_transactions SET requires_confirmation = 0, updated_at = :updatedAt WHERE id = :id")
    void confirmTransaction(String id, Date updatedAt);

    @Query("UPDATE offline_transactions SET conflict_resolution = :resolution, updated_at = :updatedAt WHERE id = :id")
    void updateConflictResolution(String id, String resolution, Date updatedAt);

    @Query("DELETE FROM offline_transactions WHERE user_id = :userId")
    void deleteUserTransactions(String userId);

    @Query("DELETE FROM offline_transactions WHERE sync_status = 'SYNCED' AND sync_timestamp < :cutoffDate")
    void deleteOldSyncedTransactions(Date cutoffDate);

    @Query("DELETE FROM offline_transactions WHERE expiry_date IS NOT NULL AND expiry_date <= :currentDate")
    void deleteExpiredTransactions(Date currentDate);

    @Query("DELETE FROM offline_transactions WHERE sync_status = 'FAILED' AND retry_count >= max_retries AND updated_at < :cutoffDate")
    void deleteOldFailedTransactions(Date cutoffDate);

    @Insert
    void insert(OfflineTransaction transaction);

    @Insert
    void insertAll(List<OfflineTransaction> transactions);

    @Update
    void update(OfflineTransaction transaction);

    @Delete
    void delete(OfflineTransaction transaction);

    @Query("DELETE FROM offline_transactions WHERE id = :id")
    void deleteById(String id);

    // Advanced analytics queries
    @Query("SELECT entity_type, COUNT(*) as count FROM offline_transactions GROUP BY entity_type ORDER BY count DESC")
    LiveData<List<TransactionEntityTypeCount>> getTransactionCountsByEntityType();

    @Query("SELECT transaction_type, COUNT(*) as count FROM offline_transactions GROUP BY transaction_type ORDER BY count DESC")
    LiveData<List<TransactionTypeCount>> getTransactionCountsByType();

    @Query("SELECT sync_status, COUNT(*) as count FROM offline_transactions GROUP BY sync_status")
    LiveData<List<TransactionStatusCount>> getTransactionCountsByStatus();

    @Query("SELECT DATE(local_timestamp) as date, COUNT(*) as count FROM offline_transactions WHERE local_timestamp >= :startDate GROUP BY DATE(local_timestamp) ORDER BY date DESC")
    LiveData<List<TransactionDailyCount>> getDailyTransactionCounts(Date startDate);

    @Query("SELECT device_id, COUNT(*) as count FROM offline_transactions GROUP BY device_id ORDER BY count DESC LIMIT 10")
    LiveData<List<TransactionDeviceCount>> getTransactionCountsByDevice();

    @Query("SELECT user_id, COUNT(*) as transaction_count, SUM(CASE WHEN sync_status = 'SYNCED' THEN 1 ELSE 0 END) as synced_count FROM offline_transactions GROUP BY user_id ORDER BY transaction_count DESC LIMIT 10")
    LiveData<List<TransactionUserSummary>> getTopTransactionUsers();

    // Helper classes for query results
    class TransactionEntityTypeCount {
        public String entity_type;
        public int count;
    }

    class TransactionTypeCount {
        public String transaction_type;
        public int count;
    }

    class TransactionStatusCount {
        public String sync_status;
        public int count;
    }

    class TransactionDailyCount {
        public String date;
        public int count;
    }

    class TransactionDeviceCount {
        public String device_id;
        public int count;
    }

    class TransactionUserSummary {
        public String user_id;
        public int transaction_count;
        public int synced_count;
    }
}
