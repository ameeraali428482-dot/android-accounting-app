package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Repair;

import java.util.List;

@Dao
public interface RepairDao extends BaseDao<Repair> {
    @Query("SELECT * FROM repairs WHERE companyId = :companyId ORDER BY startDate DESC")
    LiveData<List<Repair>> getAllRepairs(String companyId);

    @Query("SELECT * FROM repairs WHERE id = :repairId AND companyId = :companyId")
    LiveData<Repair> getRepairById(String repairId, String companyId);
}
