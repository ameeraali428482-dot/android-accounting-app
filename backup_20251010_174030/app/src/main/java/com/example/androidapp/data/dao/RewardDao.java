package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.entities.Reward;

import java.util.List;

@Dao
public interface RewardDao extends BaseDao<Reward> {
    @Query("SELECT * FROM rewards WHERE companyId = :companyId")
    LiveData<List<Reward>> getAllRewards(String companyId);

    @Query("SELECT * FROM rewards WHERE id = :id AND companyId = :companyId")
    LiveData<Reward> getRewardById(String id, String companyId);
}
