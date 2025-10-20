package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Account;
import java.util.List;

@Dao
public interface AccountDao {
    @Query("SELECT * FROM accounts")
    List<Account> getAllAccounts();
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountById(int id);
    
    @Query("SELECT * FROM accounts WHERE user_id = :userId")
    List<Account> getAccountsByUserId(int userId);
    
    @Insert
    void insertAccount(Account account);
    
    @Update
    void updateAccount(Account account);
    
    @Delete
    void deleteAccount(Account account);
    
    @Query("UPDATE accounts SET balance = :balance WHERE id = :accountId")
    void updateAccountBalance(int accountId, double balance);
}
