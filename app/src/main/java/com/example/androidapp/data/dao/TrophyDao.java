package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.models.Trophy;

import java.util.List;

@Dao
public interface TrophyDao extends BaseDao<Trophy> {
    @Query("SELECT * FROM trophies WHERE companyId = :companyId ORDER BY pointsRequired ASC")
    LiveData<List<Trophy>> getAllTrophies(int companyId);

    @Query("SELECT * FROM trophies WHERE id = :trophyId AND companyId = :companyId")
    LiveData<Trophy> getTrophyById(int trophyId, int companyId);

    @Insert
    long insert(Trophy trophy);

    @Update
    void update(Trophy trophy);

    @Delete
    void delete(Trophy trophy);
}
