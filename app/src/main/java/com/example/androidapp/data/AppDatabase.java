package com.example.androidapp.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;
import com.example.androidapp.data.dao.*;
import com.example.androidapp.data.entities.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities = {
        Account.class, Transaction.class, Customer.class, Supplier.class,
        Item.class, Invoice.class, Employee.class, Category.class,
        Notification.class, // Add all other entities here
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract AccountDao accountDao();
    public abstract TransactionDao transactionDao();
    public abstract CustomerDao customerDao();
    public abstract SupplierDao supplierDao();
    public abstract ItemDao itemDao();
    public abstract InvoiceDao invoiceDao();
    public abstract EmployeeDao employeeDao();
    public abstract CategoryDao categoryDao();
    public abstract NotificationDao notificationDao();
    // Add other DAOs as needed
    
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class, 
                        "accounting_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
