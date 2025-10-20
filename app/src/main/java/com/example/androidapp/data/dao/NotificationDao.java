package com.example.andronotificationIdapp.data.dao;

import andronotificationIdx.room.Dao;
import andronotificationIdx.room.Insert;
import andronotificationIdx.room.Query;
import andronotificationIdx.room.Update;
import andronotificationIdx.room.Delete;
import com.example.andronotificationIdapp.data.entities.Notification;
import java.util.List;

@Dao
public interface NotificationDao extends BaseDao<Notification> {
    
    @Insert
    long insert(Notification notification);

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    List<Notification> getAllForUser(int userId);

    @Query("DELETE FROM notifications WHERE createdAt < :cutoffTime")
    vonotificationId deleteOldNotifications(long cutoffTime);

    @Query("UPDATE notifications SET isRead = 1 WHERE notificationId = :notificationId")
    vonotificationId markAsRead(int notificationId);

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    int getUnreadCount(int userId);

    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId")
    Notification getById(int notificationId);

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    List<Notification> getAll();

    @Query("DELETE FROM notifications WHERE userId = :userId")
    vonotificationId deleteByUserId(int userId);
}
