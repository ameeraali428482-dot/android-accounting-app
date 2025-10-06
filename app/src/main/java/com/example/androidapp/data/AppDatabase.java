package com.example.androidapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.androidapp.data.dao.*;
import com.example.androidapp.data.entities.*;

@Database(entities = {
        User.class, Company.class, Role.class, Permission.class, Membership.class,
        Account.class, Item.class, ItemUnit.class, Customer.class, Supplier.class,
        Invoice.class, InvoiceItem.class, JournalEntry.class, JournalEntryItem.class,
        Payment.class, Receipt.class, Reminder.class, Notification.class, AuditLog.class, Campaign.class,
        DeliveryReceipt.class, Connection.class, SharedLink.class, CompanySettings.class,
        PointTransaction.class, Reward.class, UserReward.class, Employee.class,
        Payroll.class, PayrollItem.class, Service.class, Doctor.class, Voucher.class,
        FinancialTransfer.class, CurrencyExchange.class, JoinRequest.class, Chat.class, Repair.class, Order.class, Trophy.class, UserPermission.class, UserRole.class, AccountStatement.class, UserTrophy.class,
        Warehouse.class, Inventory.class, Post.class, Comment.class, Like.class, Share.class
},
        version = 2, exportSchema = false) // Increment version to 2 due to schema changes
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract CompanyDao companyDao();
    public abstract RoleDao roleDao();
    public abstract PermissionDao permissionDao();
    public abstract MembershipDao membershipDao();
    public abstract AccountDao accountDao();
    public abstract ItemDao itemDao();
    public abstract ItemUnitDao itemUnitDao();
    public abstract CustomerDao customerDao();
    public abstract SupplierDao supplierDao();
    public abstract InvoiceDao invoiceDao();
    public abstract InvoiceItemDao invoiceItemDao();
    public abstract JournalEntryDao journalEntryDao();
    public abstract JournalEntryItemDao journalEntryItemDao();
    public abstract PaymentDao paymentDao();
    public abstract ReceiptDao receiptDao();
    public abstract ReminderDao reminderDao();
    public abstract NotificationDao notificationDao();
    public abstract AuditLogDao auditLogDao();
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
    public abstract ChatDao chatDao();
    public abstract RepairDao repairDao();
    public abstract OrderDao orderDao();
    public abstract TrophyDao trophyDao();
    public abstract UserPermissionDao userPermissionDao();
    public abstract UserRoleDao userRoleDao();
    public abstract AccountStatementDao accountStatementDao();
    public abstract UserTrophyDao userTrophyDao();
    public abstract WarehouseDao warehouseDao();
    public abstract InventoryDao inventoryDao();
    public abstract PostDao postDao();
    public abstract CommentDao commentDao();
    public abstract LikeDao likeDao();
    public abstract ShareDao shareDao();

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

