package com.example.androidapp.data.dao;

import androidx.room.*;
import java.util.List;

/**
 * Base DAO interface providing common CRUD operations
 * @param <T> Entity type
 */
public interface BaseDao<T> {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(T entity);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<T> entities);
    
    @Update
    int update(T entity);
    
    @Delete
    int delete(T entity);
    
    @Delete
    int deleteAll(List<T> entities);
}
