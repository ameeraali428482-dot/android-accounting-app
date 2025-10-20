package com.example.androidapp.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.Transaction;
import com.example.androidapp.data.entities.Company;
import com.example.androidapp.data.entities.Category;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.ContactSync;
import com.example.androidapp.data.entities.Friend;
import com.example.androidapp.data.entities.UserRole;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.data.entities.Notification;

import com.example.androidapp.data.dao.UserDao;
import com.example.androidapp.data.dao.AccountDao;
import com.example.androidapp.data.dao.TransactionDao;
import com.example.androidapp.data.dao.CompanyDao;
import com.example.androidapp.data.dao.CategoryDao;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.dao.ContactSyncDao;
import com.example.androidapp.data.dao.FriendDao;
import com.example.androidapp.data.dao.UserRoleDao;
import com.example.androidapp.data.dao.RoleDao;
import com.example.androidapp.data.dao.NotificationDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities = {
        User.class,
        Account.class,
        Transaction.class,
        Company.class,
        Category.class,
        Invoice.class,
        ContactSync.class,
        Friend.class,
        UserRole.class,
        Role.class,
        Notification.class
    },
    version = 6,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    private static volatile AppDatabase INSTANCE;
    
    // إضافة ExecutorService للعمليات غير المتزامنة
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);
    
    // DAOs
    public abstract UserDao userDao();
    public abstract AccountDao accountDao();
    public abstract TransactionDao transactionDao();
    public abstract CompanyDao companyDao();
    public abstract CategoryDao categoryDao();
    public abstract InvoiceDao invoiceDao();
    public abstract ContactSyncDao contactSyncDao();
    public abstract FriendDao friendDao();
    public abstract UserRoleDao userRoleDao();
    public abstract RoleDao roleDao();
    public abstract NotificationDao notificationDao();
    
    // Migration from version 1 to 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN created_at INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE users ADD COLUMN updated_at INTEGER DEFAULT 0");
            
            database.execSQL("CREATE TABLE IF NOT EXISTS companies (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, " +
                    "address TEXT, " +
                    "phone TEXT, " +
                    "email TEXT)");
            
            database.execSQL("CREATE TABLE IF NOT EXISTS categories (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, " +
                    "description TEXT, " +
                    "color TEXT)");
        }
    };
    
    // Migration from version 2 to 3
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS invoices (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "customer_id INTEGER NOT NULL, " +
                    "invoice_number TEXT, " +
                    "total_amount REAL NOT NULL, " +
                    "issue_date INTEGER NOT NULL, " +
                    "due_date INTEGER NOT NULL, " +
                    "status TEXT, " +
                    "FOREIGN KEY(customer_id) REFERENCES companies(id) ON DELETE CASCADE)");
            
            database.execSQL("CREATE INDEX IF NOT EXISTS index_invoices_customer_id ON invoices(customer_id)");
            
            database.execSQL("CREATE TABLE IF NOT EXISTS contact_syncs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "contact_identifier TEXT, " +
                    "phone_number TEXT, " +
                    "allow_sync INTEGER NOT NULL, " +
                    "last_sync_date INTEGER NOT NULL, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)");
            
            database.execSQL("CREATE INDEX IF NOT EXISTS index_contact_syncs_user_id ON contact_syncs(user_id)");
        }
    };
    
    // Migration from version 3 to 4
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS friends (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "friend_id INTEGER NOT NULL, " +
                    "created_at INTEGER NOT NULL, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(friend_id) REFERENCES users(id) ON DELETE CASCADE)");
            
            database.execSQL("CREATE INDEX IF NOT EXISTS index_friends_user_id ON friends(user_id)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_friends_friend_id ON friends(friend_id)");
            
            database.execSQL("CREATE TABLE IF NOT EXISTS user_roles (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "role_name TEXT, " +
                    "permissions TEXT, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)");
            
            database.execSQL("CREATE INDEX IF NOT EXISTS index_user_roles_user_id ON user_roles(user_id)");
        }
    };
    
    // Migration from version 4 to 5
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS roles (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "role_id TEXT, " +
                    "name TEXT, " +
                    "description TEXT, " +
                    "permissions TEXT, " +
                    "created_at INTEGER NOT NULL)");
            
            database.execSQL("CREATE INDEX IF NOT EXISTS index_roles_role_id ON roles(role_id)");
            
            database.execSQL("CREATE TABLE IF NOT EXISTS notifications (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "type TEXT, " +
                    "title TEXT, " +
                    "content TEXT, " +
                    "user_id TEXT, " +
                    "reference_id TEXT, " +
                    "is_read INTEGER NOT NULL, " +
                    "created_at TEXT, " +
                    "updated_at TEXT)");
            
            database.execSQL("CREATE INDEX IF NOT EXISTS index_notifications_user_id ON notifications(user_id)");
            
            database.execSQL("ALTER TABLE contact_syncs ADD COLUMN display_name TEXT");
            database.execSQL("ALTER TABLE contact_syncs ADD COLUMN email TEXT");
            database.execSQL("ALTER TABLE contact_syncs ADD COLUMN photo_uri TEXT");
            database.execSQL("ALTER TABLE contact_syncs ADD COLUMN is_registered_user INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE contact_syncs ADD COLUMN registered_user_id INTEGER");
            database.execSQL("ALTER TABLE contact_syncs ADD COLUMN sync_status TEXT DEFAULT 'PENDING'");
            database.execSQL("ALTER TABLE contact_syncs ADD COLUMN created_date INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE contact_syncs ADD COLUMN updated_date INTEGER DEFAULT 0");
            
            database.execSQL("ALTER TABLE users ADD COLUMN phone TEXT");
        }
    };
    
    // Migration from version 5 to 6
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // تحديث Transaction table structure
            database.execSQL("ALTER TABLE transactions ADD COLUMN account_id INTEGER");
            database.execSQL("ALTER TABLE transactions ADD COLUMN category_id INTEGER");
            database.execSQL("ALTER TABLE transactions ADD COLUMN transaction_date INTEGER");
            
            // تحديث Account table structure
            database.execSQL("ALTER TABLE accounts ADD COLUMN account_name TEXT");
            database.execSQL("ALTER TABLE accounts ADD COLUMN account_type TEXT");
            database.execSQL("ALTER TABLE accounts ADD COLUMN balance REAL DEFAULT 0");
            database.execSQL("ALTER TABLE accounts ADD COLUMN created_at INTEGER DEFAULT 0");
        }
    };
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
