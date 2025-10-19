package com.example.androidapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.androidapp.R;
import com.example.androidapp.ui.auth.LoginActivity;
import com.example.androidapp.ui.item.ItemListActivity;
import com.example.androidapp.ui.customer.CustomerListActivity;
import com.example.androidapp.ui.supplier.SupplierListActivity;
import com.example.androidapp.ui.invoice.InvoiceListActivity;
import com.example.androidapp.ui.reports.ReportsActivity;
import com.example.androidapp.ui.account.AccountListActivity;
import com.example.androidapp.ui.employee.EmployeeListActivity;
import com.example.androidapp.ui.settings.SettingsActivity;
import com.example.androidapp.utils.SessionManager;
import com.example.androidapp.utils.Material3Helper;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.*;
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.NumberFormat;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    private AppDatabase database;
    private ExecutorService executorService;
    private Handler mainHandler;
    
    // UI Components
    private MaterialToolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView welcomeTextView;
    private TextView dateTimeTextView;
    private GridLayout statsGrid;
    private LinearLayout quickActionsLayout;
    private LinearLayout recentActivitiesLayout;
    private FloatingActionButton mainFab;
    private BottomNavigationView bottomNavigation;
    
    // Stats Cards
    private MaterialCardView totalRevenueCard;
    private MaterialCardView totalCustomersCard;
    private MaterialCardView totalInvoicesCard;
    private MaterialCardView totalProductsCard;
    private MaterialCardView pendingInvoicesCard;
    private MaterialCardView lowStockCard;
    
    // Stats Values
    private TextView totalRevenueValue;
    private TextView totalCustomersValue;
    private TextView totalInvoicesValue;
    private TextView totalProductsValue;
    private TextView pendingInvoicesValue;
    private TextView lowStockValue;
    
    // Quick Action Buttons
    private MaterialButton newInvoiceBtn;
    private MaterialButton newCustomerBtn;
    private MaterialButton newProductBtn;
    private MaterialButton addPaymentBtn;
    private MaterialButton inventoryBtn;
    private MaterialButton reportsBtn;
    
    // Statistics Data
    private double totalRevenue = 0.0;
    private int totalCustomers = 0;
    private int totalInvoices = 0;
    private int totalProducts = 0;
    private int pendingInvoices = 0;
    private int lowStockProducts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_modern);

        initializeComponents();
        setupAuthentication();
        setupUI();
        setupListeners();
        loadDashboardData();
        startRealTimeUpdates();
    }
    
    private void initializeComponents() {
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(getApplicationContext());
        database = AppDatabase.getInstance(this);
        executorService = Executors.newFixedThreadPool(3);
        mainHandler = new Handler(Looper.getMainLooper());
        
        // Initialize UI components
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        dateTimeTextView = findViewById(R.id.dateTimeTextView);
        statsGrid = findViewById(R.id.statsGrid);
        quickActionsLayout = findViewById(R.id.quickActionsLayout);
        recentActivitiesLayout = findViewById(R.id.recentActivitiesLayout);
        mainFab = findViewById(R.id.mainFab);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        
        // Initialize stats cards
        totalRevenueCard = findViewById(R.id.totalRevenueCard);
        totalCustomersCard = findViewById(R.id.totalCustomersCard);
        totalInvoicesCard = findViewById(R.id.totalInvoicesCard);
        totalProductsCard = findViewById(R.id.totalProductsCard);
        pendingInvoicesCard = findViewById(R.id.pendingInvoicesCard);
        lowStockCard = findViewById(R.id.lowStockCard);
        
        // Initialize stats values
        totalRevenueValue = findViewById(R.id.totalRevenueValue);
        totalCustomersValue = findViewById(R.id.totalCustomersValue);
        totalInvoicesValue = findViewById(R.id.totalInvoicesValue);
        totalProductsValue = findViewById(R.id.totalProductsValue);
        pendingInvoicesValue = findViewById(R.id.pendingInvoicesValue);
        lowStockValue = findViewById(R.id.lowStockValue);
        
        // Initialize quick action buttons
        newInvoiceBtn = findViewById(R.id.newInvoiceBtn);
        newCustomerBtn = findViewById(R.id.newCustomerBtn);
        newProductBtn = findViewById(R.id.newProductBtn);
        addPaymentBtn = findViewById(R.id.addPaymentBtn);
        inventoryBtn = findViewById(R.id.inventoryBtn);
        reportsBtn = findViewById(R.id.reportsBtn);
    }
    
    private void setupAuthentication() {
        if (!sessionManager.isLoggedIn()) {
            logoutUser();
            return;
        }
        
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        String username = userDetails.get(SessionManager.KEY_USER_ID);
        String displayName = userDetails.get(SessionManager.KEY_NAME);
        
        if (displayName != null && !displayName.isEmpty()) {
            welcomeTextView.setText("مرحباً بك، " + displayName + " 👋");
        } else if (username != null && !username.isEmpty()) {
            welcomeTextView.setText("مرحباً بك، " + username + " 👋");
        } else {
            welcomeTextView.setText("مرحباً بك في نظام المحاسبة 👋");
        }
        
        updateDateTime();
    }
    
    private void setupUI() {
        // Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("لوحة التحكم");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        
        // Apply Material 3 styling
        setupMaterial3Components();
        
        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeColors(
            getResources().getColor(android.R.color.holo_blue_bright),
            getResources().getColor(android.R.color.holo_green_light),
            getResources().getColor(android.R.color.holo_orange_light)
        );
        
        // Setup Bottom Navigation
        setupBottomNavigation();
        
        // Create quick action chips
        createQuickActionChips();
    }
    
    private void setupMaterial3Components() {
        // Setup stats cards
        Material3Helper.Components.setupMaterialCard(totalRevenueCard, Material3Helper.CardStyle.ELEVATED);
        Material3Helper.Components.setupMaterialCard(totalCustomersCard, Material3Helper.CardStyle.FILLED);
        Material3Helper.Components.setupMaterialCard(totalInvoicesCard, Material3Helper.CardStyle.FILLED);
        Material3Helper.Components.setupMaterialCard(totalProductsCard, Material3Helper.CardStyle.FILLED);
        Material3Helper.Components.setupMaterialCard(pendingInvoicesCard, Material3Helper.CardStyle.OUTLINED);
        Material3Helper.Components.setupMaterialCard(lowStockCard, Material3Helper.CardStyle.OUTLINED);
        
        // Setup buttons
        Material3Helper.Components.setupMaterialButton(newInvoiceBtn, Material3Helper.ButtonStyle.FILLED);
        Material3Helper.Components.setupMaterialButton(newCustomerBtn, Material3Helper.ButtonStyle.OUTLINED);
        Material3Helper.Components.setupMaterialButton(newProductBtn, Material3Helper.ButtonStyle.OUTLINED);
        Material3Helper.Components.setupMaterialButton(addPaymentBtn, Material3Helper.ButtonStyle.TONAL);
        Material3Helper.Components.setupMaterialButton(inventoryBtn, Material3Helper.ButtonStyle.TEXT);
        Material3Helper.Components.setupMaterialButton(reportsBtn, Material3Helper.ButtonStyle.TEXT);
        
        // Setup FAB
        Material3Helper.Components.setupFAB(mainFab, Material3Helper.FABStyle.NORMAL);
    }
    
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Already in home
                return true;
            } else if (itemId == R.id.nav_customers) {
                startActivity(new Intent(this, CustomerListActivity.class));
                return true;
            } else if (itemId == R.id.nav_products) {
                startActivity(new Intent(this, ItemListActivity.class));
                return true;
            } else if (itemId == R.id.nav_reports) {
                startActivity(new Intent(this, ReportsActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
        
        // Set home as selected
        bottomNavigation.setSelectedItemId(R.id.nav_home);
    }
    
    private void createQuickActionChips() {
        ChipGroup quickChips = findViewById(R.id.quickChips);
        if (quickChips != null) {
            quickChips.removeAllViews();
            
            String[] chipLabels = {"فاتورة جديدة", "عميل جديد", "منتج جديد", "دفعة", "تقارير", "إعدادات"};
            for (String label : chipLabels) {
                Chip chip = Material3Helper.Components.createMaterial3Chip(this, label, Material3Helper.ChipType.ASSIST);
                chip.setOnClickListener(v -> handleQuickChipClick(label));
                quickChips.addView(chip);
            }
        }
    }
    
    private void handleQuickChipClick(String action) {
        switch (action) {
            case "فاتورة جديدة":
                startActivity(new Intent(this, InvoiceListActivity.class));
                break;
            case "عميل جديد":
                startActivity(new Intent(this, CustomerListActivity.class));
                break;
            case "منتج جديد":
                startActivity(new Intent(this, ItemListActivity.class));
                break;
            case "دفعة":
                startActivity(new Intent(this, AccountListActivity.class));
                break;
            case "تقارير":
                startActivity(new Intent(this, ReportsActivity.class));
                break;
            case "إعدادات":
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }
    
    private void setupListeners() {
        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::refreshDashboard);
        
        // Stats cards click listeners
        setupStatsCardListeners();
        
        // Quick action buttons
        setupQuickActionListeners();
        
        // FAB
        mainFab.setOnClickListener(v -> showQuickActionsMenu());
        
        // Toolbar menu
        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_notifications) {
                // Handle notifications
                return true;
            } else if (itemId == R.id.action_search) {
                // Handle search
                return true;
            } else if (itemId == R.id.action_logout) {
                logoutUser();
                return true;
            }
            return false;
        });
    }
    
    private void setupStatsCardListeners() {
        totalRevenueCard.setOnClickListener(v -> startActivity(new Intent(this, ReportsActivity.class)));
        totalCustomersCard.setOnClickListener(v -> startActivity(new Intent(this, CustomerListActivity.class)));
        totalInvoicesCard.setOnClickListener(v -> startActivity(new Intent(this, InvoiceListActivity.class)));
        totalProductsCard.setOnClickListener(v -> startActivity(new Intent(this, ItemListActivity.class)));
        pendingInvoicesCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, InvoiceListActivity.class);
            intent.putExtra("filter", "pending");
            startActivity(intent);
        });
        lowStockCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.putExtra("filter", "low_stock");
            startActivity(intent);
        });
    }
    
    private void setupQuickActionListeners() {
        newInvoiceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, InvoiceListActivity.class);
            intent.putExtra("action", "new");
            startActivity(intent);
        });
        
        newCustomerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerListActivity.class);
            intent.putExtra("action", "new");
            startActivity(intent);
        });
        
        newProductBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.putExtra("action", "new");
            startActivity(intent);
        });
        
        addPaymentBtn.setOnClickListener(v -> startActivity(new Intent(this, AccountListActivity.class)));
        inventoryBtn.setOnClickListener(v -> startActivity(new Intent(this, ItemListActivity.class)));
        reportsBtn.setOnClickListener(v -> startActivity(new Intent(this, ReportsActivity.class)));
    }
    
    private void showQuickActionsMenu() {
        // Create and show quick actions menu
        // Implementation depends on specific requirements
    }
    
    private void loadDashboardData() {
        swipeRefreshLayout.setRefreshing(true);
        
        executorService.execute(() -> {
            try {
                // Load statistics from database
                loadStatistics();
                
                mainHandler.post(() -> {
                    updateStatsUI();
                    swipeRefreshLayout.setRefreshing(false);
                });
                
            } catch (Exception e) {
                mainHandler.post(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    // Handle error
                });
            }
        });
    }
    
    private void loadStatistics() {
        try {
            // Total Revenue
            totalRevenue = calculateTotalRevenue();
            
            // Total Customers
            List<Customer> customers = database.customerDao().getAllCustomers();
            totalCustomers = customers != null ? customers.size() : 0;
            
            // Total Invoices
            List<Invoice> invoices = database.invoiceDao().getAllInvoices();
            totalInvoices = invoices != null ? invoices.size() : 0;
            
            // Total Products
            List<Product> products = database.productDao().getAllProducts();
            totalProducts = products != null ? products.size() : 0;
            
            // Pending Invoices
            pendingInvoices = countPendingInvoices();
            
            // Low Stock Products
            lowStockProducts = countLowStockProducts();
            
        } catch (Exception e) {
            // Handle database errors
            e.printStackTrace();
        }
    }
    
    private double calculateTotalRevenue() {
        try {
            List<Invoice> paidInvoices = database.invoiceDao().getPaidInvoices();
            double revenue = 0.0;
            if (paidInvoices != null) {
                for (Invoice invoice : paidInvoices) {
                    revenue += invoice.getTotalAmount();
                }
            }
            return revenue;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private int countPendingInvoices() {
        try {
            List<Invoice> pending = database.invoiceDao().getPendingInvoices();
            return pending != null ? pending.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int countLowStockProducts() {
        try {
            List<Product> lowStock = database.productDao().getLowStockProducts();
            return lowStock != null ? lowStock.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private void updateStatsUI() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        
        // Animate stats updates
        animateValue(totalRevenueValue, totalRevenue, true);
        animateValue(totalCustomersValue, totalCustomers, false);
        animateValue(totalInvoicesValue, totalInvoices, false);
        animateValue(totalProductsValue, totalProducts, false);
        animateValue(pendingInvoicesValue, pendingInvoices, false);
        animateValue(lowStockValue, lowStockProducts, false);
    }
    
    private void animateValue(TextView textView, double endValue, boolean isCurrency) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, (float) endValue);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        
        animator.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            
            if (isCurrency) {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
                textView.setText(formatter.format(currentValue));
            } else {
                textView.setText(String.valueOf((int) currentValue));
            }
        });
        
        animator.start();
    }
    
    private void refreshDashboard() {
        loadDashboardData();
        updateDateTime();
    }
    
    private void updateDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE، dd MMMM yyyy - HH:mm", new Locale("ar"));
        String currentDateTime = dateFormat.format(new Date());
        dateTimeTextView.setText(currentDateTime);
    }
    
    private void startRealTimeUpdates() {
        Handler updateHandler = new Handler(Looper.getMainLooper());
        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                updateHandler.postDelayed(this, 60000); // Update every minute
            }
        };
        updateHandler.post(updateRunnable);
    }
    
    private void logoutUser() {
        mAuth.signOut();
        sessionManager.logoutUser();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
        bottomNavigation.setSelectedItemId(R.id.nav_home);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
