package com.example.androidapp.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.androidapp.data.dao.*;
import com.example.androidapp.data.entities.*;

@Database(entities = {
    Account.class,
    Transaction.class,
    Category.class,
    User.class,
    Company.class,
    Invoice.class,
    InvoiceItem.class,
    Item.class,
    Order.class,
    OrderItem.class,
    Payment.class,
    Receipt.class,
    Warehouse.class,
    Inventory.class,
    Post.class,
    Comment.class,
    Like.class,
    Share.class,
    Chat.class,
    ChatMessage.class,
    AIConversation.class,
    BarcodeData.class,
    DataBackup.class,
    ExternalNotification.class,
    InstitutionProfile.class,
    OfflineTransaction.class,
    PeriodicReminder.class,
    SmartNotification.class,
    UserPoints.class,
    Role.class,
    Permission.class,
    UserPermission.class,
    UserRole.class,
    RolePermission.class,
    AuditLog.class,
    AccountStatement.class,
    BalanceSheet.class,
    ProfitLossStatement.class,
    Campaign.class,
    DeliveryReceipt.class,
    Connection.class,
    SharedLink.class,
    CompanySettings.class,
    PointTransaction.class,
    Reward.class,
    UserReward.class,
    Employee.class,
    Payroll.class,
    PayrollItem.class,
    Service.class,
    Doctor.class,
    Voucher.class,
    FinancialTransfer.class,
    CurrencyExchange.class,
    JoinRequest.class,
    Repair.class,
    Trophy.class,
    UserTrophy.class,
    Friend.class,
    ContactSync.class,
    Purchase.class
}, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract AccountDao accountDao();
    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract UserDao userDao();
    public abstract CompanyDao companyDao();
    public abstract InvoiceDao invoiceDao();
    public abstract InvoiceItemDao invoiceItemDao();
    public abstract ItemDao itemDao();
    public abstract OrderDao orderDao();
    public abstract OrderItemDao orderItemDao();
    public abstract PaymentDao paymentDao();
    public abstract ReceiptDao receiptDao();
    public abstract WarehouseDao warehouseDao();
    public abstract InventoryDao inventoryDao();
    public abstract PostDao postDao();
    public abstract CommentDao commentDao();
    public abstract LikeDao likeDao();
    public abstract ShareDao shareDao();
    public abstract ChatDao chatDao();
    public abstract ChatMessageDao chatMessageDao();
    public abstract AIConversationDao aiConversationDao();
    public abstract BarcodeDataDao barcodeDataDao();
    public abstract DataBackupDao dataBackupDao();
    public abstract ExternalNotificationDao externalNotificationDao();
    public abstract InstitutionProfileDao institutionProfileDao();
    public abstract OfflineTransactionDao offlineTransactionDao();
    public abstract PeriodicReminderDao periodicReminderDao();
    public abstract SmartNotificationDao smartNotificationDao();
    public abstract UserPointsDao userPointsDao();
    public abstract RoleDao roleDao();
    public abstract PermissionDao permissionDao();
    public abstract UserPermissionDao userPermissionDao();
    public abstract UserRoleDao userRoleDao();
    public abstract RolePermissionDao rolePermissionDao();
    public abstract AuditLogDao auditLogDao();
    public abstract AccountStatementDao accountStatementDao();
    public abstract CampaignDao campaignDao();
    public abstract DeliveryReceiptDao deliveryReceiptDao();
    public abstract ConnectionDao connectionDao();
    public abstract SharedLinkDao sharedLinkDao();
    public abstract CompanySettingsDao companySettingsDao();
    public abstract PointTransactionDao pointTransactionDao();
    public abstract RewardDao rewardDao();
    public abstract UserRewardDao userRewardDao();
    public abstract EmployeeDao employeeDao();
    public abstract PayrollDao payrollDao();
    public abstract PayrollItemDao payrollItemDao();
    public abstract ServiceDao serviceDao();
    public abstract DoctorDao doctorDao();
    public abstract VoucherDao voucherDao();
    public abstract FinancialTransferDao financialTransferDao();
    public abstract CurrencyExchangeDao currencyExchangeDao();
    public abstract JoinRequestDao joinRequestDao();
    public abstract RepairDao repairDao();
    public abstract TrophyDao trophyDao();
    public abstract UserTrophyDao userTrophyDao();
    public abstract FriendDao friendDao();
    public abstract ContactSyncDao contactSyncDao();
    public abstract PurchaseDao purchaseDao();
    
    private static volatile AppDatabase INSTANCE;
    
    public static AppDatabase getDatabase() {
        return INSTANCE;
    }
}
