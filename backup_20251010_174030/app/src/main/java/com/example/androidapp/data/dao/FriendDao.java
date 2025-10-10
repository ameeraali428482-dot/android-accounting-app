package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Friend;
import com.example.androidapp.data.entities.User;

import java.util.List;

@Dao
public interface FriendDao {
    
    @Insert
    long insert(Friend friend);
    
    @Update
    int update(Friend friend);
    
    @Delete
    int delete(Friend friend);
    
    @Query("SELECT * FROM friends WHERE id = :id")
    Friend getFriendById(String id);
    
    @Query("SELECT * FROM friends WHERE userId = :userId AND status = :status ORDER BY acceptedDate DESC")
    LiveData<List<Friend>> getFriendsByStatus(String userId, String status);
    
    @Query("SELECT * FROM friends WHERE (userId = :userId AND friendId = :friendId) OR (userId = :friendId AND friendId = :userId)")
    Friend getFriendship(String userId, String friendId);
    
    @Query("SELECT u.* FROM Users u INNER JOIN friends f ON u.id = f.friendId WHERE f.userId = :userId AND f.status = 'ACCEPTED' AND u.isOnline = 1")
    LiveData<List<User>> getOnlineFriends(String userId);
}
