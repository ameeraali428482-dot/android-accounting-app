package com.example.androidapp.ui.invoice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.ui.common.EnhancedBaseActivity;
import com.example.androidapp.ui.invoice.viewmodel.InvoiceViewModel;
import com.example.androidapp.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class InvoiceDetailActivity extends EnhancedBaseActivity {

    // المكونات الأساسية للفاتورة
    private EditText etInvoiceNumber, etInvoiceDate, etInvoiceType, etSubTotal, etTax, etDiscount, etGrandTotal;
    private AutoCompleteTextView etCustomerName;
    private LinearLayout invoiceItemsContainer;
    private Button btnAddItem, btnSave, btnDelete, btnPreview, btnPrint, btnShare, btnReadInvoice;
    private ImageButton btnVoiceSearch;

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

    /**
     * تهيئة المكونات
     */
    private void initializeViews() {
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
        btnPreview = findViewById(R.id.btn_preview_invoice);
        btnPrint = findViewById(R.id.btn_print_invoice);
        btnShare = findViewById(R.id.btn_share_invoice);
        btnReadInvoice = findViewById(R.id.btn_read_invoice);
        btnVoiceSearch = findViewById(R.id.btn_voice_search);

        // المكونات المتقدمة
        tvInvoiceTitle = findViewById(R.id.tv_invoice_title);
        tvTotalInWords = findViewById(R.id.tv_total_in_words);
        layoutCustomerDetails = findViewById(R.id.layout_customer_details);
        layoutCompanyInfo = findViewById(R.id.layout_company_info);
        layoutNotes = findViewById(R.id.layout_notes);

        // تعطيل الحسابات التلقائية
        etSubTotal.setEnabled(false);
        etGrandTotal.setEnabled(false);
    }

    /**
     * إعداد الاقتراحات الذكية
     */
    private void setupSmartSuggestions() {
        // اقتراحات أنواع الفواتير
        String[] invoiceTypes = {"فاتورة مبيعات", "فاتورة مشتريات", "فاتورة خدمات", "فاتورة مرتجعات"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, invoiceTypes);
        etInvoiceType.setAdapter(typeAdapter);

        // اقتراحات أسماء العملاء
        String[] customerNames = {"عميل نقدي", "شركة التقنية المتطورة", "مؤسسة النهضة", "شركة الأماني"};
        ArrayAdapter<String> customerAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, customerNames);
        etCustomerName.setAdapter(customerAdapter);
    }

    /**
     * إعداد مستمعات الأحداث
     */
    private void setupEventListeners() {
        btnAddItem.setOnClickListener(v -> addItemView(null));
        btnSave.setOnClickListener(v -> saveInvoice());
        btnDelete.setOnClickListener(v -> deleteInvoice());
        btnPreview.setOnClickListener(v -> previewInvoice());
        btnPrint.setOnClickListener(v -> printInvoice());
        btnShare.setOnClickListener(v -> shareInvoice());
        btnReadInvoice.setOnClickListener(v -> readInvoiceDetails());
        btnVoiceSearch.setOnClickListener(v -> performVoiceSearch());

        // مستمعات التغيير في الحسابات
        etTax.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateTotals();
        });
        etDiscount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateTotals();
        });
    }

    /**
     * إعداد شريط الأدوات
     */
    private void setupToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * معالجة Intent
     */
    private void handleIntent() {
        invoiceId = getIntent().getStringExtra("invoice_id");
        if (invoiceId != null) {
            setTitle("تعديل فاتورة");
            loadInvoiceDetails(invoiceId);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            setTitle("إضافة فاتورة جديدة");
            etInvoiceDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            btnDelete.setVisibility(View.GONE);
            addItemView(null); // إضافة عنصر فارغ للفاتورة الجديدة
        }
    }

    /**
     * إعداد التخطيط بناءً على الإعدادات
     */
    private void setupLayoutBasedOnSettings() {
        // إظهار/إخفاء تفاصيل العميل
        if (layoutCustomerDetails != null) {
            layoutCustomerDetails.setVisibility(showCustomerDetails ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * تحميل تفاصيل الفاتورة
     */
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

                // تحميل عناصر الفاتورة
                loadInvoiceItems(id);
            } else {
                Toast.makeText(this, "لم يتم العثور على الفاتورة", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * تحميل عناصر الفاتورة
     */
    private void loadInvoiceItems(String invoiceId) {
        viewModel.getInvoiceItems(invoiceId).observe(this, items -> {
            if (items != null) {
                currentItems.clear();
                currentItems.addAll(items);
                invoiceItemsContainer.removeAllViews();
                for (InvoiceItem item : items) {
                    addItemView(item);
                }
                calculateTotals();
            }
        });
    }

    /**
     * إضافة عنصر فاتورة
     */
    private void addItemView(InvoiceItem item) {
        View itemView = getLayoutInflater().inflate(R.layout.invoice_item_row, invoiceItemsContainer, false);
        EditText etItemName = itemView.findViewById(R.id.itemName);
        EditText etItemCode = itemView.findViewById(R.id.itemCode);
        EditText etQuantity = itemView.findViewById(R.id.quantity);
        EditText etUnitPrice = itemView.findViewById(R.id.price);
        EditText etItemTotal = itemView.findViewById(R.id.total);
        View btnRemove = itemView.findViewById(R.id.btnDelete);

        etItemTotal.setEnabled(false);

        if (item != null) {
            etItemName.setText(item.getItemName());
            etItemCode.setText(item.getItemCode());
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

    /**
     * حساب إجمالي العنصر
     */
    private void calculateItemTotal(View itemView) {
        EditText etQuantity = itemView.findViewById(R.id.quantity);
        EditText etUnitPrice = itemView.findViewById(R.id.price);
        EditText etItemTotal = itemView.findViewById(R.id.total);

        String quantityStr = etQuantity.getText().toString().trim();
        String unitPriceStr = etUnitPrice.getText().toString().trim();
        
        if (quantityStr.isEmpty() || unitPriceStr.isEmpty()) {
            etItemTotal.setText("0");
            return;
        }

        try {
            float quantity = Float.parseFloat(quantityStr);
            float unitPrice = Float.parseFloat(unitPriceStr);
            float itemTotal = quantity * unitPrice;
            etItemTotal.setText(String.valueOf(itemTotal));
            calculateTotals();
        } catch (NumberFormatException e) {
            etItemTotal.setText("0");
        }
    }

    /**
     * حساب الإجماليات
     */
    private void calculateTotals() {
        float subTotal = 0.0f;
        for (int i = 0; i < invoiceItemsContainer.getChildCount(); i++) {
            View itemView = invoiceItemsContainer.getChildAt(i);
            EditText etItemTotal = itemView.findViewById(R.id.total);
            String totalStr = etItemTotal.getText().toString().trim();
            if (!totalStr.isEmpty()) {
                try {
                    subTotal += Float.parseFloat(totalStr);
                } catch (NumberFormatException e) {
                    // تجاهل الأخطاء
                }
            }
        }
        etSubTotal.setText(String.valueOf(subTotal));

        float taxAmount = 0;
        String taxStr = etTax.getText().toString().trim();
        if (!taxStr.isEmpty()) {
            try {
                taxAmount = Float.parseFloat(taxStr);
            } catch (NumberFormatException e) {
                // تجاهل الأخطاء
            }
        }

        float discountAmount = 0;
        String discountStr = etDiscount.getText().toString().trim();
        if (!discountStr.isEmpty()) {
            try {
                discountAmount = Float.parseFloat(discountStr);
            } catch (NumberFormatException e) {
                // تجاهل الأخطاء
            }
        }

        float grandTotal = subTotal + taxAmount - discountAmount;
        etGrandTotal.setText(String.valueOf(grandTotal));

        // تحديث المبلغ كتابة
        updateTotalInWords(grandTotal);
    }

    /**
     * تحديث المبلغ كتابة
     */
    private void updateTotalInWords(float amount) {
        // هذه دالة مبسطة - يمكن تطويرها لتحويل الأرقام إلى كلمات
        String inWords = "مبلغ: " + amount + " ريال سعودي";
        if (tvTotalInWords != null) {
            tvTotalInWords.setText(inWords);
        }
    }

    /**
     * حفظ الفاتورة
     */
    private void saveInvoice() {
        String invoiceNumber = etInvoiceNumber.getText().toString().trim();
        String invoiceDate = etInvoiceDate.getText().toString().trim();
        String customerName = etCustomerName.getText().toString().trim();
        String invoiceType = etInvoiceType.getText().toString().trim();
        
        String subTotalStr = etSubTotal.getText().toString().trim();
        String taxStr = etTax.getText().toString().trim();
        String discountStr = etDiscount.getText().toString().trim();
        String grandTotalStr = etGrandTotal.getText().toString().trim();

        if (TextUtils.isEmpty(invoiceNumber) || TextUtils.isEmpty(invoiceDate) || 
            TextUtils.isEmpty(customerName) || TextUtils.isEmpty(invoiceType)) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول الرئيسية", Toast.LENGTH_SHORT).show();
            return;
        }

        float subTotal = 0, taxAmount = 0, discountAmount = 0, grandTotal = 0;
        
        try {
            subTotal = Float.parseFloat(subTotalStr);
            taxAmount = Float.parseFloat(taxStr);
            discountAmount = Float.parseFloat(discountStr);
            grandTotal = Float.parseFloat(grandTotalStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "قيم الأرقام غير صالحة", Toast.LENGTH_SHORT).show();
            return;
        }

        Invoice invoice;
        if (invoiceId == null) {
            invoiceId = UUID.randomUUID().toString();
            invoice = new Invoice(invoiceId, companyId, customerName, null, invoiceNumber, 
                invoiceDate, null, grandTotal, "PENDING", invoiceType, 0, subTotal, taxAmount, discountAmount);
            viewModel.insert(invoice);
            Toast.makeText(this, "تم إضافة الفاتورة بنجاح", Toast.LENGTH_SHORT).show();
        } else {
            invoice = new Invoice(invoiceId, companyId, customerName, null, invoiceNumber, 
                invoiceDate, null, grandTotal, "PENDING", invoiceType, 0, subTotal, taxAmount, discountAmount);
            viewModel.update(invoice);
            Toast.makeText(this, "تم تحديث الفاتورة بنجاح", Toast.LENGTH_SHORT).show();
        }

        // حفظ عناصر الفاتورة
        saveInvoiceItems(invoiceId);
        
        finish();
    }

    /**
     * حفظ عناصر الفاتورة
     */
    private void saveInvoiceItems(String invoiceId) {
        // حذف العناصر القديمة أولاً
        viewModel.deleteInvoiceItems(invoiceId);
        
        // إضافة العناصر الجديدة
        for (int i = 0; i < invoiceItemsContainer.getChildCount(); i++) {
            View itemView = invoiceItemsContainer.getChildAt(i);
            saveInvoiceItem(itemView, invoiceId);
        }
    }

    /**
     * حفظ عنصر فاتورة فردي
     */
    private void saveInvoiceItem(View itemView, String invoiceId) {
        EditText etItemName = itemView.findViewById(R.id.itemName);
        EditText etItemCode = itemView.findViewById(R.id.itemCode);
        EditText etQuantity = itemView.findViewById(R.id.quantity);
        EditText etUnitPrice = itemView.findViewById(R.id.price);
        EditText etItemTotal = itemView.findViewById(R.id.total);

        String itemName = etItemName.getText().toString().trim();
        String itemCode = etItemCode.getText().toString().trim();
        
        if (TextUtils.isEmpty(itemName)) {
            return; // تخطي العناصر الفارغة
        }

        float quantity = 0, unitPrice = 0, total = 0;
        
        try {
            quantity = Float.parseFloat(etQuantity.getText().toString().trim());
            unitPrice = Float.parseFloat(etUnitPrice.getText().toString().trim());
            total = Float.parseFloat(etItemTotal.getText().toString().trim());
        } catch (NumberFormatException e) {
            // تجاهل الأخطاء
        }

        InvoiceItem item = new InvoiceItem(
            UUID.randomUUID().toString(),
            invoiceId,
            itemName,
            itemCode,
            quantity,
            unitPrice,
            total
        );

        viewModel.insertInvoiceItem(item);
    }

    /**
     * حذف الفاتورة
     */
    private void deleteInvoice() {
        if (invoiceId != null) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("حذف الفاتورة")
                .setMessage("هل أنت متأكد من حذف هذه الفاتورة؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    viewModel.getInvoiceById(invoiceId, companyId).observe(this, invoice -> {
                        if (invoice != null) {
                            viewModel.delete(invoice);
                            Toast.makeText(this, "تم حذف الفاتورة بنجاح", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                })
                .setNegativeButton("لا", null)
                .show();
        }
    }

    /**
     * معاينة الفاتورة
     */
    private void previewInvoice() {
        Toast.makeText(this, "معاينة الفاتورة", Toast.LENGTH_SHORT).show();
        // سيتم تطوير هذه الميزة لاحقاً
    }

    /**
     * طباعة الفاتورة
     */
    private void printInvoice() {
        Toast.makeText(this, "طباعة الفاتورة", Toast.LENGTH_SHORT).show();
        // سيتم تطوير هذه الميزة لاحقاً
    }

    /**
     * مشاركة الفاتورة
     */
    private void shareInvoice() {
        Toast.makeText(this, "مشاركة الفاتورة", Toast.LENGTH_SHORT).show();
        // سيتم تطوير هذه الميزة لاحقاً
    }

    /**
     * قراءة تفاصيل الفاتورة
     */
    private void readInvoiceDetails() {
        if (!isTTSEnabled()) {
            Toast.makeText(this, "تحويل النص إلى كلام غير مفعل", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("تفاصيل الفاتورة. ");
        content.append("رقم الفاتورة: ").append(etInvoiceNumber.getText().toString()).append(". ");
        content.append("التاريخ: ").append(etInvoiceDate.getText().toString()).append(". ");
        content.append("العميل: ").append(etCustomerName.getText().toString()).append(". ");
        content.append("النوع: ").append(etInvoiceType.getText().toString()).append(". ");
        content.append("الإجمالي: ").append(etGrandTotal.getText().toString()).append(" ريال. ");

        readDocument("فاتورة", content.toString());
    }

    /**
     * البحث الصوتي
     */
    private void performVoiceSearch() {
        if (!isVoiceInputEnabled()) {
            Toast.makeText(this, "الإدخال الصوتي غير مفعل", Toast.LENGTH_SHORT).show();
            return;
        }

        voiceInputManager.startListening(etCustomerName, new VoiceInputManager.VoiceInputCallback() {
            @Override
            public void onVoiceInputResult(String result) {
                etCustomerName.setText(result);
            }

            @Override
            public void onVoiceInputError(String error) {
                Toast.makeText(InvoiceDetailActivity.this, "خطأ في الإدخال الصوتي: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVoiceInputStarted() {
                if (isTTSEnabled()) {
                    speakText("قل اسم العميل");
                }
            }

            @Override
            public void onVoiceInputStopped() {
                // انتهاء الإدخال الصوتي
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_invoice_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void performSearch(String query) {
        // البحث في الفاتورة - يمكن تنفيذه لاحقاً
        Toast.makeText(this, "البحث في الفاتورة: " + query, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String getAutoReadContent() {
        return "صفحة تفاصيل الفاتورة. يمكنك إضافة أو تعديل الفاتورة وعناصرها";
    }
}
