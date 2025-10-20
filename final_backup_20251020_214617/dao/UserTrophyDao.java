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
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.androidapp.data.entities.UserTrophy;
import com.example.androidapp.data.entities.Trophy;
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
