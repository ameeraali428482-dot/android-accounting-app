package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Account;
import java.util.List;

@Dao
public interface AccountDao {
    @Query("SELECT * FROM accounts WHERE companyId = :companyId")
    LiveData<List<Account>> getAllAccounts(String companyId);

    @Query("SELECT * FROM accounts WHERE id = :accountId AND companyId = :companyId")
    Account getAccountById(String accountId, String companyId);

    @Query("SELECT * FROM accounts WHERE id = :accountId AND companyId = :companyId")
    LiveData<Account> getAccountByIdLiveData(String accountId, String companyId);

    @Insert
    void insert(Account account);

    @Update
    void update(Account account);

    @Delete
    void delete(Account account);
}
