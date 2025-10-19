#!/bin/bash

# أوامر إصلاح أخطاء الكومبايل في مشروع Android
# تشغيل هذه الأوامر من مجلد ~/android-accounting-app

# 1. إصلاح ItemDao.java - إضافة الطرق المفقودة
cat > app/src/main/java/com/example/androidapp/data/dao/ItemDao.java <<'EOF'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Item;
import java.util.List;

@Dao
public interface ItemDao extends BaseDao<Item> {
    
    @Query("SELECT * FROM items")
    List<Item> getAllItems();

    @Query("SELECT * FROM items WHERE companyId = :companyId")
    List<Item> getItemsByCompany(int companyId);

    @Query("SELECT * FROM items WHERE id = :id")
    Item getItemById(int id);

    @Query("SELECT * FROM items WHERE barcode = :barcode")
    Item getItemByBarcode(String barcode);

    @Query("SELECT * FROM items WHERE categoryId = :categoryId")
    List<Item> getItemsByCategory(int categoryId);

    @Query("SELECT * FROM items WHERE quantity < minStockLevel")
    List<Item> getLowStockItems();

    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    List<Item> searchItems(String query);

    @Query("UPDATE items SET quantity = :quantity WHERE id = :itemId")
    void updateQuantity(int itemId, double quantity);

    @Query("SELECT COUNT(*) FROM items WHERE companyId = :companyId")
    int getItemCount(int companyId);

    // طرق مطلوبة للتوافق مع Product
    @Query("SELECT * FROM items")
    List<Item> getAllProducts();

    @Query("SELECT * FROM items WHERE quantity < minStockLevel")
    List<Item> getLowStockProducts();

    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%'")
    List<Item> searchProducts(String query);
}
EOF

# 2. إصلاح AppDatabase.java - إضافة productDao alias
cat > app/src/main/java/com/example/androidapp/data/AppDatabase.java <<'EOF'
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
EOF

# 3. إصلاح InvoiceDao.java - إضافة الطرق المفقودة
cat > app/src/main/java/com/example/androidapp/data/dao/InvoiceDao.java <<'EOF'
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Invoice;
import java.util.List;

@Dao
public interface InvoiceDao extends BaseDao<Invoice> {
    
    @Query("SELECT * FROM invoices")
    List<Invoice> getAllInvoices();

    @Query("SELECT * FROM invoices WHERE companyId = :companyId")
    List<Invoice> getInvoicesByCompany(int companyId);

    @Query("SELECT * FROM invoices WHERE id = :id")
    Invoice getInvoiceById(int id);

    @Query("SELECT * FROM invoices WHERE customerId = :customerId")
    List<Invoice> getInvoicesByCustomer(int customerId);

    @Query("SELECT * FROM invoices WHERE status = :status")
    List<Invoice> getInvoicesByStatus(String status);

    @Query("SELECT * FROM invoices WHERE date BETWEEN :startDate AND :endDate")
    List<Invoice> getInvoicesByDateRange(long startDate, long endDate);

    @Query("SELECT SUM(total) FROM invoices WHERE companyId = :companyId AND status = 'paid'")
    double getTotalPaidAmount(int companyId);

    @Query("SELECT SUM(total) FROM invoices WHERE companyId = :companyId AND status = 'pending'")
    double getTotalPendingAmount(int companyId);

    @Query("SELECT COUNT(*) FROM invoices WHERE companyId = :companyId")
    int getInvoiceCount(int companyId);

    @Query("SELECT * FROM invoices WHERE invoiceNumber LIKE '%' || :query || '%'")
    List<Invoice> searchInvoices(String query);

    // طرق مطلوبة للـ MainActivity
    @Query("SELECT * FROM invoices WHERE companyId = :companyId")
    List<Invoice> getAllInvoices(int companyId);

    @Query("SELECT * FROM invoices WHERE status = 'paid'")
    List<Invoice> getPaidInvoices();

    @Query("SELECT * FROM invoices WHERE status = 'pending'")
    List<Invoice> getPendingInvoices();
}
EOF

