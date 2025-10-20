package com.example.androidapp.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

// Import all entity classes
import com.example.androidapp.data.entities.*;

// Import all DAO interfaces
import com.example.androidapp.data.dao.*;

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
        Notification.class,
        Permission.class,
        UserPermission.class,
        Item.class,
        Customer.class,
        Employee.class
    },
    version = 6,
    exportSchema = true
)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    // Abstract methods for DAOs
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
    public abstract PermissionDao permissionDao();
    public abstract UserPermissionDao userPermissionDao();
    public abstract ItemDao itemDao();
    public abstract CustomerDao customerDao();
    public abstract EmployeeDao employeeDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "accounting_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // For compatibility
    public static AppDatabase getInstance(Context context) {
        return getDatabase(context);
    }

    // Migration from version 1 to 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Previous migration code...
            database.execSQL("ALTER TABLE users ADD COLUMN company_id TEXT");
            database.execSQL("ALTER TABLE accounts ADD COLUMN user_id INTEGER");
            database.execSQL("ALTER TABLE accounts ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1");
        }
    };

    // Migration from version 2 to 3
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Previous migration code...
            database.execSQL("CREATE TABLE IF NOT EXISTS contact_sync (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "user_id INTEGER, " +
                    "contact_identifier TEXT, " +
                    "phone_number TEXT, " +
                    "display_name TEXT, " +
                    "email TEXT, " +
                    "photo_uri TEXT, " +
                    "is_registered_user INTEGER NOT NULL DEFAULT 0, " +
                    "allow_sync INTEGER NOT NULL DEFAULT 1, " +
                    "last_sync_date INTEGER, " +
                    "status TEXT, " +
                    "updated_date INTEGER)");
        }
    };

    // Migration from version 3 to 4
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS roles (" +
                    "role_id TEXT PRIMARY KEY NOT NULL, " +
                    "name TEXT, " +
                    "description TEXT, " +
                    "permissions TEXT, " +
                    "created_at INTEGER, " +
                    "updated_at INTEGER)");

            database.execSQL("CREATE TABLE IF NOT EXISTS notifications (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "userId INTEGER, " +
                    "type TEXT, " +
                    "title TEXT, " +
                    "content TEXT, " +
                    "message TEXT, " +
                    "relatedId INTEGER, " +
                    "timestamp INTEGER, " +
                    "isRead INTEGER NOT NULL DEFAULT 0, " +
                    "entityId TEXT)");
        }
    };

    // Migration from version 4 to 5
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS permissions (" +
                    "permissionId TEXT PRIMARY KEY NOT NULL, " +
                    "name TEXT, " +
                    "description TEXT, " +
                    "category TEXT, " +
                    "createdAt INTEGER)");

            database.execSQL("CREATE TABLE IF NOT EXISTS user_permissions (" +
                    "userId INTEGER NOT NULL, " +
                    "permissionId TEXT NOT NULL, " +
                    "grantedAt INTEGER, " +
                    "grantedBy TEXT, " +
                    "PRIMARY KEY(userId, permissionId))");
        }
    };

    // Migration from version 5 to 6
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, " +
                    "code TEXT, " +
                    "description TEXT, " +
                    "price REAL, " +
                    "category TEXT, " +
                    "quantity INTEGER, " +
                    "unit TEXT, " +
                    "createdAt INTEGER)");

            database.execSQL("CREATE TABLE IF NOT EXISTS customers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, " +
                    "email TEXT, " +
                    "phone TEXT, " +
                    "address TEXT, " +
                    "companyName TEXT, " +
                    "taxNumber TEXT, " +
                    "totalPurchases REAL, " +
                    "createdAt INTEGER)");

            database.execSQL("CREATE TABLE IF NOT EXISTS employees (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "employeeId TEXT, " +
                    "name TEXT, " +
                    "email TEXT, " +
                    "phone TEXT, " +
                    "department TEXT, " +
                    "position TEXT, " +
                    "salary REAL, " +
                    "hireDate INTEGER, " +
                    "isActive INTEGER NOT NULL DEFAULT 1, " +
                    "createdAt INTEGER)");

            // Add missing fields to existing tables
            database.execSQL("ALTER TABLE accounts ADD COLUMN code TEXT");
            database.execSQL("ALTER TABLE users ADD COLUMN phone_number TEXT");
            database.execSQL("ALTER TABLE transactions ADD COLUMN date INTEGER");
        }
    };

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // يمكن إضافة بيانات أولية هنا
        }
    };

    public static void populateInitialData(AppDatabase db) {
        // إدراج الفئات الافتراضية
        CategoryDao categoryDao = db.categoryDao();
        
        // فئات الإيرادات
        Category incomeGeneral = new Category();
        incomeGeneral.name = "إيرادات عامة";
        incomeGeneral.type = "INCOME";
        incomeGeneral.color = "#4CAF50";
        incomeGeneral.icon = "money";
        incomeGeneral.isDefault = true;
        incomeGeneral.isActive = true;
        categoryDao.insert(incomeGeneral);

        Category incomeSales = new Category();
        incomeSales.name = "مبيعات";
        incomeSales.type = "INCOME";
        incomeSales.color = "#2196F3";
        incomeSales.icon = "shopping_cart";
        incomeSales.isDefault = true;
        incomeSales.isActive = true;
        categoryDao.insert(incomeSales);

        // فئات المصروفات
        Category expenseOffice = new Category();
        expenseOffice.name = "مصاريف مكتبية";
        expenseOffice.type = "EXPENSE";
        expenseOffice.color = "#FF9800";
        expenseOffice.icon = "business";
        expenseOffice.isDefault = true;
        expenseOffice.isActive = true;
        categoryDao.insert(expenseOffice);

        Category expenseTravel = new Category();
        expenseTravel.name = "مصاريف سفر";
        expenseTravel.type = "EXPENSE";
        expenseTravel.color = "#9C27B0";
        expenseTravel.icon = "flight";
        expenseTravel.isDefault = true;
        expenseTravel.isActive = true;
        categoryDao.insert(expenseTravel);
    }
}
