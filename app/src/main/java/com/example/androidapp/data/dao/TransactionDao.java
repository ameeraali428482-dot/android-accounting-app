package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Transaction;
import java.util.List;

@Dao
public interface TransactionDao extends BaseDao<Transaction> {
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<Transaction> getAllTransactions();
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    Transaction getTransactionById(long id);
    
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<Transaction> getTransactionsByDateRange(long startDate, long endDate);
    
    @Query("SELECT * FROM transactions WHERE fromAccountId = :accountId OR toAccountId = :accountId ORDER BY date DESC")
    List<Transaction> getTransactionsByAccount(long accountId);
    
    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    List<Transaction> getTransactionsByCategory(long categoryId);
    
    @Query("SELECT COUNT(*) FROM transactions")
    int getTransactionsCount();
    
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE date BETWEEN :startDate AND :endDate")
    double getTotalAmountByDateRange(long startDate, long endDate);
    
    @Query("DELETE FROM transactions WHERE id = :id")
    void deleteTransaction(long id);
}
