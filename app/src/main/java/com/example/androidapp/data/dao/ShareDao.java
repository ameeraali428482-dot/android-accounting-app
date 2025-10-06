package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.androidapp.models.Share;

import java.util.List;

@Dao
public interface ShareDao {
    @Insert
    void insert(Share share);

    @Delete
    void delete(Share share);

    @Query("SELECT * FROM shares WHERE postId = :postId AND companyId = :companyId")
    List<Share> getSharesForPost(String postId, String companyId);

    @Query("SELECT COUNT(*) FROM shares WHERE postId = :postId AND companyId = :companyId")
    int countSharesForPost(String postId, String companyId);

    @Query("SELECT * FROM shares WHERE postId = :postId AND userId = :userId AND companyId = :companyId LIMIT 1")
    Share getShareByPostAndUser(String postId, String userId, String companyId);
}

