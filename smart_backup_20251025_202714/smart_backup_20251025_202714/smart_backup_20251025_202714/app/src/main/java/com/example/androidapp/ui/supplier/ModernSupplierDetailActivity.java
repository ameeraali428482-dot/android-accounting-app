package com.example.androidapp.ui.supplier;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.models.Supplier;
import com.example.androidapp.models.Product;
import com.example.androidapp.models.PurchaseOrder;
import com.example.androidapp.ui.common.VoiceInputEditText;
import com.example.androidapp.ui.product.ProductAdapter;
import com.example.androidapp.ui.order.adapters.PurchaseOrderAdapter;
import com.example.androidapp.utils.Material3Helper;
import com.example.androidapp.utils.VoiceInputManager;
import com.example.androidapp.utils.SmartSuggestionsManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * نشاط تفاصيل المورد المتطور - Material 3 Design
 * مع جميع الميزات الاحترافية لإدارة الموردين
 */
public class ModernSupplierDetailActivity extends AppCompatActivity {

    // UI Components - Header
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private TextView supplierNameText;
    private TextView supplierCodeText;
    private TextView memberSinceText;
    private MaterialCardView summaryCard;

    // Supplier Info Section
    private TextInputEditText supplierNameEditText;
    private TextInputEditText contactPersonEditText;
    private TextInputEditText phoneEditText;
    private TextInputEditText emailEditText;
    private VoiceInputEditText addressEditText;
    private TextInputEditText websiteEditText;
    private TextInputEditText taxNumberEditText;
    private TextInputEditText creditLimitEditText;
    private TextInputEditText paymentTermsEditText;

    // Status and Category Chips
    private ChipGroup statusChipGroup;
    private Chip activeChip;
    private Chip inactiveChip;
    private Chip suspendedChip;

    private ChipGroup categoryChipGroup;
    private Chip localChip;
    private Chip internationalChip;
    private Chip preferredChip;
    private Chip regularChip;

    // Action Buttons
    private MaterialButton saveButton;
    private MaterialButton callButton;
    private MaterialButton emailButton;
    private MaterialButton websiteButton;
    private MaterialButton shareButton;
    private MaterialButton deleteButton;

    // Statistics Cards
    private MaterialCardView totalOrdersCard;
    private MaterialCardView totalAmountCard;
    private MaterialCardView averageOrderCard;
    private MaterialCardView lastOrderCard;
    
    private TextView totalOrdersText;
    private TextView totalOrdersAmountText;
    private TextView totalAmountText;
    private TextView totalAmountValueText;
    private TextView averageOrderText;
    private TextView averageOrderValueText;
    private TextView lastOrderText;
    private TextView lastOrderDateText;

    // Performance Metrics
    private MaterialCardView performanceCard;
    private TextView deliveryRatingText;
    private TextView qualityRatingText;
    private TextView pricingRatingText;
    private TextView overallRatingText;

    // Tabs and Content
    private TabLayout tabLayout;
    private RecyclerView productsRecyclerView;
    private RecyclerView ordersRecyclerView;
    private View paymentsView;
    private View documentsView;
    
    // FAB
    private FloatingActionButton addOrderFab;

    // Data and Adapters
    private Supplier supplier;
    private List<Product> supplierProducts;
    private List<PurchaseOrder> supplierOrders;
    private ProductAdapter productAdapter;
    private PurchaseOrderAdapter orderAdapter;

    // Managers
    private VoiceInputManager voiceInputManager;
    private SmartSuggestionsManager suggestionsManager;
    private Material3Helper material3Helper;

