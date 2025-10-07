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

import com.example.androidapp.models.Share;

import java.util.List;

@Dao
public interface ShareDao {
    @Insert
    void insert(Share share);

    @Delete
    void delete(Share share);

    @Query("SELECT * FROM shares WHERE postId = :postId AND companyId = :companyId")
    List<Share> getSharesForPost(String postId, String companyId);

    @Query("SELECT COUNT(*) FROM shares WHERE postId = :postId AND companyId = :companyId")
    int countSharesForPost(String postId, String companyId);

    @Query("SELECT * FROM shares WHERE postId = :postId AND userId = :userId AND companyId = :companyId LIMIT 1")
    Share getShareByPostAndUser(String postId, String userId, String companyId);
}

