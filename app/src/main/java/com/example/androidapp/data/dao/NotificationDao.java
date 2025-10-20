package com.example.andronotificationIdapp.data.dao;

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

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    List<Notification> getAllForUser(int userId);

    @Query("DELETE FROM notifications WHERE createdAt < :cutoffTime")
    void deleteOldNotifications(long cutoffTime);

    @Query("UPDATE notifications SET isRead = 1 WHERE notificationId = :notificationId")
    void markAsRead(int notificationId);

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    int getUnreadCount(int userId);

    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId")
    Notification getById(int notificationId);

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    List<Notification> getAll();

    @Query("DELETE FROM notifications WHERE userId = :userId")
    void deleteByUserId(int userId);
}
