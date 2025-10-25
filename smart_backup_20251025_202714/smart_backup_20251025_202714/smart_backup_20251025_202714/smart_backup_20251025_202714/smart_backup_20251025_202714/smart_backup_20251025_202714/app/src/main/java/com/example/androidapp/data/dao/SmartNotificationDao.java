package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.SmartNotification;

import java.util.Date;
import java.util.List;

/**
 * DAO للتنبيهات الذكية - لإدارة العمليات على قاعدة البيانات للتنبيهات الذكية
 */
@Dao
public interface SmartNotificationDao {

    @Query("SELECT * FROM smart_notifications ORDER BY created_at DESC")
    LiveData<List<SmartNotification>> getAllNotifications();

    @Query("SELECT * FROM smart_notifications WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<SmartNotification>> getNotificationsByUser(String userId);

    @Query("SELECT * FROM smart_notifications WHERE user_id = :userId AND is_read = 0 ORDER BY priority DESC, created_at DESC")
    LiveData<List<SmartNotification>> getUnreadNotificationsByUser(String userId);

    @Query("SELECT * FROM smart_notifications WHERE notification_type = :type ORDER BY created_at DESC")
    LiveData<List<SmartNotification>> getNotificationsByType(String type);

    @Query("SELECT * FROM smart_notifications WHERE priority = :priority ORDER BY created_at DESC")
    LiveData<List<SmartNotification>> getNotificationsByPriority(String priority);

    @Query("SELECT * FROM smart_notifications WHERE trigger_date BETWEEN :startDate AND :endDate ORDER BY trigger_date ASC")
    LiveData<List<SmartNotification>> getNotificationsByDateRange(Date startDate, Date endDate);

    @Query("SELECT * FROM smart_notifications WHERE is_recurring = 1 AND is_dismissed = 0")
    LiveData<List<SmartNotification>> getActiveRecurringNotifications();

    @Query("SELECT * FROM smart_notifications WHERE related_entity_type = :entityType AND related_entity_id = :entityId")
    LiveData<List<SmartNotification>> getNotificationsByEntity(String entityType, String entityId);

    @Query("SELECT * FROM smart_notifications WHERE trigger_date <= :currentDate AND is_dismissed = 0 ORDER BY priority DESC")
    List<SmartNotification> getPendingNotifications(Date currentDate);

    @Query("SELECT COUNT(*) FROM smart_notifications WHERE user_id = :userId AND is_read = 0")
    LiveData<Integer> getUnreadCount(String userId);

    @Query("SELECT COUNT(*) FROM smart_notifications WHERE user_id = :userId AND priority = 'HIGH' AND is_read = 0")
    LiveData<Integer> getHighPriorityUnreadCount(String userId);

    @Query("SELECT * FROM smart_notifications WHERE id = :id")
    LiveData<SmartNotification> getNotificationById(String id);

    @Query("UPDATE smart_notifications SET is_read = 1, updated_at = :updatedAt WHERE id = :id")
    void markAsRead(String id, Date updatedAt);

    @Query("UPDATE smart_notifications SET is_dismissed = 1, updated_at = :updatedAt WHERE id = :id")
    void markAsDismissed(String id, Date updatedAt);

    @Query("UPDATE smart_notifications SET is_read = 1, updated_at = :updatedAt WHERE user_id = :userId")
    void markAllAsRead(String userId, Date updatedAt);

    @Query("UPDATE smart_notifications SET is_dismissed = 1, updated_at = :updatedAt WHERE user_id = :userId AND notification_type = :type")
    void dismissByType(String userId, String type, Date updatedAt);

    @Query("DELETE FROM smart_notifications WHERE is_dismissed = 1 AND created_at < :cutoffDate")
    void deleteOldDismissedNotifications(Date cutoffDate);

    @Query("DELETE FROM smart_notifications WHERE user_id = :userId")
    void deleteUserNotifications(String userId);

    @Insert
    void insert(SmartNotification notification);

    @Insert
    void insertAll(List<SmartNotification> notifications);

    @Update
    void update(SmartNotification notification);

    @Delete
    void delete(SmartNotification notification);

    @Query("DELETE FROM smart_notifications WHERE id = :id")
    void deleteById(String id);

    // Advanced queries for analytics
    @Query("SELECT notification_type, COUNT(*) as count FROM smart_notifications WHERE user_id = :userId GROUP BY notification_type")
    LiveData<List<NotificationTypeCount>> getNotificationCountsByType(String userId);

    @Query("SELECT DATE(created_at) as date, COUNT(*) as count FROM smart_notifications WHERE user_id = :userId AND created_at >= :startDate GROUP BY DATE(created_at) ORDER BY date DESC")
    LiveData<List<NotificationDailyCount>> getDailyNotificationCounts(String userId, Date startDate);

    // Helper classes for query results
    class NotificationTypeCount {
        public String notification_type;
        public int count;
    }

    class NotificationDailyCount {
        public String date;
        public int count;
    }
}
