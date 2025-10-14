package com.example.androidapp.data;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;
import com.example.androidapp.data.dao.*;
import com.example.androidapp.data.entities.*;

@Database(entities = {
    Account.class, AccountStatement.class, AuditLog.class, BalanceSheet.class,
    Campaign.class, Chat.class, ChatMessage.class, Comment.class, Company.class,
    CompanySettings.class, Connection.class, ContactSync.class, CurrencyExchange.class,
    Customer.class, DeliveryReceipt.class, Doctor.class, Employee.class,
    FinancialTransfer.class, Friend.class, Inventory.class, Invoice.class,
    InvoiceItem.class, Item.class, ItemUnit.class, JoinRequest.class,
    JournalEntry.class, JournalEntryItem.class, Like.class, Membership.class,
    Notification.class, Order.class, Payment.class, Payroll.class,
    PayrollItem.class, Permission.class, PointTransaction.class, Post.class,
    ProfitLossStatement.class, Purchase.class, Receipt.class, Reminder.class,
    Repair.class, Reward.class, Role.class, RolePermission.class, Service.class,
    Share.class, SharedLink.class, Supplier.class, Trophy.class, User.class,
    UserPermission.class, UserReward.class, UserRole.class, UserTrophy.class,
    Voucher.class, Warehouse.class
}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    public abstract AccountDao accountDao();
    public abstract AccountStatementDao accountStatementDao();
    public abstract AuditLogDao auditLogDao();
    public abstract CampaignDao campaignDao();
    public abstract ChatDao chatDao();
    public abstract ChatMessageDao chatMessageDao();
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
    public abstract PurchaseDao purchaseDao();
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
    private static final String DATABASE_NAME = "business_database";

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static AppDatabase getInstance(Context context) {
        return getDatabase(context);
    }
}
