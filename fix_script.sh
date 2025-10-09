#!/bin/bash

# --- File: app/build.gradle ---
cat > app/build.gradle << 'EOP'
plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services'
}

android {
    namespace 'com.example.androidapp'
    compileSdk 34

    defaultConfig {
        applicationId "com.example.androidapp"
        minSdk 26
        targetSdk 34
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        dataBinding true
        viewBinding true
    }
    signingConfigs {
        release {
            storeFile file("my-release-key.jks")
            storePassword "كلمة_مرور_الكي_ستور"
            keyAlias "myalias"
            keyPassword "كلمة_مرور_الكي_ستور"
        }
    }

    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    packagingOptions {
        resources {
            excludes += [
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            ]
        }
    }
}

dependencies {
    implementation 'com.google.api-client:google-api-client-android:2.2.0'
    implementation 'com.google.http-client:google-http-client-gson:1.42.3'
    implementation 'com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0'
    implementation 'com.google.android.gms:play-services-auth:20.7.0'
    
    annotationProcessor "androidx.room:room-compiler:2.6.1"
    implementation 'androidx.room:room-runtime:2.6.1'
    implementation 'androidx.room:room-guava:2.6.1'
    
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.10.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'com.google.firebase:firebase-auth:22.3.0'
    implementation 'com.google.firebase:firebase-firestore:24.10.0'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'androidx.navigation:navigation-fragment:2.7.5'
    implementation 'androidx.navigation:navigation-ui:2.7.5'
    implementation 'org.apache.poi:poi-ooxml:5.2.3'
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.mockito:mockito-core:5.8.0'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    androidTestImplementation 'androidx.room:room-testing:2.6.1'

    implementation 'com.google.firebase:firebase-messaging:23.4.1'
    implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
    implementation 'com.google.zxing:core:3.5.2'
    implementation 'com.google.firebase:firebase-functions:20.4.0'
    
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0'
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/AppDatabase.java ---
cat > app/src/main/java/com/example/androidapp/data/AppDatabase.java << 'EOP'
package com.example.androidapp.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.androidapp.data.dao.*;
import com.example.androidapp.data.entities.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        User.class, Company.class, Role.class, Permission.class, Membership.class,
        Account.class, Item.class, ItemUnit.class, Customer.class, Supplier.class,
        Invoice.class, InvoiceItem.class, JournalEntry.class, JournalEntryItem.class,
        Payment.class, Receipt.class, Reminder.class, Notification.class, Campaign.class,
        DeliveryReceipt.class, Connection.class, SharedLink.class, CompanySettings.class,
        PointTransaction.class, Reward.class, UserReward.class, Employee.class,
        Payroll.class, PayrollItem.class, Service.class, Doctor.class, Voucher.class,
        FinancialTransfer.class, CurrencyExchange.class, JoinRequest.class, Chat.class, Repair.class, Order.class, Trophy.class, UserPermission.class, UserRole.class, AccountStatement.class, UserTrophy.class,
        Warehouse.class, Inventory.class, Post.class, Comment.class, Like.class, Share.class, ContactSync.class, Friend.class, AuditLog.class,
        Purchase.class
},
        version = 3, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract PurchaseDao purchaseDao();

    public abstract AccountDao accountDao();
    public abstract AccountStatementDao accountStatementDao();
    public abstract AuditLogDao auditLogDao();
    public abstract CampaignDao campaignDao();
    public abstract ChatDao chatDao();
    public abstract CommentDao commentDao();
    public abstract CompanyDao companyDao();
    public abstract CompanySettingsDao companySettingsDao();
    public abstract ConnectionDao connectionDao();
    public abstract ContactSyncDao contactSyncDao();
    public abstract CurrencyExchangeDao currencyExchangeDao();
    public abstract CustomerDao customerDao();
    public abstract DeliveryReceiptDao deliveryReceiptDao();
    public abstract DoctorDao doctorDao();
    public abstract EmployeeDao employeeDao();
    public abstract FinancialTransferDao financialTransferDao();
    public abstract FriendDao friendDao();
    public abstract InventoryDao inventoryDao();
    public abstract InvoiceDao invoiceDao();
    public abstract InvoiceItemDao invoiceItemDao();
    public abstract ItemDao itemDao();
    public abstract ItemUnitDao itemUnitDao();
    public abstract JoinRequestDao joinRequestDao();
    public abstract JournalEntryDao journalEntryDao();
    public abstract JournalEntryItemDao journalEntryItemDao();
    public abstract LikeDao likeDao();
    public abstract MembershipDao membershipDao();
    public abstract NotificationDao notificationDao();
    public abstract OrderDao orderDao();
    public abstract PaymentDao paymentDao();
    public abstract PayrollDao payrollDao();
    public abstract PayrollItemDao payrollItemDao();
    public abstract PermissionDao permissionDao();
    public abstract PointTransactionDao pointTransactionDao();
    public abstract PostDao postDao();
    public abstract ReceiptDao receiptDao();
    public abstract ReminderDao reminderDao();
    public abstract RepairDao repairDao();
    public abstract RewardDao rewardDao();
    public abstract RoleDao roleDao();
    public abstract ServiceDao serviceDao();
    public abstract ShareDao shareDao();
    public abstract SharedLinkDao sharedLinkDao();
    public abstract SupplierDao supplierDao();
    public abstract TrophyDao trophyDao();
    public abstract UserDao userDao();
    public abstract UserPermissionDao userPermissionDao();
    public abstract UserRewardDao userRewardDao();
    public abstract UserRoleDao userRoleDao();
    public abstract UserTrophyDao userTrophyDao();
    public abstract VoucherDao voucherDao();
    public abstract WarehouseDao warehouseDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "business_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/AccountStatementDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/AccountStatementDao.java << 'EOP'
