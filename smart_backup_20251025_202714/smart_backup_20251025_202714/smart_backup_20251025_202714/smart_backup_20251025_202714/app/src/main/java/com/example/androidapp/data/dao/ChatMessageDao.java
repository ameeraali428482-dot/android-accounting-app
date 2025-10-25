package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.ChatMessage;
import java.util.List;

@Dao
public interface ChatMessageDao {
    
    @Query("SELECT * FROM chat_messages WHERE chatId = :chatId AND companyId = :companyId ORDER BY timestamp ASC")
    LiveData<List<ChatMessage>> getMessagesByChat(String chatId, String companyId);

    @Query("SELECT * FROM chat_messages WHERE id = :messageId AND companyId = :companyId")
    LiveData<ChatMessage> getMessageById(String messageId, String companyId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChatMessage message);

    @Update
    void update(ChatMessage message);

    @Delete
    void delete(ChatMessage message);

    @Query("DELETE FROM chat_messages WHERE chatId = :chatId AND companyId = :companyId")
    void deleteMessagesByChat(String chatId, String companyId);
}
