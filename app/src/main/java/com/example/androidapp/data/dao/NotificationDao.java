package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.androidapp.data.entities.Notification;
import java.util.List;

@Dao
public interface NotificationDao extends BaseDao<Notification> {
    
    @Insert
    long insert(Notification notification);

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    List<Notification> getAllForUser(int userId);

    @Query("DELETE FROM notifications WHERE timestamp < :cutoffTime")
    void deleteOldNotifications(long cutoffTime);

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    void markAsRead(int notificationId);

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    int getUnreadCount(int userId);

    @Query("SELECT * FROM notifications WHERE id = :id")
    Notification getById(int id);

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    List<Notification> getAll();

    @Query("DELETE FROM notifications WHERE userId = :userId")
    void deleteByUserId(int userId);
}