package com.example.androidapp.data.dao;

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

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId AND date <= :transactionDate ORDER BY date DESC, id DESC LIMIT 1")
    AccountStatement getLastStatementBeforeDate(String companyId, String accountId, String transactionDate);

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId AND date >= :startDate ORDER BY date ASC, id ASC")
    List<AccountStatement> getStatementsForRecalculation(String companyId, String accountId, String startDate);

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId AND date <= :transactionDate ORDER BY date DESC, id DESC")
    List<AccountStatement> getAccountStatementsForBalanceCalculation(String companyId, String accountId, String transactionDate);

    @Query("SELECT * FROM account_statements WHERE companyId = :companyId AND accountId = :accountId ORDER BY date DESC")
    LiveData<List<AccountStatement>> getAllAccountStatementsForAccount(String companyId, String accountId);
    
    @Query("SELECT * FROM account_statements WHERE id = :statementId AND companyId = :companyId AND accountId = :accountId")
    LiveData<AccountStatement> getAccountStatementById(int statementId, String companyId, String accountId);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/AuditLogDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/AuditLogDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.AuditLog;

@Dao
public interface AuditLogDao {
    @Insert
    void insert(AuditLog auditlog);

    @Update
    void update(AuditLog auditlog);

    @Delete
    void delete(AuditLog auditlog);

    @Query("SELECT * FROM audit_logs")
    List<AuditLog> getAllAuditLogs();

    @Query("SELECT * FROM audit_logs WHERE id = :id LIMIT 1")
    AuditLog getAuditLogById(String id);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/CampaignDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/CampaignDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.entities.Campaign;

import java.util.List;

@Dao
public interface CampaignDao extends BaseDao<Campaign> {
    @Query("SELECT * FROM campaigns WHERE companyId = :companyId ORDER BY createdAt DESC")
    LiveData<List<Campaign>> getAllCampaigns(String companyId);

    @Query("SELECT * FROM campaigns WHERE id = :campaignId AND companyId = :companyId")
    LiveData<Campaign> getCampaignById(String campaignId, String companyId);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/ChatDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/ChatDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.entities.Chat;

import java.util.List;

@Dao
public interface ChatDao extends BaseDao<Chat> {
    @Query("SELECT * FROM chats WHERE companyId = :companyId ORDER BY createdAt DESC")
    LiveData<List<Chat>> getAllChats(String companyId);

    @Query("SELECT * FROM chats WHERE (userId = :userId1 AND toUserId = :userId2) OR (userId = :userId2 AND toUserId = :userId1) AND companyId = :companyId ORDER BY createdAt ASC")
    LiveData<List<Chat>> getChatsBetweenUsers(String userId1, String userId2, String companyId);

