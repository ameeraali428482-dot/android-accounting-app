package com.example.androidapp.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.androidapp.data.dao.AccountDao;
import com.example.androidapp.data.dao.CustomerDao;
import com.example.androidapp.data.dao.EmployeeDao;
import com.example.androidapp.data.dao.ItemDao;
import com.example.androidapp.data.dao.NotificationDao;
import com.example.androidapp.data.dao.PermissionDao;
import com.example.androidapp.data.dao.RoleDao;
import com.example.androidapp.data.dao.TransactionDao;
import com.example.androidapp.data.dao.UserDao;
import com.example.androidapp.data.dao.UserPermissionDao;
import com.example.androidapp.data.dao.UserRoleDao;
import com.example.androidapp.data.dao.ContactSyncDao;

import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.ContactSync;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.Notification;
import com.example.androidapp.data.entities.Permission;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.data.entities.Transaction;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.UserPermission;
import com.example.androidapp.data.entities.UserRole;

@Database(
    entities = {
        User.class,
        Account.class,
        Transaction.class,
        Role.class,
        Permission.class,
        UserPermission.class,
        UserRole.class,
        ContactSync.class,
        Item.class,
        Customer.class,
        Employee.class,
        Notification.class
    },
    version = 7,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract AccountDao accountDao();
    public abstract TransactionDao transactionDao();
    public abstract RoleDao roleDao();
    public abstract PermissionDao permissionDao();
    public abstract UserPermissionDao userPermissionDao();
    public abstract UserRoleDao userRoleDao();
    public abstract ContactSyncDao contactSyncDao();
    public abstract ItemDao itemDao();
    public abstract CustomerDao customerDao();
    public abstract EmployeeDao employeeDao();
    public abstract NotificationDao notificationDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "accounting_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
