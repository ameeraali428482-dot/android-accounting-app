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
import com.example.androidapp.utils.VoiceInputManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * نشاط تفاصيل الفاتورة المحسن
 * يحتوي على جميع الميزات الجديدة:
 * - الإدخال الصوتي
 * - تحويل النص إلى كلام
 * - الاقتراحات الذكية
 * - إعدادات مفصلة للفواتير
 */
public class EnhancedInvoiceDetailActivity extends EnhancedBaseActivity {

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
    
    /**
     * تهيئة جميع المكونات
     */
    private void initializeViews() {
        // المكونات الأساسية
        etInvoiceNumber = findViewById(R.id.et_invoice_number);
        etInvoiceDate = findViewById(R.id.et_invoice_date);
        etCustomerName = findViewById(R.id.et_customer_name);
        etInvoiceType = findViewById(R.id.et_invoice_type);
        etSubTotal = findViewById(R.id.et_sub_total);
        etTax = findViewById(R.id.et_tax);
        etDiscount = findViewById(R.id.et_discount);
        etGrandTotal = findViewById(R.id.et_grand_total);
        
        // أزرار العمليات
        btnAddItem = findViewById(R.id.btn_add_item);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        btnPreview = findViewById(R.id.btn_preview);
        btnPrint = findViewById(R.id.btn_print);
        btnShare = findViewById(R.id.btn_share);
        btnReadInvoice = findViewById(R.id.btn_read_invoice);
        btnVoiceSearch = findViewById(R.id.btn_voice_search);
        
        // الحاويات والتخطيطات
        invoiceItemsContainer = findViewById(R.id.invoice_items_container);
        layoutCustomerDetails = findViewById(R.id.layout_customer_details);
        layoutCompanyInfo = findViewById(R.id.layout_company_info);
        layoutNotes = findViewById(R.id.layout_notes);
        
        // النصوص المعلوماتية
        tvInvoiceTitle = findViewById(R.id.tv_invoice_title);
        tvTotalInWords = findViewById(R.id.tv_total_in_words);
        
        // إعداد التاريخ التلقائي
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etInvoiceDate.setText(dateFormat.format(new Date()));
        
        // إنشاء رقم فاتورة تلقائي
        if (invoiceId == null) {
            etInvoiceNumber.setText("INV-" + System.currentTimeMillis());
        }
    }
    
    /**
     * إعداد الاقتراحات الذكية لجميع الحقول
     */
    private void setupSmartSuggestions() {
        if (!isSuggestionsEnabled()) {
            return;
        }
        
        // اقتراحات العملاء
        etCustomerName.setTag("customer");
        suggestionManager.setupSuggestions(etCustomerName, SearchSuggestionManager.SearchType.CUSTOMERS);
        
        // إعداد اقتراحات أخرى للحقول المختلفة
        setupInvoiceTypeSuggestions();
    }
    
    /**
     * إعداد اقتراحات أنواع الفواتير
     */
    private void setupInvoiceTypeSuggestions() {
        String[] invoiceTypes = {"فاتورة مبيعات", "فاتورة مشتريات", "فاتورة خدمات", "فاتورة إرجاع", "عرض سعر", "أمر شراء"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
            this, android.R.layout.simple_dropdown_item_1line, invoiceTypes);
        
        if (etInvoiceType instanceof AutoCompleteTextView) {
            ((AutoCompleteTextView) etInvoiceType).setAdapter(adapter);
            ((AutoCompleteTextView) etInvoiceType).setThreshold(1);
        }
    }
    
    /**
     * إعداد مستمعات الأحداث
     */
    private void setupEventListeners() {
        // زر إضافة صنف
        btnAddItem.setOnClickListener(v -> addNewInvoiceItem());
        
        // زر الحفظ
        btnSave.setOnClickListener(v -> saveInvoice());
        
        // زر الحذف
        btnDelete.setOnClickListener(v -> deleteInvoice());
        
        // زر المعاينة
        btnPreview.setOnClickListener(v -> previewInvoice());
        
        // زر الطباعة
        btnPrint.setOnClickListener(v -> printInvoice());
        
        // زر المشاركة
        btnShare.setOnClickListener(v -> shareInvoice());
        
        // زر قراءة الفاتورة
        btnReadInvoice.setOnClickListener(v -> readInvoiceContent());
        
        // زر البحث الصوتي
        btnVoiceSearch.setOnClickListener(v -> performVoiceSearch());
        
        // حساب الإجمالي تلقائياً عند تغيير الحقول
        setupAutoCalculation();
    }
    