    @Query("SELECT * FROM chats WHERE toUserId = :userId AND isRead = 0 AND companyId = :companyId")
    LiveData<List<Chat>> getUnreadChats(String userId, String companyId);

    @Query("SELECT COUNT(*) FROM chats WHERE toUserId = :userId AND isRead = 0 AND companyId = :companyId")
    LiveData<Integer> getUnreadChatCount(String userId, String companyId);

    @Query("UPDATE chats SET isRead = 1 WHERE toUserId = :userId AND userId = :senderId AND companyId = :companyId")
    void markChatsAsRead(String userId, String senderId, String companyId);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/CompanySettingsDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/CompanySettingsDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.CompanySettings;

@Dao
public interface CompanySettingsDao {
    @Insert
    void insert(CompanySettings companysettings);

    @Update
    void update(CompanySettings companysettings);

    @Delete
    void delete(CompanySettings companysettings);

    @Query("SELECT * FROM company_settings")
    List<CompanySettings> getAllCompanySettingss();

    @Query("SELECT * FROM company_settings WHERE id = :id LIMIT 1")
    CompanySettings getCompanySettingsById(String id);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/CurrencyExchangeDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/CurrencyExchangeDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.CurrencyExchange;

@Dao
public interface CurrencyExchangeDao {
    @Insert
    void insert(CurrencyExchange currencyexchange);

    @Update
    void update(CurrencyExchange currencyexchange);

    @Delete
    void delete(CurrencyExchange currencyexchange);

    @Query("SELECT * FROM currency_exchanges")
    List<CurrencyExchange> getAllCurrencyExchanges();

    @Query("SELECT * FROM currency_exchanges WHERE id = :id LIMIT 1")
    CurrencyExchange getCurrencyExchangeById(String id);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/DeliveryReceiptDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/DeliveryReceiptDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.DeliveryReceipt;

@Dao
public interface DeliveryReceiptDao {
    @Insert
    void insert(DeliveryReceipt deliveryreceipt);

    @Update
    void update(DeliveryReceipt deliveryreceipt);

    @Delete
    void delete(DeliveryReceipt deliveryreceipt);

    @Query("SELECT * FROM delivery_receipts")
    List<DeliveryReceipt> getAllDeliveryReceipts();

    @Query("SELECT * FROM delivery_receipts WHERE id = :id LIMIT 1")
    DeliveryReceipt getDeliveryReceiptById(String id);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/FinancialTransferDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/FinancialTransferDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.FinancialTransfer;

@Dao
public interface FinancialTransferDao {
    @Insert
    void insert(FinancialTransfer financialtransfer);

    @Update
    void update(FinancialTransfer financialtransfer);

    @Delete
    void delete(FinancialTransfer financialtransfer);

    @Query("SELECT * FROM financial_transfers")
    List<FinancialTransfer> getAllFinancialTransfers();

    @Query("SELECT * FROM financial_transfers WHERE id = :id LIMIT 1")
    FinancialTransfer getFinancialTransferById(String id);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/FriendDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/FriendDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Friend;
import com.example.androidapp.data.entities.User;

import java.util.List;

@Dao
public interface FriendDao {
    
    @Insert
    long insert(Friend friend);
    
    @Update
    int update(Friend friend);
    
    @Delete
    int delete(Friend friend);
    
    @Query("SELECT * FROM friends WHERE id = :id")
    Friend getFriendById(String id);
    
    @Query("SELECT * FROM friends WHERE userId = :userId AND status = :status ORDER BY acceptedDate DESC")
    LiveData<List<Friend>> getFriendsByStatus(String userId, String status);
    
    @Query("SELECT * FROM friends WHERE (userId = :userId AND friendId = :friendId) OR (userId = :friendId AND friendId = :userId)")
    Friend getFriendship(String userId, String friendId);
    
    @Query("SELECT u.* FROM Users u INNER JOIN Friends f ON u.id = f.friendId WHERE f.userId = :userId AND f.status = 'ACCEPTED' AND u.isOnline = 1")
    LiveData<List<User>> getOnlineFriends(String userId);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/ItemUnitDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/ItemUnitDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.ItemUnit;

@Dao
public interface ItemUnitDao {
    @Insert
    void insert(ItemUnit itemunit);

    @Update
    void update(ItemUnit itemunit);

