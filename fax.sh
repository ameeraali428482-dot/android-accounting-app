#!/bin/bash

# تحديد مسار المشروع
PROJECT_PATH="/data/data/com.termux/files/home/android-accounting-app"

echo "تطبيق الإصلاحات على المشروع في $PROJECT_PATH..."

# التأكد من وجود مسار المشروع
if [ ! -d "$PROJECT_PATH" ]; then
    echo "خطأ: مسار المشروع $PROJECT_PATH غير موجود. يرجى التأكد من استخراج المشروع أولاً."
    exit 1
fi

# الانتقال إلى مسار المشروع
cd "$PROJECT_PATH"

# إنشاء مجلدات repositories إذا لم تكن موجودة
mkdir -p app/src/main/java/com/example/androidapp/data/repositories

# 1. إنشاء كيان Purchase.java
echo "إنشاء Purchase.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/entities/Purchase.java
package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "purchases")
public class Purchase {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String companyId;
    private String supplierId;
    private String referenceNumber;
    private Date purchaseDate;
    private float totalAmount;
    private String description;

    public Purchase(String companyId, String supplierId, String referenceNumber, Date purchaseDate, float totalAmount, String description) {
        this.companyId = companyId;
        this.supplierId = supplierId;
        this.referenceNumber = referenceNumber;
        this.purchaseDate = purchaseDate;
        this.totalAmount = totalAmount;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
EOF

# 2. إنشاء واجهة PurchaseDao.java
echo "إنشاء PurchaseDao.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/dao/PurchaseDao.java
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Purchase;

import java.util.List;

@Dao
public interface PurchaseDao {
    @Insert
    void insert(Purchase purchase);

    @Update
    void update(Purchase purchase);

    @Query("SELECT * FROM purchases WHERE companyId = :companyId ORDER BY purchaseDate DESC")
    LiveData<List<Purchase>> getAllPurchases(String companyId);

    @Query("SELECT SUM(totalAmount) FROM purchases WHERE companyId = :companyId AND purchaseDate BETWEEN :startDate AND :endDate")
    LiveData<Float> getTotalPurchasesByDateRange(String companyId, String startDate, String endDate);

    @Query("SELECT COUNT(*) FROM purchases WHERE referenceNumber = :referenceNumber AND companyId = :companyId")
    LiveData<Integer> countPurchaseByReferenceNumber(String referenceNumber, String companyId);
}
EOF

# 3. إنشاء كيان ProfitLossStatement.java
echo "إنشاء ProfitLossStatement.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/entities/ProfitLossStatement.java
package com.example.androidapp.data.entities;

public class ProfitLossStatement {
    private float totalRevenue;
    private float totalCostOfGoodsSold;
    private float grossProfit;
    private float operatingExpenses;
    private float netProfit;

    public ProfitLossStatement(float totalRevenue, float totalCostOfGoodsSold, float grossProfit, float operatingExpenses, float netProfit) {
        this.totalRevenue = totalRevenue;
        this.totalCostOfGoodsSold = totalCostOfGoodsSold;
        this.grossProfit = grossProfit;
        this.operatingExpenses = operatingExpenses;
        this.netProfit = netProfit;
    }

    public float getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(float totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public float getTotalCostOfGoodsSold() {
        return totalCostOfGoodsSold;
    }

    public void setTotalCostOfGoodsSold(float totalCostOfGoodsSold) {
        this.totalCostOfGoodsSold = totalCostOfGoodsSold;
    }

    public float getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(float grossProfit) {
        this.grossProfit = grossProfit;
    }

    public float getOperatingExpenses() {
        return operatingExpenses;
    }

    public void setOperatingExpenses(float operatingExpenses) {
        this.operatingExpenses = operatingExpenses;
    }

    public float getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(float netProfit) {
        this.netProfit = netProfit;
    }
}
EOF

# 4. إنشاء كيان BalanceSheet.java
echo "إنشاء BalanceSheet.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/entities/BalanceSheet.java
package com.example.androidapp.data.entities;

public class BalanceSheet {
    // Placeholder for a complex BalanceSheet object
    // In a real application, this would contain aggregated data for assets, liabilities, and equity.
    // For now, it's an empty class to resolve compilation errors.

    public BalanceSheet() {
        // Default constructor
    }

    // You would add properties and methods here to represent the balance sheet data
    // For example:
    // private float totalAssets;
    // private float totalLiabilities;
    // private float totalEquity;

    // public float getTotalAssets() { return totalAssets; }
    // public void setTotalAssets(float totalAssets) { this.totalAssets = totalAssets; }
}
EOF

# 5. إنشاء واجهة AccountRepository.java
echo "إنشاء AccountRepository.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/repositories/AccountRepository.java
package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.AccountDao;
import com.example.androidapp.data.entities.Account;

import java.util.List;
import java.util.concurrent.Future;

public class AccountRepository {
    private AccountDao accountDao;

    public AccountRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        accountDao = db.accountDao();
    }

    public Future<Void> insert(Account account) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            accountDao.insert(account);
            return null;
        });
    }

    public Future<Void> update(Account account) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            accountDao.update(account);
            return null;
        });
    }

    public Future<Void> delete(Account account) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            accountDao.delete(account);
            return null;
        });
    }

    public LiveData<List<Account>> getAllAccounts(String companyId) {
        return accountDao.getAllAccounts(companyId);
    }

    public Future<Account> getAccountById(String accountId, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> accountDao.getAccountById(accountId, companyId));
    }

    public Future<Account> getAccountByNameAndCompanyId(String accountName, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> accountDao.getAccountByNameAndCompanyId(accountName, companyId));
    }
}
EOF

