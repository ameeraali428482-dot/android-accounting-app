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

    @Query("SELECT * FROM company_settings")
    List<CompanySettings> getAllCompanySettingss();

    @Query("SELECT * FROM company_settings WHERE id = :id LIMIT 1")
    CompanySettings getCompanySettingsById(String id);
    @Query("SELECT * FROM company_settings WHERE companyId = :companyId LIMIT 1")
    LiveData<CompanySettings> getSettingsByCompanyId(String companyId);
}
