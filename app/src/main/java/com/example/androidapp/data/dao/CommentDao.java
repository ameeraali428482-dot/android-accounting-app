package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.androidapp.models.Comment;

import java.util.List;

@Dao
public interface CommentDao {
    @Insert
    void insert(Comment comment);

    @Update
    void update(Comment comment);

    @Delete
    void delete(Comment comment);

    @Query("SELECT * FROM comments WHERE postId = :postId AND companyId = :companyId ORDER BY timestamp ASC")
    List<Comment> getCommentsForPost(String postId, String companyId);

    @Query("SELECT * FROM comments WHERE id = :id AND companyId = :companyId LIMIT 1")
    Comment getCommentById(String id, String companyId);
}

