package com.example.androidapp.ui.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.ui.common.VoiceInputEditText;
import com.example.androidapp.utils.Material3Helper;
import com.example.androidapp.utils.VoiceInputManager;
import com.example.androidapp.utils.SmartSuggestionsManager;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * نشاط تفاصيل الفاتورة المتطور - Material 3 Design
 * مع جميع الميزات الاحترافية والحديثة
 */
public class ModernInvoiceDetailActivity extends AppCompatActivity {

    // UI Components - Header
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private TextView invoiceStatusText;
    private TextView invoiceDateText;
    private TextView invoiceAmountText;
    
    // UI Components - Customer Info
    private VoiceInputEditText customerNameEditText;
    private VoiceInputEditText customerPhoneEditText;
    private VoiceInputEditText customerEmailEditText;
    private VoiceInputEditText customerAddressEditText;
    
    // UI Components - Invoice Details
    private TextInputEditText invoiceNumberEditText;
    private TextInputEditText invoiceDateEditText;
    private AutoCompleteTextView invoiceTypeDropdown;
    private AutoCompleteTextView paymentTermsDropdown;
    private VoiceInputEditText notesEditText;
    
    // UI Components - Financial
    private TextView subtotalText;
    private TextView taxAmountText;
    private TextView discountAmountText;
    private TextView shippingAmountText;
    private TextView totalAmountText;
    private TextView totalInWordsText;
    
    // UI Components - Settings
    private ChipGroup settingsChipGroup;
    private Chip showTaxChip;
    private Chip showDiscountChip;
    private Chip showShippingChip;
    private Chip autoCalculateChip;
    
    // UI Components - Items
    private RecyclerView invoiceItemsRecyclerView;
    private ModernInvoiceItemAdapter itemsAdapter;
    
    // UI Components - Actions
    private MaterialButton addItemButton;
    private MaterialButton previewButton;
    private MaterialButton shareButton;
    private MaterialButton printButton;
    private FloatingActionButton saveFab;
    
    // Data
    private Invoice currentInvoice;
    private List<InvoiceItem> invoiceItems;
    private boolean isEditMode = false;
    private String currentInvoiceId = null;
    
    // Database
    private InvoiceDao invoiceDao;
    private SessionManager sessionManager;
    
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
        setContentView(R.layout.activity_invoice_detail_modern);
        
        // Initialize database
        invoiceDao = AppDatabase.getDatabase(this).invoiceDao();
        sessionManager = new SessionManager(this);
        
        // Initialize formatters
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
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
        
        // Setup dropdowns
        setupDropdowns();
        
        // Setup settings chips
        setupSettingsChips();
        
