package com.example.androidapp.ui.customer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.CustomerDao;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.ui.common.VoiceInputEditText;
import com.example.androidapp.utils.Material3Helper;
import com.example.androidapp.utils.VoiceInputManager;
import com.example.androidapp.utils.SmartSuggestionsManager;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CustomerDetailActivity extends AppCompatActivity {

    // UI Components
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView customerHeaderImage;
    private ImageView customerAvatar;
    private TextView customerNameText;
    private TextView customerStatusText;
    private TextView customerSinceText;
    private TextView totalPurchasesText;
    private TextView totalOrdersText;
    private TextView accountBalanceText;
    
    // Form Fields
    private VoiceInputEditText customerNameEditText;
    private VoiceInputEditText customerPhoneEditText;
    private VoiceInputEditText customerEmailEditText;
    private VoiceInputEditText customerAddressEditText;
    private VoiceInputEditText customerNotesEditText;
    private AutoCompleteTextView customerCategoryDropdown;
    
    // Buttons
    private MaterialButton callCustomerButton;
    private MaterialButton messageCustomerButton;
    private MaterialButton addPaymentButton;
    private MaterialButton viewStatementButton;
    private MaterialButton viewAllTransactionsButton;
    private FloatingActionButton editCustomerFab;
    
    // RecyclerView
    private RecyclerView recentTransactionsRecyclerView;
    
    // Data
    private Customer currentCustomer;
    private boolean isEditMode = false;
    private String currentCustomerId = null;
    
    // Database
    private CustomerDao customerDao;
    private SessionManager sessionManager;
    
    // Managers
    private VoiceInputManager voiceInputManager;
    private SmartSuggestionsManager suggestionsManager;
    private Material3Helper material3Helper;
    
    // Number Formatter
    private NumberFormat currencyFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail_modern);
        
        // Initialize database
        customerDao = AppDatabase.getDatabase(this).customerDao();
        sessionManager = new SessionManager(this);
        
        // Initialize formatters
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        
        // Initialize managers
        voiceInputManager = VoiceInputManager.getInstance(this);
        suggestionsManager = SmartSuggestionsManager.getInstance(this);
        material3Helper = Material3Helper.getInstance();
        
        // Initialize views
        initializeViews();
        
        // Setup toolbar
        setupToolbar();
        
        // Apply Material 3 styling
        material3Helper.applyMaterial3Styling(this);
        
        // Setup listeners
        setupListeners();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup form suggestions
        setupFormSuggestions();
        
        // Load customer data
        loadCustomerData();
        
        // Setup customer categories
        setupCustomerCategories();
    }

    private void initializeViews() {
        // Toolbar and AppBar
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        customerHeaderImage = findViewById(R.id.customerHeaderImage);
        
        // Customer Info
        customerAvatar = findViewById(R.id.customerAvatar);
        customerNameText = findViewById(R.id.customerNameText);
        customerStatusText = findViewById(R.id.customerStatusText);
        customerSinceText = findViewById(R.id.customerSinceText);
        totalPurchasesText = findViewById(R.id.totalPurchasesText);
        totalOrdersText = findViewById(R.id.totalOrdersText);
        accountBalanceText = findViewById(R.id.accountBalanceText);
        
        // Form Fields
        customerNameEditText = findViewById(R.id.customerNameEditText);
        customerPhoneEditText = findViewById(R.id.customerPhoneEditText);
        customerEmailEditText = findViewById(R.id.customerEmailEditText);
        customerAddressEditText = findViewById(R.id.customerAddressEditText);
        customerNotesEditText = findViewById(R.id.customerNotesEditText);
        customerCategoryDropdown = findViewById(R.id.customerCategoryDropdown);
        
        // Buttons
        callCustomerButton = findViewById(R.id.callCustomerButton);
        messageCustomerButton = findViewById(R.id.messageCustomerButton);
        addPaymentButton = findViewById(R.id.addPaymentButton);
        viewStatementButton = findViewById(R.id.viewStatementButton);
        viewAllTransactionsButton = findViewById(R.id.viewAllTransactionsButton);
        editCustomerFab = findViewById(R.id.editCustomerFab);
        
        // RecyclerView
        recentTransactionsRecyclerView = findViewById(R.id.recentTransactionsRecyclerView);
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
        // FAB Edit Button
        editCustomerFab.setOnClickListener(v -> toggleEditMode());
        
        // Quick Action Buttons
        callCustomerButton.setOnClickListener(v -> callCustomer());
        messageCustomerButton.setOnClickListener(v -> messageCustomer());
        
        // Account Actions
        addPaymentButton.setOnClickListener(v -> addPayment());
        viewStatementButton.setOnClickListener(v -> viewAccountStatement());
        viewAllTransactionsButton.setOnClickListener(v -> viewAllTransactions());
        
        // Voice Input Setup for all fields
        setupVoiceInputForField(customerNameEditText, "customer_name");
        setupVoiceInputForField(customerPhoneEditText, "phone_number");
        setupVoiceInputForField(customerEmailEditText, "email_address");
        setupVoiceInputForField(customerAddressEditText, "address");
        setupVoiceInputForField(customerNotesEditText, "notes");
    }

    private void setupVoiceInputForField(VoiceInputEditText editText, String fieldType) {
        editText.setVoiceInputManager(voiceInputManager);
        editText.setOnVoiceInputListener(result -> {
            editText.setText(result);
            // Trigger smart suggestions based on field type
            suggestionsManager.provideSuggestions(fieldType, result, suggestions -> {
                // Handle suggestions if needed
            });
        });
    }

    private void setupRecyclerView() {
        recentTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recentTransactionsRecyclerView.setNestedScrollingEnabled(false);
        // Adapter will be set when loading data
    }

    private void setupFormSuggestions() {
        // Setup smart suggestions for customer name
        suggestionsManager.setupAutoComplete(customerNameEditText, "customer_name");
        
        // Setup phone number formatting and suggestions
        suggestionsManager.setupAutoComplete(customerPhoneEditText, "phone_number");
        
        // Setup email suggestions
        suggestionsManager.setupAutoComplete(customerEmailEditText, "email_address");
        
        // Setup address suggestions
        suggestionsManager.setupAutoComplete(customerAddressEditText, "address");
    }

    private void setupCustomerCategories() {
        String[] categories = {
                "عميل عادي",
                "عميل VIP",
                "عميل تاجر",
                "عميل جملة",
                "عميل مؤسسة",
                "عميل حكومي"
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories
        );
        customerCategoryDropdown.setAdapter(adapter);
    }

    private void loadCustomerData() {
        // Get customer ID from intent
        if (getIntent().hasExtra("customerId")) {
            currentCustomerId = getIntent().getStringExtra("customerId");
            loadCustomerFromDatabase(currentCustomerId);
        } else {
            // New customer mode
            currentCustomer = new Customer();
            setFormMode(true);
        }
    }

    private void loadCustomerFromDatabase(String customerId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Customer customer = customerDao.getById(customerId);
            runOnUiThread(() -> {
                if (customer != null) {
                    currentCustomer = customer;
                    populateCustomerData();
                    setFormMode(false);
                } else {
                    Toast.makeText(this, "العميل غير موجود", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void populateCustomerData() {
        if (currentCustomer == null) return;
        
        // Header information
        customerNameText.setText(currentCustomer.getName());
        customerStatusText.setText("عميل نشط");
        customerSinceText.setText("عميل منذ: يناير 2023");
        
        // Statistics (simulated for now)
        totalPurchasesText.setText(currencyFormatter.format(25500.50));
        totalOrdersText.setText("47");
        accountBalanceText.setText("+ " + currencyFormatter.format(1250.50));
        
        // Form fields
        customerNameEditText.setText(currentCustomer.getName());
        customerPhoneEditText.setText(currentCustomer.getPhone());
        customerEmailEditText.setText(currentCustomer.getEmail());
        customerAddressEditText.setText(currentCustomer.getAddress());
        
        // Update toolbar title
        collapsingToolbar.setTitle(currentCustomer.getName());
    }

    private void setFormMode(boolean editMode) {
        this.isEditMode = editMode;
        
        // Enable/disable form fields
        customerNameEditText.setEnabled(editMode);
        customerPhoneEditText.setEnabled(editMode);
        customerEmailEditText.setEnabled(editMode);
        customerAddressEditText.setEnabled(editMode);
        customerNotesEditText.setEnabled(editMode);
        customerCategoryDropdown.setEnabled(editMode);
        
        // Update FAB icon
        if (editMode) {
            editCustomerFab.setImageResource(R.drawable.ic_save_24);
            editCustomerFab.setContentDescription("حفظ التغييرات");
        } else {
            editCustomerFab.setImageResource(R.drawable.ic_edit_24);
            editCustomerFab.setContentDescription("تعديل العميل");
        }
        
        // Invalidate options menu to update save/cancel buttons
        invalidateOptionsMenu();
    }

    private void toggleEditMode() {
        if (isEditMode) {
            saveCustomer();
        } else {
            setFormMode(true);
            showSnackbar("وضع التعديل مُفعل", "إلغاء", v -> {
                loadCustomerData(); // Reload original data
                setFormMode(false);
            });
        }
    }

    private void saveCustomer() {
        if (!validateForm()) {
            return;
        }
        
        String name = customerNameEditText.getText().toString().trim();
        String email = customerEmailEditText.getText().toString().trim();
        String phone = customerPhoneEditText.getText().toString().trim();
        String address = customerAddressEditText.getText().toString().trim();

        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            Toast.makeText(this, "لا توجد شركة محددة.", Toast.LENGTH_LONG).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (currentCustomerId == null) {
                // New customer
                Customer customer = new Customer(UUID.randomUUID().toString(), companyId, name, email, phone, address);
                customerDao.insert(customer);
                currentCustomer = customer;
                currentCustomerId = customer.getId();
            } else {
                // Update existing customer
                currentCustomer.setName(name);
                currentCustomer.setEmail(email);
                currentCustomer.setPhone(phone);
                currentCustomer.setAddress(address);
                customerDao.update(currentCustomer);
            }
            
            runOnUiThread(() -> {
                populateCustomerData();
                setFormMode(false);
                showSnackbar("تم حفظ بيانات العميل بنجاح", null, null);
            });
        });
    }

    private boolean validateForm() {
        boolean isValid = true;
        
        // Validate name
        if (customerNameEditText.getText().toString().trim().isEmpty()) {
            customerNameEditText.setError("يرجى إدخال اسم العميل");
            isValid = false;
        }
        
        // Validate phone
        String phone = customerPhoneEditText.getText().toString().trim();
        if (phone.isEmpty()) {
            customerPhoneEditText.setError("يرجى إدخال رقم الهاتف");
            isValid = false;
        } else if (phone.length() < 10) {
            customerPhoneEditText.setError("رقم الهاتف غير صحيح");
            isValid = false;
        }
        
        // Validate email if provided
        String email = customerEmailEditText.getText().toString().trim();
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            customerEmailEditText.setError("البريد الإلكتروني غير صحيح");
            isValid = false;
        }
        
        return isValid;
    }

    private void callCustomer() {
        if (currentCustomer != null && currentCustomer.getPhone() != null) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + currentCustomer.getPhone()));
            startActivity(callIntent);
        } else {
            showSnackbar("رقم الهاتف غير متوفر", null, null);
        }
    }

    private void messageCustomer() {
        if (currentCustomer != null && currentCustomer.getPhone() != null) {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + currentCustomer.getPhone()));
            startActivity(smsIntent);
        } else {
            showSnackbar("رقم الهاتف غير متوفر", null, null);
        }
    }

    private void addPayment() {
        // Navigate to payment activity
        showSnackbar("سيتم فتح شاشة إضافة دفعة", null, null);
    }

    private void viewAccountStatement() {
        // Navigate to account statement activity
        showSnackbar("سيتم فتح كشف الحساب", null, null);
    }

    private void viewAllTransactions() {
        // Navigate to all transactions
        showSnackbar("سيتم عرض جميع المعاملات", null, null);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Show/hide menu items based on edit mode
        MenuItem saveItem = menu.findItem(R.id.action_save);
        MenuItem cancelItem = menu.findItem(R.id.action_cancel);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        
        if (saveItem != null) saveItem.setVisible(isEditMode);
        if (cancelItem != null) cancelItem.setVisible(isEditMode);
        if (deleteItem != null) deleteItem.setVisible(!isEditMode && currentCustomer != null && currentCustomer.getId() != null);
        if (shareItem != null) shareItem.setVisible(!isEditMode);
        
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isEditMode) {
                    setFormMode(false);
                } else {
                    onBackPressed();
                }
                return true;
                
            case R.id.action_save:
                saveCustomer();
                return true;
                
            case R.id.action_cancel:
                loadCustomerData(); // Reload original data
                setFormMode(false);
                return true;
                
            case R.id.action_delete:
                deleteCustomer();
                return true;
                
            case R.id.action_share:
                shareCustomer();
                return true;
                
            case R.id.action_duplicate:
                duplicateCustomer();
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteCustomer() {
        if (currentCustomerId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Customer customer = customerDao.getById(currentCustomerId);
                if (customer != null) {
                    customerDao.delete(customer);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم الحذف بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }

    private void shareCustomer() {
        if (currentCustomer != null) {
            String shareText = String.format(
                    "بيانات العميل:\n" +
                    "الاسم: %s\n" +
                    "الهاتف: %s\n" +
                    "البريد: %s\n" +
                    "العنوان: %s",
                    currentCustomer.getName(),
                    currentCustomer.getPhone(),
                    currentCustomer.getEmail(),
                    currentCustomer.getAddress()
            );
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "مشاركة بيانات العميل"));
        }
    }

    private void duplicateCustomer() {
        if (currentCustomer != null) {
            String companyId = sessionManager.getCurrentCompanyId();
            if (companyId != null) {
                // Create new customer with same data
                Customer newCustomer = new Customer(
                        UUID.randomUUID().toString(),
                        companyId,
                        currentCustomer.getName() + " (نسخة)",
                        currentCustomer.getEmail(),
                        currentCustomer.getPhone(),
                        currentCustomer.getAddress()
                );
                
                currentCustomer = newCustomer;
                currentCustomerId = null; // This will be treated as new customer
                populateCustomerData();
                setFormMode(true);
                
                showSnackbar("تم إنشاء نسخة من العميل", null, null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            setFormMode(false);
        } else {
            super.onBackPressed();
        }
    }
}
