package com.example.androidapp.data.dao;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.data.entities.Voucher;
import com.example.androidapp.data.entities.Company;
import com.example.androidapp.data.entities.Doctor;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.Supplier;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.data.entities.Trophy;
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.data.entities.Repair;
import com.example.androidapp.data.entities.Chat;
import com.example.androidapp.data.entities.UserReward;
import com.example.androidapp.data.entities.Reward;
import com.example.androidapp.data.entities.PointTransaction;
import com.example.androidapp.data.entities.Campaign;

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