    @Delete
    void delete(ItemUnit itemunit);

    @Query("SELECT * FROM item_units")
    List<ItemUnit> getAllItemUnits();

    @Query("SELECT * FROM item_units WHERE id = :id LIMIT 1")
    ItemUnit getItemUnitById(String id);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/JoinRequestDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/JoinRequestDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.JoinRequest;

@Dao
public interface JoinRequestDao {
    @Insert
    void insert(JoinRequest joinrequest);

    @Update
    void update(JoinRequest joinrequest);

    @Delete
    void delete(JoinRequest joinrequest);

    @Query("SELECT * FROM join_requests")
    List<JoinRequest> getAllJoinRequests();

    @Query("SELECT * FROM join_requests WHERE id = :id LIMIT 1")
    JoinRequest getJoinRequestById(String id);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/OrderDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/OrderDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Order;

import java.util.List;

@Dao
public interface OrderDao extends BaseDao<Order> {
    @Query("SELECT * FROM orders WHERE companyId = :companyId ORDER BY createdAt DESC")
    LiveData<List<Order>> getAllOrders(String companyId);

    @Query("SELECT * FROM orders WHERE id = :orderId AND companyId = :companyId")
    LiveData<Order> getOrderById(String orderId, String companyId);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/PayrollDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/PayrollDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.Payroll;

@Dao
public interface PayrollDao {
    @Insert
    void insert(Payroll payroll);

    @Update
    void update(Payroll payroll);

    @Delete
    void delete(Payroll payroll);

    @Query("SELECT * FROM payrolls")
    List<Payroll> getAllPayrolls();

    @Query("SELECT * FROM payrolls WHERE id = :id LIMIT 1")
    Payroll getPayrollById(String id);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/PayrollItemDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/PayrollItemDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.PayrollItem;

@Dao
public interface PayrollItemDao {
    @Insert
    void insert(PayrollItem payrollitem);

    @Update
    void update(PayrollItem payrollitem);

    @Delete
    void delete(PayrollItem payrollitem);

    @Query("SELECT * FROM payroll_items")
    List<PayrollItem> getAllPayrollItems();

    @Query("SELECT * FROM payroll_items WHERE id = :id LIMIT 1")
    PayrollItem getPayrollItemById(String id);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/PointTransactionDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/PointTransactionDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.entities.PointTransaction;

import java.util.List;

@Dao
public interface PointTransactionDao extends BaseDao<PointTransaction> {
    @Query("SELECT * FROM point_transactions WHERE companyId = :companyId")
    LiveData<List<PointTransaction>> getAllPointTransactions(String companyId);

    @Query("SELECT * FROM point_transactions WHERE id = :id AND companyId = :companyId")
    LiveData<PointTransaction> getPointTransactionById(String id, String companyId);

    @Query("SELECT SUM(points) FROM point_transactions WHERE userId = :userId AND companyId = :companyId")
    LiveData<Integer> getTotalPointsForUser(String userId, String companyId);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/RepairDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/RepairDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Repair;

import java.util.List;

@Dao
public interface RepairDao extends BaseDao<Repair> {
    @Query("SELECT * FROM repairs WHERE companyId = :companyId ORDER BY startDate DESC")
    LiveData<List<Repair>> getAllRepairs(String companyId);

    @Query("SELECT * FROM repairs WHERE id = :repairId AND companyId = :companyId")
    LiveData<Repair> getRepairById(String repairId, String companyId);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/RewardDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/RewardDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.entities.Reward;

import java.util.List;

@Dao
public interface RewardDao extends BaseDao<Reward> {
    @Query("SELECT * FROM rewards WHERE companyId = :companyId")
    LiveData<List<Reward>> getAllRewards(String companyId);

    @Query("SELECT * FROM rewards WHERE id = :id AND companyId = :companyId")
    LiveData<Reward> getRewardById(String id, String companyId);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/SharedLinkDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/SharedLinkDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.SharedLink;

@Dao
public interface SharedLinkDao {
    @Insert
    void insert(SharedLink sharedlink);

    @Update
    void update(SharedLink sharedlink);

    @Delete
    void delete(SharedLink sharedlink);

    @Query("SELECT * FROM shared_links")
    List<SharedLink> getAllSharedLinks();

