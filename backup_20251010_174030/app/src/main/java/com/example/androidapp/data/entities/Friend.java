package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {"userId", "friendId"}, unique = true),
                @Index(value = "userId"),
                @Index(value = "friendId"),
                @Index(value = "companyId")
        })
public class Friend {
    @PrimaryKey
    private @NonNull String id;
    
    private String userId;
    private String friendId;
    private String companyId;
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
    public Friend(String id, String userId, String friendId, String companyId, String status, Date requestDate, Date acceptedDate, String notes, boolean isFavorite, String nickname, boolean allowNotifications, boolean allowChatMessages, boolean allowViewProfile, boolean allowViewActivity) {
        this.id = id;
        this.userId = userId;
        this.friendId = friendId;
        this.companyId = companyId;
        this.status = status;
        this.requestDate = requestDate;
        this.acceptedDate = acceptedDate;
        this.notes = notes;
        this.isFavorite = isFavorite;
        this.nickname = nickname;
        this.allowNotifications = allowNotifications;
        this.allowChatMessages = allowChatMessages;
        this.allowViewProfile = allowViewProfile;
        this.allowViewActivity = allowViewActivity;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getFriendId() {
        return friendId;
    }
    
    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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

