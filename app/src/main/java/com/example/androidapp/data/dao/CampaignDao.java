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

import com.example.androidapp.models.Campaign;

import java.util.List;

@Dao
public interface CampaignDao extends BaseDao<Campaign> {
    @Query("SELECT * FROM campaigns WHERE companyId = :companyId ORDER BY startDate DESC")
    LiveData<List<Campaign>> getAllCampaigns(int companyId);

    @Query("SELECT * FROM campaigns WHERE id = :campaignId AND companyId = :companyId")
    LiveData<Campaign> getCampaignById(int campaignId, int companyId);

    @Insert
    long insert(Campaign campaign);

    @Update
    void update(Campaign campaign);

    @Delete
    void delete(Campaign campaign);
}
