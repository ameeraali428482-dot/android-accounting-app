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
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.androidapp.models.Warehouse;

import java.util.List;

@Dao
public interface WarehouseDao {
    @Insert
    void insert(Warehouse warehouse);

    @Update
    void update(Warehouse warehouse);

    @Delete
    void delete(Warehouse warehouse);

    @Query("SELECT * FROM warehouses WHERE companyId = :companyId")
    List<Warehouse> getAllWarehouses(String companyId);

    @Query("SELECT * FROM warehouses WHERE id = :id AND companyId = :companyId LIMIT 1")
    Warehouse getWarehouseById(String id, String companyId);

    @Query("SELECT COUNT(*) FROM warehouses WHERE name = :name AND companyId = :companyId")
    int countWarehouseByName(String name, String companyId);
}