# 6. إنشاء واجهة InventoryRepository.java
echo "إنشاء InventoryRepository.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/repositories/InventoryRepository.java
package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.InventoryDao;
import com.example.androidapp.data.entities.Inventory;
import com.example.androidapp.data.entities.InvoiceItem;

import java.util.List;
import java.util.concurrent.Future;

public class InventoryRepository {
    private InventoryDao inventoryDao;

    public InventoryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        inventoryDao = db.inventoryDao();
    }

    public Future<Void> insert(Inventory inventory) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            inventoryDao.insert(inventory);
            return null;
        });
    }

    public Future<Void> update(Inventory inventory) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            inventoryDao.update(inventory);
            return null;
        });
    }

    public Future<Void> delete(Inventory inventory) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            inventoryDao.delete(inventory);
            return null;
        });
    }

    public LiveData<List<Inventory>> getAllInventory(String companyId) {
        return inventoryDao.getAllInventory(companyId);
    }

    public Future<List<Inventory>> getInventoryForItem(String itemId, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> inventoryDao.getInventoryForItem(itemId, companyId));
    }
}
EOF

# 7. إنشاء واجهة InvoiceRepository.java
echo "إنشاء InvoiceRepository.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/repositories/InvoiceRepository.java
package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.entities.Invoice;

import java.util.List;
import java.util.concurrent.Future;

public class InvoiceRepository {
    private InvoiceDao invoiceDao;

    public InvoiceRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        invoiceDao = db.invoiceDao();
    }

    public Future<Void> insert(Invoice invoice) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            invoiceDao.insert(invoice);
            return null;
        });
    }

    public Future<Void> update(Invoice invoice) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            invoiceDao.update(invoice);
            return null;
        });
    }

    public Future<Void> delete(Invoice invoice) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            invoiceDao.delete(invoice);
            return null;
        });
    }

    public LiveData<List<Invoice>> getAllInvoices(String companyId) {
        return invoiceDao.getAllInvoices(companyId);
    }

    public Future<Integer> countInvoicesByNumber(String invoiceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> invoiceDao.countInvoicesByNumber(invoiceNumber, companyId));
    }

    public Future<Float> getTotalSalesByDateRange(String companyId, String startDate, String endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> invoiceDao.getTotalSalesByDateRange(companyId, startDate, endDate));
    }
}
EOF

# 8. إنشاء واجهة ItemRepository.java
echo "إنشاء ItemRepository.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/repositories/ItemRepository.java
package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.ItemDao;
import com.example.androidapp.data.entities.Item;

import java.util.List;
import java.util.concurrent.Future;

public class ItemRepository {
    private ItemDao itemDao;

    public ItemRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        itemDao = db.itemDao();
    }

    public Future<Void> insert(Item item) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            itemDao.insert(item);
            return null;
        });
    }

    public Future<Void> update(Item item) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            itemDao.update(item);
            return null;
        });
    }

    public Future<Void> delete(Item item) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            itemDao.delete(item);
            return null;
        });
    }

    public LiveData<List<Item>> getAllItems(String companyId) {
        return itemDao.getAllItems(companyId);
    }

    public Future<Item> getItemById(String itemId, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> itemDao.getItemById(itemId, companyId));
    }
}
EOF

