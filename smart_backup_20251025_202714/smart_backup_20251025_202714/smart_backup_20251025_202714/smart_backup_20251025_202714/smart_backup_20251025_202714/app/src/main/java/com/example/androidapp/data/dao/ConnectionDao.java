package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.Connection;
import java.util.List;

@Dao
public interface ConnectionDao {
    
    @Query("SELECT * FROM connections WHERE companyId = :companyId")
    LiveData<List<Connection>> getAllConnections(String companyId);

    @Query("SELECT * FROM connections WHERE id = :connectionId AND companyId = :companyId")
    LiveData<Connection> getConnectionById(String connectionId, String companyId);

    @Query("SELECT * FROM connections WHERE id = :connectionId")
    Connection getConnectionByIdSync(String connectionId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Connection connection);

    @Update
    void update(Connection connection);

    @Delete
    void delete(Connection connection);

    @Query("DELETE FROM connections WHERE companyId = :companyId")
    void deleteAllConnections(String companyId);
}