    @Query("SELECT * FROM shared_links WHERE id = :id LIMIT 1")
    SharedLink getSharedLinkById(String id);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/TrophyDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/TrophyDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Trophy;

import java.util.List;

@Dao
public interface TrophyDao extends BaseDao<Trophy> {
    @Query("SELECT * FROM trophies WHERE companyId = :companyId ORDER BY name ASC")
    LiveData<List<Trophy>> getAllTrophies(String companyId);

    @Query("SELECT * FROM trophies WHERE id = :trophyId AND companyId = :companyId")
    LiveData<Trophy> getTrophyById(String trophyId, String companyId);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/dao/UserRewardDao.java ---
cat > app/src/main/java/com/example/androidapp/data/dao/UserRewardDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.entities.UserReward;

import java.util.List;

@Dao
public interface UserRewardDao extends BaseDao<UserReward> {
    @Query("SELECT * FROM user_rewards WHERE companyId = :companyId")
    LiveData<List<UserReward>> getAllUserRewards(String companyId);

    @Query("SELECT * FROM user_rewards WHERE userId = :userId AND companyId = :companyId")
    LiveData<List<UserReward>> getUserRewardsByUserId(String userId, String companyId);

    @Query("SELECT * FROM user_rewards WHERE id = :id AND companyId = :companyId")
    LiveData<UserReward> getUserRewardById(String id, String companyId);
}
EOP

# --- File: app/src/main/java/com/example/androidapp/utils/GoogleDriveService.java ---
cat > app/src/main/java/com/example/androidapp/utils/GoogleDriveService.java << 'EOP'
package com.example.androidapp.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class GoogleDriveService {

    private static final String TAG = "GoogleDriveService";
    private GoogleSignInClient googleSignInClient;
    private Drive driveService;
    private Context context;

    public GoogleDriveService(Context context) {
        this.context = context;
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, signInOptions);
    }

    public Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    public void initializeDriveClient(String accountName
) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context,
                Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccountName(accountName);
        driveService = new Drive.Builder(
                new NetHttpTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName("Android Accounting App")
                .build();
        Log.d(TAG, "Google Drive client initialized.");
    }

    public Drive getDriveService() {
        return driveService;
    }

    public void uploadFile(String filePath, String mimeType, String folderName, DriveServiceCallback callback) {
        if (driveService == null) {
            callback.onFailure(new IllegalStateException("Google Drive service not initialized."));
            return;
        }

        new Thread(() -> {
            try {
                File fileContent = new File(filePath);
                com.google.api.client.http.FileContent mediaContent = new com.google.api.client.http.FileContent(mimeType, fileContent);

                // Check if folder exists, if not, create it
                String folderId = getOrCreateFolderId(folderName);

                com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
                fileMetadata.setName(fileContent.getName());
                fileMetadata.setParents(Collections.singletonList(folderId));

                com.google.api.services.drive.model.File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id,name")
                        .execute();

                callback.onSuccess(uploadedFile.getName() + " uploaded with ID: " + uploadedFile.getId());
            } catch (Exception e) {
                Log.e(TAG, "Error uploading file to Google Drive", e);
                callback.onFailure(e);
            }
        }).start();
    }

    private String getOrCreateFolderId(String folderName) throws Exception {
        // Search for the folder
        Drive.Files.List request = driveService.files().list()
                .setQ("mimeType='application/vnd.google-apps.folder' and name='" + folderName + "' and trashed = false")
                .setSpaces("drive");
        List<com.google.api.services.drive.model.File> files = request.execute().getFiles();

        if (files != null && !files.isEmpty()) {
            return files.get(0).getId(); // Folder found
        } else {
            // Folder not found, create it
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(folderName);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            com.google.api.services.drive.model.File folder = driveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            return folder.getId();
        }
    }

    public interface DriveServiceCallback {
        void onSuccess(String message);
        void onFailure(Exception e);
    }

    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(context) != null;
    }

    public void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        driveService = null;
                        Log.d(TAG, "Signed out from Google Drive.");
                    } else {
                        Log.e(TAG, "Error signing out from Google Drive", task.getException());
                    }
                });
    }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/logic/InventoryManager.java ---
cat > app/src/main/java/com/example/androidapp/logic/InventoryManager.java << 'EOP'
package com.example.androidapp.logic;

import android.content.Context;
import android.util.Log;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.InventoryDao;
import com.example.androidapp.data.dao.ItemDao;
import com.example.androidapp.data.entities.Inventory;
import com.example.androidapp.data.entities.Item;

import java.util.List;

public class InventoryManager {
    private static final String TAG = "InventoryManager";
    private final InventoryDao inventoryDao;
    private final ItemDao itemDao;

