package com.example.androidapp.data.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import java.util.List;

/**
 * Base Data Access Object (DAO) interface for common database operations.
 */
public interface BaseDao<T> {

    @Insert
    long insert(T obj);

    @Update
    void update(T obj);

    @Delete
    void delete(T obj);
}