package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.androidapp.data.entities.Account;

import java.util.List;

@Dao
public interface AccountDao {
    @Insert
    void insert(Account account);

    @Update
    void update(Account account);

    @Delete
    void delete(Account account);

    @Query("SELECT * FROM accounts WHERE companyId = :companyId")
    List<Account> getAllAccounts(String companyId);

    @Query("SELECT * FROM accounts WHERE id = :id AND companyId = :companyId LIMIT 1")
    Account getAccountById(String id, String companyId);

    @Query("SELECT * FROM accounts WHERE accountName = :accountName AND companyId = :companyId LIMIT 1")
    Account getAccountByNameAndCompanyId(String accountName, String companyId);

    @Query("SELECT COUNT(*) FROM accounts WHERE accountName = :accountName AND companyId = :companyId")
    int countAccountByNameAndCompanyId(String accountName, String companyId);
}
