package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.androidapp.models.Like;

import java.util.List;

@Dao
public interface LikeDao {
    @Insert
    void insert(Like like);

    @Delete
    void delete(Like like);

    @Query("SELECT * FROM likes WHERE postId = :postId AND companyId = :companyId")
    List<Like> getLikesForPost(String postId, String companyId);

    @Query("SELECT COUNT(*) FROM likes WHERE postId = :postId AND companyId = :companyId")
    int countLikesForPost(String postId, String companyId);

    @Query("SELECT * FROM likes WHERE postId = :postId AND userId = :userId AND companyId = :companyId LIMIT 1")
    Like getLikeByPostAndUser(String postId, String userId, String companyId);
}

