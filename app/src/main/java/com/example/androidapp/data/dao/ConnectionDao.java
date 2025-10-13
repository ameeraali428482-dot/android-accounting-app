package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Connection;

import java.util.List;

@Dao
public interface ConnectionDao {
    @Insert
    void insert(Connection connection);

    @Update
    void update(Connection connection);

    @Delete
    void delete(Connection connection);

    @Query("SELECT * FROM connections WHERE companyId = :companyId ORDER BY name ASC")
    LiveData<List<Connection>> getAllConnections(String companyId);

    @Query("SELECT * FROM connections WHERE id = :connectionId AND companyId = :companyId")
    LiveData<Connection> getConnectionById(String connectionId, String companyId);

    @Query("SELECT * FROM connections WHERE id = :connectionId")
    Connection getConnectionByIdSync(String connectionId);

    @Query("DELETE FROM connections WHERE companyId = :companyId")
    void deleteAllConnections(String companyId);
}
