package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Company;
import java.util.List;

@Dao
public interface CompanyDao {
    @Query("SELECT * FROM companies")
    List<Company> getAllCompanies();
    
    @Query("SELECT * FROM companies WHERE id = :id")
    Company getCompanyById(int id);
    
    @Query("SELECT * FROM companies WHERE name LIKE :name")
    List<Company> getCompaniesByName(String name);
    
    @Insert
    void insertCompany(Company company);
    
    @Update
    void updateCompany(Company company);
    
    @Delete
    void deleteCompany(Company company);
}
