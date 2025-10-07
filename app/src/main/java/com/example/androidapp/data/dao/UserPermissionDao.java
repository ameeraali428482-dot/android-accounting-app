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

import com.example.androidapp.models.UserPermission;

@Dao
public interface UserPermissionDao {
    @Insert
    void insert(UserPermission userPermission);

    @Query("DELETE FROM user_permissions WHERE userId = :userId AND permissionId = :permissionId")
    void delete(int userId, int permissionId);

    @Query("DELETE FROM user_permissions WHERE userId = :userId")
    void deleteAllUserPermissions(int userId);

    @Query("SELECT EXISTS(SELECT 1 FROM user_permissions WHERE userId = :userId AND permissionId = :permissionId LIMIT 1)")
    boolean hasPermission(int userId, int permissionId);
}
