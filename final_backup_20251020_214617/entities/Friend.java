package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "friends",
        foreignKeys = {
            @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "friend_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("user_id"), @Index("friend_id")})
public class Friend {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "user_id")
    private int userId;
    
    @ColumnInfo(name = "friend_id")
    private int friendId;
    
    @ColumnInfo(name = "created_at")
    private long createdAt;

    // Constructor
    public Friend(int userId, int friendId, long createdAt) {
        this.userId = userId;
        this.friendId = friendId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getFriendId() { return friendId; }
    public void setFriendId(int friendId) { this.friendId = friendId; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