# 4. إصلاح MainActivity.java - تصحيح أنواع البيانات
cat > app/src/main/java/com/example/androidapp/ui/main/MainActivity.java <<'EOF'
package com.example.androidapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.ui.invoices.InvoiceListActivity;
import com.example.androidapp.ui.items.ItemListActivity;
import com.example.androidapp.ui.customers.CustomerListActivity;
import com.example.androidapp.ui.reports.ReportsActivity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    
    private TextView tvTotalInvoices;
    private TextView tvPendingInvoices;
    private TextView tvLowStock;
    private TextView tvTotalRevenue;
    private RecyclerView rvRecentActivities;
    
    private AppDatabase database;
    private ExecutorService executor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupDatabase();
        loadDashboardData();
    }
    
    private void initViews() {
        tvTotalInvoices = findViewById(R.id.tv_total_invoices);
        tvPendingInvoices = findViewById(R.id.tv_pending_invoices);
        tvLowStock = findViewById(R.id.tv_low_stock);
        tvTotalRevenue = findViewById(R.id.tv_total_revenue);
        rvRecentActivities = findViewById(R.id.rv_recent_activities);
        
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void setupDatabase() {
        database = AppDatabase.getInstance(this);
        executor = Executors.newFixedThreadPool(4);
    }
    
    private void loadDashboardData() {
        executor.execute(() -> {
            try {
                // Load invoices data
                List<Invoice> allInvoices = database.invoiceDao().getAllInvoices();
                List<Invoice> pendingInvoices = database.invoiceDao().getPendingInvoices();
                
                // Load items data - تصحيح النوع من Product إلى Item
                List<Item> lowStockItems = database.productDao().getLowStockProducts();
                
                // Calculate revenue
                double totalRevenue = 0;
                List<Invoice> paidInvoices = database.invoiceDao().getPaidInvoices();
                for (Invoice invoice : paidInvoices) {
                    totalRevenue += invoice.getTotal();
                }
                
                final double finalRevenue = totalRevenue;
                
                runOnUiThread(() -> {
                    tvTotalInvoices.setText(String.valueOf(allInvoices.size()));
                    tvPendingInvoices.setText(String.valueOf(pendingInvoices.size()));
                    tvLowStock.setText(String.valueOf(lowStockItems.size()));
                    tvTotalRevenue.setText(String.format("%.2f", finalRevenue));
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    tvTotalInvoices.setText("0");
                    tvPendingInvoices.setText("0");
                    tvLowStock.setText("0");
                    tvTotalRevenue.setText("0.00");
                });
            }
        });
    }
    
    public void onInvoicesClicked(View view) {
        Intent intent = new Intent(this, InvoiceListActivity.class);
        startActivity(intent);
    }
    
    public void onItemsClicked(View view) {
        Intent intent = new Intent(this, ItemListActivity.class);
        startActivity(intent);
    }
    
    public void onCustomersClicked(View view) {
        Intent intent = new Intent(this, CustomerListActivity.class);
        startActivity(intent);
    }
    
    public void onReportsClicked(View view) {
        Intent intent = new Intent(this, ReportsActivity.class);
        startActivity(intent);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
EOF

# 5. إنشاء Product.java - كلاس wrapper للتوافق
cat > app/src/main/java/com/example/androidapp/models/Product.java <<'EOF'
package com.example.androidapp.models;

import com.example.androidapp.data.entities.Item;

/**
 * Product wrapper class that extends Item for backward compatibility
 * This allows existing code that expects Product objects to work with Item entities
 */
public class Product extends Item {
    
    public Product() {
        super();
    }
    
    public Product(Item item) {
        super();
        this.setId(item.getId());
        this.setName(item.getName());
        this.setDescription(item.getDescription());
        this.setPrice(item.getPrice());
        this.setQuantity(item.getQuantity());
        this.setCategoryId(item.getCategoryId());
        this.setCompanyId(item.getCompanyId());
        this.setBarcode(item.getBarcode());
        this.setMinStockLevel(item.getMinStockLevel());
        this.setUnit(item.getUnit());
        this.setCreatedAt(item.getCreatedAt());
        this.setUpdatedAt(item.getUpdatedAt());
    }
    
    // Wrapper methods for Product-specific naming
    public String getProductName() {
        return getName();
    }
    
    public void setProductName(String productName) {
        setName(productName);
    }
    
    public String getProductDescription() {
        return getDescription();
    }
    
    public void setProductDescription(String productDescription) {
        setDescription(productDescription);
    }
    
    public double getProductPrice() {
        return getPrice();
    }
    
    public void setProductPrice(double productPrice) {
        setPrice(productPrice);
    }
    
    public double getCurrentStock() {
        return getQuantity();
    }
    
    public void setCurrentStock(double currentStock) {
        setQuantity(currentStock);
    }
    
    public double getMinimumStock() {
        return getMinStockLevel();
    }
    
    public void setMinimumStock(double minimumStock) {
        setMinStockLevel(minimumStock);
    }
    
    public boolean isLowStock() {
        return getQuantity() < getMinStockLevel();
    }
    
    public double getStockValue() {
        return getQuantity() * getPrice();
    }
}
EOF

# 6. إصلاح SmartSuggestionsManager.java - تصحيح أنواع البيانات
cat > app/src/main/java/com/example/androidapp/utils/SmartSuggestionsManager.java <<'EOF'
package com.example.androidapp.utils;

import android.content.Context;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.Customer;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmartSuggestionsManager {
    
    private static SmartSuggestionsManager instance;
    private AppDatabase database;
    private ExecutorService executor;
    private Context context;
    
    private SmartSuggestionsManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(this.context);
        this.executor = Executors.newFixedThreadPool(2);
    }
    
    public static synchronized SmartSuggestionsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SmartSuggestionsManager(context);
        }
        return instance;
    }
    
    public interface SuggestionCallback {
        void onSuggestionsReceived(List<String> suggestions);
        void onError(String error);
    }
    
    public void getProductSuggestions(String query, SuggestionCallback callback) {
        executor.execute(() -> {
            try {
                // تصحيح النوع من List<Product> إلى List<Item>
                List<Item> products = database.productDao().searchProducts(query);
                List<String> suggestions = new ArrayList<>();
                
                for (Item product : products) {
                    suggestions.add(product.getName());
                }
                
                // Post results to main thread
                if (callback != null) {
                    callback.onSuggestionsReceived(suggestions);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("Failed to load product suggestions: " + e.getMessage());
                }
            }
        });
    }
    
    public void getCustomerSuggestions(String query, SuggestionCallback callback) {
        executor.execute(() -> {
            try {
                List<Customer> customers = database.customerDao().searchCustomers(query);
                List<String> suggestions = new ArrayList<>();
                
                for (Customer customer : customers) {
                    suggestions.add(customer.getName());
                }
                
                if (callback != null) {
                    callback.onSuggestionsReceived(suggestions);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("Failed to load customer suggestions: " + e.getMessage());
                }
            }
        });
    }
    
    public void getRecentInvoiceSuggestions(SuggestionCallback callback) {
        executor.execute(() -> {
            try {
                List<Invoice> recentInvoices = database.invoiceDao().getAllInvoices();
                List<String> suggestions = new ArrayList<>();
                
                // Get the last 5 invoices
                int limit = Math.min(5, recentInvoices.size());
                for (int i = 0; i < limit; i++) {
                    Invoice invoice = recentInvoices.get(i);
                    suggestions.add("Invoice #" + invoice.getInvoiceNumber());
                }
                
                if (callback != null) {
                    callback.onSuggestionsReceived(suggestions);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("Failed to load invoice suggestions: " + e.getMessage());
                }
            }
        });
    }
    
    public void getLowStockAlerts(SuggestionCallback callback) {
        executor.execute(() -> {
            try {
                // تصحيح النوع من List<Product> إلى List<Item>
                List<Item> lowStockItems = database.productDao().getLowStockProducts();
                List<String> alerts = new ArrayList<>();
                
                for (Item item : lowStockItems) {
                    alerts.add(item.getName() + " - Stock: " + item.getQuantity());
                }
                
                if (callback != null) {
                    callback.onSuggestionsReceived(alerts);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("Failed to load low stock alerts: " + e.getMessage());
                }
            }
        });
    }
    
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
EOF

echo "تم إنشاء جميع ملفات الإصلاح بنجاح!"
echo "لتطبيق الإصلاحات، قم بتشغيل هذا الملف من مجلد ~/android-accounting-app"
echo "chmod +x android_fixes.sh && ./android_fixes.sh"
