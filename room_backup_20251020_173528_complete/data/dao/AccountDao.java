package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Account;
import java.util.List;

@Dao
public interface AccountDao extends BaseDao<Account> {
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountById(long id);

    @Query("SELECT * FROM accounts ORDER BY name ASC")
    List<Account> getAllAccounts();

    @Query("SELECT * FROM accounts WHERE is_active = 1 ORDER BY name ASC")
    List<Account> getActiveAccounts();
    
    @Query("SELECT * FROM accounts WHERE type = :type ORDER BY name ASC")
    List<Account> getAccountsByType(String type);

    @Query("SELECT * FROM accounts WHERE type = :type AND is_active = 1 ORDER BY name ASC")
    List<Account> getActiveAccountsByType(String type);
    
    @Query("SELECT * FROM accounts WHERE user_id = :userId ORDER BY name ASC")
    List<Account> getAccountsByUserId(String userId);

    @Query("SELECT * FROM accounts WHERE user_id = :userId AND is_active = 1 ORDER BY name ASC")
    List<Account> getActiveAccountsByUserId(String userId);
    
    @Query("SELECT * FROM accounts WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    List<Account> searchAccounts(String searchQuery);

    @Query("SELECT * FROM accounts WHERE name LIKE '%' || :searchQuery || '%' AND is_active = 1 ORDER BY name ASC")
    List<Account> searchActiveAccounts(String searchQuery);
    
    @Query("SELECT COUNT(*) FROM accounts")
    int getAccountsCount();

    @Query("SELECT COUNT(*) FROM accounts WHERE is_active = 1")
    int getActiveAccountsCount();

    @Query("SELECT COUNT(*) FROM accounts WHERE type = :type")
    int getAccountsCountByType(String type);

    @Query("SELECT COUNT(*) FROM accounts WHERE user_id = :userId")
    int getAccountsCountByUserId(String userId);
    
    @Query("SELECT COALESCE(SUM(balance), 0) FROM accounts WHERE is_active = 1")
    double getTotalBalance();

    @Query("SELECT COALESCE(SUM(balance), 0) FROM accounts WHERE type = :type AND is_active = 1")
    double getTotalBalanceByType(String type);

    @Query("SELECT COALESCE(SUM(balance), 0) FROM accounts WHERE user_id = :userId AND is_active = 1")
    double getTotalBalanceByUserId(String userId);

    @Query("SELECT COALESCE(SUM(CASE WHEN balance > 0 THEN balance ELSE 0 END), 0) FROM accounts WHERE is_active = 1")
    double getTotalAssets();

    @Query("SELECT COALESCE(SUM(CASE WHEN balance < 0 THEN ABS(balance) ELSE 0 END), 0) FROM accounts WHERE is_active = 1")
    double getTotalLiabilities();

    @Query("UPDATE accounts SET balance = :balance, last_modified = :lastModified WHERE id = :id")
    void updateBalance(long id, double balance, long lastModified);

    @Query("UPDATE accounts SET balance = balance + :amount, last_modified = :lastModified WHERE id = :id")
    void increaseBalance(long id, double amount, long lastModified);

    @Query("UPDATE accounts SET balance = balance - :amount, last_modified = :lastModified WHERE id = :id")
    void decreaseBalance(long id, double amount, long lastModified);

    @Query("UPDATE accounts SET is_active = 0, last_modified = :lastModified WHERE id = :id")
    void deactivateAccount(long id, long lastModified);

    @Query("UPDATE accounts SET is_active = 1, last_modified = :lastModified WHERE id = :id")
    void activateAccount(long id, long lastModified);

    @Query("UPDATE accounts SET name = :name, description = :description, last_modified = :lastModified WHERE id = :id")
    void updateAccountDetails(long id, String name, String description, long lastModified);
    
    @Query("DELETE FROM accounts WHERE id = :id")
    void deleteAccount(long id);

    @Query("DELETE FROM accounts WHERE user_id = :userId")
    void deleteAccountsByUserId(String userId);

    // استعلامات إضافية مفيدة
    @Query("SELECT * FROM accounts WHERE balance > :minBalance ORDER BY balance DESC")
    List<Account> getAccountsWithMinBalance(double minBalance);

    @Query("SELECT * FROM accounts WHERE balance BETWEEN :minBalance AND :maxBalance ORDER BY balance DESC")
    List<Account> getAccountsByBalanceRange(double minBalance, double maxBalance);

    @Query("SELECT * FROM accounts WHERE currency = :currency AND is_active = 1 ORDER BY balance DESC")
    List<Account> getAccountsByCurrency(String currency);

    @Query("SELECT DISTINCT currency FROM accounts WHERE is_active = 1")
    List<String> getAllCurrencies();

    @Query("SELECT * FROM accounts WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    List<Account> getAccountsByDateRange(long startDate, long endDate);
}
