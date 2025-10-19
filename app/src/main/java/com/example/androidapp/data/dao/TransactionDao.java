package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Transaction;

import java.util.List;

@Dao
public interface TransactionDao extends BaseDao<Transaction> {
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    Transaction getById(long id);

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

    @Query("SELECT * FROM transactions WHERE transaction_type = :transactionType ORDER BY date DESC")
    List<Transaction> getByTransactionType(String transactionType);

    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY date DESC")
    List<Transaction> getByStatus(String status);

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY date DESC")
    List<Transaction> getByUserId(String userId);

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

    @Query("SELECT SUM(amount) FROM transactions WHERE to_account_id = :accountId AND amount > 0")
    double getTotalDebitAmount(long accountId);

    @Query("SELECT SUM(amount) FROM transactions WHERE from_account_id = :accountId AND amount < 0")
    double getTotalCreditAmount(long accountId);

    @Query("SELECT COUNT(*) FROM transactions WHERE user_id = :userId")
    int getCountByUserId(String userId);

    @Query("SELECT COUNT(*) FROM transactions WHERE status = :status")
    int getCountByStatus(String status);

    @Query("DELETE FROM transactions WHERE status = 'CANCELLED'")
    void deleteCancelledTransactions();

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    List<Transaction> getRecentTransactions(int limit);

    @Query("SELECT COUNT(*) FROM transactions WHERE date BETWEEN :startDate AND :endDate")
    int getTransactionsCountByDate(long startDate, long endDate);

    @Query("SELECT * FROM transactions WHERE from_account_id = :fromAccountId AND to_account_id = :toAccountId ORDER BY date DESC")
    List<Transaction> getTransactionsBetweenAccounts(long fromAccountId, long toAccountId);
}