    public InventoryManager(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.inventoryDao = db.inventoryDao();
        this.itemDao = db.itemDao();
    }

    public void addInventory(String itemId, String warehouseId, float quantity, String companyId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Inventory inventory = inventoryDao.getInventoryByItemAndWarehouse(itemId, warehouseId, companyId);
                if (inventory != null) {
                    inventory.setQuantity(inventory.getQuantity() + quantity);
                    inventoryDao.update(inventory);
                } else {
                    // Assuming a default cost price for new inventory items. This should be improved.
                    inventory = new Inventory(itemId, companyId, itemId, warehouseId, quantity, 0, "");
                    inventoryDao.insert(inventory);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error adding inventory: " + e.getMessage());
            }
        });
    }

    public void removeInventory(String itemId, String warehouseId, float quantity, String companyId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Inventory inventory = inventoryDao.getInventoryByItemAndWarehouse(itemId, warehouseId, companyId);
                if (inventory != null) {
                    float newQuantity = inventory.getQuantity() - quantity;
                    inventory.setQuantity(newQuantity);
                    inventoryDao.update(inventory);
                } else {
                    Log.e(TAG, "No inventory found for item " + itemId + " in warehouse " + warehouseId);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error removing inventory: " + e.getMessage());
            }
        });
    }

    public void transferItem(String itemId, String fromWarehouseId, String toWarehouseId, float quantity, String companyId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            removeInventory(itemId, fromWarehouseId, quantity, companyId);
            addInventory(itemId, toWarehouseId, quantity, companyId);
        });
    }

    public void checkLowStockAlerts(String companyId, LowStockAlertCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<Item> allItems = itemDao.getAllItems(companyId);
                for (Item item : allItems) {
                    float totalStock = inventoryDao.getTotalQuantityByItem(item.getId(), companyId);
                    // This assumes Item entity has a minStockLevel field, which it doesn't.
                    // This logic needs to be adapted or the Item entity updated.
                    // For now, we'll use a placeholder logic.
                    if (item.getReorderLevel() != null && totalStock < item.getReorderLevel()) {
                        if (callback != null) callback.onLowStock(item, totalStock);
                    }
                }
            } catch (Exception e) {
                if (callback != null) callback.onFailure(e.getMessage());
            }
        });
    }

    public interface LowStockAlertCallback {
        void onLowStock(Item item, float currentStock);
        void onFailure(String message);
    }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/entities/AccountStatement.java ---
cat > app/src/main/java/com/example/androidapp/data/entities/AccountStatement.java << 'EOP'
package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "account_statements",
        foreignKeys = {
                @ForeignKey(entity = Account.class,
                            parentColumns = "id",
                            childColumns = "accountId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "companyId",
                            onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "accountId"), @Index(value = "companyId")})
public class AccountStatement {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private @NonNull String companyId;
    private @NonNull String accountId;
    private @NonNull String date;
    private @NonNull String description;
    private float debit;
    private float credit;
    private float runningBalance;
    private String referenceType;
    private String referenceId;

