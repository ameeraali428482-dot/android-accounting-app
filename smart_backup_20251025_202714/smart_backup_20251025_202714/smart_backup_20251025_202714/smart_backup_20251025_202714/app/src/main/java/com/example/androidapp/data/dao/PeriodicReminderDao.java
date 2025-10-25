package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.PeriodicReminder;

import java.util.Date;
import java.util.List;

/**
 * DAO للتذكيرات الدورية - لإدارة العمليات على قاعدة البيانات للتذكيرات الدورية
 */
@Dao
public interface PeriodicReminderDao {

    @Query("SELECT * FROM periodic_reminders ORDER BY created_at DESC")
    LiveData<List<PeriodicReminder>> getAllReminders();

    @Query("SELECT * FROM periodic_reminders WHERE user_id = :userId ORDER BY next_trigger ASC")
    LiveData<List<PeriodicReminder>> getRemindersByUser(String userId);

    @Query("SELECT * FROM periodic_reminders WHERE is_active = 1 ORDER BY next_trigger ASC")
    LiveData<List<PeriodicReminder>> getActiveReminders();

    @Query("SELECT * FROM periodic_reminders WHERE is_active = 1 AND next_trigger <= :currentTime ORDER BY next_trigger ASC")
    List<PeriodicReminder> getDueReminders(Date currentTime);

    @Query("SELECT * FROM periodic_reminders WHERE reminder_type = :type AND is_active = 1")
    LiveData<List<PeriodicReminder>> getRemindersByType(String type);

    @Query("SELECT * FROM periodic_reminders WHERE frequency = :frequency AND is_active = 1")
    LiveData<List<PeriodicReminder>> getRemindersByFrequency(String frequency);

    @Query("SELECT * FROM periodic_reminders WHERE user_id = :userId AND reminder_type = :type")
    LiveData<List<PeriodicReminder>> getUserRemindersByType(String userId, String type);

    @Query("SELECT * FROM periodic_reminders WHERE next_trigger BETWEEN :startDate AND :endDate AND is_active = 1 ORDER BY next_trigger ASC")
    LiveData<List<PeriodicReminder>> getRemindersInDateRange(Date startDate, Date endDate);

    @Query("SELECT * FROM periodic_reminders WHERE end_date IS NOT NULL AND end_date < :currentDate AND is_active = 1")
    List<PeriodicReminder> getExpiredReminders(Date currentDate);

    @Query("SELECT * FROM periodic_reminders WHERE id = :id")
    LiveData<PeriodicReminder> getReminderById(String id);

    @Query("SELECT COUNT(*) FROM periodic_reminders WHERE user_id = :userId AND is_active = 1")
    LiveData<Integer> getActiveReminderCount(String userId);

    @Query("SELECT COUNT(*) FROM periodic_reminders WHERE is_active = 1 AND next_trigger <= :currentTime")
    LiveData<Integer> getDueReminderCount(Date currentTime);

    @Query("UPDATE periodic_reminders SET is_active = 0, updated_at = :updatedAt WHERE id = :id")
    void deactivateReminder(String id, Date updatedAt);

    @Query("UPDATE periodic_reminders SET is_active = 1, updated_at = :updatedAt WHERE id = :id")
    void activateReminder(String id, Date updatedAt);

    @Query("UPDATE periodic_reminders SET last_triggered = :lastTriggered, next_trigger = :nextTrigger, updated_at = :updatedAt WHERE id = :id")
    void updateTriggerDates(String id, Date lastTriggered, Date nextTrigger, Date updatedAt);

    @Query("UPDATE periodic_reminders SET next_trigger = :nextTrigger, updated_at = :updatedAt WHERE id = :id")
    void updateNextTrigger(String id, Date nextTrigger, Date updatedAt);

    @Query("UPDATE periodic_reminders SET is_active = 0, updated_at = :updatedAt WHERE end_date IS NOT NULL AND end_date < :currentDate")
    void deactivateExpiredReminders(Date currentDate, Date updatedAt);

    @Query("DELETE FROM periodic_reminders WHERE user_id = :userId")
    void deleteUserReminders(String userId);

    @Query("DELETE FROM periodic_reminders WHERE is_active = 0 AND updated_at < :cutoffDate")
    void deleteOldInactiveReminders(Date cutoffDate);

    @Insert
    void insert(PeriodicReminder reminder);

    @Insert
    void insertAll(List<PeriodicReminder> reminders);

    @Update
    void update(PeriodicReminder reminder);

    @Delete
    void delete(PeriodicReminder reminder);

    @Query("DELETE FROM periodic_reminders WHERE id = :id")
    void deleteById(String id);

    // Advanced analytics queries
    @Query("SELECT reminder_type, COUNT(*) as count FROM periodic_reminders WHERE user_id = :userId AND is_active = 1 GROUP BY reminder_type")
    LiveData<List<ReminderTypeCount>> getReminderCountsByType(String userId);

    @Query("SELECT frequency, COUNT(*) as count FROM periodic_reminders WHERE user_id = :userId AND is_active = 1 GROUP BY frequency")
    LiveData<List<ReminderFrequencyCount>> getReminderCountsByFrequency(String userId);

    @Query("SELECT DATE(last_triggered) as date, COUNT(*) as count FROM periodic_reminders WHERE user_id = :userId AND last_triggered >= :startDate GROUP BY DATE(last_triggered) ORDER BY date DESC")
    LiveData<List<ReminderDailyTriggered>> getDailyTriggeredCounts(String userId, Date startDate);

    // Helper classes for query results
    class ReminderTypeCount {
        public String reminder_type;
        public int count;
    }

    class ReminderFrequencyCount {
        public String frequency;
        public int count;
    }

    class ReminderDailyTriggered {
        public String date;
        public int count;
    }
}
