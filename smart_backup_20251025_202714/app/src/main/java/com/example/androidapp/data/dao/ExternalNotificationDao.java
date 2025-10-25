package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.ExternalNotification;

import java.util.Date;
import java.util.List;

/**
 * DAO للإشعارات الخارجية - لإدارة العمليات على قاعدة البيانات للإشعارات الخارجية
 */
@Dao
public interface ExternalNotificationDao {

    @Query("SELECT * FROM external_notifications ORDER BY created_at DESC")
    LiveData<List<ExternalNotification>> getAllNotifications();

    @Query("SELECT * FROM external_notifications WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<ExternalNotification>> getNotificationsByUser(String userId);

    @Query("SELECT * FROM external_notifications WHERE status = :status ORDER BY created_at DESC")
    LiveData<List<ExternalNotification>> getNotificationsByStatus(String status);

    @Query("SELECT * FROM external_notifications WHERE notification_channel = :channel ORDER BY created_at DESC")
    LiveData<List<ExternalNotification>> getNotificationsByChannel(String channel);

    @Query("SELECT * FROM external_notifications WHERE status = 'PENDING' ORDER BY priority DESC, created_at ASC")
    List<ExternalNotification> getPendingNotifications();

    @Query("SELECT * FROM external_notifications WHERE status = 'FAILED' AND retry_count < max_retries ORDER BY created_at ASC")
    List<ExternalNotification> getFailedNotificationsForRetry();

    @Query("SELECT * FROM external_notifications WHERE scheduled_time IS NOT NULL AND scheduled_time <= :currentTime AND status = 'PENDING'")
    List<ExternalNotification> getScheduledNotificationsDue(Date currentTime);

    @Query("SELECT * FROM external_notifications WHERE is_bulk = 1 AND bulk_id = :bulkId")
    LiveData<List<ExternalNotification>> getBulkNotifications(String bulkId);

    @Query("SELECT * FROM external_notifications WHERE recipient_id = :recipientId ORDER BY created_at DESC")
    LiveData<List<ExternalNotification>> getNotificationsByRecipient(String recipientId);

    @Query("SELECT * FROM external_notifications WHERE related_entity_type = :entityType AND related_entity_id = :entityId")
    LiveData<List<ExternalNotification>> getNotificationsByEntity(String entityType, String entityId);

    @Query("SELECT * FROM external_notifications WHERE priority = :priority ORDER BY created_at DESC")
    LiveData<List<ExternalNotification>> getNotificationsByPriority(String priority);

    @Query("SELECT * FROM external_notifications WHERE sent_time BETWEEN :startDate AND :endDate ORDER BY sent_time DESC")
    LiveData<List<ExternalNotification>> getNotificationsBySentDate(Date startDate, Date endDate);

    @Query("SELECT * FROM external_notifications WHERE id = :id")
    LiveData<ExternalNotification> getNotificationById(String id);

    @Query("SELECT COUNT(*) FROM external_notifications WHERE user_id = :userId AND status = :status")
    LiveData<Integer> getNotificationCountByStatus(String userId, String status);

    @Query("SELECT COUNT(*) FROM external_notifications WHERE status = 'PENDING'")
    LiveData<Integer> getPendingNotificationCount();

    @Query("SELECT COUNT(*) FROM external_notifications WHERE status = 'FAILED' AND retry_count < max_retries")
    LiveData<Integer> getFailedNotificationCount();

    @Query("SELECT SUM(cost) FROM external_notifications WHERE user_id = :userId AND sent_time BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalCostByUser(String userId, Date startDate, Date endDate);

    @Query("SELECT SUM(cost) FROM external_notifications WHERE notification_channel = :channel AND sent_time BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalCostByChannel(String channel, Date startDate, Date endDate);

    @Query("UPDATE external_notifications SET status = :status, sent_time = :sentTime, external_message_id = :externalId, updated_at = :updatedAt WHERE id = :id")
    void markAsSent(String id, String status, Date sentTime, String externalId, Date updatedAt);

    @Query("UPDATE external_notifications SET status = :status, delivered_time = :deliveredTime, updated_at = :updatedAt WHERE id = :id")
    void markAsDelivered(String id, String status, Date deliveredTime, Date updatedAt);

    @Query("UPDATE external_notifications SET status = :status, read_time = :readTime, updated_at = :updatedAt WHERE id = :id")
    void markAsRead(String id, String status, Date readTime, Date updatedAt);

    @Query("UPDATE external_notifications SET status = 'FAILED', retry_count = retry_count + 1, error_message = :errorMessage, updated_at = :updatedAt WHERE id = :id")
    void markAsFailed(String id, String errorMessage, Date updatedAt);

    @Query("UPDATE external_notifications SET cost = :cost, currency = :currency, updated_at = :updatedAt WHERE id = :id")
    void updateCost(String id, double cost, String currency, Date updatedAt);

    @Query("UPDATE external_notifications SET api_response = :apiResponse, updated_at = :updatedAt WHERE id = :id")
    void updateApiResponse(String id, String apiResponse, Date updatedAt);

    @Query("DELETE FROM external_notifications WHERE user_id = :userId")
    void deleteUserNotifications(String userId);

    @Query("DELETE FROM external_notifications WHERE status IN ('DELIVERED', 'READ') AND updated_at < :cutoffDate")
    void deleteOldSuccessfulNotifications(Date cutoffDate);

    @Query("DELETE FROM external_notifications WHERE status = 'FAILED' AND retry_count >= max_retries AND updated_at < :cutoffDate")
    void deleteOldFailedNotifications(Date cutoffDate);

    @Insert
    void insert(ExternalNotification notification);

    @Insert
    void insertAll(List<ExternalNotification> notifications);

    @Update
    void update(ExternalNotification notification);

    @Delete
    void delete(ExternalNotification notification);

    @Query("DELETE FROM external_notifications WHERE id = :id")
    void deleteById(String id);

    // Advanced analytics queries
    @Query("SELECT notification_channel, COUNT(*) as count FROM external_notifications GROUP BY notification_channel ORDER BY count DESC")
    LiveData<List<NotificationChannelCount>> getNotificationCountsByChannel();

    @Query("SELECT status, COUNT(*) as count FROM external_notifications GROUP BY status ORDER BY count DESC")
    LiveData<List<NotificationStatusCount>> getNotificationCountsByStatus();

    @Query("SELECT DATE(sent_time) as date, COUNT(*) as count FROM external_notifications WHERE sent_time >= :startDate GROUP BY DATE(sent_time) ORDER BY date DESC")
    LiveData<List<NotificationDailyCount>> getDailyNotificationCounts(Date startDate);

    @Query("SELECT notification_channel, SUM(cost) as total_cost FROM external_notifications WHERE sent_time BETWEEN :startDate AND :endDate GROUP BY notification_channel")
    LiveData<List<NotificationChannelCost>> getCostByChannel(Date startDate, Date endDate);

    @Query("SELECT bulk_id, COUNT(*) as notification_count, SUM(CASE WHEN status = 'DELIVERED' THEN 1 ELSE 0 END) as delivered_count FROM external_notifications WHERE is_bulk = 1 GROUP BY bulk_id ORDER BY notification_count DESC")
    LiveData<List<BulkNotificationSummary>> getBulkNotificationSummaries();

    // Helper classes for query results
    class NotificationChannelCount {
        public String notification_channel;
        public int count;
    }

    class NotificationStatusCount {
        public String status;
        public int count;
    }

    class NotificationDailyCount {
        public String date;
        public int count;
    }

    class NotificationChannelCost {
        public String notification_channel;
        public double total_cost;
    }

    class BulkNotificationSummary {
        public String bulk_id;
        public int notification_count;
        public int delivered_count;
    }
}
