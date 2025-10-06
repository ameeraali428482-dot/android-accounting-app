package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import com.example.androidapp.models.Chat;

import java.util.List;

@Dao
public interface ChatDao extends BaseDao<Chat> {
    @Query("SELECT * FROM chats WHERE orgId = :orgId ORDER BY timestamp DESC")
    LiveData<List<Chat>> getAllChats(int orgId);

    @Query("SELECT * FROM chats WHERE (senderId = :userId1 AND receiverId = :userId2) OR (senderId = :userId2 AND receiverId = :userId1) AND orgId = :orgId ORDER BY timestamp ASC")
    LiveData<List<Chat>> getChatsBetweenUsers(int userId1, int userId2, int orgId);

    @Query("SELECT * FROM chats WHERE receiverId = :userId AND isRead = 0 AND orgId = :orgId")
    LiveData<List<Chat>> getUnreadChats(int userId, int orgId);

    @Query("SELECT COUNT(*) FROM chats WHERE receiverId = :userId AND isRead = 0 AND orgId = :orgId")
    LiveData<Integer> getUnreadChatCount(int userId, int orgId);

    @Query("UPDATE chats SET isRead = 1 WHERE receiverId = :userId AND senderId = :senderId AND orgId = :orgId")
    void markChatsAsRead(int userId, int senderId, int orgId);

    @Insert
    long insert(Chat chat);

    @Update
    void update(Chat chat);

    @Delete
    void delete(Chat chat);
}
