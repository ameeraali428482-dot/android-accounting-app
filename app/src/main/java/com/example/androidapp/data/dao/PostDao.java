package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.androidapp.models.Post;

import java.util.List;

@Dao
public interface PostDao {
    @Insert
    void insert(Post post);

    @Update
    void update(Post post);

    @Delete
    void delete(Post post);

    @Query("SELECT * FROM posts WHERE companyId = :companyId ORDER BY timestamp DESC")
    List<Post> getAllPosts(String companyId);

    @Query("SELECT * FROM posts WHERE id = :id AND companyId = :companyId LIMIT 1")
    Post getPostById(String id, String companyId);
}

