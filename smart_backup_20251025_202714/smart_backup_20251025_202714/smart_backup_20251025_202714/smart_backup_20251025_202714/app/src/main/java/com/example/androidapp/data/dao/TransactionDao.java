package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Transaction;

import java.util.Date;
import java.util.List;

@Dao
public interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Transaction transaction);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Transaction> transactions);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE id = :id")
    Transaction getById(long id);

    @Query("SELECT * FROM transactions")
    List<Transaction> getAll();

    @Query("SELECT * FROM transactions WHERE from_account_id = :accountId OR to_account_id = :accountId")
    List<Transaction> getByAccountId(String accountId);

    @Query("SELECT * FROM transactions WHERE from_account_id = :fromAccountId")
    List<Transaction> getByFromAccountId(String fromAccountId);

    @Query("SELECT * FROM transactions WHERE to_account_id = :toAccountId")
    List<Transaction> getByToAccountId(String toAccountId);

    @Query("SELECT * FROM transactions WHERE transaction_type = :transactionType")
    List<Transaction> getByTransactionType(String transactionType);

    @Query("SELECT * FROM transactions WHERE status = :status")
    List<Transaction> getByStatus(String status);

    @Query("SELECT * FROM transactions WHERE user_id = :userId")
    List<Transaction> getByUserId(String userId);

    @Query("SELECT * FROM transactions WHERE company_id = :companyId")
    List<Transaction> getByCompanyId(String companyId);

    @Query("SELECT * FROM transactions WHERE category_id = :categoryId")
    List<Transaction> getByCategoryId(String categoryId);

    @Query("SELECT * FROM transactions WHERE transaction_date BETWEEN :startDate AND :endDate")
    List<Transaction> getByDateRange(Date startDate, Date endDate);

    @Query("SELECT * FROM transactions WHERE transaction_date >= :startDate")
    List<Transaction> getFromDate(Date startDate);

    @Query("SELECT * FROM transactions WHERE transaction_date <= :endDate")
    List<Transaction> getUntilDate(Date endDate);

    @Query("SELECT * FROM transactions WHERE amount >= :minAmount AND amount <= :maxAmount")
    List<Transaction> getByAmountRange(double minAmount, double maxAmount);

    @Query("SELECT * FROM transactions WHERE reference_number = :referenceNumber")
    Transaction getByReferenceNumber(String referenceNumber);

    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :searchTerm || '%'")
    List<Transaction> searchByDescription(String searchTerm);

    @Query("SELECT SUM(amount) FROM transactions WHERE from_account_id = :accountId AND status = 'COMPLETED'")
    double getTotalDebitAmount(String accountId);

    @Query("SELECT SUM(amount) FROM transactions WHERE to_account_id = :accountId AND status = 'COMPLETED'")
    double getTotalCreditAmount(String accountId);

    @Query("SELECT COUNT(*) FROM transactions WHERE user_id = :userId")
    int getCountByUserId(String userId);

    @Query("SELECT COUNT(*) FROM transactions WHERE status = :status")
    int getCountByStatus(String status);

    @Query("DELETE FROM transactions WHERE status = 'CANCELLED'")
    void deleteCancelledTransactions();

    @Query("DELETE FROM transactions WHERE company_id = :companyId")
    void deleteByCompanyId(String companyId);

    @Query("SELECT * FROM transactions ORDER BY transaction_date DESC LIMIT :limit")
    List<Transaction> getRecentTransactions(int limit);
}
