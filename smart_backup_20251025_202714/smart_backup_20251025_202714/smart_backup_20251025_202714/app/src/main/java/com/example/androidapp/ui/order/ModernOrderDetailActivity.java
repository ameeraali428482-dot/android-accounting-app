package com.example.androidapp.ui.order;

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
import com.example.androidapp.data.dao.OrderDao;
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.data.entities.OrderItem;
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
import com.google.android.material.progressindicator.LinearProgressIndicator;
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
 * نشاط تفاصيل الطلب المتطور - Material 3 Design
 * مع جميع الميزات الاحترافية والحديثة
 */
public class ModernOrderDetailActivity extends AppCompatActivity {

    // UI Components - Header
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private TextView orderStatusText;
    private TextView orderDateText;
    private TextView orderAmountText;
    private TextView orderProgressText;
    private LinearProgressIndicator orderProgressIndicator;
    
    // UI Components - Customer Info
    private VoiceInputEditText customerNameEditText;
    private VoiceInputEditText customerPhoneEditText;
    private VoiceInputEditText customerEmailEditText;
    private VoiceInputEditText customerAddressEditText;
    
    // UI Components - Order Details
    private TextInputEditText orderNumberEditText;
    private TextInputEditText orderDateEditText;
    private AutoCompleteTextView orderTypeDropdown;
    private AutoCompleteTextView priorityDropdown;
    private VoiceInputEditText notesEditText;
    
    // UI Components - Delivery Info
    private VoiceInputEditText deliveryAddressEditText;
    private TextInputEditText expectedDeliveryDateEditText;
    private AutoCompleteTextView deliveryMethodDropdown;
    private VoiceInputEditText deliveryNotesEditText;
    
    // UI Components - Financial
    private TextView subtotalText;
    private TextView taxAmountText;
    private TextView discountAmountText;
    private TextView shippingAmountText;
    private TextView totalAmountText;
    
    // UI Components - Status Management
    private ChipGroup orderStatusChipGroup;
    private Chip pendingChip;
    private Chip confirmedChip;
    private Chip processingChip;
    private Chip shippingChip;
    private Chip deliveredChip;
    private Chip cancelledChip;
    
    // UI Components - Items
    private RecyclerView orderItemsRecyclerView;
    private ModernOrderItemAdapter itemsAdapter;
    
    // UI Components - Actions
    private MaterialButton addItemButton;
    private MaterialButton trackOrderButton;
    private MaterialButton shareOrderButton;
    private MaterialButton printOrderButton;
    private FloatingActionButton saveFab;
    
    // Order Status Tracking
    private MaterialCardView statusTrackingCard;
    private LinearProgressIndicator statusProgressIndicator;
    
    // Data
    private Order currentOrder;
    private List<OrderItem> orderItems;
    private boolean isEditMode = false;
    private String currentOrderId = null;
    
    // Database
    private OrderDao orderDao;
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
        setContentView(R.layout.activity_order_detail_modern);
        
        // Initialize database
        orderDao = AppDatabase.getDatabase(this).orderDao();
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
        
        // Setup status chips
        setupStatusChips();
        
