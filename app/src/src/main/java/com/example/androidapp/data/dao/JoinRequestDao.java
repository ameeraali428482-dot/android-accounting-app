package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.JoinRequest;

@Dao
public interface JoinRequestDao {
    @Insert
    void insert(JoinRequest joinrequest);

    @Update
    void update(JoinRequest joinrequest);

    @Delete
    void delete(JoinRequest joinrequest);

    @Query("SELECT * FROM joinrequests")
    List<JoinRequest> getAllJoinRequests();

    @Query("SELECT * FROM joinrequests WHERE id = :id LIMIT 1")
    JoinRequest getJoinRequestById(String id);
}
