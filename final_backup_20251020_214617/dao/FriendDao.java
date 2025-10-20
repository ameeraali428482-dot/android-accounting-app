package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Friend;
import java.util.List;

@Dao
public interface FriendDao {
    @Query("SELECT * FROM friends")
    List<Friend> getAllFriends();
    
    @Query("SELECT * FROM friends WHERE id = :id")
    Friend getFriendById(int id);
    
    @Query("SELECT * FROM friends WHERE user_id = :userId")
    List<Friend> getFriendsByUserId(int userId);
    
    @Query("SELECT * FROM friends WHERE friend_id = :friendId")
    List<Friend> getFriendsByFriendId(int friendId);
    
    @Query("SELECT f.* FROM friends f WHERE f.user_id = :userId AND f.friend_id = :friendId")
    Friend getFriendship(int userId, int friendId);
    
    @Insert
    void insertFriend(Friend friend);
    
    @Update
    void updateFriend(Friend friend);
    
    @Delete
    void deleteFriend(Friend friend);
    
    @Query("DELETE FROM friends WHERE user_id = :userId AND friend_id = :friendId")
    void deleteFriendship(int userId, int friendId);
}
