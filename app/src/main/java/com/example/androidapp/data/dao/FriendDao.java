package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.Friend;
import com.example.androidapp.data.entities.User;

import java.util.List;

@Dao
public interface FriendDao extends BaseDao<Friend> {
    
    @Query("SELECT * FROM friends WHERE id = :id")
    Friend getById(String id);

    @Query("SELECT * FROM friends WHERE user_id = :userId OR friend_id = :userId")
    List<Friend> getByUserId(String userId);

    @Query("SELECT * FROM friends WHERE user_id = :userId AND friend_id = :friendId")
    Friend getFriendship(String userId, String friendId);

    @Query("SELECT u.* FROM users u INNER JOIN friends f ON u.id = f.friend_id WHERE f.user_id = :userId AND f.status = 'ACCEPTED'")
    LiveData<List<User>> getFriends(String userId);

    @Query("SELECT u.* FROM users u INNER JOIN friends f ON u.id = f.friend_id WHERE f.user_id = :userId AND f.status = 'PENDING'")
    LiveData<List<User>> getPendingFriends(String userId);

    @Query("SELECT u.* FROM users u INNER JOIN friends f ON u.id = f.user_id WHERE f.friend_id = :userId AND f.status = 'PENDING'")
    LiveData<List<User>> getFriendRequests(String userId);

    @Query("SELECT u.* FROM users u INNER JOIN friends f ON u.id = f.friend_id WHERE f.user_id = :userId AND f.status = 'ACCEPTED'")
    LiveData<List<User>> getOnlineFriends(String userId);

    @Query("UPDATE friends SET status = :status WHERE user_id = :userId AND friend_id = :friendId")
    void updateFriendshipStatus(String userId, String friendId, String status);

    @Query("DELETE FROM friends WHERE user_id = :userId AND friend_id = :friendId")
    void removeFriend(String userId, String friendId);

    @Query("SELECT COUNT(*) FROM friends WHERE user_id = :userId AND status = 'ACCEPTED'")
    int getFriendCount(String userId);
}
