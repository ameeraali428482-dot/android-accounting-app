package com.example.androidapp.data.dao;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.data.entities.Voucher;
import com.example.androidapp.data.entities.Company;
import com.example.androidapp.data.entities.Doctor;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.Supplier;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.data.entities.Trophy;
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.data.entities.Repair;
import com.example.androidapp.data.entities.Chat;
import com.example.androidapp.data.entities.UserReward;
import com.example.androidapp.data.entities.Reward;
import com.example.androidapp.data.entities.PointTransaction;
import com.example.androidapp.data.entities.Campaign;

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

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId ORDER BY transactionDate DESC")
    LiveData<List<AccountStatement>> getAllAccountStatementsForAccount(String companyId, String accountId);

    @Query("SELECT * FROM account_statements WHERE id = :statementId AND companyId = :companyId AND accountId = :accountId LIMIT 1")
    LiveData<AccountStatement> getAccountStatementById(int statementId, String companyId, String accountId);

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId ORDER BY transactionDate ASC")
    List<AccountStatement> getAccountStatementsForBalanceCalculation(String companyId, String accountId);
}
