package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Transaction;
import java.util.List;

@Dao
public interface TransactionDao extends BaseDao<Transaction> {
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    Transaction getById(long id);
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    Transaction getById(int id);
    
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    Transaction getTransactionByIdSync(long transactionId);
    
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    Transaction getTransactionByIdSync(Long transactionId);
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<Transaction> getAll();
    
    @Query("SELECT * FROM transactions WHERE from_account_id = :accountId OR to_account_id = :accountId ORDER BY date DESC")
    List<Transaction> getTransactionsByAccount(long accountId);
    
    @Query("SELECT * FROM transactions WHERE category_id = :categoryId ORDER BY date DESC")
    List<Transaction> getTransactionsByCategory(long categoryId);
    
    @Query("SELECT * FROM transactions WHERE from_account_id = :fromAccountId ORDER BY date DESC")
    List<Transaction> getByFromAccountId(long fromAccountId);
    
    @Query("SELECT * FROM transactions WHERE to_account_id = :toAccountId ORDER BY date DESC")
    List<Transaction> getByToAccountId(long toAccountId);
    
    @Query("SELECT * FROM transactions WHERE type = :transactionType ORDER BY date DESC")
    List<Transaction> getByTransactionType(String transactionType);
    
    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY date DESC")
    List<Transaction> getByStatus(String status);
    
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    List<Transaction> getByUserId(int userId);
    
    @Query("SELECT * FROM transactions WHERE company_id = :companyId ORDER BY date DESC")
    List<Transaction> getByCompanyId(String companyId);
    
    @Query("SELECT * FROM transactions WHERE category_id = :categoryId ORDER BY date DESC")
    List<Transaction> getByCategoryId(long categoryId);
    
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<Transaction> getByDateRange(long startDate, long endDate);
    
    @Query("SELECT * FROM transactions WHERE date >= :startDate ORDER BY date DESC")
    List<Transaction> getFromDate(long startDate);
    
    @Query("SELECT * FROM transactions WHERE date <= :endDate ORDER BY date DESC")
    List<Transaction> getUntilDate(long endDate);
    
    @Query("SELECT * FROM transactions WHERE amount BETWEEN :minAmount AND :maxAmount ORDER BY date DESC")
    List<Transaction> getByAmountRange(double minAmount, double maxAmount);
    
    @Query("SELECT * FROM transactions WHERE reference_number = :referenceNumber")
    Transaction getByReferenceNumber(String referenceNumber);
    
    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :searchTerm || '%' ORDER BY date DESC")
    List<Transaction> searchByDescription(String searchTerm);
    
    @Query("SELECT SUM(amount) FROM transactions WHERE to_account_id = :accountId")
    double getTotalDebitAmount(long accountId);
    
    @Query("SELECT SUM(amount) FROM transactions WHERE from_account_id = :accountId")
    double getTotalCreditAmount(long accountId);
    
    @Query("SELECT COUNT(*) FROM transactions WHERE userId = :userId")
    int getCountByUserId(int userId);
    
    @Query("SELECT COUNT(*) FROM transactions WHERE status = :status")
    int getCountByStatus(String status);
    
    @Query("DELETE FROM transactions WHERE status = 'cancelled'")
    void deleteCancelledTransactions();
    
    @Query("DELETE FROM transactions")
    void deleteAll();
}
