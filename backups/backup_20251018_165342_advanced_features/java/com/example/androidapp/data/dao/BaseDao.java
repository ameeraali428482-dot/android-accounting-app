package com.example.androidapp.data.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import java.util.List;

public interface BaseDao<T> {
    @Insert
    void insert(T entity);

    @Insert
    void insertAll(List<T> entities);

    @Update
    void update(T entity);

    @Delete
    void delete(T entity);
}
