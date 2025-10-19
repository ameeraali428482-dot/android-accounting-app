package com.example.androidapp.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import com.example.androidapp.data.entities.*;
import com.example.androidapp.data.dao.*;

@Database(
    entities = {
        Account.class,
        AccountStatement.class,
        AIConversation.class,
        AuditLog.class,
        BarcodeData.class,
        Campaign.class,
        Category.class,
        Chat.class,
        ChatMessage.class,
        Comment.class,
        Company.class,
        CompanySettings.class,
        Connection.class,
        ContactSync.class,
        CurrencyExchange.class,
        Customer.class,
        DataBackup.class,
        DeliveryReceipt.class,
        Doctor.class,
        Employee.class,
        ExternalNotification.class,
        FinancialTransfer.class,
        Friend.class,
        InstitutionProfile.class,
        Inventory.class,
        Invoice.class,
        InvoiceItem.class,
        Item.class,
        ItemUnit.class,
        JoinRequest.class,
        JournalEntry.class,
        JournalEntryItem.class,
        Like.class,
        Membership.class,
        Notification.class,
        OfflineTransaction.class,
        Order.class,
        OrderItem.class,
        Payment.class,
        Payroll.class,
        PayrollItem.class,
        PeriodicReminder.class,
        Permission.class,
        PointTransaction.class,
        Post.class,
        Purchase.class,
        Receipt.class,
        Reminder.class,
        Repair.class,
        Reward.class,
        Role.class,
        Service.class,
        Share.class,
        SharedLink.class,
        SmartNotification.class,
        Supplier.class,
        Transaction.class,
        Trophy.class,
        User.class,
        UserPermission.class,
        UserPoints.class,
        UserReward.class,
        UserRole.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static volatile AppDatabase INSTANCE;
    
    // DAOs
    public abstract AccountDao accountDao();
    public abstract AccountStatementDao accountStatementDao();
    public abstract AIConversationDao aiConversationDao();
    public abstract AuditLogDao auditLogDao();
    public abstract BarcodeDataDao barcodeDataDao();
    public abstract CampaignDao campaignDao();
    public abstract CategoryDao categoryDao();
    public abstract ChatDao chatDao();
    public abstract ChatMessageDao chatMessageDao();
    public abstract CommentDao commentDao();
    public abstract CompanyDao companyDao();
    public abstract CompanySettingsDao companySettingsDao();
    public abstract ConnectionDao connectionDao();
    public abstract ContactSyncDao contactSyncDao();
    public abstract CurrencyExchangeDao currencyExchangeDao();
    public abstract CustomerDao customerDao();
    public abstract DataBackupDao dataBackupDao();
    public abstract DeliveryReceiptDao deliveryReceiptDao();
    public abstract DoctorDao doctorDao();
    public abstract EmployeeDao employeeDao();
    public abstract ExternalNotificationDao externalNotificationDao();
    public abstract FinancialTransferDao financialTransferDao();
    public abstract FriendDao friendDao();
    public abstract InstitutionProfileDao institutionProfileDao();
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
    public abstract OfflineTransactionDao offlineTransactionDao();
    public abstract OrderDao orderDao();
    public abstract OrderItemDao orderItemDao();
    public abstract PaymentDao paymentDao();
    public abstract PayrollDao payrollDao();
    public abstract PayrollItemDao payrollItemDao();
    public abstract PeriodicReminderDao periodicReminderDao();
    public abstract PermissionDao permissionDao();
    public abstract PointTransactionDao pointTransactionDao();
    public abstract PostDao postDao();
    public abstract PurchaseDao purchaseDao();
    public abstract ReceiptDao receiptDao();
    public abstract ReminderDao reminderDao();
    public abstract RepairDao repairDao();
    public abstract RewardDao rewardDao();
    public abstract RoleDao roleDao();
    public abstract ServiceDao serviceDao();
    public abstract ShareDao shareDao();
    public abstract SharedLinkDao sharedLinkDao();
    public abstract SmartNotificationDao smartNotificationDao();
    public abstract SupplierDao supplierDao();
    public abstract TransactionDao transactionDao();
    public abstract TrophyDao trophyDao();
    public abstract UserDao userDao();
    public abstract UserPermissionDao userPermissionDao();
    public abstract UserPointsDao userPointsDao();
    public abstract UserRewardDao userRewardDao();
    public abstract UserRoleDao userRoleDao();
    
    // Alias for Item DAO for backward compatibility
    public ItemDao productDao() {
        return itemDao();
    }
    
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "accounting_app_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
    
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
