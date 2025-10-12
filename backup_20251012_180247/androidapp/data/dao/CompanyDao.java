package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.Company;




@Dao
public interface CompanyDao {
    @Insert
    void insert(Company company);

    @Update
    void update(Company company);

    @Delete
    void delete(Company company);

    @Query("SELECT * FROM companies")
        List<Company> getAllCompanies();

    @Query("SELECT * FROM companies WHERE id = :id LIMIT 1")
    Company getCompanyById(String id);
}