# 9. إنشاء واجهة JournalEntryRepository.java
echo "إنشاء JournalEntryRepository.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/repositories/JournalEntryRepository.java
package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.JournalEntryDao;
import com.example.androidapp.data.entities.JournalEntry;

import java.util.List;
import java.util.concurrent.Future;

public class JournalEntryRepository {
    private JournalEntryDao journalEntryDao;

    public JournalEntryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        journalEntryDao = db.journalEntryDao();
    }

    public Future<Void> insert(JournalEntry journalEntry) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            journalEntryDao.insert(journalEntry);
            return null;
        });
    }

    public Future<Void> update(JournalEntry journalEntry) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            journalEntryDao.update(journalEntry);
            return null;
        });
    }

    public Future<Void> delete(JournalEntry journalEntry) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            journalEntryDao.delete(journalEntry);
            return null;
        });
    }

    public LiveData<List<JournalEntry>> getAllJournalEntries(String companyId) {
        return journalEntryDao.getAllJournalEntries(companyId);
    }

    public Future<Integer> countJournalEntryByReferenceNumber(String referenceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryDao.countJournalEntryByReferenceNumber(referenceNumber, companyId));
    }
}
EOF

# 10. إنشاء واجهة JournalEntryItemRepository.java
echo "إنشاء JournalEntryItemRepository.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/repositories/JournalEntryItemRepository.java
package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.JournalEntryItemDao;
import com.example.androidapp.data.entities.JournalEntryItem;

import java.util.List;
import java.util.concurrent.Future;

public class JournalEntryItemRepository {
    private JournalEntryItemDao journalEntryItemDao;

    public JournalEntryItemRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        journalEntryItemDao = db.journalEntryItemDao();
    }

    public Future<Void> insert(JournalEntryItem journalEntryItem) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            journalEntryItemDao.insert(journalEntryItem);
            return null;
        });
    }

    public Future<Void> update(JournalEntryItem journalEntryItem) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            journalEntryItemDao.update(journalEntryItem);
            return null;
        });
    }

    public Future<Void> delete(JournalEntryItem journalEntryItem) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            journalEntryItemDao.delete(journalEntryItem);
            return null;
        });
    }

    public LiveData<List<JournalEntryItem>> getJournalEntryItems(String journalEntryId) {
        return journalEntryItemDao.getJournalEntryItems(journalEntryId);
    }

    public Future<Float> getTotalAmountForAccountTypeAndDateRange(String accountType, String companyId, String startDate, String endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> journalEntryItemDao.getTotalAmountForAccountTypeAndDateRange(accountType, companyId, startDate, endDate));
    }
}
EOF

# 11. إنشاء واجهة PaymentRepository.java
echo "إنشاء PaymentRepository.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/repositories/PaymentRepository.java
package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.data.entities.Payment;

import java.util.List;
import java.util.concurrent.Future;

public class PaymentRepository {
    private PaymentDao paymentDao;

    public PaymentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        paymentDao = db.paymentDao();
    }

    public Future<Void> insert(Payment payment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            paymentDao.insert(payment);
            return null;
        });
    }

    public Future<Void> update(Payment payment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            paymentDao.update(payment);
            return null;
        });
    }

    public Future<Void> delete(Payment payment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            paymentDao.delete(payment);
            return null;
        });
    }

    public LiveData<List<Payment>> getAllPayments(String companyId) {
        return paymentDao.getAllPayments(companyId);
    }

    public Future<Integer> countPaymentByReferenceNumber(String referenceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.countPaymentByReferenceNumber(referenceNumber, companyId));
    }
}
EOF

# 12. إنشاء واجهة ReceiptRepository.java
echo "إنشاء ReceiptRepository.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/repositories/ReceiptRepository.java
package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.ReceiptDao;
import com.example.androidapp.data.entities.Receipt;

import java.util.List;
import java.util.concurrent.Future;

public class ReceiptRepository {
    private ReceiptDao receiptDao;

    public ReceiptRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        receiptDao = db.receiptDao();
    }

    public Future<Void> insert(Receipt receipt) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            receiptDao.insert(receipt);
            return null;
        });
    }

    public Future<Void> update(Receipt receipt) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            receiptDao.update(receipt);
            return null;
        });
    }

    public Future<Void> delete(Receipt receipt) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            receiptDao.delete(receipt);
            return null;
        });
    }

    public LiveData<List<Receipt>> getAllReceipts(String companyId) {
        return receiptDao.getAllReceipts(companyId);
    }

    public Future<Integer> countReceiptByReferenceNumber(String referenceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> receiptDao.countReceiptByReferenceNumber(referenceNumber, companyId));
    }
}
EOF

