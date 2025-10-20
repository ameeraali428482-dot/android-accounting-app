package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Notification;
import java.util.List;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY created_at DESC")
    List<Notification> getAllNotifications();
    
    @Query("SELECT * FROM notifications WHERE id = :id")
    Notification getNotificationById(int id);
    
    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC")
    List<Notification> getNotificationsByUserId(String userId);
    
    @Query("SELECT * FROM notifications WHERE user_id = :userId AND is_read = 0 ORDER BY created_at DESC")
    List<Notification> getUnreadNotificationsByUserId(String userId);
    
    @Query("SELECT * FROM notifications WHERE type = :type")
    List<Notification> getNotificationsByType(String type);
    
    @Insert
    void insertNotification(Notification notification);
    
    @Update
    void updateNotification(Notification notification);
    
    @Delete
    void deleteNotification(Notification notification);
    
    @Query("UPDATE notifications SET is_read = 1 WHERE id = :id")
    void markAsRead(int id);
    
    @Query("UPDATE notifications SET is_read = 1 WHERE user_id = :userId")
    void markAllAsReadForUser(String userId);
}
