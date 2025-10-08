package com.example.androidapp.data.dao;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.data.entities.Voucher;
import com.example.androidapp.data.entities.Company;
import com.example.androidapp.data.entities.Doctor;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.Supplier;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.data.entities.Trophy;
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.data.entities.Repair;
import com.example.androidapp.data.entities.Chat;
import com.example.androidapp.data.entities.UserReward;
import com.example.androidapp.data.entities.Reward;
import com.example.androidapp.data.entities.PointTransaction;
import com.example.androidapp.data.entities.Campaign;

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

