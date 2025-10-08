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
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Trophy;

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
