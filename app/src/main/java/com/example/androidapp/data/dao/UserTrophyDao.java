package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.androidapp.models.UserTrophy;
import com.example.androidapp.models.Trophy;

import java.util.List;

@Dao
public interface UserTrophyDao {
    @Insert
    void insert(UserTrophy userTrophy);

    @Query("DELETE FROM user_trophies WHERE userId = :userId AND trophyId = :trophyId")
    void delete(int userId, int trophyId);

    @Query("SELECT T.* FROM trophies T INNER JOIN user_trophies UT ON T.id = UT.trophyId WHERE UT.userId = :userId")
    LiveData<List<Trophy>> getTrophiesForUser(int userId);

    @Query("SELECT EXISTS(SELECT 1 FROM user_trophies WHERE userId = :userId AND trophyId = :trophyId LIMIT 1)")
    LiveData<Boolean> hasUserAchievedTrophy(int userId, int trophyId);
}