# 13. إنشاء واجهة PurchaseRepository.java
echo "إنشاء PurchaseRepository.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/repositories/PurchaseRepository.java
package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PurchaseDao;
import com.example.androidapp.data.entities.Purchase;

import java.util.List;
import java.util.concurrent.Future;

public class PurchaseRepository {
    private PurchaseDao purchaseDao;

    public PurchaseRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        purchaseDao = db.purchaseDao();
    }

    public Future<Void> insert(Purchase purchase) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            purchaseDao.insert(purchase);
            return null;
        });
    }

    public Future<Void> update(Purchase purchase) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            purchaseDao.update(purchase);
            return null;
        });
    }

    public Future<Void> delete(Purchase purchase) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            purchaseDao.delete(purchase);
            return null;
        });
    }

    public LiveData<List<Purchase>> getAllPurchases(String companyId) {
        return purchaseDao.getAllPurchases(companyId);
    }

    public Future<Float> getTotalPurchasesByDateRange(String companyId, String startDate, String endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> purchaseDao.getTotalPurchasesByDateRange(companyId, startDate, endDate));
    }

    public Future<Integer> countPurchaseByReferenceNumber(String referenceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> purchaseDao.countPurchaseByReferenceNumber(referenceNumber, companyId));
    }
}
EOF

# 14. تحديث AppDatabase.java
echo "تحديث AppDatabase.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/data/AppDatabase.java
package com.example.androidapp.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.androidapp.data.entities.AccountStatement;
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
        Purchase.class, AccountStatement.class
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
                            // .fallbackToDestructiveMigration() // يجب تنفيذ استراتيجية ترحيل مناسبة هنا
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
EOF

