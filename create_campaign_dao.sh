#!/bin/bash

echo "Creating missing file: CampaignDao.java"

# Define the file path
DAO_FILE="app/src/main/java/com/example/androidapp/data/dao/CampaignDao.java"

# Create the directory if it doesn't exist
mkdir -p "$(dirname "$DAO_FILE")"

# Create the CampaignDao.java file with the correct content
cat > "$DAO_FILE" << 'EOP'
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Campaign;

import java.util.List;

@Dao
public interface CampaignDao {
    @Insert
    void insert(Campaign campaign);

    @Update
    void update(Campaign campaign);

    @Delete
    void delete(Campaign campaign);

    @Query("SELECT * FROM campaigns WHERE companyId = :companyId ORDER BY createdAt DESC")
    LiveData<List<Campaign>> getAllCampaigns(String companyId);

    @Query("SELECT * FROM campaigns WHERE id = :campaignId AND companyId = :companyId")
    LiveData<Campaign> getCampaignById(String campaignId, String companyId);
}
EOP

echo "File CampaignDao.java created successfully."
