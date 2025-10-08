package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.Notification;

@Dao
public interface NotificationDao {
    @Insert
    void insert(Notification notification);

    @Update
    void update(Notification notification);

    @Delete
    void delete(Notification notification);

    @Query("SELECT * FROM notifications")
    List<Notification> getAllNotifications();

    @Query("SELECT * FROM notifications WHERE id = :id LIMIT 1")
    Notification getNotificationById(String id);
}
