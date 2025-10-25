package com.example.androidapp.ui.customer;

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
import com.example.androidapp.models.Customer;
import com.example.androidapp.models.Invoice;
import com.example.androidapp.models.Order;
import com.example.androidapp.ui.common.VoiceInputEditText;
import com.example.androidapp.ui.invoice.InvoiceAdapter;
import com.example.androidapp.ui.order.adapters.OrderAdapter;
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
import java.util.List;
import java.util.Locale;

/**
 * نشاط تفاصيل العميل المتطور - Material 3 Design
 * مع جميع الميزات الاحترافية والحديثة
 */
public class ModernCustomerDetailActivity extends AppCompatActivity {

    // UI Components - Header
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private TextView customerNameText;
    private TextView customerCodeText;
    private TextView memberSinceText;
    private MaterialCardView summaryCard;

    // Customer Info Section
    private TextInputEditText customerNameEditText;
    private TextInputEditText phoneEditText;
    private TextInputEditText emailEditText;
    private VoiceInputEditText addressEditText;
    private TextInputEditText companyEditText;
    private TextInputEditText taxNumberEditText;
    private TextInputEditText creditLimitEditText;
    private TextInputEditText discountRateEditText;

    // Status and Tags
    private ChipGroup statusChipGroup;
    private Chip activeChip;
    private Chip inactiveChip;
    private Chip vipChip;
    private Chip regularChip;

    // Action Buttons
    private MaterialButton saveButton;
    private MaterialButton callButton;
    private MaterialButton emailButton;
    private MaterialButton shareButton;
    private MaterialButton deleteButton;

    // Statistics Cards
    private MaterialCardView totalInvoicesCard;
    private MaterialCardView totalOrdersCard;
    private MaterialCardView totalDebtCard;
    private MaterialCardView lastActivityCard;
    
    private TextView totalInvoicesText;
    private TextView totalInvoicesAmountText;
    private TextView totalOrdersText;
    private TextView totalOrdersAmountText;
    private TextView totalDebtText;
    private TextView lastActivityText;

    // Tabs and Content
    private TabLayout tabLayout;
    private RecyclerView invoicesRecyclerView;
    private RecyclerView ordersRecyclerView;
    private View transactionsView;
    
    // FAB
    private FloatingActionButton addInvoiceFab;

    // Data and Adapters
    private Customer customer;
    private List<Invoice> customerInvoices;
    private List<Order> customerOrders;
    private InvoiceAdapter invoiceAdapter;
    private OrderAdapter orderAdapter;

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
        setContentView(R.layout.activity_customer_detail_modern);

        // Initialize managers
        voiceInputManager = VoiceInputManager.getInstance(this);
        suggestionsManager = SmartSuggestionsManager.getInstance(this);
        material3Helper = Material3Helper.getInstance();

        // Initialize formatters
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get customer from intent
        getCustomerFromIntent();

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

        // Load customer data
        loadCustomerData();

        // Load related data
        loadCustomerInvoices();
        loadCustomerOrders();

