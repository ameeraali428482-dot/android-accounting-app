package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.AccountStatement;

import java.util.List;

@Dao
public interface AccountStatementDao {
    @Insert
    void insert(AccountStatement accountStatement);

    @Update
    void update(AccountStatement accountStatement);

    @Delete
    void delete(AccountStatement accountStatement);

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId AND date <= :transactionDate ORDER BY date DESC, id DESC LIMIT 1")
    AccountStatement getLastStatementBeforeDate(String companyId, String accountId, String transactionDate);

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId AND date >= :startDate ORDER BY date ASC, id ASC")
    List<AccountStatement> getStatementsForRecalculation(String companyId, String accountId, String startDate);

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId AND date <= :transactionDate ORDER BY date DESC, id DESC")
    List<AccountStatement> getAccountStatementsForBalanceCalculation(String companyId, String accountId, String transactionDate);

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId ORDER BY date DESC")
    LiveData<List<AccountStatement>> getAllAccountStatementsForAccount(String companyId, String accountId);
    
    @Query("SELECT * FROM account_statements WHERE id = :statementId AND companyId = :companyId AND accountId = :accountId")
    LiveData<AccountStatement> getAccountStatementById(int statementId, String companyId, String accountId);
}