# 15. تحديث AccountingManager.java
echo "تحديث AccountingManager.java..."
cat << \EOF > app/src/main/java/com/example/androidapp/logic/AccountingManager.java
package com.example.androidapp.logic;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.AccountStatementDao;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.AccountStatement;
import com.example.androidapp.data.entities.BalanceSheet;
import com.example.androidapp.data.entities.Inventory;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.JournalEntry;
import com.example.androidapp.data.entities.JournalEntryItem;
import com.example.androidapp.data.entities.Payment;
import com.example.androidapp.data.entities.ProfitLossStatement;
import com.example.androidapp.data.entities.Receipt;
import com.example.androidapp.data.repositories.AccountRepository;
import com.example.androidapp.data.repositories.InventoryRepository;
import com.example.androidapp.data.repositories.InvoiceRepository;
import com.example.androidapp.data.repositories.ItemRepository;
import com.example.androidapp.data.repositories.JournalEntryItemRepository;
import com.example.androidapp.data.repositories.JournalEntryRepository;
import com.example.androidapp.data.repositories.PaymentRepository;
import com.example.androidapp.data.repositories.PurchaseRepository;
import com.example.androidapp.data.repositories.ReceiptRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class AccountingManager {
    private static final String TAG = "AccountingManager";
    private final AccountRepository accountRepository;
    private final InventoryRepository inventoryRepository;
    private final InvoiceRepository invoiceRepository;
    private final ItemRepository itemRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final JournalEntryItemRepository journalEntryItemRepository;
    private final PaymentRepository paymentRepository;
    private final ReceiptRepository receiptRepository;
    private final PurchaseRepository purchaseRepository;
    private final AppDatabase database;

    public AccountingManager(Context context) {
        Application application = (Application) context.getApplicationContext();
        this.database = AppDatabase.getDatabase(application);
        this.accountRepository = new AccountRepository(application);
        this.inventoryRepository = new InventoryRepository(application);
        this.invoiceRepository = new InvoiceRepository(application);
        this.itemRepository = new ItemRepository(application);
        this.journalEntryRepository = new JournalEntryRepository(application);
        this.journalEntryItemRepository = new JournalEntryItemRepository(application);
        this.paymentRepository = new PaymentRepository(application);
        this.receiptRepository = new ReceiptRepository(application);
        this.purchaseRepository = new PurchaseRepository(application);
    }

    /**
     * Creates automatic journal entries for an invoice.
     * This method assumes a double-entry accounting system.
     * For a sales invoice, typically: Accounts Receivable (Debit), Sales Revenue (Credit), Sales Tax Payable (Credit)
     * For a purchase invoice, typically: Purchases/Expenses (Debit), Accounts Payable (Credit), Input Tax Credit (Debit)
     *
     * @param invoice The invoice for which to create the journal entry.
     * @param invoiceItems The list of items in the invoice.
     */
    public void createJournalEntriesForInvoice(Invoice invoice, List<InvoiceItem> invoiceItems) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                String journalEntryId = UUID.randomUUID().toString();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                float totalAmount = 0;
                for (InvoiceItem item : invoiceItems) {
                    totalAmount += item.getQuantity() * item.getUnitPrice();
                }

                // Create main journal entry with initial total debit/credit as totalAmount
                JournalEntry journalEntry = new JournalEntry(
                    journalEntryId,
                    invoice.getCompanyId(),
                    currentDate,
                    "Invoice No: " + invoice.getInvoiceNumber(),
                    invoice.getId(), // referenceNumber
                    "AUTO_INVOICE", // entryType
                    totalAmount, // totalDebit (will be updated after all items are processed)
                    totalAmount  // totalCredit (will be updated after all items are processed)
                );
                journalEntryRepository.insert(journalEntry).get();

                // Debit Accounts Receivable or Cash
                Account debitAccount = null;
                if ("CASH".equals(invoice.getInvoiceType())) {
                    debitAccount = accountRepository.getAccountByNameAndCompanyId("Cash", invoice.getCompanyId()).get();
                } else { // CREDIT or CASH_CREDIT
                    debitAccount = accountRepository.getAccountByNameAndCompanyId("Accounts Receivable", invoice.getCompanyId()).get();
                }
                if (debitAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId, 
                        debitAccount.getId(), 
                        totalAmount, 
                        0.0f, 
                        "Accounts Receivable/Cash for Invoice " + invoice.getInvoiceNumber()
                    )).get();
                }

                // Credit Sales Revenue
                Account salesRevenueAccount = accountRepository.getAccountByNameAndCompanyId("Sales Revenue", invoice.getCompanyId()).get();
                if (salesRevenueAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId, 
                        salesRevenueAccount.getId(), 
                        0.0f, 
                        totalAmount, 
                        "Sales Revenue for Invoice " + invoice.getInvoiceNumber()
                    )).get();
                }

                // Create Cost of Goods Sold entries and update inventory
                createCostOfGoodsSoldEntries(journalEntryId, invoiceItems, invoice.getInvoiceNumber(), invoice.getCompanyId());

                // Update the main JournalEntry with final calculated totals (if necessary, though for simple sales, totalAmount should balance)
                // For more complex scenarios (e.g., taxes, discounts), these totals would need recalculation
                journalEntry.setTotalDebit(totalAmount);
                journalEntry.setTotalCredit(totalAmount);
                journalEntryRepository.update(journalEntry).get();

                Log.d(TAG, "Journal entries created for invoice: " + invoice.getInvoiceNumber());
            } catch (Exception e) {
                Log.e(TAG, "Error creating journal entries for invoice: " + e.getMessage());
            }
        });
    }

    /**
     * Creates cost of goods sold journal entries and updates inventory.
     *
     * @param journalEntryId The ID of the main journal entry.
     * @param invoiceItems The list of items in the invoice.
     * @param invoiceNumber The invoice number.
     * @param companyId The ID of the company.
     */
    private void createCostOfGoodsSoldEntries(String journalEntryId, List<InvoiceItem> invoiceItems, String invoiceNumber, String companyId) {
        try {
            float totalCost = 0;
            for (InvoiceItem invoiceItem : invoiceItems) {
                Item item = itemRepository.getItemById(invoiceItem.getItemId(), companyId).get();
                if (item != null) {
                    float itemCost = item.getCostPrice() * invoiceItem.getQuantity();
                    totalCost += itemCost;
                    updateInventoryForSale(invoiceItem, companyId);
                }
            }

            if (totalCost > 0) {
                // Debit Cost of Goods Sold
                Account cogsAccount = accountRepository.getAccountByNameAndCompanyId("Cost of Goods Sold", companyId).get();
                if (cogsAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId, 
                        cogsAccount.getId(), 
                        totalCost, 
                        0.0f, 
                        "COGS for Invoice " + invoiceNumber
                    )).get();
                }

                // Credit Inventory
                Account inventoryAccount = accountRepository.getAccountByNameAndCompanyId("Inventory", companyId).get();
                if (inventoryAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(journalEntryId, 
                        inventoryAccount.getId(), 
                        0.0f, 
                        totalCost, 
                        "Inventory for Invoice " + invoiceNumber
                    )).get();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating COGS entries: " + e.getMessage());
        }
    }

    /**
     * Updates inventory quantities for sold items.
     *
     * @param invoiceItem The invoice item being sold.
     * @param companyId The ID of the company.
     */
    private void updateInventoryForSale(InvoiceItem invoiceItem, String companyId) {
        try {
            List<Inventory> inventoryList = inventoryRepository.getInventoryForItem(invoiceItem.getItemId(), companyId).get();
            float remainingQty = invoiceItem.getQuantity();

            for (Inventory inventory : inventoryList) {
                if (remainingQty <= 0) break;

                float availableQty = inventory.getQuantity();
                if (availableQty > 0) {
                    float qtyToReduce = Math.min(remainingQty, availableQty);
                    inventory.setQuantity(availableQty - qtyToReduce);
                    inventoryRepository.update(inventory).get();
                    remainingQty -= qtyToReduce;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating inventory: " + e.getMessage());
        }
    }

    /**
     * Creates journal entries for payments.
     * Typically: Cash/Bank (Debit), Accounts Receivable (Credit)
     *
     * @param payment The payment object.
     */
    public void createJournalEntriesForPayment(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                String journalEntryId = UUID.randomUUID().toString();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                // Create main journal entry
                JournalEntry journalEntry = new JournalEntry(
                    journalEntryId,
                    payment.getCompanyId(),
                    currentDate,
                    "Payment Ref: " + payment.getReferenceNumber(),
                    payment.getId(), // referenceNumber
                    "AUTO_PAYMENT", // entryType
                    payment.getAmount(), // totalDebit
                    payment.getAmount()  // totalCredit
                );
                journalEntryRepository.insert(journalEntry).get();

                // Debit Cash/Bank account
                Account cashAccount = accountRepository.getAccountByNameAndCompanyId(getAccountNameForPaymentMethod(payment.getPaymentMethod()), payment.getCompanyId()).get();
                if (cashAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId, 
                        cashAccount.getId(), 
                        payment.getAmount(), 
                        0.0f, 
                        "Cash/Bank received for payment " + payment.getReferenceNumber()
                    )).get();
                }

                // Credit Accounts Receivable (assuming payment is against an invoice)
                Account accountsReceivableAccount = accountRepository.getAccountByNameAndCompanyId("Accounts Receivable", payment.getCompanyId()).get();
                if (accountsReceivableAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId, 
                        accountsReceivableAccount.getId(), 
                        0.0f, 
                        payment.getAmount(), 
                        "Payment received against Accounts Receivable for " + payment.getReferenceNumber()
                    )).get();
                }

                Log.d(TAG, "Journal entries created for payment: " + payment.getReferenceNumber());
            } catch (Exception e) {
                Log.e(TAG, "Error creating journal entries for payment: " + e.getMessage());
            }
        });
    }

    /**
     * Creates journal entries for receipts.
     * Typically: Cash/Bank (Debit), Specific Revenue Account (Credit)
     *
     * @param receipt The receipt object.
     */
    public void createJournalEntriesForReceipt(Receipt receipt) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                String journalEntryId = UUID.randomUUID().toString();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                // Create main journal entry
                JournalEntry journalEntry = new JournalEntry(
                    journalEntryId,
                    receipt.getCompanyId(),
                    currentDate,
                    "Receipt Ref: " + receipt.getReferenceNumber(),
                    receipt.getId(), // referenceNumber
                    "AUTO_RECEIPT", // entryType
                    receipt.getAmount(), // totalDebit
                    receipt.getAmount()  // totalCredit
                );
                journalEntryRepository.insert(journalEntry).get();

                // Debit Cash/Bank account
                Account cashAccount = accountRepository.getAccountByNameAndCompanyId(getAccountNameForPaymentMethod(receipt.getPaymentMethod()), receipt.getCompanyId()).get();
                if (cashAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId, 
                        cashAccount.getId(), 
                        receipt.getAmount(), 
                        0.0f, 
                        "Cash/Bank received for receipt " + receipt.getReferenceNumber()
                    )).get();
                }

                // Credit a specific revenue account (e.g., Other Income, Service Revenue)
                Account revenueAccount = accountRepository.getAccountByNameAndCompanyId("Other Income", receipt.getCompanyId()).get(); // Example
                if (revenueAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId, 
                        revenueAccount.getId(), 
                        0.0f, 
                        receipt.getAmount(), 
                        "Revenue from receipt " + receipt.getReferenceNumber()
                    )).get();
                }

                Log.d(TAG, "Journal entries created for receipt: " + receipt.getReferenceNumber());
            } catch (Exception e) {
                Log.e(TAG, "Error creating journal entries for receipt: " + e.getMessage());
            }
        });
    }

    /**
     * Gets the appropriate account name based on the payment method.
     *
     * @param paymentMethod The payment method string.
     * @return The corresponding account name.
     */
    private String getAccountNameForPaymentMethod(String paymentMethod) {
        switch (paymentMethod) {
            case "Cash":
            case "نقد":
                return "Cash";
            case "Bank Transfer":
            case "تحويل بنكي":
            case "Credit Card":
            case "بطاقة ائتمان":
                return "Bank";
            default:
                return "Cash"; // Default to Cash
        }
    }

    /**
     * Validates if the total debits equal total credits for a given journal entry.
     *
     * @param journalEntryId The ID of the journal entry to validate.
     * @return True if balanced, false otherwise.
     */
    public boolean validateJournalEntryBalance(String journalEntryId) {
        try {
            List<JournalEntryItem> items = journalEntryItemRepository.getJournalEntryItems(journalEntryId).get();
            float totalDebit = 0;
            float totalCredit = 0;
            for (JournalEntryItem item : items) {
                totalDebit += item.getDebit();
                totalCredit += item.getCredit();
            }
            return Math.abs(totalDebit - totalCredit) < 0.001; // Using a tolerance for float comparison
        } catch (Exception e) {
            Log.e(TAG, "Error validating journal entry balance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if an invoice number is unique for a given company.
     *
     * @param invoiceNumber The invoice number to check.
     * @param companyId The ID of the company.
     * @return True if unique, false otherwise.
     */
    public boolean isInvoiceNumberUnique(String invoiceNumber, String companyId) {
        try {
            return invoiceRepository.countInvoicesByNumber(invoiceNumber, companyId).get() == 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking invoice number uniqueness: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a reference number is unique for a given company and type.
     *
     * @param referenceNumber The reference number to check.
     * @param companyId The ID of the company.
     * @param type The type of reference (e.g., "PAYMENT", "RECEIPT", "JOURNAL").
     * @return True if unique, false otherwise.
     */
    public boolean isReferenceNumberUnique(String referenceNumber, String companyId, String type) {
        try {
            switch (type) {
                case "PAYMENT":
                    return paymentRepository.countPaymentByReferenceNumber(referenceNumber, companyId).get() == 0;
                case "RECEIPT":
                    return receiptRepository.countReceiptByReferenceNumber(referenceNumber, companyId).get() == 0;
                case "JOURNAL":
                    return journalEntryRepository.countJournalEntryByReferenceNumber(referenceNumber, companyId).get() == 0;
                default:
                    return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking reference number uniqueness: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calculates the total sales for a given period.
     *
     * @param companyId The ID of the company.
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @return The total sales amount.
     */
    public float getTotalSales(String companyId, String startDate, String endDate) {
        try {
            return invoiceRepository.getTotalSalesByDateRange(companyId, startDate, endDate).get();
        } catch (Exception e) {
            Log.e(TAG, "Error getting total sales: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Calculates the total purchases for a given period.
     *
     * @param companyId The ID of the company.
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @return The total purchases amount.
     */
    public float getTotalPurchases(String companyId, String startDate, String endDate) {
        try {
            return purchaseRepository.getTotalPurchasesByDateRange(companyId, startDate, endDate).get();
        } catch (Exception e) {
            Log.e(TAG, "Error getting total purchases: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Generates a profit and loss statement.
     *
     * @param companyId The ID of the company.
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @return A map or a custom object representing the P&L statement.
     */
    public ProfitLossStatement generateProfitAndLoss(String companyId, String startDate, String endDate) {
        try {
            float totalRevenue = getTotalSales(companyId, startDate, endDate);
            float totalCostOfGoodsSold = journalEntryItemRepository.getTotalAmountForAccountTypeAndDateRange("Cost of Goods Sold", companyId, startDate, endDate).get();
            float grossProfit = totalRevenue - totalCostOfGoodsSold;

            float operatingExpenses = journalEntryItemRepository.getTotalAmountForAccountTypeAndDateRange("Operating Expenses", companyId, startDate, endDate).get();
            float netProfit = grossProfit - operatingExpenses;

            return new ProfitLossStatement(totalRevenue, totalCostOfGoodsSold, grossProfit, operatingExpenses, netProfit);
        } catch (Exception e) {
            Log.e(TAG, "Error generating P&L: " + e.getMessage());
            return new ProfitLossStatement(0, 0, 0, 0, 0);
        }
    }

    /**
     * Generates a balance sheet.
     *
     * @param companyId The ID of the company.
     * @param asOfDate The date for which to generate the balance sheet.
     * @return A map or a custom object representing the balance sheet.
     */
    public BalanceSheet generateBalanceSheet(String companyId, String asOfDate) {
        // This is a highly complex function requiring aggregation of all asset, liability, and equity accounts.
        // A full implementation would involve:
        // 1. Getting all asset accounts and summing their balances up to asOfDate.
        // 2. Getting all liability accounts and summing their balances up to asOfDate.
        // 3. Calculating equity (Owner's Equity + Retained Earnings + Net Profit/Loss).
        // This placeholder returns an empty BalanceSheet object.
        Log.w(TAG, "generateBalanceSheet: Full implementation requires extensive querying of account balances.");
        return new BalanceSheet();
    }

    /**
     * Gets the last purchase price for a given item.
     * This is a placeholder as we don't have purchase records.
     * In a real scenario, you'd look at purchase invoices or bills.
     *
     * @param itemId The ID of the item.
     * @param supplierId The ID of the supplier.
     * @param companyId The ID of the company.
     * @return The last purchase price.
     */
    public float getLastPurchasePrice(String itemId, String supplierId, String companyId) {
        // Placeholder: Returning the cost price from the Item table for now.
        try {
            Item item = itemRepository.getItemById(itemId, companyId).get();
            return item != null ? item.getCostPrice() : 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting last purchase price: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Adds or updates an account statement and recalculates running balances.
     *
     * @param newStatement The AccountStatement object to add or update.
     */
    public void addOrUpdateAccountStatement(AccountStatement newStatement) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                AccountStatementDao dao = database.accountStatementDao();
                List<AccountStatement> existingStatements = dao.getAccountStatementsForBalanceCalculation(
                    newStatement.getCompanyId(),
                    newStatement.getAccountId(),
                    newStatement.getTransactionDate()
                );

                float currentRunningBalance = 0.0f;
                if (!existingStatements.isEmpty()) {
                    // Assuming the list is ordered by date descending, the first one is the latest before or on transactionDate
                    currentRunningBalance = existingStatements.get(0).getRunningBalance();
                }

                newStatement.setRunningBalance(currentRunningBalance + newStatement.getDebit() - newStatement.getCredit());
                dao.insert(newStatement);

                recalculateRunningBalances(newStatement.getCompanyId(), newStatement.getAccountId(), newStatement.getTransactionDate());

                Log.d(TAG, "Account statement added/updated and balances recalculated for account: " + newStatement.getAccountId());
            } catch (Exception e) {
                Log.e(TAG, "Error adding or updating account statement: " + e.getMessage());
            }
        });
    }

    /**
     * Recalculates running balances for account statements from a given start date.
     *
     * @param companyId The ID of the company.
     * @param accountId The ID of the account.
     * @param startDate The date from which to start recalculation.
     */
    public void recalculateRunningBalances(String companyId, String accountId, String startDate) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                AccountStatementDao dao = database.accountStatementDao();
                List<AccountStatement> statementsToRecalculate = dao.getStatementsForRecalculation(companyId, accountId, startDate);

                float runningBalance = 0.0f;
                AccountStatement lastStatementBeforeStartDate = dao.getLastStatementBeforeDate(companyId, accountId, startDate);
                if (lastStatementBeforeStartDate != null) {
                    runningBalance = lastStatementBeforeStartDate.getRunningBalance();
                }

                for (AccountStatement statement : statementsToRecalculate) {
                    runningBalance += statement.getDebit() - statement.getCredit();
                    if (statement.getRunningBalance() != runningBalance) { // Only update if changed
                        statement.setRunningBalance(runningBalance);
                        dao.update(statement);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error recalculating running balances: " + e.getMessage());
            }
        });
    }
}
EOF

echo "تم تطبيق جميع الإصلاحات بنجاح."

