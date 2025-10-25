package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.entities.Campaign;
import java.util.List;





@Dao
public interface CampaignDao extends BaseDao<Campaign> {
    @Query("SELECT * FROM campaigns WHERE companyId = :companyId ORDER BY createdAt DESC")
    LiveData<List<Campaign>> getAllCampaigns(String companyId);

    @Query("SELECT * FROM campaigns WHERE id = :campaignId AND companyId = :companyId")
    LiveData<Campaign> getCampaignById(String campaignId, String companyId);
}
