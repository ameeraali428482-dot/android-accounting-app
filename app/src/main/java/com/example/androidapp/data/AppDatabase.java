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

import com.example.androidapp.data.dao.UserDao;
import com.example.androidapp.data.dao.AccountDao;
import com.example.androidapp.data.dao.TransactionDao;
import com.example.androidapp.data.dao.CompanyDao;
import com.example.androidapp.data.dao.CategoryDao;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.dao.ContactSyncDao;
import com.example.androidapp.data.dao.FriendDao;
import com.example.androidapp.data.dao.UserRoleDao;

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
        UserRole.class
    },
    version = 4,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    private static volatile AppDatabase INSTANCE;
    
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
    
    // Migration from version 1 to 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add new columns to existing tables
            database.execSQL("ALTER TABLE users ADD COLUMN created_at INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE users ADD COLUMN updated_at INTEGER DEFAULT 0");
            
            // Create companies table
            database.execSQL("CREATE TABLE IF NOT EXISTS companies (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, " +
                    "address TEXT, " +
                    "phone TEXT, " +
                    "email TEXT)");
            
            // Create categories table
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
            // Create invoices table
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
            
            // Create contact_syncs table
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
            // Create friends table
            database.execSQL("CREATE TABLE IF NOT EXISTS friends (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "friend_id INTEGER NOT NULL, " +
                    "created_at INTEGER NOT NULL, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(friend_id) REFERENCES users(id) ON DELETE CASCADE)");
            
            database.execSQL("CREATE INDEX IF NOT EXISTS index_friends_user_id ON friends(user_id)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_friends_friend_id ON friends(friend_id)");
            
            // Create user_roles table
            database.execSQL("CREATE TABLE IF NOT EXISTS user_roles (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "role_name TEXT, " +
                    "permissions TEXT, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)");
            
            database.execSQL("CREATE INDEX IF NOT EXISTS index_user_roles_user_id ON user_roles(user_id)");
        }
    };
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
