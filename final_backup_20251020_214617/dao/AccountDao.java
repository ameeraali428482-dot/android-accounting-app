package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Account;
import java.util.List;

@Dao
public interface AccountDao extends BaseDao<Account> {
    
    @Query("SELECT * FROM accounts ORDER BY name")
    List<Account> getAllAccounts();
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountById(long id);
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountById(int id);
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountByIdSync(long id);
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountByIdSync(int id);
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountByIdSync(Integer id);
    
    @Query("SELECT * FROM accounts WHERE type = :type ORDER BY name")
    List<Account> getAccountsByType(String type);
    
    @Query("SELECT * FROM accounts WHERE name LIKE '%' || :searchQuery || '%' OR code LIKE '%' || :searchQuery || '%' ORDER BY name")
    List<Account> searchAccounts(String searchQuery);
    
    @Query("SELECT COUNT(*) FROM accounts")
    int getAccountsCount();
    
    @Query("SELECT COALESCE(SUM(balance), 0) FROM accounts")
    double getTotalBalance();
    
    @Query("DELETE FROM accounts WHERE id = :id")
    void deleteAccount(long id);
}
