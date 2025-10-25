package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.entities.UserReward;
import java.util.List;





@Dao
public interface UserRewardDao extends BaseDao<UserReward> {
    @Query("SELECT * FROM user_rewards WHERE companyId = :companyId")
    LiveData<List<UserReward>> getAllUserRewards(String companyId);

    @Query("SELECT * FROM user_rewards WHERE userId = :userId AND companyId = :companyId")
    LiveData<List<UserReward>> getUserRewardsByUserId(String userId, String companyId);

    @Query("SELECT * FROM user_rewards WHERE id = :id AND companyId = :companyId")
    LiveData<UserReward> getUserRewardById(String id, String companyId);
}
