package com.example.androidapp.data.dao;

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
