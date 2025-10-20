package com.example.androidapp.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

import com.example.androidapp.data.dao.*;
import com.example.androidapp.data.entities.*;

@Database(entities = {
    Account.class,
    Transaction.class,
    Invoice.class,
    InvoiceItem.class,
    Item.class,
    Category.class,
    User.class,
    Company.class,
    CompanySettings.class,
    Role.class,
    Permission.class,
    UserRole.class,
    UserPermission.class,
    RolePermission.class,
    JournalEntry.class,
    JournalEntryItem.class,
    AccountStatement.class,
    Customer.class,
    Supplier.class,
    Employee.class,
    Payment.class,
    Receipt.class,
    Purchase.class,
    Order.class,
    OrderItem.class,
    Warehouse.class,
    Inventory.class,
    AuditLog.class,
    Notification.class,
    Reminder.class,
    Campaign.class,
    DeliveryReceipt.class,
    Connection.class,
    SharedLink.class,
    PointTransaction.class,
    Reward.class,
    UserReward.class,
    Trophy.class,
    UserTrophy.class,
    Service.class,
    Doctor.class,
    Voucher.class,
    FinancialTransfer.class,
    CurrencyExchange.class,
    JoinRequest.class,
    Chat.class,
    ChatMessage.class,
    Repair.class,
    Post.class,
    Comment.class,
    Like.class,
    Share.class,
    ContactSync.class,
    Friend.class,
    ProfitLossStatement.class,
    BalanceSheet.class,
    AIConversation.class,
    BarcodeData.class,
    DataBackup.class,
    ExternalNotification.class,
    InstitutionProfile.class,
    OfflineTransaction.class,
    PeriodicReminder.class,
    SmartNotification.class,
    UserPoints.class
}, version = 2, exportSchema = false) // زيادة الإصدار لتطبيق التحديثات
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static volatile AppDatabase INSTANCE;
    
    // DAO declarations
    public abstract AccountDao accountDao();
    public abstract TransactionDao transactionDao();
    public abstract InvoiceDao invoiceDao();
    public abstract InvoiceItemDao invoiceItemDao();
    public abstract ItemDao itemDao();
    public abstract CategoryDao categoryDao();
    public abstract UserDao userDao();
    public abstract CompanyDao companyDao();
    public abstract CompanySettingsDao companySettingsDao();
    public abstract RoleDao roleDao();
    public abstract PermissionDao permissionDao();
    public abstract UserRoleDao userRoleDao();
    public abstract UserPermissionDao userPermissionDao();
    public abstract RolePermissionDao rolePermissionDao();
    public abstract JournalEntryDao journalEntryDao();
    public abstract JournalEntryItemDao journalEntryItemDao();
    public abstract AccountStatementDao accountStatementDao();
    public abstract CustomerDao customerDao();
    public abstract SupplierDao supplierDao();
    public abstract EmployeeDao employeeDao();
    public abstract PaymentDao paymentDao();
    public abstract ReceiptDao receiptDao();
    public abstract PurchaseDao purchaseDao();
    public abstract OrderDao orderDao();
    public abstract OrderItemDao orderItemDao();
    public abstract WarehouseDao warehouseDao();
    public abstract InventoryDao inventoryDao();
    public abstract AuditLogDao auditLogDao();
    public abstract NotificationDao notificationDao();
    public abstract ReminderDao reminderDao();
    public abstract CampaignDao campaignDao();
    public abstract DeliveryReceiptDao deliveryReceiptDao();
    public abstract ConnectionDao connectionDao();
    public abstract SharedLinkDao sharedLinkDao();
    public abstract PointTransactionDao pointTransactionDao();
    public abstract RewardDao rewardDao();
    public abstract UserRewardDao userRewardDao();
    public abstract TrophyDao trophyDao();
    public abstract UserTrophyDao userTrophyDao();
    public abstract ServiceDao serviceDao();
    public abstract DoctorDao doctorDao();
    public abstract VoucherDao voucherDao();
    public abstract FinancialTransferDao financialTransferDao();
    public abstract CurrencyExchangeDao currencyExchangeDao();
    public abstract JoinRequestDao joinRequestDao();
    public abstract ChatDao chatDao();
    public abstract ChatMessageDao chatMessageDao();
    public abstract RepairDao repairDao();
    public abstract PostDao postDao();
    public abstract CommentDao commentDao();
    public abstract LikeDao likeDao();
    public abstract ShareDao shareDao();
    public abstract ContactSyncDao contactSyncDao();
    public abstract FriendDao friendDao();
    public abstract AIConversationDao aiConversationDao();
    public abstract BarcodeDataDao barcodeDataDao();
    public abstract DataBackupDao dataBackupDao();
    public abstract ExternalNotificationDao externalNotificationDao();
    public abstract InstitutionProfileDao institutionProfileDao();
    public abstract OfflineTransactionDao offlineTransactionDao();
    public abstract PeriodicReminderDao periodicReminderDao();
    public abstract SmartNotificationDao smartNotificationDao();
    public abstract UserPointsDao userPointsDao();

    // Database executor for background tasks
    public static final java.util.concurrent.ExecutorService databaseWriteExecutor = 
        java.util.concurrent.Executors.newFixedThreadPool(4);

    // Singleton pattern للحصول على instance واحد فقط من قاعدة البيانات
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "accounting_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Migration من الإصدار 1 إلى 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // إضافة الأعمدة الجديدة للـ User table
            database.execSQL("ALTER TABLE users ADD COLUMN company_id TEXT");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_users_company_id ON users(company_id)");
            
            // إضافة الأعمدة الجديدة للـ Account table
            database.execSQL("ALTER TABLE accounts ADD COLUMN user_id TEXT");
            database.execSQL("ALTER TABLE accounts ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_accounts_user_id ON accounts(user_id)");
            
            // إضافة الأعمدة الجديدة للـ Transaction table
            database.execSQL("ALTER TABLE transactions ADD COLUMN transaction_type TEXT");
            database.execSQL("ALTER TABLE transactions ADD COLUMN status TEXT DEFAULT 'PENDING'");
            database.execSQL("ALTER TABLE transactions ADD COLUMN reference_number TEXT");
            database.execSQL("ALTER TABLE transactions ADD COLUMN user_id TEXT");
            database.execSQL("ALTER TABLE transactions ADD COLUMN company_id TEXT");
            database.execSQL("ALTER TABLE transactions ADD COLUMN created_at INTEGER");
            database.execSQL("ALTER TABLE transactions ADD COLUMN notes TEXT");
            database.execSQL("ALTER TABLE transactions ADD COLUMN is_reconciled INTEGER NOT NULL DEFAULT 0");
            
            // إنشاء الفهارس الجديدة للـ Transaction table
            database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_user_id ON transactions(user_id)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_company_id ON transactions(company_id)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_reference_number ON transactions(reference_number)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_date ON transactions(date)");
            
            // إضافة الأعمدة الجديدة للـ Category table
            database.execSQL("ALTER TABLE categories ADD COLUMN color TEXT");
            database.execSQL("ALTER TABLE categories ADD COLUMN icon TEXT");
            database.execSQL("ALTER TABLE categories ADD COLUMN created_by TEXT");
            database.execSQL("ALTER TABLE categories ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1");
            database.execSQL("ALTER TABLE categories ADD COLUMN is_default INTEGER NOT NULL DEFAULT 0");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_categories_created_by ON categories(created_by)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_categories_type ON categories(type)");
            
            // تحديث Company table لاستخدام Long بدلاً من String للـ Foreign Keys
            database.execSQL("ALTER TABLE companies ADD COLUMN email TEXT");
            database.execSQL("ALTER TABLE companies ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1");
        }
    };

    // طريقة لإنشاء البيانات الافتراضية
    public static void populateInitialData(AppDatabase db) {
        databaseWriteExecutor.execute(() -> {
            // إنشاء التصنيفات الافتراضية
            CategoryDao categoryDao = db.categoryDao();
            
            if (categoryDao.getDefaultCategories().isEmpty()) {
                Category[] defaultCategories = {
                    createDefaultCategory("دخل عام", "INCOME", "#4CAF50"),
                    createDefaultCategory("مصروف عام", "EXPENSE", "#F44336"),
                    createDefaultCategory("تحويل", "TRANSFER", "#2196F3"),
                    createDefaultCategory("مبيعات", "INCOME", "#8BC34A"),
                    createDefaultCategory("مشتريات", "EXPENSE", "#FF9800"),
                    createDefaultCategory("رواتب", "EXPENSE", "#9C27B0"),
                    createDefaultCategory("إيجار", "EXPENSE", "#795548"),
                    createDefaultCategory("مرافق", "EXPENSE", "#607D8B")
                };
                
                for (Category category : defaultCategories) {
                    categoryDao.insert(category);
                }
            }
        });
    }
    
    private static Category createDefaultCategory(String name, String type, String color) {
        Category category = new Category();
        category.setName(name);
        category.setType(type);
        category.setColor(color);
        category.setDefault(true);
        category.setActive(true);
        category.setCreatedAt(System.currentTimeMillis());
        category.setLastModified(System.currentTimeMillis());
        return category;
    }
}
