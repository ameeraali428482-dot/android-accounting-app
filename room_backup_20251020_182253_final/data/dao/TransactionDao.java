package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Transaction;
import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY transaction_date DESC")
    List<Transaction> getAllTransactions();
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    Transaction getTransactionById(int id);
    
    @Query("SELECT * FROM transactions WHERE account_id = :accountId ORDER BY transaction_date DESC")
    List<Transaction> getTransactionsByAccountId(int accountId);
    
    @Query("SELECT * FROM transactions WHERE category_id = :categoryId")
    List<Transaction> getTransactionsByCategoryId(int categoryId);
    
    @Insert
    void insertTransaction(Transaction transaction);
    
    @Update
    void updateTransaction(Transaction transaction);
    
    @Delete
    void deleteTransaction(Transaction transaction);
    
    @Query("SELECT SUM(amount) FROM transactions WHERE account_id = :accountId AND type = 'INCOME'")
    double getTotalIncomeByAccount(int accountId);
    
    @Query("SELECT SUM(amount) FROM transactions WHERE account_id = :accountId AND type = 'EXPENSE'")
    double getTotalExpenseByAccount(int accountId);
}