    /**
     * إعداد الحساب التلقائي للضرائب والإجمالي
     */
    private void setupAutoCalculation() {
        if (!autoCalculateTax) {
            return;
        }
        
        // مستمع لحقل المجموع الفرعي
        etSubTotal.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                calculateTotals();
            }
        });
        
        // مستمع لحقل الخصم
        etDiscount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                calculateTotals();
            }
        });
    }
    
    /**
     * حساب الإجماليات تلقائياً
     */
    private void calculateTotals() {
        try {
            double subTotal = Double.parseDouble(etSubTotal.getText().toString().trim());
            double discount = etDiscount.getText().toString().trim().isEmpty() ? 0 : 
                Double.parseDouble(etDiscount.getText().toString().trim());
            
            // حساب المبلغ بعد الخصم
            double afterDiscount = subTotal - discount;
            
            // حساب الضريبة (15% افتراضياً)
            double taxRate = 0.15;
            double taxAmount = afterDiscount * taxRate;
            etTax.setText(String.format(Locale.getDefault(), "%.2f", taxAmount));
            
            // حساب الإجمالي
            double grandTotal = afterDiscount + taxAmount;
            etGrandTotal.setText(String.format(Locale.getDefault(), "%.2f", grandTotal));
            
            // تحديث المبلغ بالكلمات
            updateTotalInWords(grandTotal);
            
        } catch (NumberFormatException e) {
            // في حالة وجود خطأ في التحويل
        }
    }
    
    /**
     * تحديث المبلغ بالكلمات
     */
    private void updateTotalInWords(double amount) {
        if (tvTotalInWords != null) {
            String amountInWords = convertNumberToArabicWords(amount);
            tvTotalInWords.setText(amountInWords + " ريال سعودي");
        }
    }
    
    /**
     * تحويل الرقم إلى كلمات عربية (تطبيق مبسط)
     */
    private String convertNumberToArabicWords(double amount) {
        // هذه طريقة مبسطة - يمكن تطويرها لتكون أكثر دقة
        long integerPart = (long) amount;
        
        String[] ones = {"", "واحد", "اثنان", "ثلاثة", "أربعة", "خمسة", "ستة", "سبعة", "ثمانية", "تسعة"};
        String[] teens = {"عشرة", "أحد عشر", "اثنا عشر", "ثلاثة عشر", "أربعة عشر", "خمسة عشر", "ستة عشر", "سبعة عشر", "ثمانية عشر", "تسعة عشر"};
        String[] tens = {"", "", "عشرون", "ثلاثون", "أربعون", "خمسون", "ستون", "سبعون", "ثمانون", "تسعون"};
        String[] hundreds = {"", "مائة", "مائتان", "ثلاثمائة", "أربعمائة", "خمسمائة", "ستمائة", "سبعمائة", "ثمانمائة", "تسعمائة"};
        
        if (integerPart == 0) {
            return "صفر";
        }
        
        if (integerPart < 10) {
            return ones[(int) integerPart];
        } else if (integerPart < 20) {
            return teens[(int) integerPart - 10];
        } else if (integerPart < 100) {
            int tensDigit = (int) (integerPart / 10);
            int onesDigit = (int) (integerPart % 10);
            return tens[tensDigit] + (onesDigit > 0 ? " " + ones[onesDigit] : "");
        } else if (integerPart < 1000) {
            int hundredsDigit = (int) (integerPart / 100);
            int remainder = (int) (integerPart % 100);
            String result = hundreds[hundredsDigit];
            if (remainder > 0) {
                result += " " + convertNumberToArabicWords(remainder);
            }
            return result;
        } else {
            return integerPart + ""; // رقم كبير - يعرض كرقم
        }
    }
    
    /**
     * إضافة صنف جديد للفاتورة
     */
    private void addNewInvoiceItem() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemView = inflater.inflate(R.layout.invoice_item_row_enhanced, invoiceItemsContainer, false);
        
        // إعداد مكونات الصنف
        AutoCompleteTextView etItemName = itemView.findViewById(R.id.et_item_name);
        EditText etItemCode = itemView.findViewById(R.id.et_item_code);
        EditText etQuantity = itemView.findViewById(R.id.et_quantity);
        EditText etUnitPrice = itemView.findViewById(R.id.et_unit_price);
        EditText etTotalPrice = itemView.findViewById(R.id.et_total_price);
        Button btnRemoveItem = itemView.findViewById(R.id.btn_remove_item);
        
        // إعداد اقتراحات الأصناف
        etItemName.setTag("item");
        suggestionManager.setupItemSuggestionsForInvoice(etItemName);
        
        // إخفاء كود الصنف إذا كان معطلاً في الإعدادات
        if (!showItemCodes) {
            etItemCode.setVisibility(View.GONE);
        }
        
        // حساب السعر الإجمالي تلقائياً
        setupItemCalculation(etQuantity, etUnitPrice, etTotalPrice);
        
        // زر إزالة الصنف
        btnRemoveItem.setOnClickListener(v -> {
            invoiceItemsContainer.removeView(itemView);
            recalculateInvoiceTotal();
        });
        
        // إضافة الصنف للحاوية
        invoiceItemsContainer.addView(itemView);
        
        // قراءة إرشادات للمستخدم
        if (isAutoReadEnabled()) {
            speakText("تم إضافة صنف جديد. يمكنك إدخال اسم الصنف والكمية والسعر");
        }
    }
    
    /**
     * إعداد حساب السعر التلقائي للصنف
     */
    private void setupItemCalculation(EditText etQuantity, EditText etUnitPrice, EditText etTotalPrice) {
        android.text.TextWatcher calculationWatcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(android.text.Editable s) {
                calculateItemTotal(etQuantity, etUnitPrice, etTotalPrice);
            }
        };
        
        etQuantity.addTextChangedListener(calculationWatcher);
        etUnitPrice.addTextChangedListener(calculationWatcher);
    }
    
    /**
     * حساب إجمالي الصنف
     */
    private void calculateItemTotal(EditText etQuantity, EditText etUnitPrice, EditText etTotalPrice) {
        try {
            String quantityStr = etQuantity.getText().toString().trim();
            String unitPriceStr = etUnitPrice.getText().toString().trim();
            
            if (!quantityStr.isEmpty() && !unitPriceStr.isEmpty()) {
                double quantity = Double.parseDouble(quantityStr);
                double unitPrice = Double.parseDouble(unitPriceStr);
                double total = quantity * unitPrice;
                
                etTotalPrice.setText(String.format(Locale.getDefault(), "%.2f", total));
                recalculateInvoiceTotal();
            }
        } catch (NumberFormatException e) {
            // خطأ في التحويل - لا نفعل شيئاً
        }
    }
    
    /**
     * إعادة حساب إجمالي الفاتورة
     */
    private void recalculateInvoiceTotal() {
        double subTotal = 0.0;
        
        for (int i = 0; i < invoiceItemsContainer.getChildCount(); i++) {
            View itemView = invoiceItemsContainer.getChildAt(i);
            EditText etTotalPrice = itemView.findViewById(R.id.et_total_price);
            
            try {
                String totalStr = etTotalPrice.getText().toString().trim();
                if (!totalStr.isEmpty()) {
                    subTotal += Double.parseDouble(totalStr);
                }
            } catch (NumberFormatException e) {
                // تجاهل الأخطاء
            }
        }
        
        etSubTotal.setText(String.format(Locale.getDefault(), "%.2f", subTotal));
        calculateTotals();
    }
    
    /**
     * البحث الصوتي في الفواتير
     */
    private void performVoiceSearch() {
        if (!isVoiceInputEnabled()) {
            Toast.makeText(this, "الإدخال الصوتي غير مفعل", Toast.LENGTH_SHORT).show();
            return;
        }
        
        voiceInputManager.startListening(etCustomerName, new VoiceInputManager.VoiceInputCallback() {
            @Override
            public void onVoiceInputResult(String result) {
                // البحث عن العميل في قاعدة البيانات
                searchCustomerByVoice(result);
            }
            
            @Override
            public void onVoiceInputError(String error) {
                Toast.makeText(EnhancedInvoiceDetailActivity.this, "خطأ في البحث الصوتي: " + error, Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onVoiceInputStarted() {
                btnVoiceSearch.setImageResource(android.R.drawable.ic_media_pause);
            }
            
            @Override
            public void onVoiceInputStopped() {
                btnVoiceSearch.setImageResource(android.R.drawable.ic_btn_speak_now);
            }
        });
    }
    
    /**
     * البحث عن العميل بالصوت
     */
    private void searchCustomerByVoice(String customerName) {
        suggestionManager.performAdvancedSearch(customerName, 
            SearchSuggestionManager.SearchType.CUSTOMERS, 
            results -> {
                if (!results.isEmpty()) {
                    etCustomerName.setText(results.get(0));
                    if (isTTSEnabled()) {
                        speakText("تم العثور على العميل: " + results.get(0));
                    }
                } else {
                    if (isTTSEnabled()) {
                        speakText("لم يتم العثور على عميل بهذا الاسم");
                    }
                }
            });
    }
    
    /**
     * قراءة محتوى الفاتورة
     */
    private void readInvoiceContent() {
        if (!isTTSEnabled()) {
            Toast.makeText(this, "تحويل النص إلى كلام غير مفعل", Toast.LENGTH_SHORT).show();
            return;
        }
        
        StringBuilder content = new StringBuilder();
        content.append("فاتورة رقم ").append(etInvoiceNumber.getText().toString());
        content.append(". العميل: ").append(etCustomerName.getText().toString());
        content.append(". التاريخ: ").append(etInvoiceDate.getText().toString());
        content.append(". المبلغ الإجمالي: ").append(etGrandTotal.getText().toString()).append(" ريال");
        
        // إضافة تفاصيل الأصناف
        int itemCount = invoiceItemsContainer.getChildCount();
        if (itemCount > 0) {
            content.append(". الأصناف: ");
            for (int i = 0; i < itemCount; i++) {
                View itemView = invoiceItemsContainer.getChildAt(i);
                AutoCompleteTextView etItemName = itemView.findViewById(R.id.et_item_name);
                EditText etQuantity = itemView.findViewById(R.id.et_quantity);
                
                if (etItemName != null && etQuantity != null) {
                    content.append(etItemName.getText().toString())
                           .append(" كمية ").append(etQuantity.getText().toString())
                           .append(". ");
                }
            }
        }
        
        readDocument("فاتورة", content.toString());
    }
    
    /**
     * حفظ الفاتورة
     */
    private void saveInvoice() {
        if (validateInvoiceData()) {
            Invoice invoice = buildInvoiceFromForm();
            
            // حفظ الفاتورة في قاعدة البيانات
            viewModel.saveInvoice(invoice).observe(this, result -> {
                if (result != null) {
                    Toast.makeText(this, "تم حفظ الفاتورة بنجاح", Toast.LENGTH_SHORT).show();
                    
                    // قراءة تأكيد الحفظ
                    if (isTTSEnabled()) {
                        speakText("تم حفظ الفاتورة رقم " + etInvoiceNumber.getText().toString() + " بنجاح");
                    }
                    
                    // طباعة تلقائية إذا كانت مفعلة
                    if (invoiceSettings.getBoolean("print_after_save", false)) {
                        printInvoice();
                    }
                    
                    finish();
                } else {
                    Toast.makeText(this, "فشل في حفظ الفاتورة", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    /**
     * التحقق من صحة بيانات الفاتورة
     */
    private boolean validateInvoiceData() {
        if (etCustomerName.getText().toString().trim().isEmpty()) {
            etCustomerName.setError("اسم العميل مطلوب");
            etCustomerName.requestFocus();
            return false;
        }
        
        if (invoiceItemsContainer.getChildCount() == 0) {
            Toast.makeText(this, "يجب إضافة صنف واحد على الأقل", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    /**
     * بناء كائن الفاتورة من البيانات المدخلة
     */
    private Invoice buildInvoiceFromForm() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(etInvoiceNumber.getText().toString());
        invoice.setCustomerName(etCustomerName.getText().toString());
        invoice.setInvoiceDate(etInvoiceDate.getText().toString());
        invoice.setInvoiceType(etInvoiceType.getText().toString());
        
        try {
            invoice.setSubTotal(Double.parseDouble(etSubTotal.getText().toString()));
            invoice.setTax(Double.parseDouble(etTax.getText().toString()));
            invoice.setDiscount(Double.parseDouble(etDiscount.getText().toString()));
            invoice.setGrandTotal(Double.parseDouble(etGrandTotal.getText().toString()));
        } catch (NumberFormatException e) {
            // قيم افتراضية في حالة الخطأ
            invoice.setSubTotal(0.0);
            invoice.setTax(0.0);
            invoice.setDiscount(0.0);
            invoice.setGrandTotal(0.0);
        }
        
        invoice.setCompanyId(companyId);
        invoice.setCreatedAt(new Date());
        
        return invoice;
    }
    
    /**
     * حذف الفاتورة
     */
    private void deleteInvoice() {
        if (invoiceId != null) {
            // عرض حوار تأكيد
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("تأكيد الحذف")
                .setMessage("هل أنت متأكد من حذف هذه الفاتورة؟")
                .setPositiveButton("حذف", (dialog, which) -> {
                    viewModel.deleteInvoice(invoiceId).observe(this, result -> {
                        if (result) {
                            Toast.makeText(this, "تم حذف الفاتورة", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "فشل في حذف الفاتورة", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("إلغاء", null)
                .show();
        } else {
            Toast.makeText(this, "لا يمكن حذف فاتورة غير محفوظة", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * معاينة الفاتورة
     */
    private void previewInvoice() {
        Toast.makeText(this, "سيتم تطوير معاينة الفاتورة قريباً", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * طباعة الفاتورة
     */
    private void printInvoice() {
        Toast.makeText(this, "سيتم تطوير طباعة الفاتورة قريباً", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * مشاركة الفاتورة
     */
    private void shareInvoice() {
        Toast.makeText(this, "سيتم تطوير مشاركة الفاتورة قريباً", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * إعداد شريط الأدوات
     */
    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("تفاصيل الفاتورة");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    /**
     * معالجة Intent (للتحرير أو الإنشاء الجديد)
     */
    private void handleIntent() {
        invoiceId = getIntent().getStringExtra("invoice_id");
        if (invoiceId != null) {
            // تحميل بيانات الفاتورة للتحرير
            loadInvoiceData(invoiceId);
        } else {
            // فاتورة جديدة
            if (btnDelete != null) {
                btnDelete.setVisibility(View.GONE);
            }
        }
    }
    
    /**
     * تحميل بيانات الفاتورة للتحرير
     */
    private void loadInvoiceData(String invoiceId) {
        viewModel.getInvoiceById(invoiceId).observe(this, invoice -> {
            if (invoice != null) {
                populateFormWithInvoiceData(invoice);
            }
        });
    }
    
    /**
     * ملء النموذج ببيانات الفاتورة
     */
    private void populateFormWithInvoiceData(Invoice invoice) {
        etInvoiceNumber.setText(invoice.getInvoiceNumber());
        etCustomerName.setText(invoice.getCustomerName());
        etInvoiceDate.setText(invoice.getInvoiceDate());
        etInvoiceType.setText(invoice.getInvoiceType());
        etSubTotal.setText(String.valueOf(invoice.getSubTotal()));
        etTax.setText(String.valueOf(invoice.getTax()));
        etDiscount.setText(String.valueOf(invoice.getDiscount()));
        etGrandTotal.setText(String.valueOf(invoice.getGrandTotal()));
    }
    
    /**
     * إعداد التخطيط بناءً على الإعدادات
     */
    private void setupLayoutBasedOnSettings() {
        // إخفاء/إظهار تفاصيل العميل
        if (layoutCustomerDetails != null) {
            layoutCustomerDetails.setVisibility(showCustomerDetails ? View.VISIBLE : View.GONE);
        }
        
        // إخفاء/إظهار الضرائب
        if (!showTaxes) {
            etTax.setVisibility(View.GONE);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_invoice_detail, menu);
        
        // إعداد البحث في شريط الأدوات
        setupSearchView(menu, R.id.action_search, new OnSearchListener() {
            @Override
            public void onSearch(String query) {
                // البحث في البيانات
                performSearch(query);
            }
            
            @Override
            public void onSearchTextChanged(String query) {
                // البحث المباشر أثناء الكتابة
                if (query.length() > 2) {
                    performSearch(query);
                }
            }
        });
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            // فتح إعدادات الفواتير
            openInvoiceSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * فتح إعدادات الفواتير
     */
    private void openInvoiceSettings() {
        // سيتم إنشاء نشاط منفصل لإعدادات الفواتير
        Toast.makeText(this, "إعدادات الفاتورة", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void performSearch(String query) {
        // البحث في العملاء والأصناف
        suggestionManager.performAdvancedSearch(query, 
            SearchSuggestionManager.SearchType.ALL, 
            results -> {
                // عرض النتائج
                if (!results.isEmpty()) {
                    Toast.makeText(this, "تم العثور على " + results.size() + " نتائج", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    @Override
    protected String getAutoReadContent() {
        return "صفحة تفاصيل الفاتورة. يمكنك إدخال بيانات العميل والأصناف";
    }
}