        // Load order data
        loadOrderData();
    }

    private void initializeViews() {
        // Toolbar and AppBar
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        orderStatusText = findViewById(R.id.orderStatusText);
        orderDateText = findViewById(R.id.orderDateText);
        orderAmountText = findViewById(R.id.orderAmountText);
        orderProgressText = findViewById(R.id.orderProgressText);
        orderProgressIndicator = findViewById(R.id.orderProgressIndicator);
        
        // Customer Info
        customerNameEditText = findViewById(R.id.customerNameEditText);
        customerPhoneEditText = findViewById(R.id.customerPhoneEditText);
        customerEmailEditText = findViewById(R.id.customerEmailEditText);
        customerAddressEditText = findViewById(R.id.customerAddressEditText);
        
        // Order Details
        orderNumberEditText = findViewById(R.id.orderNumberEditText);
        orderDateEditText = findViewById(R.id.orderDateEditText);
        orderTypeDropdown = findViewById(R.id.orderTypeDropdown);
        priorityDropdown = findViewById(R.id.priorityDropdown);
        notesEditText = findViewById(R.id.notesEditText);
        
        // Delivery Info
        deliveryAddressEditText = findViewById(R.id.deliveryAddressEditText);
        expectedDeliveryDateEditText = findViewById(R.id.expectedDeliveryDateEditText);
        deliveryMethodDropdown = findViewById(R.id.deliveryMethodDropdown);
        deliveryNotesEditText = findViewById(R.id.deliveryNotesEditText);
        
        // Financial
        subtotalText = findViewById(R.id.subtotalText);
        taxAmountText = findViewById(R.id.taxAmountText);
        discountAmountText = findViewById(R.id.discountAmountText);
        shippingAmountText = findViewById(R.id.shippingAmountText);
        totalAmountText = findViewById(R.id.totalAmountText);
        
        // Status Management
        orderStatusChipGroup = findViewById(R.id.orderStatusChipGroup);
        pendingChip = findViewById(R.id.pendingChip);
        confirmedChip = findViewById(R.id.confirmedChip);
        processingChip = findViewById(R.id.processingChip);
        shippingChip = findViewById(R.id.shippingChip);
        deliveredChip = findViewById(R.id.deliveredChip);
        cancelledChip = findViewById(R.id.cancelledChip);
        
        // Status Tracking
        statusTrackingCard = findViewById(R.id.statusTrackingCard);
        statusProgressIndicator = findViewById(R.id.statusProgressIndicator);
        
        // Items
        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView);
        
        // Actions
        addItemButton = findViewById(R.id.addItemButton);
        trackOrderButton = findViewById(R.id.trackOrderButton);
        shareOrderButton = findViewById(R.id.shareOrderButton);
        printOrderButton = findViewById(R.id.printOrderButton);
        saveFab = findViewById(R.id.saveFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        collapsingToolbar.setTitle("تفاصيل الطلب");
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.white, getTheme()));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white, getTheme()));
    }

    private void setupListeners() {
        // FAB Save Button
        saveFab.setOnClickListener(v -> saveOrder());
        
        // Action Buttons
        addItemButton.setOnClickListener(v -> addNewItem());
        trackOrderButton.setOnClickListener(v -> trackOrder());
        shareOrderButton.setOnClickListener(v -> shareOrder());
        printOrderButton.setOnClickListener(v -> printOrder());
        
        // Voice Input Setup for all fields
        setupVoiceInputForField(customerNameEditText, "customer_name");
        setupVoiceInputForField(customerPhoneEditText, "phone_number");
        setupVoiceInputForField(customerEmailEditText, "email_address");
        setupVoiceInputForField(customerAddressEditText, "address");
        setupVoiceInputForField(deliveryAddressEditText, "delivery_address");
        setupVoiceInputForField(notesEditText, "notes");
        setupVoiceInputForField(deliveryNotesEditText, "delivery_notes");
        
        // Date fields
        orderDateEditText.setText(dateFormatter.format(new Date()));
        orderDateEditText.setOnClickListener(v -> showDatePicker("order"));
        expectedDeliveryDateEditText.setOnClickListener(v -> showDatePicker("delivery"));
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
        orderItems = new ArrayList<>();
        itemsAdapter = new ModernOrderItemAdapter(orderItems, this::onItemChanged, this::onItemRemoved);
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderItemsRecyclerView.setAdapter(itemsAdapter);
        orderItemsRecyclerView.setNestedScrollingEnabled(false);
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
        suggestionsManager.setupAutoComplete(deliveryAddressEditText, "delivery_address");
    }

    private void setupDropdowns() {
        // Order Types
        String[] orderTypes = {
                "طلب عادي",
                "طلب عاجل",
                "طلب بالجملة",
                "طلب خاص",
                "طلب موسمي",
                "طلب تجريبي"
        };
        
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                orderTypes
        );
        orderTypeDropdown.setAdapter(typesAdapter);
        orderTypeDropdown.setText(orderTypes[0], false);
        
        // Priority Levels
        String[] priorities = {
                "عادي",
                "مهم",
                "عاجل",
                "عاجل جداً"
        };
        
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                priorities
        );
        priorityDropdown.setAdapter(priorityAdapter);
        priorityDropdown.setText(priorities[0], false);
        
        // Delivery Methods
        String[] deliveryMethods = {
                "توصيل عادي",
                "توصيل سريع",
                "توصيل فوري",
                "استلام من المحل",
                "شحن بالبريد",
                "توصيل مبرمج"
        };
        
        ArrayAdapter<String> deliveryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                deliveryMethods
        );
        deliveryMethodDropdown.setAdapter(deliveryAdapter);
        deliveryMethodDropdown.setText(deliveryMethods[0], false);
    }

    private void setupStatusChips() {
        // Set up single selection for status chips
        orderStatusChipGroup.setSingleSelection(true);
        orderStatusChipGroup.setSelectionRequired(true);
        
        // Set default status
        pendingChip.setChecked(true);
        
        // Setup status change listeners
        orderStatusChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                String status = getStatusFromChipId(checkedId);
                updateOrderStatus(status);
                updateStatusProgress(status);
            }
        });
    }

    private String getStatusFromChipId(int chipId) {
        if (chipId == R.id.pendingChip) return "pending";
        else if (chipId == R.id.confirmedChip) return "confirmed";
        else if (chipId == R.id.processingChip) return "processing";
        else if (chipId == R.id.shippingChip) return "shipping";
        else if (chipId == R.id.deliveredChip) return "delivered";
        else if (chipId == R.id.cancelledChip) return "cancelled";
        return "pending";
    }

    private void updateOrderStatus(String status) {
        // Update header status text
        String statusText = getStatusDisplayText(status);
        orderStatusText.setText(statusText);
        
        // Update status color
        int statusColor = getStatusColor(status);
        orderStatusText.setBackgroundTintList(getColorStateList(statusColor));
    }

    private void updateStatusProgress(String status) {
        int progress = 0;
        String progressText = "";
        
        switch (status) {
            case "pending":
                progress = 0;
                progressText = "في انتظار التأكيد";
                break;
            case "confirmed":
                progress = 20;
                progressText = "تم تأكيد الطلب";
                break;
            case "processing":
                progress = 40;
                progressText = "جاري التحضير";
                break;
            case "shipping":
                progress = 70;
                progressText = "تم الشحن";
                break;
            case "delivered":
                progress = 100;
                progressText = "تم التسليم";
                break;
            case "cancelled":
                progress = 0;
                progressText = "تم الإلغاء";
                break;
        }
        
        orderProgressIndicator.setProgress(progress);
        statusProgressIndicator.setProgress(progress);
        orderProgressText.setText(progressText);
    }

    private String getStatusDisplayText(String status) {
        switch (status) {
            case "pending": return "في الانتظار";
            case "confirmed": return "مؤكد";
            case "processing": return "قيد التحضير";
            case "shipping": return "قيد الشحن";
            case "delivered": return "تم التسليم";
            case "cancelled": return "ملغي";
            default: return "غير محدد";
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "pending": return R.color.warning;
            case "confirmed": return R.color.info;
            case "processing": return R.color.primary;
            case "shipping": return R.color.tertiary;
            case "delivered": return R.color.success;
            case "cancelled": return R.color.error;
            default: return R.color.outline;
        }
    }

    private void loadOrderData() {
        // Get order ID from intent
        if (getIntent().hasExtra("orderId")) {
            currentOrderId = getIntent().getStringExtra("orderId");
            loadOrderFromDatabase(currentOrderId);
        } else {
            // New order mode
            currentOrder = new Order();
            generateOrderNumber();
            setFormMode(true);
            addNewItem(); // Add first empty item
        }
    }

    private void loadOrderFromDatabase(String orderId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Order order = orderDao.getById(orderId);
            runOnUiThread(() -> {
                if (order != null) {
                    currentOrder = order;
                    populateOrderData();
                    setFormMode(false);
                } else {
                    Toast.makeText(this, "الطلب غير موجود", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void populateOrderData() {
        if (currentOrder == null) return;
        
        // Header information
        orderStatusText.setText(getStatusDisplayText(currentOrder.getStatus()));
        orderDateText.setText(currentOrder.getOrderDate());
        orderAmountText.setText(currencyFormatter.format(currentOrder.getTotalAmount()));
        
        // Update status progress
        updateStatusProgress(currentOrder.getStatus());
        
        // Customer information
        customerNameEditText.setText(currentOrder.getCustomerName());
        customerPhoneEditText.setText(currentOrder.getCustomerPhone());
        customerEmailEditText.setText(currentOrder.getCustomerEmail());
        customerAddressEditText.setText(currentOrder.getCustomerAddress());
        
        // Order details
        orderNumberEditText.setText(currentOrder.getOrderNumber());
        orderDateEditText.setText(currentOrder.getOrderDate());
        orderTypeDropdown.setText(currentOrder.getOrderType(), false);
        notesEditText.setText(currentOrder.getNotes());
        
        // Delivery information
        deliveryAddressEditText.setText(currentOrder.getDeliveryAddress());
        expectedDeliveryDateEditText.setText(currentOrder.getExpectedDeliveryDate());
        deliveryNotesEditText.setText(currentOrder.getDeliveryNotes());
        
        // Update status chip
        updateStatusChip(currentOrder.getStatus());
        
        // Update toolbar title
        collapsingToolbar.setTitle("طلب رقم " + currentOrder.getOrderNumber());
        
        // Calculate and display totals
        calculateTotals();
    }

    private void updateStatusChip(String status) {
        switch (status) {
            case "pending":
                pendingChip.setChecked(true);
                break;
            case "confirmed":
                confirmedChip.setChecked(true);
                break;
            case "processing":
                processingChip.setChecked(true);
                break;
            case "shipping":
                shippingChip.setChecked(true);
                break;
            case "delivered":
                deliveredChip.setChecked(true);
                break;
            case "cancelled":
                cancelledChip.setChecked(true);
                break;
        }
    }

    private void setFormMode(boolean editMode) {
        this.isEditMode = editMode;
        
        // Enable/disable form fields
        customerNameEditText.setEnabled(editMode);
        customerPhoneEditText.setEnabled(editMode);
        customerEmailEditText.setEnabled(editMode);
        customerAddressEditText.setEnabled(editMode);
        orderNumberEditText.setEnabled(editMode);
        orderDateEditText.setEnabled(editMode);
        orderTypeDropdown.setEnabled(editMode);
        priorityDropdown.setEnabled(editMode);
        notesEditText.setEnabled(editMode);
        deliveryAddressEditText.setEnabled(editMode);
        expectedDeliveryDateEditText.setEnabled(editMode);
        deliveryMethodDropdown.setEnabled(editMode);
        deliveryNotesEditText.setEnabled(editMode);
        
        // Update buttons visibility
        addItemButton.setVisibility(editMode ? View.VISIBLE : View.GONE);
        
        // Update FAB icon
        if (editMode) {
            saveFab.setImageResource(R.drawable.ic_save_24);
            saveFab.setContentDescription("حفظ الطلب");
        } else {
            saveFab.setImageResource(R.drawable.ic_edit_24);
            saveFab.setContentDescription("تعديل الطلب");
        }
        
        // Invalidate options menu to update menu items
        invalidateOptionsMenu();
    }

    private void generateOrderNumber() {
        String orderNumber = "ORD-" + System.currentTimeMillis();
        orderNumberEditText.setText(orderNumber);
    }

    private void addNewItem() {
        OrderItem newItem = new OrderItem();
        newItem.setId(UUID.randomUUID().toString());
        newItem.setProductName("");
        newItem.setQuantity(1);
        newItem.setUnitPrice(0.0);
        newItem.setTotalPrice(0.0);
        
        orderItems.add(newItem);
        itemsAdapter.notifyItemInserted(orderItems.size() - 1);
    }

    private void onItemChanged(int position) {
        calculateTotals();
    }

    private void onItemRemoved(int position) {
        if (position >= 0 && position < orderItems.size()) {
            orderItems.remove(position);
            itemsAdapter.notifyItemRemoved(position);
            calculateTotals();
        }
    }

    private void calculateTotals() {
        double subtotal = 0.0;
        
        for (OrderItem item : orderItems) {
            subtotal += item.getTotalPrice();
        }
        
        double taxRate = 0.15; // 15% VAT
        double taxAmount = subtotal * taxRate;
        double discountAmount = 0.0; // Can be calculated based on input
        double shippingAmount = 50.0; // Fixed shipping amount for now
        
        double totalAmount = subtotal + taxAmount - discountAmount + shippingAmount;
        
        // Update UI
        subtotalText.setText(currencyFormatter.format(subtotal));
        taxAmountText.setText(currencyFormatter.format(taxAmount));
        discountAmountText.setText(currencyFormatter.format(discountAmount));
        shippingAmountText.setText(currencyFormatter.format(shippingAmount));
        totalAmountText.setText(currencyFormatter.format(totalAmount));
        
        // Update header amount
        orderAmountText.setText(currencyFormatter.format(totalAmount));
    }

    private void showDatePicker(String type) {
        // Implement date picker dialog
        showSnackbar("سيتم فتح محدد التاريخ لـ " + type, null, null);
    }

    private void saveOrder() {
        if (!validateForm()) {
            return;
        }
        
        // Collect form data
        String customerName = customerNameEditText.getText().toString().trim();
        String customerPhone = customerPhoneEditText.getText().toString().trim();
        String customerEmail = customerEmailEditText.getText().toString().trim();
        String customerAddress = customerAddressEditText.getText().toString().trim();
        String orderNumber = orderNumberEditText.getText().toString().trim();
        String orderDate = orderDateEditText.getText().toString().trim();
        String orderType = orderTypeDropdown.getText().toString();
        String notes = notesEditText.getText().toString().trim();
        String deliveryAddress = deliveryAddressEditText.getText().toString().trim();
        String expectedDeliveryDate = expectedDeliveryDateEditText.getText().toString().trim();
        String deliveryNotes = deliveryNotesEditText.getText().toString().trim();

        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            Toast.makeText(this, "لا توجد شركة محددة.", Toast.LENGTH_LONG).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (currentOrderId == null) {
                // New order
                Order order = new Order();
                order.setId(UUID.randomUUID().toString());
                order.setCompanyId(companyId);
                order.setOrderNumber(orderNumber);
                order.setOrderDate(orderDate);
                order.setCustomerName(customerName);
                order.setCustomerPhone(customerPhone);
                order.setCustomerEmail(customerEmail);
                order.setCustomerAddress(customerAddress);
                order.setOrderType(orderType);
                order.setNotes(notes);
                order.setDeliveryAddress(deliveryAddress);
                order.setExpectedDeliveryDate(expectedDeliveryDate);
                order.setDeliveryNotes(deliveryNotes);
                order.setStatus("pending");
                
                // Calculate totals
                double subtotal = orderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();
                double taxAmount = subtotal * 0.15;
                double totalAmount = subtotal + taxAmount + 50.0; // Adding shipping
                
                order.setSubtotal(subtotal);
                order.setTaxAmount(taxAmount);
                order.setDiscountAmount(0.0);
                order.setShippingAmount(50.0);
                order.setTotalAmount(totalAmount);
                
                orderDao.insert(order);
                currentOrder = order;
                currentOrderId = order.getId();
            } else {
                // Update existing order
                currentOrder.setOrderNumber(orderNumber);
                currentOrder.setOrderDate(orderDate);
                currentOrder.setCustomerName(customerName);
                currentOrder.setCustomerPhone(customerPhone);
                currentOrder.setCustomerEmail(customerEmail);
                currentOrder.setCustomerAddress(customerAddress);
                currentOrder.setOrderType(orderType);
                currentOrder.setNotes(notes);
                currentOrder.setDeliveryAddress(deliveryAddress);
                currentOrder.setExpectedDeliveryDate(expectedDeliveryDate);
                currentOrder.setDeliveryNotes(deliveryNotes);
                
                // Calculate totals
                double subtotal = orderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();
                double taxAmount = subtotal * 0.15;
                double totalAmount = subtotal + taxAmount + 50.0;
                
                currentOrder.setSubtotal(subtotal);
                currentOrder.setTaxAmount(taxAmount);
                currentOrder.setShippingAmount(50.0);
                currentOrder.setTotalAmount(totalAmount);
                
                orderDao.update(currentOrder);
            }
            
            runOnUiThread(() -> {
                populateOrderData();
                setFormMode(false);
                showSnackbar("تم حفظ الطلب بنجاح", null, null);
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
        
        // Validate order number
        if (orderNumberEditText.getText().toString().trim().isEmpty()) {
            orderNumberEditText.setError("يرجى إدخال رقم الطلب");
            isValid = false;
        }
        
        // Validate at least one item
        if (orderItems.isEmpty()) {
            showSnackbar("يرجى إضافة عنصر واحد على الأقل", null, null);
            isValid = false;
        }
        
        return isValid;
    }

    private void trackOrder() {
        showSnackbar("سيتم فتح شاشة تتبع الطلب", null, null);
    }

    private void shareOrder() {
        if (currentOrder != null) {
            String shareText = String.format(
                    "طلب رقم: %s\n" +
                    "العميل: %s\n" +
                    "التاريخ: %s\n" +
                    "الحالة: %s\n" +
                    "المبلغ الإجمالي: %s",
                    currentOrder.getOrderNumber(),
                    currentOrder.getCustomerName(),
                    currentOrder.getOrderDate(),
                    getStatusDisplayText(currentOrder.getStatus()),
                    currencyFormatter.format(currentOrder.getTotalAmount())
            );
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "مشاركة الطلب"));
        }
    }

    private void printOrder() {
        showSnackbar("سيتم طباعة الطلب", null, null);
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
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
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
        if (deleteItem != null) deleteItem.setVisible(!isEditMode && currentOrder != null && currentOrder.getId() != null);
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
                saveOrder();
                return true;
                
            case R.id.action_cancel:
                loadOrderData(); // Reload original data
                setFormMode(false);
                return true;
                
            case R.id.action_delete:
                deleteOrder();
                return true;
                
            case R.id.action_duplicate:
                duplicateOrder();
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteOrder() {
        if (currentOrderId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Order order = orderDao.getById(currentOrderId);
                if (order != null) {
                    orderDao.delete(order);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم حذف الطلب بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }

    private void duplicateOrder() {
        if (currentOrder != null) {
            String companyId = sessionManager.getCurrentCompanyId();
            if (companyId != null) {
                // Create new order with same data
                currentOrder = new Order();
                currentOrder.setId(UUID.randomUUID().toString());
                currentOrder.setCompanyId(companyId);
                generateOrderNumber();
                currentOrderId = null; // This will be treated as new order
                populateOrderData();
                setFormMode(true);
                
                showSnackbar("تم إنشاء نسخة من الطلب", null, null);
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