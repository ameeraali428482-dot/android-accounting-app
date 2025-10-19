package com.example.androidapp.data.dao;

import androidx.room.*;
import java.util.List;

public interface BaseDao<T> {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(T entity);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<T> entities);
    
    @Update
    void update(T entity);
    
    @Delete
    void delete(T entity);
}
