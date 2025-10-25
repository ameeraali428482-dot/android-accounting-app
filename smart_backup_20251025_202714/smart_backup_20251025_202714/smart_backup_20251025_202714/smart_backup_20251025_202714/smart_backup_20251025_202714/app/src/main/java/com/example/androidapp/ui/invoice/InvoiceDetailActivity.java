package com.example.androidapp.ui.invoice;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.ui.common.EnhancedBaseActivity;
import com.example.androidapp.ui.invoice.viewmodel.InvoiceViewModel;
import com.example.androidapp.utils.SessionManager;
import com.example.androidapp.utils.SearchSuggestionManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class InvoiceDetailActivity extends EnhancedBaseActivity {

    // المكونات الأساسية للفاتورة
    private EditText etInvoiceNumber, etInvoiceDate, etInvoiceType, etSubTotal, etTax, etDiscount, etGrandTotal;
    private AutoCompleteTextView etCustomerName; // تحويل لـ AutoCompleteTextView للاقتراحات
    private LinearLayout invoiceItemsContainer;
    private Button btnAddItem, btnSave, btnDelete, btnPreview, btnPrint, btnShare, btnReadInvoice;
    private ImageButton btnVoiceSearch; // زر البحث الصوتي
    
    // المكونات المتقدمة
    private TextView tvInvoiceTitle, tvTotalInWords;
    private View layoutCustomerDetails, layoutCompanyInfo, layoutNotes;
    
    private InvoiceViewModel viewModel;
    private SessionManager sessionManager;
    private SharedPreferences invoiceSettings;
    private String companyId;
    private String invoiceId = null;
    private List<InvoiceItem> currentItems = new ArrayList<>();
    
    // إعدادات الفاتورة
    private boolean showCustomerDetails = true;
    private boolean showItemCodes = true;
    private boolean showTaxes = true;
    private boolean autoCalculateTax = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail_enhanced);

        // تهيئة المدراء
        sessionManager = new SessionManager(this);
        invoiceSettings = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "معرف الشركة غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // تحميل إعدادات الفاتورة
        loadInvoiceSettings();
        
        // تهيئة المكونات
        initializeViews();
        
        // إعداد الاقتراحات الذكية
        setupSmartSuggestions();
        
        // إعداد الأحداث
        setupEventListeners();
        
        // تهيئة ViewModel
        viewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);
        
        // إعداد شريط الأدوات
        setupToolbar();
        
        // معالجة Intent للتحرير أو الإنشاء الجديد
        handleIntent();
        
        // إعداد التخطيط بناءً على الإعدادات
        setupLayoutBasedOnSettings();
    }
    
    /**
     * تحميل إعدادات الفاتورة من SharedPreferences
     */
    private void loadInvoiceSettings() {
        showCustomerDetails = invoiceSettings.getBoolean("show_customer_details", true);
        showItemCodes = invoiceSettings.getBoolean("show_item_codes", true);
        showTaxes = invoiceSettings.getBoolean("show_taxes", true);
        autoCalculateTax = invoiceSettings.getBoolean("auto_calculate_tax", true);
    }

        viewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        initViews();
        setupListeners();

        invoiceId = getIntent().getStringExtra("invoice_id");

        if (invoiceId != null) {
            setTitle("تعديل فاتورة");
            loadInvoiceDetails(invoiceId);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            setTitle("إضافة فاتورة جديدة");
            etInvoiceDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            btnDelete.setVisibility(View.GONE);
            addItemView(null); // Add one empty item for new invoice
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        etInvoiceNumber = findViewById(R.id.et_invoice_number);
        etInvoiceDate = findViewById(R.id.et_invoice_date);
        etCustomerName = findViewById(R.id.et_customer_name);
        etInvoiceType = findViewById(R.id.et_invoice_type);
        etSubTotal = findViewById(R.id.et_sub_total);
        etTax = findViewById(R.id.et_tax);
        etDiscount = findViewById(R.id.et_discount);
        etGrandTotal = findViewById(R.id.et_grand_total);
        invoiceItemsContainer = findViewById(R.id.invoice_items_container);
        btnAddItem = findViewById(R.id.btn_add_item);
        btnSave = findViewById(R.id.btn_save_invoice);
        btnDelete = findViewById(R.id.btn_delete_invoice);

        etSubTotal.setEnabled(false);
        etGrandTotal.setEnabled(false);
    }

    private void setupListeners() {
        btnAddItem.setOnClickListener(v -> addItemView(null));
        btnSave.setOnClickListener(v -> saveInvoice());
        btnDelete.setOnClickListener(v -> deleteInvoice());
    }

    private void loadInvoiceDetails(String id) {
        viewModel.getInvoiceById(id, companyId).observe(this, invoice -> {
            if (invoice != null) {
                etInvoiceNumber.setText(invoice.getInvoiceNumber());
                etInvoiceDate.setText(invoice.getInvoiceDate());
                etCustomerName.setText(invoice.getCustomerName());
                etInvoiceType.setText(invoice.getInvoiceType());
                etSubTotal.setText(String.valueOf(invoice.getSubTotal()));
                etTax.setText(String.valueOf(invoice.getTaxAmount()));
                etDiscount.setText(String.valueOf(invoice.getDiscountAmount()));
                etGrandTotal.setText(String.valueOf(invoice.getTotalAmount()));
            } else {
                Toast.makeText(this, "لم يتم العثور على الفاتورة", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void addItemView(InvoiceItem item) {
        View itemView = getLayoutInflater().inflate(R.layout.invoice_item_row, invoiceItemsContainer, false);
        EditText etItemName = itemView.findViewById(R.id.itemName);
        EditText etQuantity = itemView.findViewById(R.id.quantity);
        EditText etUnitPrice = itemView.findViewById(R.id.price);
        EditText etItemTotal = itemView.findViewById(R.id.total);
        View btnRemove = itemView.findViewById(R.id.btnDelete);

        etItemTotal.setEnabled(false);

        if (item != null) {
            etItemName.setText(item.getItemName());
            etQuantity.setText(String.valueOf(item.getQuantity()));
            etUnitPrice.setText(String.valueOf(item.getUnitPrice()));
            etItemTotal.setText(String.valueOf(item.getTotal()));
        }

        btnRemove.setOnClickListener(v -> {
            invoiceItemsContainer.removeView(itemView);
            calculateTotals();
        });

        etQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateItemTotal(itemView);
        });
        etUnitPrice.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateItemTotal(itemView);
        });

        invoiceItemsContainer.addView(itemView);
        calculateTotals();
    }

    private void calculateItemTotal(View itemView) {
        EditText etQuantity = itemView.findViewById(R.id.quantity);
        EditText etUnitPrice = itemView.findViewById(R.id.price);
        EditText etItemTotal = itemView.findViewById(R.id.total);

        float quantity = Float.parseFloat(etQuantity.getText().toString().trim().isEmpty() ? "0" : etQuantity.getText().toString().trim());
        float unitPrice = Float.parseFloat(etUnitPrice.getText().toString().trim().isEmpty() ? "0" : etUnitPrice.getText().toString().trim());
        float itemTotal = quantity * unitPrice;
        etItemTotal.setText(String.valueOf(itemTotal));
        calculateTotals();
    }

    private void calculateTotals() {
        float subTotal = 0.0f;
        for (int i = 0; i < invoiceItemsContainer.getChildCount(); i++) {
            View itemView = invoiceItemsContainer.getChildAt(i);
            EditText etItemTotal = itemView.findViewById(R.id.total);
            subTotal += Float.parseFloat(etItemTotal.getText().toString().trim().isEmpty() ? "0" : etItemTotal.getText().toString().trim());
        }
        etSubTotal.setText(String.valueOf(subTotal));

        float taxAmount = Float.parseFloat(etTax.getText().toString().trim().isEmpty() ? "0" : etTax.getText().toString().trim());
        float discountAmount = Float.parseFloat(etDiscount.getText().toString().trim().isEmpty() ? "0" : etDiscount.getText().toString().trim());
        float grandTotal = subTotal + taxAmount - discountAmount;

        etGrandTotal.setText(String.valueOf(grandTotal));
    }

    private void saveInvoice() {
        String invoiceNumber = etInvoiceNumber.getText().toString().trim();
        String invoiceDate = etInvoiceDate.getText().toString().trim();
        String customerName = etCustomerName.getText().toString().trim();
        String invoiceType = etInvoiceType.getText().toString().trim();
        float subTotal = Float.parseFloat(etSubTotal.getText().toString().trim().isEmpty() ? "0" : etSubTotal.getText().toString().trim());
        float taxAmount = Float.parseFloat(etTax.getText().toString().trim().isEmpty() ? "0" : etTax.getText().toString().trim());
        float discountAmount = Float.parseFloat(etDiscount.getText().toString().trim().isEmpty() ? "0" : etDiscount.getText().toString().trim());
        float grandTotal = Float.parseFloat(etGrandTotal.getText().toString().trim().isEmpty() ? "0" : etGrandTotal.getText().toString().trim());

        if (invoiceNumber.isEmpty() || invoiceDate.isEmpty() || customerName.isEmpty() || invoiceType.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول الرئيسية.", Toast.LENGTH_SHORT).show();
            return;
        }

        Invoice invoice;
        if (invoiceId == null) {
            invoiceId = UUID.randomUUID().toString();
            invoice = new Invoice(invoiceId, companyId, customerName, null, invoiceNumber, invoiceDate, null, grandTotal, "PENDING", invoiceType, 0, subTotal, taxAmount, discountAmount);
            viewModel.insert(invoice);
            Toast.makeText(this, "تم إضافة الفاتورة بنجاح.", Toast.LENGTH_SHORT).show();
        } else {
            invoice = new Invoice(invoiceId, companyId, customerName, null, invoiceNumber, invoiceDate, null, grandTotal, "PENDING", invoiceType, 0, subTotal, taxAmount, discountAmount);
            viewModel.update(invoice);
            Toast.makeText(this, "تم تحديث الفاتورة بنجاح.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteInvoice() {
        if (invoiceId != null) {
            viewModel.getInvoiceById(invoiceId, companyId).observe(this, invoice -> {
                if (invoice != null) {
                    viewModel.delete(invoice);
                    Toast.makeText(this, "تم حذف الفاتورة بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
