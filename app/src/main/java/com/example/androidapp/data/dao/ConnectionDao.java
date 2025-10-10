package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.Connection;


@Dao
public interface ConnectionDao {
    @Insert
    void insert(Connection connection);
    @Update
    void update(Connection connection);
    @Delete
    void delete(Connection connection);
    @Query("SELECT * FROM connections")
    List<Connection> getAllConnections();
    @Query("SELECT * FROM connections WHERE id = :id LIMIT 1")
    Connection getConnectionById(String id);
    @Query("SELECT * FROM connections WHERE companyId = :companyId")
    LiveData<List<Connection>> getConnectionsByCompanyId(String companyId);
}
