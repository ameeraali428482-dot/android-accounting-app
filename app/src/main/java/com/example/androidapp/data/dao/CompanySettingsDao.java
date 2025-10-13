package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.CompanySettings;
import java.util.List;

@Dao
public interface CompanySettingsDao {
    @Insert
    void insert(CompanySettings companySettings);

    @Update
    void update(CompanySettings companySettings);

    @Delete
    void delete(CompanySettings companySettings);

    @Query("SELECT * FROM companysettings WHERE companyId = :companyId LIMIT 1")
    CompanySettings getCompanySettingsByCompanyId(String companyId);

    @Query("SELECT * FROM companysettings WHERE companyId = :companyId LIMIT 1")
    LiveData<CompanySettings> getCompanySettingsByCompanyIdLive(String companyId);

    @Query("SELECT * FROM companysettings")
    LiveData<List<CompanySettings>> getAllCompanySettings();
}
