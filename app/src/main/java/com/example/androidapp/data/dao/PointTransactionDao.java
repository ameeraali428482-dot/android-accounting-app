package com.example.androidapp.data.dao;
import com.example.androidapp.data.entities.Product;
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

import com.example.androidapp.models.PointTransaction;

import java.util.List;

@Dao
public interface PointTransactionDao extends BaseDao<PointTransaction> {
    @Query("SELECT * FROM point_transactions WHERE orgId = :orgId")
    LiveData<List<PointTransaction>> getAllPointTransactions(int orgId);

    @Query("SELECT * FROM point_transactions WHERE id = :id AND orgId = :orgId")
    LiveData<PointTransaction> getPointTransactionById(int id, int orgId);

    @Query("SELECT SUM(points) FROM point_transactions WHERE userId = :userId AND orgId = :orgId")
    LiveData<Integer> getTotalPointsForUser(int userId, int orgId);

    @Insert
    long insert(PointTransaction pointTransaction);

    @Update
    void update(PointTransaction pointTransaction);

    @Delete
    void delete(PointTransaction pointTransaction);
}

