package com.example.androidapp.data.entities;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;


import java.util.Date;

@Entity(tableName = "friends",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "friendId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {"userId", "friendId"}, unique = true),
                @Index(value = "userId"),
                @Index(value = "friendId")
        })
public class Friend {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int userId;
    private int friendId;
    private String status; // PENDING, ACCEPTED, BLOCKED
    private Date requestDate;
    private Date acceptedDate;
    private String notes;
    private boolean isFavorite;
    private String nickname;
    private boolean allowNotifications;
    private boolean allowChatMessages;
    private boolean allowViewProfile;
    private boolean allowViewActivity;
    
    // Constructors
    public Friend() {}
    
    public Friend(int userId, int friendId, String status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
        this.requestDate = new Date();
        this.allowNotifications = true;
        this.allowChatMessages = true;
        this.allowViewProfile = true;
        this.allowViewActivity = false;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getFriendId() {
        return friendId;
    }
    
    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getRequestDate() {
        return requestDate;
    }
    
    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }
    
    public Date getAcceptedDate() {
        return acceptedDate;
    }
    
    public void setAcceptedDate(Date acceptedDate) {
        this.acceptedDate = acceptedDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public boolean isFavorite() {
        return isFavorite;
    }
    
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public boolean isAllowNotifications() {
        return allowNotifications;
    }
    
    public void setAllowNotifications(boolean allowNotifications) {
        this.allowNotifications = allowNotifications;
    }
    
    public boolean isAllowChatMessages() {
        return allowChatMessages;
    }
    
    public void setAllowChatMessages(boolean allowChatMessages) {
        this.allowChatMessages = allowChatMessages;
    }
    
    public boolean isAllowViewProfile() {
        return allowViewProfile;
    }
    
    public void setAllowViewProfile(boolean allowViewProfile) {
        this.allowViewProfile = allowViewProfile;
    }
    
    public boolean isAllowViewActivity() {
        return allowViewActivity;
    }
    
    public void setAllowViewActivity(boolean allowViewActivity) {
        this.allowViewActivity = allowViewActivity;
    }
    
    // Status constants
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_BLOCKED = "BLOCKED";
    public static final String STATUS_REJECTED = "REJECTED";
}
