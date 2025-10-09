package com.example.androidapp.data.dao;

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

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId AND transactionDate <= :transactionDate ORDER BY transactionDate DESC, id DESC LIMIT 1")
    AccountStatement getLastStatementBeforeDate(String companyId, String accountId, String transactionDate);

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId AND transactionDate >= :startDate ORDER BY transactionDate ASC, id ASC")
    List<AccountStatement> getStatementsForRecalculation(String companyId, String accountId, String startDate);

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId AND transactionDate <= :transactionDate ORDER BY transactionDate DESC, id DESC")
    List<AccountStatement> getAccountStatementsForBalanceCalculation(String companyId, String accountId, String transactionDate);

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId ORDER BY transactionDate DESC")
    List<AccountStatement> getAllAccountStatementsForAccount(String companyId, String accountId);
}
