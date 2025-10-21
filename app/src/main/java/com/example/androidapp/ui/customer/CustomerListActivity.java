package com.example.androidapp.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.SearchView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.CustomerDao;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.ui.common.VoiceInputEditText;
import com.example.androidapp.utils.SessionManager;
import com.example.androidapp.utils.Material3Helper;
import com.example.androidapp.utils.SmartSuggestionsManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ModernCustomerAdapter adapter;
    private CustomerDao customerDao;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private Handler mainHandler;
    
    // UI Components
    private MaterialToolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private VoiceInputEditText searchInput;
    private ChipGroup filterChips;
    private TextView emptyStateText;
    private MaterialCardView searchCard;
    private FloatingActionButton fabAddCustomer;
    
    // Data
    private List<Customer> allCustomers;
    private List<Customer> filteredCustomers;
    private String currentSearchQuery = "";
    private FilterType currentFilter = FilterType.ALL;
    
    public enum FilterType {
        ALL, ACTIVE, INACTIVE, VIP, NEW_THIS_MONTH
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list_modern);

        initializeComponents();
        setupUI();
        setupListeners();
        loadData();
        
        // تحقق من action للانتقال المباشر
        handleIntent();
    }
    
    private void initializeComponents() {
        customerDao = AppDatabase.getInstance(this).customerDao();
        sessionManager = new SessionManager(this);
        executorService = Executors.newFixedThreadPool(2);
        mainHandler = new Handler(Looper.getMainLooper());
        
        // Initialize UI components
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchCard = findViewById(R.id.searchCard);
        searchInput = findViewById(R.id.searchInput);
        filterChips = findViewById(R.id.filterChips);
        recyclerView = findViewById(R.id.recyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
        fabAddCustomer = findViewById(R.id.fabAddCustomer);
        
        allCustomers = new ArrayList<>();
        filteredCustomers = new ArrayList<>();
    }
    
    private void setupUI() {
        // Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("العملاء");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeColors(
            getResources().getColor(R.color.primary),
            getResources().getColor(R.color.secondary),
            getResources().getColor(R.color.tertiary)
        );
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ModernCustomerAdapter(filteredCustomers, this::onCustomerClick);
        recyclerView.setAdapter(adapter);
        
        // Setup Search Input
        searchInput.setHint("البحث عن عميل...");
        searchInput.setSuggestionType(SmartSuggestionsManager.SuggestionType.CUSTOMER_NAME);
        
        // Apply Material 3 styling
        Material3Helper.Components.setupMaterialCard(searchCard, Material3Helper.CardStyle.OUTLINED);
        Material3Helper.Components.setupFAB(fabAddCustomer, Material3Helper.FABStyle.NORMAL);
        
        // Setup Filter Chips
        setupFilterChips();
    }
    
    private void setupFilterChips() {
        filterChips.removeAllViews();
        
        String[] filterLabels = {"الكل", "نشط", "غير نشط", "VIP", "جديد هذا الشهر"};
        FilterType[] filterTypes = {FilterType.ALL, FilterType.ACTIVE, FilterType.INACTIVE, FilterType.VIP, FilterType.NEW_THIS_MONTH};
        
        for (int i = 0; i < filterLabels.length; i++) {
            Chip chip = Material3Helper.Components.createMaterial3Chip(
                this, filterLabels[i], Material3Helper.ChipType.FILTER
            );
            
            final FilterType filterType = filterTypes[i];
            chip.setOnClickListener(v -> {
                currentFilter = filterType;
                applyFilter();
                updateChipSelection(chip);
            });
            
            if (i == 0) {
                chip.setChecked(true);
            }
            
            filterChips.addView(chip);
        }
    }
    
    private void updateChipSelection(Chip selectedChip) {
        for (int i = 0; i < filterChips.getChildCount(); i++) {
            Chip chip = (Chip) filterChips.getChildAt(i);
            chip.setChecked(chip == selectedChip);
        }
    }
    
    private void setupListeners() {
        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        
        // Search functionality
        searchInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                applyFilter();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // FAB click
        fabAddCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerDetailActivity.class);
            intent.putExtra("action", "new");
            startActivity(intent);
        });
        
        // Toolbar back button
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    
    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getStringExtra("action");
            if ("new".equals(action)) {
                // فتح شاشة إضافة عميل جديد مباشرة
                fabAddCustomer.performClick();
            }
            
            String filter = intent.getStringExtra("filter");
            if (filter != null) {
                // تطبيق فلتر معين
                applySpecificFilter(filter);
            }
        }
    }
    
    private void applySpecificFilter(String filter) {
        switch (filter) {
            case "active":
                currentFilter = FilterType.ACTIVE;
                break;
            case "inactive":
                currentFilter = FilterType.INACTIVE;
                break;
            case "vip":
                currentFilter = FilterType.VIP;
                break;
            case "new":
                currentFilter = FilterType.NEW_THIS_MONTH;
                break;
            default:
                currentFilter = FilterType.ALL;
                break;
        }
        applyFilter();
        updateChipSelectionByFilter();
    }
    
    private void updateChipSelectionByFilter() {
        int index = 0;
        switch (currentFilter) {
            case ACTIVE: index = 1; break;
            case INACTIVE: index = 2; break;
            case VIP: index = 3; break;
            case NEW_THIS_MONTH: index = 4; break;
            default: index = 0; break;
        }
        
        if (index < filterChips.getChildCount()) {
            Chip chip = (Chip) filterChips.getChildAt(index);
            updateChipSelection(chip);
        }
    }
    
    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        
        executorService.execute(() -> {
            try {
                List<Customer> customers = customerDao.getAllCustomers();
                
                mainHandler.post(() -> {
                    allCustomers.clear();
                    if (customers != null) {
                        allCustomers.addAll(customers);
                    }
                    applyFilter();
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
    
    private void refreshData() {
        loadData();
    }
    
    private void applyFilter() {
        filteredCustomers.clear();
        
        for (Customer customer : allCustomers) {
            if (matchesSearchQuery(customer) && matchesFilter(customer)) {
                filteredCustomers.add(customer);
            }
        }
        
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }
    
    private boolean matchesSearchQuery(Customer customer) {
        if (currentSearchQuery.isEmpty()) {
            return true;
        }
        
        String query = currentSearchQuery.toLowerCase();
        return (customer.getName() != null && customer.getName().toLowerCase().contains(query)) ||
               (customer.getPhone() != null && customer.getPhone().contains(query)) ||
               (customer.getEmail() != null && customer.getEmail().toLowerCase().contains(query)) ||
               (customer.getAddress() != null && customer.getAddress().toLowerCase().contains(query));
    }
    
    private boolean matchesFilter(Customer customer) {
        switch (currentFilter) {
            case ALL:
                return true;
            case ACTIVE:
                return customer.isActive();
            case INACTIVE:
                return !customer.isActive();
            case VIP:
                return customer.isVip();
            case NEW_THIS_MONTH:
                // تحديد العملاء الجدد هذا الشهر (يحتاج لتطبيق منطق التاريخ)
                return isNewThisMonth(customer);
            default:
                return true;
        }
    }
    
    private boolean isNewThisMonth(Customer customer) {
        // منطق للتحقق من العملاء الجدد هذا الشهر
        // يمكن تطويره لاحقاً باستخدام تاريخ الإنشاء
        return false;
    }
    
    private void updateEmptyState() {
        if (filteredCustomers.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
            
            if (currentSearchQuery.isEmpty()) {
                emptyStateText.setText("لا توجد عملاء حالياً\nاضغط على + لإضافة عميل جديد");
            } else {
                emptyStateText.setText("لم يتم العثور على نتائج مطابقة\nجرب تعديل كلمات البحث");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
        }
    }
    
    private void onCustomerClick(Customer customer) {
        Intent intent = new Intent(this, CustomerDetailActivity.class);
        intent.putExtra("customer_id", customer.getId());
        intent.putExtra("action", "view");
        startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_list_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_export) {
            exportCustomers();
            return true;
        } else if (itemId == R.id.action_import) {
            importCustomers();
            return true;
        } else if (itemId == R.id.action_bulk_actions) {
            showBulkActionsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void exportCustomers() {
        // تصدير العملاء
        // يمكن تطويره لاحقاً
    }
    
    private void importCustomers() {
        // استيراد العملاء
        // يمكن تطويره لاحقاً
    }
    
    private void showBulkActionsDialog() {
        // إظهار خيارات العمل المجمع
        // يمكن تطويره لاحقاً
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
            intent.putExtra("customerId", customer.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.customer_list_row;
            }

            @Override
            protected void bindView(View itemView, Customer customer) {
                TextView customerName = itemView.findViewById(R.id.customer_name);
                TextView customerEmail = itemView.findViewById(R.id.customer_email);
                TextView customerPhone = itemView.findViewById(R.id.customer_phone);

                customerName.setText(customer.getName());
                customerEmail.setText(customer.getEmail());
                customerPhone.setText(customer.getPhone());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                List<Customer> customers = customerDao.getCustomersByCompanyId(companyId);
                runOnUiThread(() -> adapter.updateData(customers));
            });
        }
    }
}
