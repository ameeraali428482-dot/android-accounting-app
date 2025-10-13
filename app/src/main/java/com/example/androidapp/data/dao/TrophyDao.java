package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Trophy;
import java.util.List;

@Dao
public interface TrophyDao {
    @Insert
    void insert(Trophy trophy);

    @Update
    void update(Trophy trophy);

    @Delete
    void delete(Trophy trophy);

    @Query("SELECT * FROM trophies WHERE companyId = :companyId")
    LiveData<List<Trophy>> getAllTrophies(String companyId);

    @Query("SELECT * FROM trophies WHERE id = :id AND companyId = :companyId LIMIT 1")
    LiveData<Trophy> getTrophyById(String id, String companyId);

    @Query("SELECT * FROM trophies WHERE id = :id AND companyId = :companyId LIMIT 1")
    Trophy getTrophyByIdSync(String id, String companyId);
}
