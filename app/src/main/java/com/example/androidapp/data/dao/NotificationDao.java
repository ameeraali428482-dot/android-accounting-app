package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
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
    long insertNotification(Notification notification);

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<Notification>> getAllForUser(int userId);

    @Query("DELETE FROM notifications WHERE createdAt < :cutoffTime")
    void deleteOldNotifications(long cutoffTime);

    @Query("UPDATE notifications SET isRead = 1 WHERE notificationId = :notificationId")
    void markAsRead(long notificationId);

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    int getUnreadCount(int userId);

    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId")
    LiveData<Notification> getById(long notificationId);

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    LiveData<List<Notification>> getAllNotifications();

    @Query("DELETE FROM notifications WHERE userId = :userId")
    void deleteByUserId(int userId);
}
