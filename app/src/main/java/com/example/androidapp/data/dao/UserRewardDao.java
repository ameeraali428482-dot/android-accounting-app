package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import com.example.androidapp.models.UserReward;

import java.util.List;

@Dao
public interface UserRewardDao extends BaseDao<UserReward> {
    @Query("SELECT * FROM user_rewards WHERE orgId = :orgId")
    LiveData<List<UserReward>> getAllUserRewards(int orgId);

    @Query("SELECT * FROM user_rewards WHERE userId = :userId AND orgId = :orgId")
    LiveData<List<UserReward>> getUserRewardsByUserId(int userId, int orgId);

    @Query("SELECT * FROM user_rewards WHERE id = :id AND orgId = :orgId")
    LiveData<UserReward> getUserRewardById(int id, int orgId);

    @Insert
    long insert(UserReward userReward);

    @Update
    void update(UserReward userReward);

    @Delete
    void delete(UserReward userReward);
}

