package com.example.androidapp.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

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
}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
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
}