    public AccountStatement(@NonNull String companyId, @NonNull String accountId, @NonNull String date, @NonNull String description, float debit, float credit, float runningBalance, String referenceType, String referenceId) {
        this.companyId = companyId;
        this.accountId = accountId;
        this.date = date;
        this.description = description;
        this.debit = debit;
        this.credit = credit;
        this.runningBalance = runningBalance;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    @NonNull
    public String getAccountId() { return accountId; }
    public void setAccountId(@NonNull String accountId) { this.accountId = accountId; }
    @NonNull
    public String getDate() { return date; }
    public void setDate(@NonNull String date) { this.date = date; }
    @NonNull
    public String getDescription() { return description; }
    public void setDescription(@NonNull String description) { this.description = description; }
    public float getDebit() { return debit; }
    public void setDebit(float debit) { this.debit = debit; }
    public float getCredit() { return credit; }
    public void setCredit(float credit) { this.credit = credit; }
    public float getRunningBalance() { return runningBalance; }
    public void setRunningBalance(float runningBalance) { this.runningBalance = runningBalance; }
    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/entities/Campaign.java ---
cat > app/src/main/java/com/example/androidapp/data/entities/Campaign.java << 'EOP'
package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "campaigns",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class Campaign {
    @PrimaryKey
    private @NonNull String id;
    private String name;
    private String description;
    private @NonNull String companyId;
    private String createdAt;
    private String startDate;

    public Campaign(@NonNull String id, String name, String description, @NonNull String companyId, String createdAt, String startDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.companyId = companyId;
        this.createdAt = createdAt;
        this.startDate = startDate;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/entities/Chat.java ---
cat > app/src/main/java/com/example/androidapp/data/entities/Chat.java << 'EOP'
package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "chats",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "userId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "toUserId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "companyId"), @Index(value = "userId"), @Index(value = "toUserId")})
public class Chat {
    @PrimaryKey
    private @NonNull String id;
    private String message;
    private @NonNull String companyId;
    private @NonNull String userId;
    private @NonNull String toUserId;
    private @NonNull Date createdAt;
    private boolean isRead;
    private String senderId;
    private String receiverId;
    private String orgId;

    public Chat(@NonNull String id, String message, @NonNull String companyId, @NonNull String userId, @NonNull String toUserId, @NonNull Date createdAt, boolean isRead, String senderId, String receiverId, String orgId) {
        this.id = id;
        this.message = message;
        this.companyId = companyId;
        this.userId = userId;
        this.toUserId = toUserId;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.orgId = orgId;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }
    @NonNull
    public String getToUserId() { return toUserId; }
    public void setToUserId(@NonNull String toUserId) { this.toUserId = toUserId; }
    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/entities/User.java ---
cat > app/src/main/java/com/example/androidapp/data/entities/User.java << 'EOP'
package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "personalCompanyId",
                                  onDelete = ForeignKey.SET_NULL),
        indices = {@Index(value = "personalCompanyId")})
public class User {
    @PrimaryKey
    private @NonNull String id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String phoneHash;
    private int points;
    private String createdAt;
    private String updatedAt;
    private String personalCompanyId;
    private boolean isOnline;

    public User(@NonNull String id, String email, String password, String name, String phone, String phoneHash, int points, String createdAt, String updatedAt, String personalCompanyId, boolean isOnline) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.phoneHash = phoneHash;
        this.points = points;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.personalCompanyId = personalCompanyId;
        this.isOnline = isOnline;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhoneHash() { return phoneHash; }
    public void setPhoneHash(String phoneHash) { this.phoneHash = phoneHash; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public String getPersonalCompanyId() { return personalCompanyId; }
    public void setPersonalCompanyId(String personalCompanyId) { this.personalCompanyId = personalCompanyId; }
    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/entities/Order.java ---
cat > app/src/main/java/com/example/androidapp/data/entities/Order.java << 'EOP'
package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "orders",
        foreignKeys = {
                @ForeignKey(entity = Customer.class,
                           parentColumns = "id",
                           childColumns = "customerId",
                           onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "customerId"), @Index(value = "companyId")})
public class Order {
    @PrimaryKey
    public @NonNull String id;
    public String customerId;
    public @NonNull String companyId;
    public double totalAmount;
    public @NonNull Date createdAt;
    public @NonNull Date orderDate;

    public Order(@NonNull String id, String customerId, @NonNull String companyId, double totalAmount, @NonNull Date createdAt, @NonNull Date orderDate) {
        this.id = id;
        this.customerId = customerId;
        this.companyId = companyId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.orderDate = orderDate;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }
    @NonNull
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(@NonNull Date orderDate) { this.orderDate = orderDate; }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/entities/PointTransaction.java ---
cat > app/src/main/java/com/example/androidapp/data/entities/PointTransaction.java << 'EOP'
package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "point_transactions",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "userId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "userId"), @Index(value = "companyId")})
public class PointTransaction {
    @PrimaryKey
    public @NonNull String id;
    public @NonNull String userId;
    public @NonNull String companyId;
    public int points;
    public @NonNull Date createdAt;
    public String orgId;

