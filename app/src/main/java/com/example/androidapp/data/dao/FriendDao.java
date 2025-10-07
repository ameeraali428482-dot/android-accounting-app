package com.example.androidapp.data.dao;
import com.example.androidapp.data.entities.Product;
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

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.models.Friend;

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
    Friend getFriendById(int id);
    
    @Query("SELECT * FROM friends WHERE id = :id")
    LiveData<Friend> getFriendByIdLive(int id);
    
    @Query("SELECT * FROM friends WHERE userId = :userId AND status = :status ORDER BY acceptedDate DESC")
    LiveData<List<Friend>> getFriendsByStatus(int userId, String status);
    
    @Query("SELECT * FROM friends WHERE userId = :userId AND status = 'ACCEPTED' ORDER BY acceptedDate DESC")
    LiveData<List<Friend>> getAcceptedFriends(int userId);
    
    @Query("SELECT * FROM friends WHERE userId = :userId AND status = 'PENDING' ORDER BY requestDate DESC")
    LiveData<List<Friend>> getPendingFriendRequests(int userId);
    
    @Query("SELECT * FROM friends WHERE friendId = :userId AND status = 'PENDING' ORDER BY requestDate DESC")
    LiveData<List<Friend>> getIncomingFriendRequests(int userId);
    
    @Query("SELECT * FROM friends WHERE userId = :userId AND status = 'BLOCKED' ORDER BY requestDate DESC")
    LiveData<List<Friend>> getBlockedFriends(int userId);
    
    @Query("SELECT * FROM friends WHERE userId = :userId AND isFavorite = 1 AND status = 'ACCEPTED' ORDER BY acceptedDate DESC")
    LiveData<List<Friend>> getFavoriteFriends(int userId);
    
    @Query("SELECT * FROM friends WHERE (userId = :userId AND friendId = :friendId) OR (userId = :friendId AND friendId = :userId)")
    Friend getFriendship(int userId, int friendId);
    
    @Query("SELECT * FROM friends WHERE (userId = :userId AND friendId = :friendId) OR (userId = :friendId AND friendId = :userId)")
    LiveData<Friend> getFriendshipLive(int userId, int friendId);
    
    @Query("SELECT COUNT(*) FROM friends WHERE userId = :userId AND status = 'ACCEPTED'")
    LiveData<Integer> getFriendsCount(int userId);
    
    @Query("SELECT COUNT(*) FROM friends WHERE friendId = :userId AND status = 'PENDING'")
    LiveData<Integer> getPendingRequestsCount(int userId);
    
    @Query("UPDATE friends SET status = :status, acceptedDate = datetime('now') WHERE id = :friendId")
    int updateFriendStatus(int friendId, String status);
    
    @Query("UPDATE friends SET isFavorite = :isFavorite WHERE id = :friendId")
    int updateFavoriteStatus(int friendId, boolean isFavorite);
    
    @Query("UPDATE friends SET nickname = :nickname WHERE id = :friendId")
    int updateNickname(int friendId, String nickname);
    
    @Query("UPDATE friends SET allowNotifications = :allow WHERE id = :friendId")
    int updateNotificationPermission(int friendId, boolean allow);
    
    @Query("UPDATE friends SET allowChatMessages = :allow WHERE id = :friendId")
    int updateChatPermission(int friendId, boolean allow);
    
    @Query("UPDATE friends SET allowViewProfile = :allow WHERE id = :friendId")
    int updateProfileViewPermission(int friendId, boolean allow);
    
    @Query("UPDATE friends SET allowViewActivity = :allow WHERE id = :friendId")
    int updateActivityViewPermission(int friendId, boolean allow);
    
    @Query("DELETE FROM friends WHERE (userId = :userId AND friendId = :friendId) OR (userId = :friendId AND friendId = :userId)")
    int deleteFriendship(int userId, int friendId);
    
    @Query("DELETE FROM friends WHERE userId = :userId AND status = 'BLOCKED'")
    int deleteAllBlockedFriends(int userId);
    
    @Query("SELECT * FROM friends WHERE userId = :userId AND (nickname LIKE '%' || :searchQuery || '%' OR notes LIKE '%' || :searchQuery || '%') AND status = 'ACCEPTED'")
    LiveData<List<Friend>> searchFriends(int userId, String searchQuery);
    
    @Query("SELECT * FROM friends WHERE userId = :userId AND status = 'ACCEPTED' ORDER BY " +
           "CASE WHEN :sortBy = 'name' THEN nickname END ASC, " +
           "CASE WHEN :sortBy = 'date' THEN acceptedDate END DESC, " +
           "CASE WHEN :sortBy = 'favorite' THEN isFavorite END DESC")
    LiveData<List<Friend>> getFriendsSorted(int userId, String sortBy);
    
    @Query("SELECT DISTINCT f.* FROM friends f " +
           "INNER JOIN users u ON f.friendId = u.id " +
           "WHERE f.userId = :userId AND f.status = 'ACCEPTED' " +
           "AND (u.isOnline = 1 OR u.lastSeenDate > datetime('now', '-5 minutes'))")
    LiveData<List<Friend>> getOnlineFriends(int userId);
    
    @Query("SELECT * FROM friends WHERE userId = :userId AND status = 'ACCEPTED' " +
           "AND allowChatMessages = 1 ORDER BY acceptedDate DESC")
    LiveData<List<Friend>> getFriendsAllowingChat(int userId);
    
    @Query("SELECT * FROM friends WHERE userId = :userId AND status = 'ACCEPTED' " +
           "AND allowNotifications = 1 ORDER BY acceptedDate DESC")
    LiveData<List<Friend>> getFriendsAllowingNotifications(int userId);
}
