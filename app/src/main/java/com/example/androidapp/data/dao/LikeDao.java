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

import com.example.androidapp.models.Like;

import java.util.List;

@Dao
public interface LikeDao {
    @Insert
    void insert(Like like);

    @Delete
    void delete(Like like);

    @Query("SELECT * FROM likes WHERE postId = :postId AND companyId = :companyId")
    List<Like> getLikesForPost(String postId, String companyId);

    @Query("SELECT COUNT(*) FROM likes WHERE postId = :postId AND companyId = :companyId")
    int countLikesForPost(String postId, String companyId);

    @Query("SELECT * FROM likes WHERE postId = :postId AND userId = :userId AND companyId = :companyId LIMIT 1")
    Like getLikeByPostAndUser(String postId, String userId, String companyId);
}

