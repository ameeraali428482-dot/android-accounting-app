package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.BalanceSheet;
import java.util.List;

@Dao
public interface BalanceSheetDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BalanceSheet balanceSheet);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BalanceSheet> balanceSheets);
    
    @Update
    void update(BalanceSheet balanceSheet);
    
    @Delete
    void delete(BalanceSheet balanceSheet);
    
    @Query("SELECT * FROM balance_sheets WHERE id = :id")
    BalanceSheet getBalanceSheetById(String id);
    
    @Query("SELECT * FROM balance_sheets WHERE id = :id")
    LiveData<BalanceSheet> getBalanceSheetByIdLive(String id);
    
    @Query("SELECT * FROM balance_sheets WHERE companyId = :companyId ORDER BY reportDate DESC")
    LiveData<List<BalanceSheet>> getBalanceSheetsByCompany(String companyId);
    
    @Query("SELECT * FROM balance_sheets WHERE companyId = :companyId ORDER BY reportDate DESC")
    List<BalanceSheet> getBalanceSheetsByCompanySync(String companyId);
    
    @Query("SELECT * FROM balance_sheets WHERE companyId = :companyId AND reportDate = :reportDate")
    BalanceSheet getBalanceSheetByDate(String companyId, String reportDate);
    
    @Query("SELECT * FROM balance_sheets WHERE companyId = :companyId AND reportDate BETWEEN :startDate AND :endDate ORDER BY reportDate DESC")
    List<BalanceSheet> getBalanceSheetsByDateRange(String companyId, String startDate, String endDate);
    
    @Query("SELECT * FROM balance_sheets WHERE companyId = :companyId ORDER BY reportDate DESC LIMIT 1")
    BalanceSheet getLatestBalanceSheet(String companyId);
    
    @Query("SELECT * FROM balance_sheets WHERE companyId = :companyId ORDER BY reportDate DESC LIMIT 1")
    LiveData<BalanceSheet> getLatestBalanceSheetLive(String companyId);
    
    @Query("DELETE FROM balance_sheets WHERE companyId = :companyId")
    void deleteAllCompanyBalanceSheets(String companyId);
    
    @Query("DELETE FROM balance_sheets WHERE id = :id")
    void deleteById(String id);
    
    @Query("SELECT COUNT(*) FROM balance_sheets WHERE companyId = :companyId")
    int getBalanceSheetsCount(String companyId);
    
    @Query("SELECT SUM(totalAssets) as totalAssets FROM balance_sheets WHERE companyId = :companyId AND reportDate BETWEEN :startDate AND :endDate")
    double getTotalAssetsForPeriod(String companyId, String startDate, String endDate);
    
    @Query("SELECT SUM(totalLiabilities) as totalLiabilities FROM balance_sheets WHERE companyId = :companyId AND reportDate BETWEEN :startDate AND :endDate")
    double getTotalLiabilitiesForPeriod(String companyId, String startDate, String endDate);
}