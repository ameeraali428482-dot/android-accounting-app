package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.entities.Chat;
import java.util.List;





@Dao
public interface ChatDao extends BaseDao<Chat> {
    @Query("SELECT * FROM chats WHERE companyId = :companyId ORDER BY createdAt DESC")
    LiveData<List<Chat>> getAllChats(String companyId);

    @Query("SELECT * FROM chats WHERE (userId = :userId1 AND toUserId = :userId2) OR (userId = :userId2 AND toUserId = :userId1) AND companyId = :companyId ORDER BY createdAt ASC")
    LiveData<List<Chat>> getChatsBetweenUsers(String userId1, String userId2, String companyId);

    @Query("SELECT * FROM chats WHERE toUserId = :userId AND isRead = 0 AND companyId = :companyId")
    LiveData<List<Chat>> getUnreadChats(String userId, String companyId);

    @Query("SELECT COUNT(*) FROM chats WHERE toUserId = :userId AND isRead = 0 AND companyId = :companyId")
    LiveData<Integer> getUnreadChatCount(String userId, String companyId);

    @Query("UPDATE chats SET isRead = 1 WHERE toUserId = :userId AND userId = :senderId AND companyId = :companyId")
    void markChatsAsRead(String userId, String senderId, String companyId);
}
