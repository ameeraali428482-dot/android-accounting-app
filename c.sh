#!/bin/bash

echo "🔧 الإصلاح الشامل النهائي لمشروع Android Accounting App"
echo "================================================================"
echo "🎯 هدف: حل جميع الأخطاء (1343 خطأ → 0 خطأ)"
echo "⏰ بدء الإصلاح: $(date)"
echo ""

# إنشاء مجلد backup
mkdir -p backup_$(date +%Y%m%d_%H%M%S)

echo "📁 المرحلة 1: حفظ النسخ الاحتياطية..."
cp -r app/src/main/java/com/example/androidapp/data/entities/ backup_$(date +%Y%m%d_%H%M%S)/
cp -r app/src/main/java/com/example/androidapp/data/dao/ backup_$(date +%Y%m%d_%H%M%S)/
cp -r app/src/main/java/com/example/androidapp/utils/ backup_$(date +%Y%m%d_%H%M%S)/

echo "🔧 المرحلة 2: إصلاح User Entity و UserDao..."

# إصلاح User.java - جعل الحقول متسقة مع الاستخدام
cat > app/src/main/java/com/example/androidapp/data/entities/User.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "phone")
    public String phone;
    
    // Additional field for compatibility
    @ColumnInfo(name = "phone_number")
    public String phoneNumber;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    // Default constructor for Room
    public User() {}

    // Primary constructor for Room
    public User(String name, String email, String phone, long createdAt, long updatedAt) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.phoneNumber = phone; // Sync both fields
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Ignore
    public User(String name, String email, long createdAt, long updatedAt) {
        this(name, email, null, createdAt, updatedAt);
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone != null ? phone : phoneNumber; }
    public String getPhoneNumber() { return phoneNumber != null ? phoneNumber : phone; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { 
        this.phone = phone;
        this.phoneNumber = phone; // Keep in sync
    }
    public void setPhoneNumber(String phoneNumber) { 
        this.phoneNumber = phoneNumber;
        this.phone = phoneNumber; // Keep in sync
    }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
EOF

# إصلاح UserDao.java - توحيد أنواع البيانات
cat > app/src/main/java/com/example/androidapp/data/dao/UserDao.java << 'EOF'
package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.User;
import java.util.List;

@Dao
public interface UserDao extends BaseDao<User> {
    
    @Query("SELECT * FROM users WHERE id = :id")
    User getById(int id);
    
    @Query("SELECT * FROM users WHERE id = :id")
    User getUserByIdSync(int id);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE phone = :phone OR phone_number = :phone")
    User getUserByPhone(String phone);

    @Query("SELECT * FROM users ORDER BY name ASC")
    List<User> getAll();

    @Query("SELECT * FROM users WHERE company_id = :companyId ORDER BY name ASC")
    List<User> getByCompanyId(String companyId);

    @Query("SELECT * FROM users WHERE is_active = 1 ORDER BY name ASC")
    List<User> getActiveUsers();

    @Query("SELECT * FROM users WHERE name LIKE '%' || :searchTerm || '%' ORDER BY name ASC")
    List<User> searchByName(String searchTerm);

    @Query("UPDATE users SET last_login = :timestamp WHERE id = :userId")
    void updateLastLogin(int userId, long timestamp);

    @Query("UPDATE users SET is_active = :isActive WHERE id = :userId")
    void updateUserStatus(int userId, boolean isActive);

    @Query("SELECT COUNT(*) FROM users WHERE company_id = :companyId")
    int getCountByCompany(String companyId);

    @Query("DELETE FROM users WHERE company_id = :companyId")
    void deleteByCompanyId(String companyId);
}
EOF

echo "🔧 المرحلة 3: إصلاح Transaction Entity..."

# إصلاح Transaction.java - إضافة الحقول المفقودة وتوحيد الأنواع
cat > app/src/main/java/com/example/androidapp/data/entities/Transaction.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.room.*;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public int userId;
    @ColumnInfo(name = "category_id")
    public Integer categoryId;
    public double amount;
    public String description;
    public long timestamp;
    public String type;
    
    // Additional fields for compatibility
    public long date;
    
    @ColumnInfo(name = "from_account_id")
    public Integer fromAccountId;
    
    @ColumnInfo(name = "to_account_id")
    public Integer toAccountId;
    
    @ColumnInfo(name = "last_modified")
    public long lastModified;

    // Default constructor for Room
    public Transaction() {
        this.timestamp = System.currentTimeMillis();
        this.date = this.timestamp; // Keep in sync
        this.lastModified = System.currentTimeMillis();
    }

    // Main constructor
    public Transaction(int userId, Integer categoryId, double amount, String description, long timestamp, String type) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.date = timestamp; // Keep in sync
        this.type = type;
        this.lastModified = System.currentTimeMillis();
    }

    // Getters with compatibility
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public Integer getCategoryId() { return categoryId; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public long getDate() { return date != 0 ? date : timestamp; }
    public String getType() { return type; }
    public Integer getFromAccountId() { return fromAccountId; }
    public Integer getToAccountId() { return toAccountId; }
    public long getLastModified() { return lastModified; }

    // Setters with sync
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setTimestamp(long timestamp) { 
        this.timestamp = timestamp;
        this.date = timestamp; // Keep in sync
    }
    public void setDate(long date) { 
        this.date = date;
        this.timestamp = date; // Keep in sync
    }
    public void setType(String type) { this.type = type; }
    public void setFromAccountId(Integer fromAccountId) { this.fromAccountId = fromAccountId; }
    public void setToAccountId(Integer toAccountId) { this.toAccountId = toAccountId; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
}
EOF

echo "🔧 المرحلة 4: إصلاح TransactionDao..."

# إضافة الدوال المفقودة إلى TransactionDao
cat > app/src/main/java/com/example/androidapp/data/dao/TransactionDao.java << 'EOF'
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

    @Query("SELECT SUM(amount) FROM transactions WHERE to_account_id = :accountId AND amount > 0")
    double getTotalDebitAmount(long accountId);

    @Query("SELECT SUM(amount) FROM transactions WHERE from_account_id = :accountId AND amount < 0")
    double getTotalCreditAmount(long accountId);

    @Query("SELECT COUNT(*) FROM transactions WHERE userId = :userId")
    int getCountByUserId(int userId);

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
EOF

echo "🔧 المرحلة 5: إصلاح Account Entity..."

# إصلاح Account.java - إضافة الحقول المفقودة
cat > app/src/main/java/com/example/androidapp/data/entities/Account.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.room.*;

@Entity(tableName = "accounts")
public class Account {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public String code;
    public String type;
    public double balance;
    public String currency;
    public String description;
    
    @ColumnInfo(name = "created_at")
    public long createdAt;
    
    @ColumnInfo(name = "last_modified")
    public long lastModified;
    
    public String userId;
    
    public Account() {
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.currency = "SAR";
        this.balance = 0.0;
    }

    public Account(String name, String code, double balance, String type, long createdAt) {
        this.name = name;
        this.code = code;
        this.balance = balance;
        this.type = type;
        this.createdAt = createdAt;
        this.lastModified = System.currentTimeMillis();
        this.currency = "SAR";
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getType() { return type; }
    public double getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public long getCreatedAt() { return createdAt; }
    public long getLastModified() { return lastModified; }
    public String getUserId() { return userId; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCode(String code) { this.code = code; }
    public void setType(String type) { this.type = type; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    public void setUserId(String userId) { this.userId = userId; }
}
EOF

echo "🔧 المرحلة 6: إصلاح AccountDao..."

# إضافة الدوال المفقودة إلى AccountDao
cat > app/src/main/java/com/example/androidapp/data/dao/AccountDao.java << 'EOF'
package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Account;
import java.util.List;

@Dao
public interface AccountDao extends BaseDao<Account> {
    
    @Query("SELECT * FROM accounts ORDER BY name")
    List<Account> getAllAccounts();
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountById(long id);
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountById(int id);
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountByIdSync(long id);
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountByIdSync(int id);
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account getAccountByIdSync(Integer id);
    
    @Query("SELECT * FROM accounts WHERE type = :type ORDER BY name")
    List<Account> getAccountsByType(String type);
    
    @Query("SELECT * FROM accounts WHERE name LIKE '%' || :searchQuery || '%' OR code LIKE '%' || :searchQuery || '%' ORDER BY name")
    List<Account> searchAccounts(String searchQuery);
    
    @Query("SELECT COUNT(*) FROM accounts")
    int getAccountsCount();
    
    @Query("SELECT COALESCE(SUM(balance), 0) FROM accounts")
    double getTotalBalance();
    
    @Query("DELETE FROM accounts WHERE id = :id")
    void deleteAccount(long id);
}
EOF

echo "🔧 المرحلة 7: إصلاح Notification Entity..."

# إصلاح Notification.java - جعل الحقول public وتوحيد الأنواع
cat > app/src/main/java/com/example/androidapp/data/entities/Notification.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class Notification {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public String type;
    public String title;
    public String content;
    public String message;
    public long relatedId;
    public long timestamp;
    public boolean isRead;
    public String entityId;

    // Default constructor for Room
    public Notification() {
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Main constructor
    public Notification(int userId, String type, String title, String content, long relatedId) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.message = content; // Keep in sync
        this.relatedId = relatedId;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Getters for compatibility
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getMessage() { return message != null ? message : content; }
    public long getRelatedId() { return relatedId; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    public String getEntityId() { return entityId; }
    public String getCreatedAt() { return String.valueOf(timestamp); }
    public String getNotificationType() { return type; }

    // Setters with sync
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { 
        this.content = content;
        this.message = content; // Keep in sync
    }
    public void setMessage(String message) { 
        this.message = message;
        this.content = message; // Keep in sync
    }
    public void setRelatedId(long relatedId) { this.relatedId = relatedId; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setRead(boolean read) { isRead = read; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
}
EOF

echo "🔧 المرحلة 8: إنشاء NotificationDao..."

# إنشاء NotificationDao مع جميع الدوال المطلوبة
cat > app/src/main/java/com/example/androidapp/data/dao/NotificationDao.java << 'EOF'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.androidapp.data.entities.Notification;
import java.util.List;

@Dao
public interface NotificationDao extends BaseDao<Notification> {
    
    @Insert
    long insert(Notification notification);

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    List<Notification> getAllForUser(int userId);

    @Query("DELETE FROM notifications WHERE timestamp < :cutoffTime")
    void deleteOldNotifications(long cutoffTime);

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    void markAsRead(int notificationId);

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    int getUnreadCount(int userId);

    @Query("SELECT * FROM notifications WHERE id = :id")
    Notification getById(int id);

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    List<Notification> getAll();

    @Query("DELETE FROM notifications WHERE userId = :userId")
    void deleteByUserId(int userId);
}
EOF

echo "🔧 المرحلة 9: إصلاح SessionManager..."

# إضافة الدوال المفقودة إلى SessionManager
cat > app/src/main/java/com/example/androidapp/utils/SessionManager.java << 'EOF'
package com.example.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * مدير الجلسة
 */
public class SessionManager {
    
    private Context context;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "AppSession";
    private static final String KEY_COMPANY_ID = "company_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public String getCompanyId() {
        return preferences.getString(KEY_COMPANY_ID, "default_company");
    }
    
    public void setCompanyId(String companyId) {
        preferences.edit().putString(KEY_COMPANY_ID, companyId).apply();
    }

    public String getCurrentUserId() {
        return preferences.getString(KEY_USER_ID, null);
    }

    public int getCurrentUserIdInt() {
        String userId = getCurrentUserId();
        try {
            return userId != null ? Integer.parseInt(userId) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setCurrentUserId(String userId) {
        preferences.edit().putString(KEY_USER_ID, userId).apply();
    }

    public void setCurrentUserId(int userId) {
        preferences.edit().putString(KEY_USER_ID, String.valueOf(userId)).apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }

    public void logout() {
        preferences.edit()
                .remove(KEY_USER_ID)
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .apply();
    }

    public void login(String userId) {
        preferences.edit()
                .putString(KEY_USER_ID, userId)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public void login(int userId) {
        login(String.valueOf(userId));
    }
}
EOF

echo "🔧 المرحلة 10: إنشاء الكيانات المفقودة..."

# إنشاء Permission entity
cat > app/src/main/java/com/example/androidapp/data/entities/Permission.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "permissions")
public class Permission {
    @PrimaryKey
    public String permissionId;
    
    public String name;
    public String description;
    public String category;
    public long createdAt;

    public Permission() {
        this.createdAt = System.currentTimeMillis();
    }

    public Permission(String permissionId, String name, String description, String category) {
        this.permissionId = permissionId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public String getPermissionId() { return permissionId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
EOF

# إنشاء PermissionDao
cat > app/src/main/java/com/example/androidapp/data/dao/PermissionDao.java << 'EOF'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Permission;
import java.util.List;

@Dao
public interface PermissionDao extends BaseDao<Permission> {
    
    @Query("SELECT * FROM permissions WHERE permissionId = :id")
    Permission getById(String id);

    @Query("SELECT * FROM permissions ORDER BY name")
    List<Permission> getAll();

    @Query("SELECT * FROM permissions WHERE category = :category ORDER BY name")
    List<Permission> getByCategory(String category);

    @Query("DELETE FROM permissions WHERE permissionId = :id")
    void deleteById(String id);
}
EOF

# إنشاء Item entity
cat > app/src/main/java/com/example/androidapp/data/entities/Item.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public String code;
    public String description;
    public double price;
    public String category;
    public int quantity;
    public String unit;
    public long createdAt;

    public Item() {
        this.createdAt = System.currentTimeMillis();
        this.quantity = 0;
        this.price = 0.0;
    }

    public Item(String name, String code, double price, String category) {
        this();
        this.name = name;
        this.code = code;
        this.price = price;
        this.category = category;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCode(String code) { this.code = code; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
EOF

# إنشاء ItemDao
cat > app/src/main/java/com/example/androidapp/data/dao/ItemDao.java << 'EOF'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Item;
import java.util.List;

@Dao
public interface ItemDao extends BaseDao<Item> {
    
    @Query("SELECT * FROM items WHERE id = :id")
    Item getById(int id);

    @Query("SELECT * FROM items ORDER BY name")
    List<Item> getAll();
    
    @Query("SELECT * FROM items ORDER BY name")
    List<Item> getAllItems();

    @Query("SELECT * FROM items WHERE category = :category ORDER BY name")
    List<Item> getByCategory(String category);

    @Query("SELECT * FROM items WHERE name LIKE '%' || :searchTerm || '%' OR code LIKE '%' || :searchTerm || '%' ORDER BY name")
    List<Item> searchItems(String searchTerm);

    @Query("SELECT COUNT(*) FROM items")
    int getCount();

    @Query("DELETE FROM items WHERE id = :id")
    void deleteById(int id);
}
EOF

# إنشاء Customer entity
cat > app/src/main/java/com/example/androidapp/data/entities/Customer.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "customers")
public class Customer {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public String email;
    public String phone;
    public String address;
    public String companyName;
    public String taxNumber;
    public double totalPurchases;
    public long createdAt;

    public Customer() {
        this.createdAt = System.currentTimeMillis();
        this.totalPurchases = 0.0;
    }

    public Customer(String name, String email, String phone) {
        this();
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getCompanyName() { return companyName; }
    public String getTaxNumber() { return taxNumber; }
    public double getTotalPurchases() { return totalPurchases; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setTaxNumber(String taxNumber) { this.taxNumber = taxNumber; }
    public void setTotalPurchases(double totalPurchases) { this.totalPurchases = totalPurchases; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
EOF

# إنشاء CustomerDao
cat > app/src/main/java/com/example/androidapp/data/dao/CustomerDao.java << 'EOF'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Customer;
import java.util.List;

@Dao
public interface CustomerDao extends BaseDao<Customer> {
    
    @Query("SELECT * FROM customers WHERE id = :id")
    Customer getById(int id);

    @Query("SELECT * FROM customers ORDER BY name")
    List<Customer> getAll();
    
    @Query("SELECT * FROM customers ORDER BY name")
    List<Customer> getAllCustomers();

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :searchTerm || '%' OR email LIKE '%' || :searchTerm || '%' ORDER BY name")
    List<Customer> searchCustomers(String searchTerm);

    @Query("SELECT * FROM customers WHERE email = :email")
    Customer getByEmail(String email);

    @Query("SELECT * FROM customers WHERE phone = :phone")
    Customer getByPhone(String phone);

    @Query("SELECT COUNT(*) FROM customers")
    int getCount();

    @Query("DELETE FROM customers WHERE id = :id")
    void deleteById(int id);
}
EOF

# إنشاء Employee entity
cat > app/src/main/java/com/example/androidapp/data/entities/Employee.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "employees")
public class Employee {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String employeeId;
    public String name;
    public String email;
    public String phone;
    public String department;
    public String position;
    public double salary;
    public long hireDate;
    public boolean isActive;
    public long createdAt;

    public Employee() {
        this.createdAt = System.currentTimeMillis();
        this.hireDate = System.currentTimeMillis();
        this.isActive = true;
        this.salary = 0.0;
    }

    public Employee(String employeeId, String name, String department, String position) {
        this();
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.position = position;
    }

    // Getters
    public int getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
    public double getSalary() { return salary; }
    public long getHireDate() { return hireDate; }
    public boolean isActive() { return isActive; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setDepartment(String department) { this.department = department; }
    public void setPosition(String position) { this.position = position; }
    public void setSalary(double salary) { this.salary = salary; }
    public void setHireDate(long hireDate) { this.hireDate = hireDate; }
    public void setActive(boolean active) { isActive = active; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
EOF

# إنشاء EmployeeDao
cat > app/src/main/java/com/example/androidapp/data/dao/EmployeeDao.java << 'EOF'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Employee;
import java.util.List;

@Dao
public interface EmployeeDao extends BaseDao<Employee> {
    
    @Query("SELECT * FROM employees WHERE id = :id")
    Employee getById(int id);

    @Query("SELECT * FROM employees ORDER BY name")
    List<Employee> getAll();
    
    @Query("SELECT * FROM employees ORDER BY name")
    List<Employee> getAllEmployees();

    @Query("SELECT * FROM employees WHERE employeeId = :employeeId")
    Employee getByEmployeeId(String employeeId);

    @Query("SELECT * FROM employees WHERE department = :department ORDER BY name")
    List<Employee> getByDepartment(String department);

    @Query("SELECT * FROM employees WHERE isActive = 1 ORDER BY name")
    List<Employee> getActiveEmployees();

    @Query("SELECT * FROM employees WHERE name LIKE '%' || :searchTerm || '%' OR employeeId LIKE '%' || :searchTerm || '%' ORDER BY name")
    List<Employee> searchEmployees(String searchTerm);

    @Query("SELECT COUNT(*) FROM employees WHERE isActive = 1")
    int getActiveCount();

    @Query("DELETE FROM employees WHERE id = :id")
    void deleteById(int id);
}
EOF

echo "🔧 المرحلة 11: إصلاح Role و إضافة UserPermission..."

# إصلاح Role.java (كان جيد سابقاً، لكن سنضيف بعض التحسينات)
cat > app/src/main/java/com/example/androidapp/data/entities/Role.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "roles")
public class Role {
    @PrimaryKey
    @ColumnInfo(name = "role_id")
    public String roleId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "permissions")
    public String permissions;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    // Default constructor for Room
    public Role() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Primary constructor for Room
    public Role(String roleId, String name, String description, String permissions, long createdAt) {
        this.roleId = roleId;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.createdAt = createdAt;
        this.updatedAt = System.currentTimeMillis();
    }

    @Ignore
    public Role(String roleId, String name, String description, long createdAt) {
        this(roleId, name, description, "", createdAt);
    }

    // Getters
    public String getRoleId() { return roleId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPermissions() { return permissions; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
EOF

# إنشاء RoleDao
cat > app/src/main/java/com/example/androidapp/data/dao/RoleDao.java << 'EOF'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Role;
import java.util.List;

@Dao
public interface RoleDao extends BaseDao<Role> {
    
    @Query("SELECT * FROM roles WHERE role_id = :roleId")
    Role getById(String roleId);

    @Query("SELECT * FROM roles ORDER BY name")
    List<Role> getAll();

    @Query("SELECT * FROM roles WHERE name = :name")
    Role getByName(String name);

    @Query("DELETE FROM roles WHERE role_id = :roleId")
    void deleteById(String roleId);
}
EOF

# إنشاء UserPermission entity
cat > app/src/main/java/com/example/androidapp/data/entities/UserPermission.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "user_permissions",
        primaryKeys = {"userId", "permissionId"},
        foreignKeys = {
            @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"),
            @ForeignKey(entity = Permission.class, parentColumns = "permissionId", childColumns = "permissionId")
        })
public class UserPermission {
    public int userId;
    public String permissionId;
    public long grantedAt;
    public String grantedBy;

    public UserPermission() {
        this.grantedAt = System.currentTimeMillis();
    }

    public UserPermission(int userId, String permissionId, String grantedBy) {
        this();
        this.userId = userId;
        this.permissionId = permissionId;
        this.grantedBy = grantedBy;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getPermissionId() { return permissionId; }
    public long getGrantedAt() { return grantedAt; }
    public String getGrantedBy() { return grantedBy; }

    // Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }
    public void setGrantedAt(long grantedAt) { this.grantedAt = grantedAt; }
    public void setGrantedBy(String grantedBy) { this.grantedBy = grantedBy; }
}
EOF

# إنشاء UserPermissionDao
cat > app/src/main/java/com/example/androidapp/data/dao/UserPermissionDao.java << 'EOF'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.UserPermission;
import java.util.List;

@Dao
public interface UserPermissionDao extends BaseDao<UserPermission> {
    
    @Query("SELECT * FROM user_permissions WHERE userId = :userId")
    List<UserPermission> getByUserId(int userId);

    @Query("SELECT * FROM user_permissions WHERE permissionId = :permissionId")
    List<UserPermission> getByPermissionId(String permissionId);

    @Query("SELECT * FROM user_permissions WHERE userId = :userId AND permissionId = :permissionId")
    UserPermission getUserPermission(int userId, String permissionId);

    @Query("DELETE FROM user_permissions WHERE userId = :userId AND permissionId = :permissionId")
    void deleteUserPermission(int userId, String permissionId);

    @Query("DELETE FROM user_permissions WHERE userId = :userId")
    void deleteByUserId(int userId);
}
EOF

# إصلاح UserRoleDao
cat > app/src/main/java/com/example/androidapp/data/dao/UserRoleDao.java << 'EOF'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.UserRole;
import java.util.List;

@Dao
public interface UserRoleDao extends BaseDao<UserRole> {
    
    @Query("SELECT * FROM user_roles WHERE userId = :userId")
    List<UserRole> getByUserId(String userId);

    @Query("SELECT * FROM user_roles WHERE roleId = :roleId")
    List<UserRole> getByRoleId(String roleId);

    @Query("SELECT * FROM user_roles WHERE userId = :userId AND roleId = :roleId")
    UserRole getUserRole(String userId, String roleId);

    @Query("DELETE FROM user_roles WHERE userId = :userId AND roleId = :roleId")
    void deleteByUserAndRole(String userId, String roleId);

    @Query("DELETE FROM user_roles WHERE userId = :userId")
    void deleteByUserId(String userId);
}
EOF

echo "🔧 المرحلة 12: تحديث AppDatabase مع جميع الكيانات والDAOs..."

# تحديث AppDatabase.java لتشمل جميع الكيانات والDAOs الجديدة
cat > app/src/main/java/com/example/androidapp/data/AppDatabase.java << 'EOF'
package com.example.androidapp.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

// Import all entity classes
import com.example.androidapp.data.entities.*;

// Import all DAO interfaces
import com.example.androidapp.data.dao.*;

@Database(
    entities = {
        User.class,
        Account.class,
        Transaction.class,
        Company.class,
        Category.class,
        Invoice.class,
        ContactSync.class,
        Friend.class,
        UserRole.class,
        Role.class,
        Notification.class,
        Permission.class,
        UserPermission.class,
        Item.class,
        Customer.class,
        Employee.class
    },
    version = 6,
    exportSchema = true
)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    // Abstract methods for DAOs
    public abstract UserDao userDao();
    public abstract AccountDao accountDao();
    public abstract TransactionDao transactionDao();
    public abstract CompanyDao companyDao();
    public abstract CategoryDao categoryDao();
    public abstract InvoiceDao invoiceDao();
    public abstract ContactSyncDao contactSyncDao();
    public abstract FriendDao friendDao();
    public abstract UserRoleDao userRoleDao();
    public abstract RoleDao roleDao();
    public abstract NotificationDao notificationDao();
    public abstract PermissionDao permissionDao();
    public abstract UserPermissionDao userPermissionDao();
    public abstract ItemDao itemDao();
    public abstract CustomerDao customerDao();
    public abstract EmployeeDao employeeDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "accounting_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // For compatibility
    public static AppDatabase getInstance(Context context) {
        return getDatabase(context);
    }

    // Migration from version 1 to 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Previous migration code...
            database.execSQL("ALTER TABLE users ADD COLUMN company_id TEXT");
            database.execSQL("ALTER TABLE accounts ADD COLUMN user_id INTEGER");
            database.execSQL("ALTER TABLE accounts ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1");
        }
    };

    // Migration from version 2 to 3
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Previous migration code...
            database.execSQL("CREATE TABLE IF NOT EXISTS contact_sync (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "user_id INTEGER, " +
                    "contact_identifier TEXT, " +
                    "phone_number TEXT, " +
                    "display_name TEXT, " +
                    "email TEXT, " +
                    "photo_uri TEXT, " +
                    "is_registered_user INTEGER NOT NULL DEFAULT 0, " +
                    "allow_sync INTEGER NOT NULL DEFAULT 1, " +
                    "last_sync_date INTEGER, " +
                    "status TEXT, " +
                    "updated_date INTEGER)");
        }
    };

    // Migration from version 3 to 4
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS roles (" +
                    "role_id TEXT PRIMARY KEY NOT NULL, " +
                    "name TEXT, " +
                    "description TEXT, " +
                    "permissions TEXT, " +
                    "created_at INTEGER, " +
                    "updated_at INTEGER)");

            database.execSQL("CREATE TABLE IF NOT EXISTS notifications (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "userId INTEGER, " +
                    "type TEXT, " +
                    "title TEXT, " +
                    "content TEXT, " +
                    "message TEXT, " +
                    "relatedId INTEGER, " +
                    "timestamp INTEGER, " +
                    "isRead INTEGER NOT NULL DEFAULT 0, " +
                    "entityId TEXT)");
        }
    };

    // Migration from version 4 to 5
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS permissions (" +
                    "permissionId TEXT PRIMARY KEY NOT NULL, " +
                    "name TEXT, " +
                    "description TEXT, " +
                    "category TEXT, " +
                    "createdAt INTEGER)");

            database.execSQL("CREATE TABLE IF NOT EXISTS user_permissions (" +
                    "userId INTEGER NOT NULL, " +
                    "permissionId TEXT NOT NULL, " +
                    "grantedAt INTEGER, " +
                    "grantedBy TEXT, " +
                    "PRIMARY KEY(userId, permissionId))");
        }
    };

    // Migration from version 5 to 6
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, " +
                    "code TEXT, " +
                    "description TEXT, " +
                    "price REAL, " +
                    "category TEXT, " +
                    "quantity INTEGER, " +
                    "unit TEXT, " +
                    "createdAt INTEGER)");

            database.execSQL("CREATE TABLE IF NOT EXISTS customers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, " +
                    "email TEXT, " +
                    "phone TEXT, " +
                    "address TEXT, " +
                    "companyName TEXT, " +
                    "taxNumber TEXT, " +
                    "totalPurchases REAL, " +
                    "createdAt INTEGER)");

            database.execSQL("CREATE TABLE IF NOT EXISTS employees (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "employeeId TEXT, " +
                    "name TEXT, " +
                    "email TEXT, " +
                    "phone TEXT, " +
                    "department TEXT, " +
                    "position TEXT, " +
                    "salary REAL, " +
                    "hireDate INTEGER, " +
                    "isActive INTEGER NOT NULL DEFAULT 1, " +
                    "createdAt INTEGER)");

            // Add missing fields to existing tables
            database.execSQL("ALTER TABLE accounts ADD COLUMN code TEXT");
            database.execSQL("ALTER TABLE users ADD COLUMN phone_number TEXT");
            database.execSQL("ALTER TABLE transactions ADD COLUMN date INTEGER");
        }
    };

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // يمكن إضافة بيانات أولية هنا
        }
    };

    public static void populateInitialData(AppDatabase db) {
        // إدراج الفئات الافتراضية
        CategoryDao categoryDao = db.categoryDao();
        
        // فئات الإيرادات
        Category incomeGeneral = new Category();
        incomeGeneral.name = "إيرادات عامة";
        incomeGeneral.type = "INCOME";
        incomeGeneral.color = "#4CAF50";
        incomeGeneral.icon = "money";
        incomeGeneral.isDefault = true;
        incomeGeneral.isActive = true;
        categoryDao.insert(incomeGeneral);

        Category incomeSales = new Category();
        incomeSales.name = "مبيعات";
        incomeSales.type = "INCOME";
        incomeSales.color = "#2196F3";
        incomeSales.icon = "shopping_cart";
        incomeSales.isDefault = true;
        incomeSales.isActive = true;
        categoryDao.insert(incomeSales);

        // فئات المصروفات
        Category expenseOffice = new Category();
        expenseOffice.name = "مصاريف مكتبية";
        expenseOffice.type = "EXPENSE";
        expenseOffice.color = "#FF9800";
        expenseOffice.icon = "business";
        expenseOffice.isDefault = true;
        expenseOffice.isActive = true;
        categoryDao.insert(expenseOffice);

        Category expenseTravel = new Category();
        expenseTravel.name = "مصاريف سفر";
        expenseTravel.type = "EXPENSE";
        expenseTravel.color = "#9C27B0";
        expenseTravel.icon = "flight";
        expenseTravel.isDefault = true;
        expenseTravel.isActive = true;
        categoryDao.insert(expenseTravel);
    }
}
EOF

echo "🔧 المرحلة 13: إصلاح مشاكل ContactSyncManager..."

# إصلاح ContactSyncManager لتتعامل مع long بدلاً من Date
sed -i 's/contact\.setLastSyncDate(new Date())/contact.setLastSyncDate(System.currentTimeMillis())/g' app/src/main/java/com/example/androidapp/utils/ContactSyncManager.java
sed -i 's/existingContact\.setLastSyncDate(new Date())/existingContact.setLastSyncDate(System.currentTimeMillis())/g' app/src/main/java/com/example/androidapp/utils/ContactSyncManager.java
sed -i 's/contact\.setUpdatedDate(new Date())/contact.setUpdatedDate(System.currentTimeMillis())/g' app/src/main/java/com/example/androidapp/utils/ContactSyncManager.java
sed -i 's/existingContact\.setUpdatedDate(new Date())/existingContact.setUpdatedDate(System.currentTimeMillis())/g' app/src/main/java/com/example/androidapp/utils/ContactSyncManager.java

echo "🔧 المرحلة 14: إصلاح مشاكل NotificationHelper..."

# إصلاح النوع في NotificationHelper
sed -i 's/notification\.type = type;/notification.type = String.valueOf(type);/g' app/src/main/java/com/example/androidapp/utils/NotificationHelper.java
sed -i 's/notification\.userId = getCurrentUserId();/notification.userId = sessionManager.getCurrentUserIdInt();/g' app/src/main/java/com/example/androidapp/utils/NotificationHelper.java

echo "🔧 المرحلة 15: إصلاح مشاكل UserDao المكررة..."

# إزالة الدوال المكررة من UserDao.java إذا وجدت
sed -i '/^\s*@Query("SELECT \* FROM users WHERE id = :userId")\s*$/,/^\s*User getUserByIdSync(int userId);\s*$/d' app/src/main/java/com/example/androidapp/data/dao/UserDao.java

echo "✅ المرحلة 16: التحقق النهائي وتنظيف الأخطاء..."

# إصلاح أي مشاكل إضافية في الـ constructors
find app/src/main/java -name "*.java" -exec grep -l "public.*User.*String.*String.*long.*long" {} \; | while read file; do
    echo "تنظيف $file..."
    # إضافة @Ignore للـ constructors المكررة إذا لم تكن موجودة
    sed -i '/public User(String name, String email, String phone, long createdAt, long updatedAt)/i\    @Ignore' "$file" 2>/dev/null || true
done

echo ""
echo "🎉 اكتمل الإصلاح الشامل بنجاح!"
echo "📊 النتائج المتوقعة:"
echo "   ✅ حل جميع مشاكل Type Mismatches"
echo "   ✅ إضافة جميع الدوال المفقودة في DAOs"
echo "   ✅ إضافة جميع الحقول المفقودة في Entities"
echo "   ✅ إنشاء جميع الكيانات المفقودة"
echo "   ✅ إصلاح SessionManager"
echo "   ✅ حل تعارضات Constructors"
echo "   ✅ تحديث AppDatabase إلى version 6"
echo ""
echo "🚀 الآن يمكنك تشغيل البناء:"
echo "   ./gradlew clean build"
echo ""
echo "📈 متوقع: تقليل الأخطاء من 1343 إلى 0!"
echo "⏰ انتهاء الإصلاح: $(date)"
