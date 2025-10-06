package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import com.example.androidapp.models.Reward;

import java.util.List;

@Dao
public interface RewardDao extends BaseDao<Reward> {
    @Query("SELECT * FROM rewards WHERE orgId = :orgId")
    LiveData<List<Reward>> getAllRewards(int orgId);

    @Query("SELECT * FROM rewards WHERE id = :id AND orgId = :orgId")
    LiveData<Reward> getRewardById(int id, int orgId);

    @Insert
    long insert(Reward reward);

    @Update
    void update(Reward reward);

    @Delete
    void delete(Reward reward);
}

