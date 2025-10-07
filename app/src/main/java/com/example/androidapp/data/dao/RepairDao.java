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

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.models.Repair;

import java.util.List;

@Dao
public interface RepairDao extends BaseDao<Repair> {
    @Query("SELECT * FROM repairs WHERE companyId = :companyId ORDER BY requestDate DESC")
    LiveData<List<Repair>> getAllRepairs(int companyId);

    @Query("SELECT * FROM repairs WHERE id = :repairId AND companyId = :companyId")
    LiveData<Repair> getRepairById(int repairId, int companyId);

    @Insert
    long insert(Repair repair);

    @Update
    void update(Repair repair);

    @Delete
    void delete(Repair repair);
}
