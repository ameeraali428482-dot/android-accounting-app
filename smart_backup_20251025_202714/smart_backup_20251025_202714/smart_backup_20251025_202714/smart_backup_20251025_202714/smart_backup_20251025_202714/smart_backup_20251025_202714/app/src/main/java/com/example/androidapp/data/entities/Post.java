package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;



@Entity(tableName = "posts",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "userId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "userId"), @Index(value = "companyId")})
public class Post {
    @PrimaryKey
    public @NonNull String id;
    public @NonNull String userId;
    public @NonNull String companyId;
    public String content;
    public String imageUrl;
    public String videoUrl;
    public String timestamp;

    public Post(String id, String userId, String companyId, String content, String imageUrl, String videoUrl, String timestamp) {
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.timestamp = timestamp;
    }

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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

