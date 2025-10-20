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

    @Query("SELECT * FROM transactions WHERE company_id = :companyId ORDER BY date DESC")
    List<Transaction> getAllByCompany(String companyId);

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

    @Query("SELECT * FROM transactions WHERE transaction_type = :transactionType AND company_id = :companyId ORDER BY date DESC")
    List<Transaction> getByTransactionTypeAndCompany(String transactionType, String companyId);

    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY date DESC")
    List<Transaction> getByStatus(String status);

    @Query("SELECT * FROM transactions WHERE status = :status AND company_id = :companyId ORDER BY date DESC")
    List<Transaction> getByStatusAndCompany(String status, String companyId);

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY date DESC")
    List<Transaction> getByUserId(String userId);

    @Query("SELECT * FROM transactions WHERE company_id = :companyId ORDER BY date DESC")
    List<Transaction> getByCompanyId(String companyId);

    @Query("SELECT * FROM transactions WHERE category_id = :categoryId AND company_id = :companyId ORDER BY date DESC")
    List<Transaction> getByCategoryIdAndCompany(long categoryId, String companyId);

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<Transaction> getByDateRange(long startDate, long endDate);

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate AND company_id = :companyId ORDER BY date DESC")
    List<Transaction> getByDateRangeAndCompany(long startDate, long endDate, String companyId);

    @Query("SELECT * FROM transactions WHERE date >= :startDate ORDER BY date DESC")
    List<Transaction> getFromDate(long startDate);

    @Query("SELECT * FROM transactions WHERE date <= :endDate ORDER BY date DESC")
    List<Transaction> getUntilDate(long endDate);

    @Query("SELECT * FROM transactions WHERE amount BETWEEN :minAmount AND :maxAmount ORDER BY date DESC")
    List<Transaction> getByAmountRange(double minAmount, double maxAmount);

    @Query("SELECT * FROM transactions WHERE amount BETWEEN :minAmount AND :maxAmount AND company_id = :companyId ORDER BY date DESC")
    List<Transaction> getByAmountRangeAndCompany(double minAmount, double maxAmount, String companyId);

    @Query("SELECT * FROM transactions WHERE reference_number = :referenceNumber")
    Transaction getByReferenceNumber(String referenceNumber);

    @Query("SELECT * FROM transactions WHERE reference_number = :referenceNumber AND company_id = :companyId")
    Transaction getByReferenceNumberAndCompany(String referenceNumber, String companyId);

    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :searchTerm || '%' ORDER BY date DESC")
    List<Transaction> searchByDescription(String searchTerm);

    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :searchTerm || '%' AND company_id = :companyId ORDER BY date DESC")
    List<Transaction> searchByDescriptionAndCompany(String searchTerm, String companyId);

    @Query("SELECT * FROM transactions WHERE (description LIKE '%' || :searchTerm || '%' OR reference_number LIKE '%' || :searchTerm || '%' OR notes LIKE '%' || :searchTerm || '%') AND company_id = :companyId ORDER BY date DESC")
    List<Transaction> searchTransactions(String searchTerm, String companyId);

    // إحصائيات المبالغ
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE to_account_id = :accountId AND amount > 0 AND status = 'COMPLETED'")
    double getTotalDebitAmount(long accountId);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE from_account_id = :accountId AND amount > 0 AND status = 'COMPLETED'")
    double getTotalCreditAmount(long accountId);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE company_id = :companyId AND transaction_type = :transactionType AND status = 'COMPLETED' AND date BETWEEN :startDate AND :endDate")
    double getTotalAmountByTypeAndDateRange(String companyId, String transactionType, long startDate, long endDate);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE category_id = :categoryId AND status = 'COMPLETED' AND date BETWEEN :startDate AND :endDate")
    double getTotalAmountByCategoryAndDateRange(long categoryId, long startDate, long endDate);

    // إحصائيات العدد
    @Query("SELECT COUNT(*) FROM transactions WHERE user_id = :userId")
    int getCountByUserId(String userId);

    @Query("SELECT COUNT(*) FROM transactions WHERE company_id = :companyId")
    int getCountByCompanyId(String companyId);

    @Query("SELECT COUNT(*) FROM transactions WHERE status = :status")
    int getCountByStatus(String status);

    @Query("SELECT COUNT(*) FROM transactions WHERE status = :status AND company_id = :companyId")
    int getCountByStatusAndCompany(String status, String companyId);

    @Query("SELECT COUNT(*) FROM transactions WHERE date BETWEEN :startDate AND :endDate AND company_id = :companyId")
    int getTransactionsCountByDateAndCompany(long startDate, long endDate, String companyId);

    @Query("SELECT COUNT(*) FROM transactions WHERE transaction_type = :transactionType AND company_id = :companyId")
    int getCountByTypeAndCompany(String transactionType, String companyId);

    // عمليات التحديث
    @Query("UPDATE transactions SET status = :status, last_modified = :lastModified WHERE id = :transactionId")
    void updateStatus(long transactionId, String status, long lastModified);

    @Query("UPDATE transactions SET is_reconciled = :isReconciled, last_modified = :lastModified WHERE id = :transactionId")
    void updateReconciliationStatus(long transactionId, boolean isReconciled, long lastModified);

    @Query("UPDATE transactions SET notes = :notes, last_modified = :lastModified WHERE id = :transactionId")
    void updateNotes(long transactionId, String notes, long lastModified);

    @Query("UPDATE transactions SET category_id = :categoryId, last_modified = :lastModified WHERE id = :transactionId")
    void updateCategory(long transactionId, Long categoryId, long lastModified);

    // عمليات الحذف
    @Query("DELETE FROM transactions WHERE status = 'CANCELLED'")
    void deleteCancelledTransactions();

    @Query("DELETE FROM transactions WHERE status = 'CANCELLED' AND company_id = :companyId")
    void deleteCancelledTransactionsByCompany(String companyId);

    @Query("DELETE FROM transactions WHERE company_id = :companyId")
    void deleteByCompanyId(String companyId);

    @Query("DELETE FROM transactions WHERE user_id = :userId")
    void deleteByUserId(String userId);

    // استعلامات متقدمة
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    List<Transaction> getRecentTransactions(int limit);

    @Query("SELECT * FROM transactions WHERE company_id = :companyId ORDER BY date DESC LIMIT :limit")
    List<Transaction> getRecentTransactionsByCompany(String companyId, int limit);

    @Query("SELECT * FROM transactions WHERE from_account_id = :fromAccountId AND to_account_id = :toAccountId ORDER BY date DESC")
    List<Transaction> getTransactionsBetweenAccounts(long fromAccountId, long toAccountId);

    @Query("SELECT * FROM transactions WHERE from_account_id = :fromAccountId AND to_account_id = :toAccountId AND company_id = :companyId ORDER BY date DESC")
    List<Transaction> getTransactionsBetweenAccountsAndCompany(long fromAccountId, long toAccountId, String companyId);

    @Query("SELECT * FROM transactions WHERE is_reconciled = 0 AND company_id = :companyId ORDER BY date DESC")
    List<Transaction> getUnreconciledTransactions(String companyId);

    @Query("SELECT * FROM transactions WHERE (from_account_id = :accountId OR to_account_id = :accountId) AND is_reconciled = 0 ORDER BY date DESC")
    List<Transaction> getUnreconciledTransactionsByAccount(long accountId);

    @Query("SELECT DISTINCT transaction_type FROM transactions WHERE company_id = :companyId")
    List<String> getDistinctTransactionTypes(String companyId);

    @Query("SELECT DISTINCT status FROM transactions WHERE company_id = :companyId")
    List<String> getDistinctStatuses(String companyId);

    // إحصائيات يومية/شهرية/سنوية
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE company_id = :companyId AND status = 'COMPLETED' AND date >= :startOfDay AND date < :endOfDay")
    double getDailyTotal(String companyId, long startOfDay, long endOfDay);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE company_id = :companyId AND status = 'COMPLETED' AND transaction_type = 'DEBIT' AND date BETWEEN :startDate AND :endDate")
    double getTotalIncome(String companyId, long startDate, long endDate);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE company_id = :companyId AND status = 'COMPLETED' AND transaction_type = 'CREDIT' AND date BETWEEN :startDate AND :endDate")
    double getTotalExpense(String companyId, long startDate, long endDate);
}