    // Formatters
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_detail_modern);

        // Initialize managers
        voiceInputManager = VoiceInputManager.getInstance(this);
        suggestionsManager = SmartSuggestionsManager.getInstance(this);
        material3Helper = Material3Helper.getInstance();

        // Initialize formatters
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get supplier from intent
        getSupplierFromIntent();

        // Initialize views
        initializeViews();

        // Setup toolbar
        setupToolbar();

        // Apply Material 3 styling
        material3Helper.applyMaterial3Styling(this);

        // Setup listeners
        setupListeners();

        // Setup tabs
        setupTabs();

        // Setup RecyclerViews
        setupRecyclerViews();

        // Setup voice input
        setupVoiceInput();

        // Load supplier data
        loadSupplierData();

        // Load related data
        loadSupplierProducts();
        loadSupplierOrders();

        // Update statistics
        updateStatistics();
    }

    private void getSupplierFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("supplier")) {
            supplier = (Supplier) intent.getSerializableExtra("supplier");
        } else {
            // Create empty supplier for new supplier
            supplier = new Supplier();
        }
    }

    private void initializeViews() {
        // Header
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        supplierNameText = findViewById(R.id.supplierNameText);
        supplierCodeText = findViewById(R.id.supplierCodeText);
        memberSinceText = findViewById(R.id.memberSinceText);
        summaryCard = findViewById(R.id.summaryCard);

        // Supplier Info
        supplierNameEditText = findViewById(R.id.supplierNameEditText);
        contactPersonEditText = findViewById(R.id.contactPersonEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        addressEditText = findViewById(R.id.addressEditText);
        websiteEditText = findViewById(R.id.websiteEditText);
        taxNumberEditText = findViewById(R.id.taxNumberEditText);
        creditLimitEditText = findViewById(R.id.creditLimitEditText);
        paymentTermsEditText = findViewById(R.id.paymentTermsEditText);

        // Status and Category Chips
        statusChipGroup = findViewById(R.id.statusChipGroup);
        activeChip = findViewById(R.id.activeChip);
        inactiveChip = findViewById(R.id.inactiveChip);
        suspendedChip = findViewById(R.id.suspendedChip);

        categoryChipGroup = findViewById(R.id.categoryChipGroup);
        localChip = findViewById(R.id.localChip);
        internationalChip = findViewById(R.id.internationalChip);
        preferredChip = findViewById(R.id.preferredChip);
        regularChip = findViewById(R.id.regularChip);

        // Action Buttons
        saveButton = findViewById(R.id.saveButton);
        callButton = findViewById(R.id.callButton);
        emailButton = findViewById(R.id.emailButton);
        websiteButton = findViewById(R.id.websiteButton);
        shareButton = findViewById(R.id.shareButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Statistics Cards
        totalOrdersCard = findViewById(R.id.totalOrdersCard);
        totalAmountCard = findViewById(R.id.totalAmountCard);
        averageOrderCard = findViewById(R.id.averageOrderCard);
        lastOrderCard = findViewById(R.id.lastOrderCard);
        
        totalOrdersText = findViewById(R.id.totalOrdersText);
        totalOrdersAmountText = findViewById(R.id.totalOrdersAmountText);
        totalAmountText = findViewById(R.id.totalAmountText);
        totalAmountValueText = findViewById(R.id.totalAmountValueText);
        averageOrderText = findViewById(R.id.averageOrderText);
        averageOrderValueText = findViewById(R.id.averageOrderValueText);
        lastOrderText = findViewById(R.id.lastOrderText);
        lastOrderDateText = findViewById(R.id.lastOrderDateText);

        // Performance Metrics
        performanceCard = findViewById(R.id.performanceCard);
        deliveryRatingText = findViewById(R.id.deliveryRatingText);
        qualityRatingText = findViewById(R.id.qualityRatingText);
        pricingRatingText = findViewById(R.id.pricingRatingText);
        overallRatingText = findViewById(R.id.overallRatingText);

        // Tabs and Content
        tabLayout = findViewById(R.id.tabLayout);
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        paymentsView = findViewById(R.id.paymentsView);
        documentsView = findViewById(R.id.documentsView);

        // FAB
        addOrderFab = findViewById(R.id.addOrderFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        collapsingToolbar.setTitle("تفاصيل المورد");
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.white, getTheme()));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white, getTheme()));
    }

    private void setupListeners() {
        // Action Buttons
        saveButton.setOnClickListener(v -> saveSupplier());
        callButton.setOnClickListener(v -> makePhoneCall());
        emailButton.setOnClickListener(v -> sendEmail());
        websiteButton.setOnClickListener(v -> openWebsite());
        shareButton.setOnClickListener(v -> shareSupplier());
        deleteButton.setOnClickListener(v -> deleteSupplier());

        // FAB
        addOrderFab.setOnClickListener(v -> addNewOrder());

        // Statistics Cards Click
        totalOrdersCard.setOnClickListener(v -> showOrdersTab());
        totalAmountCard.setOnClickListener(v -> showPaymentsTab());
        averageOrderCard.setOnClickListener(v -> showOrderAnalysis());
        lastOrderCard.setOnClickListener(v -> showLastOrderDetails());
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("المنتجات"));
        tabLayout.addTab(tabLayout.newTab().setText("الطلبات"));
        tabLayout.addTab(tabLayout.newTab().setText("المدفوعات"));
        tabLayout.addTab(tabLayout.newTab().setText("الوثائق"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchTabContent(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerViews() {
        // Products RecyclerView
        supplierProducts = new ArrayList<>();
        productAdapter = new ProductAdapter(this, supplierProducts);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);

        // Orders RecyclerView
        supplierOrders = new ArrayList<>();
        orderAdapter = new PurchaseOrderAdapter(this, supplierOrders);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setAdapter(orderAdapter);
    }

    private void setupVoiceInput() {
        // Setup voice input for address field
        voiceInputManager.setupVoiceInput(addressEditText, this);
        
        // Setup smart suggestions
        List<String> addressSuggestions = new ArrayList<>();
        addressSuggestions.add("الرياض، المملكة العربية السعودية");
        addressSuggestions.add("جدة، المملكة العربية السعودية");
        addressSuggestions.add("الدمام، المملكة العربية السعودية");
        suggestionsManager.setupSuggestions(addressEditText, addressSuggestions);
    }

    private void loadSupplierData() {
        if (supplier != null) {
            supplierNameText.setText(supplier.getName());
            supplierCodeText.setText("كود المورد: " + supplier.getCode());
            memberSinceText.setText("مورد منذ: " + dateFormatter.format(supplier.getCreatedDate()));

            // Fill form fields
            supplierNameEditText.setText(supplier.getName());
            contactPersonEditText.setText(supplier.getContactPerson());
            phoneEditText.setText(supplier.getPhone());
            emailEditText.setText(supplier.getEmail());
            addressEditText.setText(supplier.getAddress());
            websiteEditText.setText(supplier.getWebsite());
            taxNumberEditText.setText(supplier.getTaxNumber());
            creditLimitEditText.setText(String.valueOf(supplier.getCreditLimit()));
            paymentTermsEditText.setText(supplier.getPaymentTerms());

            // Set status chips
            updateStatusChips();
        }
    }

    private void updateStatusChips() {
        // Status chips
        switch (supplier.getStatus()) {
            case ACTIVE:
                activeChip.setChecked(true);
                break;
            case INACTIVE:
                inactiveChip.setChecked(true);
                break;
            case SUSPENDED:
                suspendedChip.setChecked(true);
                break;
        }

        // Category chips
        if (supplier.isLocal()) {
            localChip.setChecked(true);
        } else {
            internationalChip.setChecked(true);
        }

        if (supplier.isPreferred()) {
            preferredChip.setChecked(true);
        } else {
            regularChip.setChecked(true);
        }
    }

    private void loadSupplierProducts() {
        // TODO: Load products from database
        supplierProducts.clear();
        productAdapter.notifyDataSetChanged();
    }

    private void loadSupplierOrders() {
        // TODO: Load orders from database
        supplierOrders.clear();
        orderAdapter.notifyDataSetChanged();
    }

    private void updateStatistics() {
        // Calculate statistics
        int totalOrdersCount = supplierOrders.size();
        double totalAmount = 0;
        for (PurchaseOrder order : supplierOrders) {
            totalAmount += order.getTotalAmount();
        }
        double averageOrder = totalOrdersCount > 0 ? totalAmount / totalOrdersCount : 0;

        // Update UI
        totalOrdersText.setText(String.valueOf(totalOrdersCount));
        totalOrdersAmountText.setText("طلب");
        totalAmountText.setText(currencyFormatter.format(totalAmount));
        totalAmountValueText.setText("إجمالي المشتريات");
        averageOrderText.setText(currencyFormatter.format(averageOrder));
        averageOrderValueText.setText("متوسط الطلب");

        if (!supplierOrders.isEmpty()) {
            PurchaseOrder lastOrder = supplierOrders.get(0); // Assuming sorted by date
            lastOrderText.setText(currencyFormatter.format(lastOrder.getTotalAmount()));
            lastOrderDateText.setText(dateFormatter.format(lastOrder.getOrderDate()));
        } else {
            lastOrderText.setText("لا يوجد");
            lastOrderDateText.setText("--");
        }

        // Update performance ratings (dummy data)
        deliveryRatingText.setText("4.5/5");
        qualityRatingText.setText("4.2/5");
        pricingRatingText.setText("4.0/5");
        overallRatingText.setText("4.2/5");
    }

    private void switchTabContent(int position) {
        // Hide all content views
        productsRecyclerView.setVisibility(View.GONE);
        ordersRecyclerView.setVisibility(View.GONE);
        paymentsView.setVisibility(View.GONE);
        documentsView.setVisibility(View.GONE);

        // Show selected content
        switch (position) {
            case 0: // Products
                productsRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 1: // Orders
                ordersRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 2: // Payments
                paymentsView.setVisibility(View.VISIBLE);
                break;
            case 3: // Documents
                documentsView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showOrdersTab() {
        tabLayout.selectTab(tabLayout.getTabAt(1));
    }

    private void showPaymentsTab() {
        tabLayout.selectTab(tabLayout.getTabAt(2));
    }

    private void showOrderAnalysis() {
        showSnackbar("عرض تحليل الطلبات", null, null);
    }

    private void showLastOrderDetails() {
        showSnackbar("عرض تفاصيل آخر طلب", null, null);
    }

    private void saveSupplier() {
        // Validate input
        if (supplierNameEditText.getText().toString().trim().isEmpty()) {
            supplierNameEditText.setError("يرجى إدخال اسم المورد");
            return;
        }

        // Update supplier object
        supplier.setName(supplierNameEditText.getText().toString().trim());
        supplier.setContactPerson(contactPersonEditText.getText().toString().trim());
        supplier.setPhone(phoneEditText.getText().toString().trim());
        supplier.setEmail(emailEditText.getText().toString().trim());
        supplier.setAddress(addressEditText.getText().toString().trim());
        supplier.setWebsite(websiteEditText.getText().toString().trim());
        supplier.setTaxNumber(taxNumberEditText.getText().toString().trim());
        supplier.setPaymentTerms(paymentTermsEditText.getText().toString().trim());
        
        String creditLimitStr = creditLimitEditText.getText().toString().trim();
        if (!creditLimitStr.isEmpty()) {
            supplier.setCreditLimit(Double.parseDouble(creditLimitStr));
        }

        // Update status based on chips
        if (activeChip.isChecked()) {
            supplier.setStatus(Supplier.SupplierStatus.ACTIVE);
        } else if (inactiveChip.isChecked()) {
            supplier.setStatus(Supplier.SupplierStatus.INACTIVE);
        } else if (suspendedChip.isChecked()) {
            supplier.setStatus(Supplier.SupplierStatus.SUSPENDED);
        }

        supplier.setLocal(localChip.isChecked());
        supplier.setPreferred(preferredChip.isChecked());

        // TODO: Save to database
        
        showSnackbar("تم حفظ بيانات المورد بنجاح", "موافق", null);
        
        // Update header
        loadSupplierData();
    }

    private void makePhoneCall() {
        String phone = phoneEditText.getText().toString().trim();
        if (!phone.isEmpty()) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phone));
            startActivity(callIntent);
        } else {
            showSnackbar("لا يوجد رقم هاتف للمورد", null, null);
        }
    }

    private void sendEmail() {
        String email = emailEditText.getText().toString().trim();
        if (!email.isEmpty()) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + email));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "مراسلة من تطبيق المحاسبة");
            startActivity(Intent.createChooser(emailIntent, "إرسال إيميل"));
        } else {
            showSnackbar("لا يوجد إيميل للمورد", null, null);
        }
    }

    private void openWebsite() {
        String website = websiteEditText.getText().toString().trim();
        if (!website.isEmpty()) {
            if (!website.startsWith("http://") && !website.startsWith("https://")) {
                website = "https://" + website;
            }
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
            startActivity(websiteIntent);
        } else {
            showSnackbar("لا يوجد موقع إلكتروني للمورد", null, null);
        }
    }

    private void shareSupplier() {
        String shareText = "معلومات المورد:\n" +
                "الاسم: " + supplier.getName() + "\n" +
                "جهة الاتصال: " + supplier.getContactPerson() + "\n" +
                "الهاتف: " + supplier.getPhone() + "\n" +
                "الإيميل: " + supplier.getEmail() + "\n" +
                "العنوان: " + supplier.getAddress();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "مشاركة معلومات المورد"));
    }

    private void deleteSupplier() {
        // TODO: Show confirmation dialog and delete supplier
        showSnackbar("سيتم حذف المورد", "تراجع", v -> {});
    }

    private void addNewOrder() {
        // TODO: Navigate to new purchase order activity with supplier pre-selected
        showSnackbar("سيتم فتح طلب شراء جديد للمورد", null, null);
    }

    private void showSnackbar(String message, String actionText, View.OnClickListener actionListener) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        if (actionText != null && actionListener != null) {
            snackbar.setAction(actionText, actionListener);
        }
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_supplier_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
                
            case R.id.action_edit:
                // Toggle edit mode
                showSnackbar("سيتم تفعيل وضع التعديل", null, null);
                return true;
                
            case R.id.action_duplicate:
                // Duplicate supplier
                showSnackbar("سيتم نسخ المورد", null, null);
                return true;
                
            case R.id.action_export:
                // Export supplier data
                showSnackbar("سيتم تصدير بيانات المورد", null, null);
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}