    public PointTransaction(@NonNull String id, @NonNull String userId, @NonNull String companyId, int points, @NonNull Date createdAt, String orgId) {
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.points = points;
        this.createdAt = createdAt;
        this.orgId = orgId;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/entities/Repair.java ---
cat > app/src/main/java/com/example/androidapp/data/entities/Repair.java << 'EOP'
package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "repairs",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Customer.class,
                           parentColumns = "id",
                           childColumns = "customerId",
                           onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "customerId")})
public class Repair {
    @PrimaryKey
    public @NonNull String id;
    public @NonNull String companyId;
    public String customerId;
    public String deviceName;
    public String issueDescription;
    public String status;
    public @NonNull Date startDate;
    public Date endDate;
    public float cost;
    public @NonNull Date requestDate;

    public Repair(@NonNull String id, @NonNull String companyId, String customerId, String deviceName, String issueDescription, String status, @NonNull Date startDate, Date endDate, float cost, @NonNull Date requestDate) {
        this.id = id;
        this.companyId = companyId;
        this.customerId = customerId;
        this.deviceName = deviceName;
        this.issueDescription = issueDescription;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.requestDate = requestDate;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    @NonNull
    public Date getStartDate() { return startDate; }
    public void setStartDate(@NonNull Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public float getCost() { return cost; }
    public void setCost(float cost) { this.cost = cost; }
    @NonNull
    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(@NonNull Date requestDate) { this.requestDate = requestDate; }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/entities/Reward.java ---
cat > app/src/main/java/com/example/androidapp/data/entities/Reward.java << 'EOP'
package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "rewards",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class Reward {
    @PrimaryKey
    public @NonNull String id;
    public String name;
    public int pointsRequired;
    private @NonNull String companyId;
    private String orgId;

    public Reward(@NonNull String id, String name, int pointsRequired, @NonNull String companyId, String orgId) {
        this.id = id;
        this.name = name;
        this.pointsRequired = pointsRequired;
        this.companyId = companyId;
        this.orgId = orgId;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPointsRequired() { return pointsRequired; }
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/entities/Trophy.java ---
cat > app/src/main/java/com/example/androidapp/data/entities/Trophy.java << 'EOP'
package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "trophies",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class Trophy {
    @PrimaryKey
    public @NonNull String id;
    public String name;
    public String description;
    private @NonNull String companyId;
    private int pointsRequired;

    public Trophy(@NonNull String id, String name, String description, @NonNull String companyId, int pointsRequired) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.companyId = companyId;
        this.pointsRequired = pointsRequired;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public int getPointsRequired() { return pointsRequired; }
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/entities/UserReward.java ---
cat > app/src/main/java/com/example/androidapp/data/entities/UserReward.java << 'EOP'
package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_rewards",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "userId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Reward.class,
                           parentColumns = "id",
                           childColumns = "rewardId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "userId"), @Index(value = "rewardId"), @Index(value = "companyId")})
public class UserReward {
    @PrimaryKey
    public @NonNull String id;
    public @NonNull String userId;
    public String rewardId;
    public @NonNull String companyId;
    public String orgId;

    public UserReward(@NonNull String id, @NonNull String userId, String rewardId, @NonNull String companyId, String orgId) {
        this.id = id;
        this.userId = userId;
        this.rewardId = rewardId;
        this.companyId = companyId;
        this.orgId = orgId;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }
    public String getRewardId() { return rewardId; }
    public void setRewardId(String rewardId) { this.rewardId = rewardId; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
}
EOP

# --- File: app/src/main/java/com/example/androidapp/data/entities/Item.java ---
cat > app/src/main/java/com/example/androidapp/data/entities/Item.java << 'EOP'
package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "items",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class Item {
    @PrimaryKey
    private @NonNull String id;
    private String name;
    private double price;
    private @NonNull String companyId;
    private String description;
    private float costPrice;
    private Integer reorderLevel;

    public Item(@NonNull String id, String name, double price, @NonNull String companyId, String description, float costPrice, Integer reorderLevel) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.companyId = companyId;
        this.description = description;
        this.costPrice = costPrice;
        this.reorderLevel = reorderLevel;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public float getCostPrice() { return costPrice; }
    public void setCostPrice(float costPrice) { this.costPrice = costPrice; }
    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }
}
EOP

chmod +x fix_script.sh
./fix_script.sh
