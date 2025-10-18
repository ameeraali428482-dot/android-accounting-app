package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
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
    LiveData<List<Notification>> getAllNotifications();

    @Query("SELECT * FROM notifications WHERE id = :id LIMIT 1")
    LiveData<Notification> getNotificationById(String id);

    @Query("SELECT * FROM notifications WHERE id = :id LIMIT 1")
    Notification getNotificationByIdSync(String id);
}