        // Update statistics
        updateStatistics();
    }

    private void getCustomerFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("customer")) {
            customer = (Customer) intent.getSerializableExtra("customer");
        } else {
            // Create empty customer for new customer
            customer = new Customer();
        }
    }

    private void initializeViews() {
        // Header
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        customerNameText = findViewById(R.id.customerNameText);
        customerCodeText = findViewById(R.id.customerCodeText);
        memberSinceText = findViewById(R.id.memberSinceText);
        summaryCard = findViewById(R.id.summaryCard);

        // Customer Info
        customerNameEditText = findViewById(R.id.customerNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        addressEditText = findViewById(R.id.addressEditText);
        companyEditText = findViewById(R.id.companyEditText);
        taxNumberEditText = findViewById(R.id.taxNumberEditText);
        creditLimitEditText = findViewById(R.id.creditLimitEditText);
        discountRateEditText = findViewById(R.id.discountRateEditText);

        // Status and Tags
        statusChipGroup = findViewById(R.id.statusChipGroup);
        activeChip = findViewById(R.id.activeChip);
        inactiveChip = findViewById(R.id.inactiveChip);
        vipChip = findViewById(R.id.vipChip);
        regularChip = findViewById(R.id.regularChip);

        // Action Buttons
        saveButton = findViewById(R.id.saveButton);
        callButton = findViewById(R.id.callButton);
        emailButton = findViewById(R.id.emailButton);
        shareButton = findViewById(R.id.shareButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Statistics Cards
        totalInvoicesCard = findViewById(R.id.totalInvoicesCard);
        totalOrdersCard = findViewById(R.id.totalOrdersCard);
        totalDebtCard = findViewById(R.id.totalDebtCard);
        lastActivityCard = findViewById(R.id.lastActivityCard);
        
        totalInvoicesText = findViewById(R.id.totalInvoicesText);
        totalInvoicesAmountText = findViewById(R.id.totalInvoicesAmountText);
        totalOrdersText = findViewById(R.id.totalOrdersText);
        totalOrdersAmountText = findViewById(R.id.totalOrdersAmountText);
        totalDebtText = findViewById(R.id.totalDebtText);
        lastActivityText = findViewById(R.id.lastActivityText);

        // Tabs and Content
        tabLayout = findViewById(R.id.tabLayout);
        invoicesRecyclerView = findViewById(R.id.invoicesRecyclerView);
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        transactionsView = findViewById(R.id.transactionsView);

        // FAB
        addInvoiceFab = findViewById(R.id.addInvoiceFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        collapsingToolbar.setTitle("تفاصيل العميل");
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.white, getTheme()));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white, getTheme()));
    }

    private void setupListeners() {
        // Action Buttons
        saveButton.setOnClickListener(v -> saveCustomer());
        callButton.setOnClickListener(v -> makePhoneCall());
        emailButton.setOnClickListener(v -> sendEmail());
        shareButton.setOnClickListener(v -> shareCustomer());
        deleteButton.setOnClickListener(v -> deleteCustomer());

        // FAB
        addInvoiceFab.setOnClickListener(v -> addNewInvoice());

        // Statistics Cards Click
        totalInvoicesCard.setOnClickListener(v -> showInvoicesTab());
        totalOrdersCard.setOnClickListener(v -> showOrdersTab());
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("الفواتير"));
        tabLayout.addTab(tabLayout.newTab().setText("الطلبات"));
        tabLayout.addTab(tabLayout.newTab().setText("المعاملات"));

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
        // Invoices RecyclerView
        customerInvoices = new ArrayList<>();
        invoiceAdapter = new InvoiceAdapter(this, customerInvoices);
        invoicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        invoicesRecyclerView.setAdapter(invoiceAdapter);

        // Orders RecyclerView
        customerOrders = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, customerOrders);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setAdapter(orderAdapter);
    }

    private void setupVoiceInput() {
        // Setup voice input for address field
        voiceInputManager.setupVoiceInput(addressEditText, this);
        
        // Setup smart suggestions
        List<String> addressSuggestions = new ArrayList<>();
        addressSuggestions.add("الرياض، حي الملك فهد");
        addressSuggestions.add("جدة، حي الزهراء");
        addressSuggestions.add("الدمام، حي الشاطئ");
        suggestionsManager.setupSuggestions(addressEditText, addressSuggestions);
    }

    private void loadCustomerData() {
        if (customer != null) {
            customerNameText.setText(customer.getName());
            customerCodeText.setText("كود العميل: " + customer.getCode());
            memberSinceText.setText("عضو منذ: " + dateFormatter.format(customer.getCreatedDate()));

            // Fill form fields
            customerNameEditText.setText(customer.getName());
            phoneEditText.setText(customer.getPhone());
            emailEditText.setText(customer.getEmail());
            addressEditText.setText(customer.getAddress());
            companyEditText.setText(customer.getCompany());
            taxNumberEditText.setText(customer.getTaxNumber());
            creditLimitEditText.setText(String.valueOf(customer.getCreditLimit()));
            discountRateEditText.setText(String.valueOf(customer.getDiscountRate()));

            // Set status chips
            updateStatusChips();
        }
    }

    private void updateStatusChips() {
        if (customer.isActive()) {
            activeChip.setChecked(true);
        } else {
            inactiveChip.setChecked(true);
        }

        if (customer.isVip()) {
            vipChip.setChecked(true);
        } else {
            regularChip.setChecked(true);
        }
    }

    private void loadCustomerInvoices() {
        // TODO: Load invoices from database
        // For now, use dummy data
        customerInvoices.clear();
        // Add dummy invoices
        invoiceAdapter.notifyDataSetChanged();
    }

    private void loadCustomerOrders() {
        // TODO: Load orders from database
        // For now, use dummy data
        customerOrders.clear();
        // Add dummy orders
        orderAdapter.notifyDataSetChanged();
    }

    private void updateStatistics() {
        // Calculate statistics
        int totalInvoicesCount = customerInvoices.size();
        double totalInvoicesAmount = 0;
        for (Invoice invoice : customerInvoices) {
            totalInvoicesAmount += invoice.getTotalAmount();
        }

        int totalOrdersCount = customerOrders.size();
        double totalOrdersAmount = 0;
        for (Order order : customerOrders) {
            totalOrdersAmount += order.getTotalAmount();
        }

        double totalDebt = customer.getTotalDebt();

        // Update UI
        totalInvoicesText.setText(String.valueOf(totalInvoicesCount));
        totalInvoicesAmountText.setText(currencyFormatter.format(totalInvoicesAmount));
        totalOrdersText.setText(String.valueOf(totalOrdersCount));
        totalOrdersAmountText.setText(currencyFormatter.format(totalOrdersAmount));
        totalDebtText.setText(currencyFormatter.format(totalDebt));
        lastActivityText.setText(dateFormatter.format(customer.getLastActivity()));
    }

    private void switchTabContent(int position) {
        // Hide all content views
        invoicesRecyclerView.setVisibility(View.GONE);
        ordersRecyclerView.setVisibility(View.GONE);
        transactionsView.setVisibility(View.GONE);

        // Show selected content
        switch (position) {
            case 0: // Invoices
                invoicesRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 1: // Orders
                ordersRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 2: // Transactions
                transactionsView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showInvoicesTab() {
        tabLayout.selectTab(tabLayout.getTabAt(0));
    }

    private void showOrdersTab() {
        tabLayout.selectTab(tabLayout.getTabAt(1));
    }

    private void saveCustomer() {
        // Validate input
        if (customerNameEditText.getText().toString().trim().isEmpty()) {
            customerNameEditText.setError("يرجى إدخال اسم العميل");
            return;
        }

        // Update customer object
        customer.setName(customerNameEditText.getText().toString().trim());
        customer.setPhone(phoneEditText.getText().toString().trim());
        customer.setEmail(emailEditText.getText().toString().trim());
        customer.setAddress(addressEditText.getText().toString().trim());
        customer.setCompany(companyEditText.getText().toString().trim());
        customer.setTaxNumber(taxNumberEditText.getText().toString().trim());
        
        String creditLimitStr = creditLimitEditText.getText().toString().trim();
        if (!creditLimitStr.isEmpty()) {
            customer.setCreditLimit(Double.parseDouble(creditLimitStr));
        }
        
        String discountRateStr = discountRateEditText.getText().toString().trim();
        if (!discountRateStr.isEmpty()) {
            customer.setDiscountRate(Double.parseDouble(discountRateStr));
        }

        customer.setActive(activeChip.isChecked());
        customer.setVip(vipChip.isChecked());

        // TODO: Save to database
        
        showSnackbar("تم حفظ بيانات العميل بنجاح", "موافق", null);
        
        // Update header
        loadCustomerData();
    }

    private void makePhoneCall() {
        String phone = phoneEditText.getText().toString().trim();
        if (!phone.isEmpty()) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phone));
            startActivity(callIntent);
        } else {
            showSnackbar("لا يوجد رقم هاتف للعميل", null, null);
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
            showSnackbar("لا يوجد إيميل للعميل", null, null);
        }
    }

    private void shareCustomer() {
        String shareText = "معلومات العميل:\n" +
                "الاسم: " + customer.getName() + "\n" +
                "الهاتف: " + customer.getPhone() + "\n" +
                "الإيميل: " + customer.getEmail() + "\n" +
                "العنوان: " + customer.getAddress();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "مشاركة معلومات العميل"));
    }

    private void deleteCustomer() {
        // TODO: Show confirmation dialog and delete customer
        showSnackbar("سيتم حذف العميل", "تراجع", v -> {});
    }

    private void addNewInvoice() {
        // TODO: Navigate to new invoice activity with customer pre-selected
        showSnackbar("سيتم فتح فاتورة جديدة للعميل", null, null);
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
        getMenuInflater().inflate(R.menu.menu_customer_detail, menu);
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
                // Duplicate customer
                showSnackbar("سيتم نسخ العميل", null, null);
                return true;
                
            case R.id.action_export:
                // Export customer data
                showSnackbar("سيتم تصدير بيانات العميل", null, null);
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}