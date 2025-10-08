package com.example.androidapp.data;
import com.example.androidapp.data.dao.AccountDao;
import com.example.androidapp.data.dao.AccountStatementDao;
import com.example.androidapp.data.dao.CampaignDao;
import com.example.androidapp.data.dao.ChatDao;
import com.example.androidapp.data.dao.CommentDao;
import com.example.androidapp.data.dao.CompanyDao;
import com.example.androidapp.data.dao.CompanySettingsDao;
import com.example.androidapp.data.dao.ConnectionDao;
import com.example.androidapp.data.dao.ContactSyncDao;
import com.example.androidapp.data.dao.CurrencyExchangeDao;
import com.example.androidapp.data.dao.CustomerDao;
import com.example.androidapp.data.dao.DeliveryReceiptDao;
import com.example.androidapp.data.dao.DoctorDao;
import com.example.androidapp.data.dao.EmployeeDao;
import com.example.androidapp.data.dao.FinancialTransferDao;
import com.example.androidapp.data.dao.FriendDao;
import com.example.androidapp.data.dao.InventoryDao;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.dao.InvoiceItemDao;
import com.example.androidapp.data.dao.ItemDao;
import com.example.androidapp.data.dao.ItemUnitDao;
import com.example.androidapp.data.dao.JoinRequestDao;
import com.example.androidapp.data.dao.JournalEntryDao;
import com.example.androidapp.data.dao.JournalEntryItemDao;
import com.example.androidapp.data.dao.LikeDao;
import com.example.androidapp.data.dao.MembershipDao;
import com.example.androidapp.data.dao.NotificationDao;
import com.example.androidapp.data.dao.OrderDao;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.data.dao.PayrollDao;
import com.example.androidapp.data.dao.PayrollItemDao;
import com.example.androidapp.data.dao.PermissionDao;
import com.example.androidapp.data.dao.PointTransactionDao;
import com.example.androidapp.data.dao.PostDao;
import com.example.androidapp.data.dao.ReceiptDao;
import com.example.androidapp.data.dao.ReminderDao;
import com.example.androidapp.data.dao.RepairDao;
import com.example.androidapp.data.dao.RewardDao;
import com.example.androidapp.data.dao.RoleDao;
import com.example.androidapp.data.dao.ServiceDao;
import com.example.androidapp.data.dao.ShareDao;
import com.example.androidapp.data.dao.SharedLinkDao;
import com.example.androidapp.data.dao.SupplierDao;
import com.example.androidapp.data.dao.TrophyDao;
import com.example.androidapp.data.dao.UserDao;
import com.example.androidapp.data.dao.UserPermissionDao;
import com.example.androidapp.data.dao.UserRewardDao;
import com.example.androidapp.data.dao.UserRoleDao;
import com.example.androidapp.data.dao.UserTrophyDao;
import com.example.androidapp.data.dao.VoucherDao;
import com.example.androidapp.data.dao.WarehouseDao;


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
        Payment.class, Receipt.class, Reminder.class, Notification.class, Campaign.class,
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

