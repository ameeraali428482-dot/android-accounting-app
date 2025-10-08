package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.CompanySettings;

@Dao
public interface CompanySettingsDao {
    @Insert
    void insert(CompanySettings companysettings);

    @Update
    void update(CompanySettings companysettings);

    @Delete
    void delete(CompanySettings companysettings);

    @Query("SELECT * FROM companysettingses")
    List<CompanySettings> getAllCompanySettingss();

    @Query("SELECT * FROM companysettingses WHERE id = :id LIMIT 1")
    CompanySettings getCompanySettingsById(String id);
}