        // Load invoice data
        loadInvoiceData();
    }

    private void initializeViews() {
        // Toolbar and AppBar
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        invoiceStatusText = findViewById(R.id.invoiceStatusText);
        invoiceDateText = findViewById(R.id.invoiceDateText);
        invoiceAmountText = findViewById(R.id.invoiceAmountText);
        
        // Customer Info
        customerNameEditText = findViewById(R.id.customerNameEditText);
        customerPhoneEditText = findViewById(R.id.customerPhoneEditText);
        customerEmailEditText = findViewById(R.id.customerEmailEditText);
        customerAddressEditText = findViewById(R.id.customerAddressEditText);
        
        // Invoice Details
        invoiceNumberEditText = findViewById(R.id.invoiceNumberEditText);
        invoiceDateEditText = findViewById(R.id.invoiceDateEditText);
        invoiceTypeDropdown = findViewById(R.id.invoiceTypeDropdown);
        paymentTermsDropdown = findViewById(R.id.paymentTermsDropdown);
        notesEditText = findViewById(R.id.notesEditText);
        
        // Financial
        subtotalText = findViewById(R.id.subtotalText);
        taxAmountText = findViewById(R.id.taxAmountText);
        discountAmountText = findViewById(R.id.discountAmountText);
        shippingAmountText = findViewById(R.id.shippingAmountText);
        totalAmountText = findViewById(R.id.totalAmountText);
        totalInWordsText = findViewById(R.id.totalInWordsText);
        
        // Settings
        settingsChipGroup = findViewById(R.id.settingsChipGroup);
        showTaxChip = findViewById(R.id.showTaxChip);
        showDiscountChip = findViewById(R.id.showDiscountChip);
        showShippingChip = findViewById(R.id.showShippingChip);
        autoCalculateChip = findViewById(R.id.autoCalculateChip);
        
        // Items
        invoiceItemsRecyclerView = findViewById(R.id.invoiceItemsRecyclerView);
        
        // Actions
        addItemButton = findViewById(R.id.addItemButton);
        previewButton = findViewById(R.id.previewButton);
        shareButton = findViewById(R.id.shareButton);
        printButton = findViewById(R.id.printButton);
        saveFab = findViewById(R.id.saveFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        collapsingToolbar.setTitle("تفاصيل الفاتورة");
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.white, getTheme()));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white, getTheme()));
    }

    private void setupListeners() {
        // FAB Save Button
        saveFab.setOnClickListener(v -> saveInvoice());
        
        // Action Buttons
        addItemButton.setOnClickListener(v -> addNewItem());
        previewButton.setOnClickListener(v -> previewInvoice());
        shareButton.setOnClickListener(v -> shareInvoice());
        printButton.setOnClickListener(v -> printInvoice());
        
        // Voice Input Setup for all fields
        setupVoiceInputForField(customerNameEditText, "customer_name");
        setupVoiceInputForField(customerPhoneEditText, "phone_number");
        setupVoiceInputForField(customerEmailEditText, "email_address");
        setupVoiceInputForField(customerAddressEditText, "address");
        setupVoiceInputForField(notesEditText, "notes");
        
        // Date field
        invoiceDateEditText.setText(dateFormatter.format(new Date()));
        invoiceDateEditText.setOnClickListener(v -> showDatePicker());
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
        invoiceItems = new ArrayList<>();
        itemsAdapter = new ModernInvoiceItemAdapter(invoiceItems, this::onItemChanged, this::onItemRemoved);
        invoiceItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        invoiceItemsRecyclerView.setAdapter(itemsAdapter);
        invoiceItemsRecyclerView.setNestedScrollingEnabled(false);
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

    private void setupDropdowns() {
        // Invoice Types
        String[] invoiceTypes = {
                "فاتورة مبيعات",
                "فاتورة خدمات",
                "فاتورة مرتجعات",
                "فاتورة ائتمان",
                "عرض سعر",
                "فاتورة مقدمة"
        };
        
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                invoiceTypes
        );
        invoiceTypeDropdown.setAdapter(typesAdapter);
        invoiceTypeDropdown.setText(invoiceTypes[0], false);
        
        // Payment Terms
        String[] paymentTerms = {
                "نقداً",
                "آجل 30 يوم",
                "آجل 60 يوم",
                "آجل 90 يوم",
                "تقسيط شهري",
                "دفع عند التسليم"
        };
        
        ArrayAdapter<String> termsAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                paymentTerms
        );
        paymentTermsDropdown.setAdapter(termsAdapter);
        paymentTermsDropdown.setText(paymentTerms[0], false);
    }

    private void setupSettingsChips() {
        showTaxChip.setChecked(true);
        showDiscountChip.setChecked(true);
        showShippingChip.setChecked(false);
        autoCalculateChip.setChecked(true);
        
        // Setup chip listeners
        showTaxChip.setOnCheckedChangeListener((buttonView, isChecked) -> updateFinancialSectionVisibility());
        showDiscountChip.setOnCheckedChangeListener((buttonView, isChecked) -> updateFinancialSectionVisibility());
        showShippingChip.setOnCheckedChangeListener((buttonView, isChecked) -> updateFinancialSectionVisibility());
        autoCalculateChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                calculateTotals();
            }
        });
    }

    private void updateFinancialSectionVisibility() {
        findViewById(R.id.taxAmountRow).setVisibility(showTaxChip.isChecked() ? View.VISIBLE : View.GONE);
        findViewById(R.id.discountAmountRow).setVisibility(showDiscountChip.isChecked() ? View.VISIBLE : View.GONE);
        findViewById(R.id.shippingAmountRow).setVisibility(showShippingChip.isChecked() ? View.VISIBLE : View.GONE);
    }

    private void loadInvoiceData() {
        // Get invoice ID from intent
        if (getIntent().hasExtra("invoiceId")) {
            currentInvoiceId = getIntent().getStringExtra("invoiceId");
            loadInvoiceFromDatabase(currentInvoiceId);
        } else {
            // New invoice mode
            currentInvoice = new Invoice();
            generateInvoiceNumber();
            setFormMode(true);
            addNewItem(); // Add first empty item
        }
    }

    private void loadInvoiceFromDatabase(String invoiceId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Invoice invoice = invoiceDao.getById(invoiceId);
            runOnUiThread(() -> {
                if (invoice != null) {
                    currentInvoice = invoice;
                    populateInvoiceData();
                    setFormMode(false);
                } else {
                    Toast.makeText(this, "الفاتورة غير موجودة", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void populateInvoiceData() {
        if (currentInvoice == null) return;
        
        // Header information
        invoiceStatusText.setText("فاتورة نشطة");
        invoiceDateText.setText(currentInvoice.getInvoiceDate());
        invoiceAmountText.setText(currencyFormatter.format(currentInvoice.getTotalAmount()));
        
        // Customer information
        customerNameEditText.setText(currentInvoice.getCustomerName());
        customerPhoneEditText.setText(currentInvoice.getCustomerPhone());
        customerEmailEditText.setText(currentInvoice.getCustomerEmail());
        customerAddressEditText.setText(currentInvoice.getCustomerAddress());
        
        // Invoice details
        invoiceNumberEditText.setText(currentInvoice.getInvoiceNumber());
        invoiceDateEditText.setText(currentInvoice.getInvoiceDate());
        invoiceTypeDropdown.setText(currentInvoice.getInvoiceType(), false);
        notesEditText.setText(currentInvoice.getNotes());
        
        // Update toolbar title
        collapsingToolbar.setTitle("فاتورة رقم " + currentInvoice.getInvoiceNumber());
        
        // Calculate and display totals
        calculateTotals();
    }

    private void setFormMode(boolean editMode) {
        this.isEditMode = editMode;
        
        // Enable/disable form fields
        customerNameEditText.setEnabled(editMode);
        customerPhoneEditText.setEnabled(editMode);
        customerEmailEditText.setEnabled(editMode);
        customerAddressEditText.setEnabled(editMode);
        invoiceNumberEditText.setEnabled(editMode);
        invoiceDateEditText.setEnabled(editMode);
        invoiceTypeDropdown.setEnabled(editMode);
        paymentTermsDropdown.setEnabled(editMode);
        notesEditText.setEnabled(editMode);
        
        // Update buttons visibility
        addItemButton.setVisibility(editMode ? View.VISIBLE : View.GONE);
        
        // Update FAB icon
        if (editMode) {
            saveFab.setImageResource(R.drawable.ic_save_24);
            saveFab.setContentDescription("حفظ الفاتورة");
        } else {
            saveFab.setImageResource(R.drawable.ic_edit_24);
            saveFab.setContentDescription("تعديل الفاتورة");
        }
        
        // Invalidate options menu to update menu items
        invalidateOptionsMenu();
    }

    private void generateInvoiceNumber() {
        String invoiceNumber = "INV-" + System.currentTimeMillis();
        invoiceNumberEditText.setText(invoiceNumber);
    }

    private void addNewItem() {
        InvoiceItem newItem = new InvoiceItem();
        newItem.setId(UUID.randomUUID().toString());
        newItem.setItemName("");
        newItem.setQuantity(1);
        newItem.setUnitPrice(0.0);
        newItem.setTotal(0.0);
        
        invoiceItems.add(newItem);
        itemsAdapter.notifyItemInserted(invoiceItems.size() - 1);
    }

    private void onItemChanged(int position) {
        if (autoCalculateChip.isChecked()) {
            calculateTotals();
        }
    }

    private void onItemRemoved(int position) {
        if (position >= 0 && position < invoiceItems.size()) {
            invoiceItems.remove(position);
            itemsAdapter.notifyItemRemoved(position);
            calculateTotals();
        }
    }

    private void calculateTotals() {
        double subtotal = 0.0;
        
        for (InvoiceItem item : invoiceItems) {
            subtotal += item.getTotal();
        }
        
        double taxRate = 0.15; // 15% VAT
        double taxAmount = showTaxChip.isChecked() ? subtotal * taxRate : 0.0;
        double discountAmount = 0.0; // Can be calculated based on input
        double shippingAmount = 0.0; // Can be calculated based on input
        
        double totalAmount = subtotal + taxAmount - discountAmount + shippingAmount;
        
        // Update UI
        subtotalText.setText(currencyFormatter.format(subtotal));
        taxAmountText.setText(currencyFormatter.format(taxAmount));
        discountAmountText.setText(currencyFormatter.format(discountAmount));
        shippingAmountText.setText(currencyFormatter.format(shippingAmount));
        totalAmountText.setText(currencyFormatter.format(totalAmount));
        
        // Update header amount
        invoiceAmountText.setText(currencyFormatter.format(totalAmount));
        
        // Convert amount to words (simplified)
        totalInWordsText.setText(convertAmountToWords(totalAmount));
    }

    private String convertAmountToWords(double amount) {
        // Simplified conversion - you can implement a more sophisticated version
        return String.format("%.2f ريال سعودي", amount);
    }

    private void showDatePicker() {
        // Implement date picker dialog
        showSnackbar("سيتم فتح محدد التاريخ", null, null);
    }

    private void saveInvoice() {
        if (!validateForm()) {
            return;
        }
        
        // Collect form data
        String customerName = customerNameEditText.getText().toString().trim();
        String customerPhone = customerPhoneEditText.getText().toString().trim();
        String customerEmail = customerEmailEditText.getText().toString().trim();
        String customerAddress = customerAddressEditText.getText().toString().trim();
        String invoiceNumber = invoiceNumberEditText.getText().toString().trim();
        String invoiceDate = invoiceDateEditText.getText().toString().trim();
        String invoiceType = invoiceTypeDropdown.getText().toString();
        String notes = notesEditText.getText().toString().trim();

        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            Toast.makeText(this, "لا توجد شركة محددة.", Toast.LENGTH_LONG).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (currentInvoiceId == null) {
                // New invoice
                Invoice invoice = new Invoice();
                invoice.setId(UUID.randomUUID().toString());
                invoice.setCompanyId(companyId);
                invoice.setInvoiceNumber(invoiceNumber);
                invoice.setInvoiceDate(invoiceDate);
                invoice.setCustomerName(customerName);
                invoice.setCustomerPhone(customerPhone);
                invoice.setCustomerEmail(customerEmail);
                invoice.setCustomerAddress(customerAddress);
                invoice.setInvoiceType(invoiceType);
                invoice.setNotes(notes);
                
                // Calculate totals
                double subtotal = invoiceItems.stream().mapToDouble(InvoiceItem::getTotal).sum();
                double taxAmount = showTaxChip.isChecked() ? subtotal * 0.15 : 0.0;
                double totalAmount = subtotal + taxAmount;
                
                invoice.setSubTotal(subtotal);
                invoice.setTaxAmount(taxAmount);
                invoice.setDiscountAmount(0.0);
                invoice.setTotalAmount(totalAmount);
                
                invoiceDao.insert(invoice);
                currentInvoice = invoice;
                currentInvoiceId = invoice.getId();
            } else {
                // Update existing invoice
                currentInvoice.setInvoiceNumber(invoiceNumber);
                currentInvoice.setInvoiceDate(invoiceDate);
                currentInvoice.setCustomerName(customerName);
                currentInvoice.setCustomerPhone(customerPhone);
                currentInvoice.setCustomerEmail(customerEmail);
                currentInvoice.setCustomerAddress(customerAddress);
                currentInvoice.setInvoiceType(invoiceType);
                currentInvoice.setNotes(notes);
                
                // Calculate totals
                double subtotal = invoiceItems.stream().mapToDouble(InvoiceItem::getTotal).sum();
                double taxAmount = showTaxChip.isChecked() ? subtotal * 0.15 : 0.0;
                double totalAmount = subtotal + taxAmount;
                
                currentInvoice.setSubTotal(subtotal);
                currentInvoice.setTaxAmount(taxAmount);
                currentInvoice.setTotalAmount(totalAmount);
                
                invoiceDao.update(currentInvoice);
            }
            
            runOnUiThread(() -> {
                populateInvoiceData();
                setFormMode(false);
                showSnackbar("تم حفظ الفاتورة بنجاح", null, null);
            });
        });
    }

    private boolean validateForm() {
        boolean isValid = true;
        
        // Validate customer name
        if (customerNameEditText.getText().toString().trim().isEmpty()) {
            customerNameEditText.setError("يرجى إدخال اسم العميل");
            isValid = false;
        }
        
        // Validate invoice number
        if (invoiceNumberEditText.getText().toString().trim().isEmpty()) {
            invoiceNumberEditText.setError("يرجى إدخال رقم الفاتورة");
            isValid = false;
        }
        
        // Validate at least one item
        if (invoiceItems.isEmpty()) {
            showSnackbar("يرجى إضافة عنصر واحد على الأقل", null, null);
            isValid = false;
        }
        
        return isValid;
    }

    private void previewInvoice() {
        showSnackbar("سيتم عرض معاينة الفاتورة", null, null);
    }

    private void shareInvoice() {
        if (currentInvoice != null) {
            String shareText = String.format(
                    "فاتورة رقم: %s\n" +
                    "العميل: %s\n" +
                    "التاريخ: %s\n" +
                    "المبلغ الإجمالي: %s",
                    currentInvoice.getInvoiceNumber(),
                    currentInvoice.getCustomerName(),
                    currentInvoice.getInvoiceDate(),
                    currencyFormatter.format(currentInvoice.getTotalAmount())
            );
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "مشاركة الفاتورة"));
        }
    }

    private void printInvoice() {
        showSnackbar("سيتم طباعة الفاتورة", null, null);
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
        getMenuInflater().inflate(R.menu.menu_invoice_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Show/hide menu items based on edit mode
        MenuItem saveItem = menu.findItem(R.id.action_save);
        MenuItem cancelItem = menu.findItem(R.id.action_cancel);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem duplicateItem = menu.findItem(R.id.action_duplicate);
        
        if (saveItem != null) saveItem.setVisible(isEditMode);
        if (cancelItem != null) cancelItem.setVisible(isEditMode);
        if (deleteItem != null) deleteItem.setVisible(!isEditMode && currentInvoice != null && currentInvoice.getId() != null);
        if (duplicateItem != null) duplicateItem.setVisible(!isEditMode);
        
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
                saveInvoice();
                return true;
                
            case R.id.action_cancel:
                loadInvoiceData(); // Reload original data
                setFormMode(false);
                return true;
                
            case R.id.action_delete:
                deleteInvoice();
                return true;
                
            case R.id.action_duplicate:
                duplicateInvoice();
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteInvoice() {
        if (currentInvoiceId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Invoice invoice = invoiceDao.getById(currentInvoiceId);
                if (invoice != null) {
                    invoiceDao.delete(invoice);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم حذف الفاتورة بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }

    private void duplicateInvoice() {
        if (currentInvoice != null) {
            String companyId = sessionManager.getCurrentCompanyId();
            if (companyId != null) {
                // Create new invoice with same data
                currentInvoice = new Invoice();
                currentInvoice.setId(UUID.randomUUID().toString());
                currentInvoice.setCompanyId(companyId);
                generateInvoiceNumber();
                currentInvoiceId = null; // This will be treated as new invoice
                populateInvoiceData();
                setFormMode(true);
                
                showSnackbar("تم إنشاء نسخة من الفاتورة", null, null);
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