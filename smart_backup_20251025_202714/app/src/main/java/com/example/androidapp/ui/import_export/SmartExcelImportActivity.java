package com.example.androidapp.ui.import_export;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.utils.ExcelImportHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * نشاط استيراد البيانات الذكي من Excel
 * Smart Excel Data Import Activity
 */
public class SmartExcelImportActivity extends AppCompatActivity {
    
    private static final String TAG = "SmartExcelImport";
    private static final int PICK_EXCEL_FILE = 1001;
    
    // UI Components
    private Spinner spinnerImportType;
    private MaterialButton buttonSelectFile;
    private MaterialButton buttonStartMapping;
    private MaterialButton buttonImportData;
    private TextView textViewFileName;
    private TextView textViewFileInfo;
    private ProgressBar progressBarImport;
    private RecyclerView recyclerViewMapping;
    private LinearLayout layoutMappingSection;
    private MaterialCardView cardPreview;
    private TextView textViewPreview;
    
    // Data
    private String selectedImportType;
    private Uri selectedFileUri;
    private ExcelImportHelper importHelper;
    private ColumnMappingAdapter mappingAdapter;
    private List<ExcelColumn> excelColumns = new ArrayList<>();
    private List<DatabaseField> databaseFields = new ArrayList<>();
    private List<ColumnMapping> columnMappings = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_excel_import);
        
        initViews();
        setupImportHelper();
        setupImportTypes();
        setupListeners();
    }
    
    private void initViews() {
        spinnerImportType = findViewById(R.id.spinnerImportType);
        buttonSelectFile = findViewById(R.id.buttonSelectFile);
        buttonStartMapping = findViewById(R.id.buttonStartMapping);
        buttonImportData = findViewById(R.id.buttonImportData);
        textViewFileName = findViewById(R.id.textViewFileName);
        textViewFileInfo = findViewById(R.id.textViewFileInfo);
        progressBarImport = findViewById(R.id.progressBarImport);
        recyclerViewMapping = findViewById(R.id.recyclerViewMapping);
        layoutMappingSection = findViewById(R.id.layoutMappingSection);
        cardPreview = findViewById(R.id.cardPreview);
        textViewPreview = findViewById(R.id.textViewPreview);
        
        // إعداد toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("استيراد البيانات الذكي");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // إخفاء العناصر في البداية
        layoutMappingSection.setVisibility(View.GONE);
        buttonStartMapping.setEnabled(false);
        buttonImportData.setEnabled(false);
    }
    
    private void setupImportHelper() {
        importHelper = new ExcelImportHelper(this);
    }
    
    private void setupImportTypes() {
        String[] importTypes = {
            "اختر نوع البيانات...",
            "العملاء (Customers)",
            "الموردين (Suppliers)", 
            "المنتجات/الأصناف (Products)",
            "الحسابات (Accounts)",
            "المعاملات المالية (Transactions)",
            "الفواتير (Invoices)",
            "المدفوعات (Payments)",
            "الموظفين (Employees)"
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, importTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImportType.setAdapter(adapter);
    }
    
    private void setupListeners() {
        // اختيار نوع الاستيراد
        spinnerImportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedImportType = getImportTypeKey(position);
                    setupDatabaseFields(selectedImportType);
                    buttonSelectFile.setEnabled(true);
                } else {
                    selectedImportType = null;
                    buttonSelectFile.setEnabled(false);
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // اختيار ملف
        buttonSelectFile.setOnClickListener(v -> selectExcelFile());
        
        // بدء المحاذاة
        buttonStartMapping.setOnClickListener(v -> startColumnMapping());
        
        // بدء الاستيراد
        buttonImportData.setOnClickListener(v -> confirmAndImportData());
    }
    
    private String getImportTypeKey(int position) {
        switch (position) {
            case 1: return "CUSTOMERS";
            case 2: return "SUPPLIERS";
            case 3: return "PRODUCTS";
            case 4: return "ACCOUNTS";
            case 5: return "TRANSACTIONS";
            case 6: return "INVOICES";
            case 7: return "PAYMENTS";
            case 8: return "EMPLOYEES";
            default: return null;
        }
    }
    
    private void setupDatabaseFields(String importType) {
        databaseFields.clear();
        
        switch (importType) {
            case "CUSTOMERS":
                databaseFields.add(new DatabaseField("name", "الاسم", true, "TEXT"));
                databaseFields.add(new DatabaseField("phone", "رقم الهاتف", false, "TEXT"));
                databaseFields.add(new DatabaseField("email", "البريد الإلكتروني", false, "TEXT"));
                databaseFields.add(new DatabaseField("address", "العنوان", false, "TEXT"));
                databaseFields.add(new DatabaseField("city", "المدينة", false, "TEXT"));
                databaseFields.add(new DatabaseField("country", "البلد", false, "TEXT"));
                databaseFields.add(new DatabaseField("credit_limit", "حد الائتمان", false, "DECIMAL"));
                databaseFields.add(new DatabaseField("debit_balance", "الرصيد المدين", false, "DECIMAL"));
                databaseFields.add(new DatabaseField("credit_balance", "الرصيد الدائن", false, "DECIMAL"));
                break;
                
            case "SUPPLIERS":
                databaseFields.add(new DatabaseField("name", "الاسم", true, "TEXT"));
                databaseFields.add(new DatabaseField("phone", "رقم الهاتف", false, "TEXT"));
                databaseFields.add(new DatabaseField("email", "البريد الإلكتروني", false, "TEXT"));
                databaseFields.add(new DatabaseField("address", "العنوان", false, "TEXT"));
                databaseFields.add(new DatabaseField("contact_person", "الشخص المسؤول", false, "TEXT"));
                databaseFields.add(new DatabaseField("tax_number", "الرقم الضريبي", false, "TEXT"));
                break;
                
            case "PRODUCTS":
                databaseFields.add(new DatabaseField("name", "اسم المنتج", true, "TEXT"));
                databaseFields.add(new DatabaseField("sku", "رمز المنتج", false, "TEXT"));
                databaseFields.add(new DatabaseField("barcode", "الباركود", false, "TEXT"));
                databaseFields.add(new DatabaseField("category", "الفئة", false, "TEXT"));
                databaseFields.add(new DatabaseField("price", "السعر", false, "DECIMAL"));
                databaseFields.add(new DatabaseField("cost", "التكلفة", false, "DECIMAL"));
                databaseFields.add(new DatabaseField("quantity", "الكمية", false, "INTEGER"));
                databaseFields.add(new DatabaseField("unit", "الوحدة", false, "TEXT"));
                databaseFields.add(new DatabaseField("description", "الوصف", false, "TEXT"));
                break;
                
            case "ACCOUNTS":
                databaseFields.add(new DatabaseField("account_number", "رقم الحساب", true, "TEXT"));
                databaseFields.add(new DatabaseField("name", "اسم الحساب", true, "TEXT"));
                databaseFields.add(new DatabaseField("type", "نوع الحساب", true, "TEXT"));
                databaseFields.add(new DatabaseField("parent_account", "الحساب الرئيسي", false, "TEXT"));
                databaseFields.add(new DatabaseField("balance", "الرصيد", false, "DECIMAL"));
                databaseFields.add(new DatabaseField("description", "الوصف", false, "TEXT"));
                break;
                
            case "TRANSACTIONS":
                databaseFields.add(new DatabaseField("transaction_number", "رقم المعاملة", true, "TEXT"));
                databaseFields.add(new DatabaseField("date", "التاريخ", true, "DATE"));
                databaseFields.add(new DatabaseField("account_debit", "الحساب المدين", true, "TEXT"));
                databaseFields.add(new DatabaseField("account_credit", "الحساب الدائن", true, "TEXT"));
                databaseFields.add(new DatabaseField("amount", "المبلغ", true, "DECIMAL"));
                databaseFields.add(new DatabaseField("description", "البيان", false, "TEXT"));
                databaseFields.add(new DatabaseField("reference", "المرجع", false, "TEXT"));
                break;
                
            case "INVOICES":
                databaseFields.add(new DatabaseField("invoice_number", "رقم الفاتورة", true, "TEXT"));
                databaseFields.add(new DatabaseField("date", "التاريخ", true, "DATE"));
                databaseFields.add(new DatabaseField("customer_name", "العميل", true, "TEXT"));
                databaseFields.add(new DatabaseField("total_amount", "المبلغ الإجمالي", true, "DECIMAL"));
                databaseFields.add(new DatabaseField("tax_amount", "مبلغ الضريبة", false, "DECIMAL"));
                databaseFields.add(new DatabaseField("discount", "الخصم", false, "DECIMAL"));
                databaseFields.add(new DatabaseField("status", "الحالة", false, "TEXT"));
                break;
                
            case "PAYMENTS":
                databaseFields.add(new DatabaseField("payment_number", "رقم الدفعة", true, "TEXT"));
                databaseFields.add(new DatabaseField("date", "التاريخ", true, "DATE"));
                databaseFields.add(new DatabaseField("customer_supplier", "العميل/المورد", true, "TEXT"));
                databaseFields.add(new DatabaseField("amount", "المبلغ", true, "DECIMAL"));
                databaseFields.add(new DatabaseField("payment_method", "طريقة الدفع", false, "TEXT"));
                databaseFields.add(new DatabaseField("reference", "المرجع", false, "TEXT"));
                break;
                
            case "EMPLOYEES":
                databaseFields.add(new DatabaseField("employee_id", "رقم الموظف", true, "TEXT"));
                databaseFields.add(new DatabaseField("name", "الاسم", true, "TEXT"));
                databaseFields.add(new DatabaseField("phone", "رقم الهاتف", false, "TEXT"));
                databaseFields.add(new DatabaseField("email", "البريد الإلكتروني", false, "TEXT"));
                databaseFields.add(new DatabaseField("position", "المنصب", false, "TEXT"));
                databaseFields.add(new DatabaseField("salary", "الراتب", false, "DECIMAL"));
                databaseFields.add(new DatabaseField("hire_date", "تاريخ التوظيف", false, "DATE"));
                break;
        }
    }
    
    private void selectExcelFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.ms-excel");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        });
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        
        try {
            startActivityForResult(
                Intent.createChooser(intent, "اختر ملف Excel"),
                PICK_EXCEL_FILE
            );
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "لا يوجد تطبيق لاختيار الملفات", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_EXCEL_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                    displayFileInfo();
                    analyzeExcelFile();
                }
            }
        }
    }
    
    private void displayFileInfo() {
        String fileName = importHelper.getFileName(selectedFileUri);
        textViewFileName.setText(fileName);
        
        // عرض معلومات إضافية عن الملف
        long fileSize = importHelper.getFileSize(selectedFileUri);
        String fileSizeFormatted = android.text.format.Formatter.formatFileSize(this, fileSize);
        textViewFileInfo.setText(String.format("حجم الملف: %s", fileSizeFormatted));
        textViewFileInfo.setVisibility(View.VISIBLE);
    }
    
    private void analyzeExcelFile() {
        progressBarImport.setVisibility(View.VISIBLE);
        
        // تحليل الملف في خيط منفصل
        new Thread(() -> {
            try {
                ExcelAnalysisResult result = importHelper.analyzeExcelFile(selectedFileUri);
                
                runOnUiThread(() -> {
                    progressBarImport.setVisibility(View.GONE);
                    
                    if (result.isSuccess()) {
                        excelColumns.clear();
                        excelColumns.addAll(result.getColumns());
                        
                        // عرض معلومات الملف
                        String info = String.format(
                            "عدد الأوراق: %d\nعدد الأعمدة: %d\nعدد الصفوف: %d",
                            result.getSheetCount(),
                            result.getColumnCount(),
                            result.getRowCount()
                        );
                        textViewFileInfo.setText(info);
                        
                        // عرض معاينة البيانات
                        showDataPreview(result.getPreviewData());
                        
                        buttonStartMapping.setEnabled(true);
                        
                    } else {
                        Toast.makeText(this, "خطأ في تحليل الملف: " + result.getError(), 
                            Toast.LENGTH_LONG).show();
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error analyzing Excel file", e);
                runOnUiThread(() -> {
                    progressBarImport.setVisibility(View.GONE);
                    Toast.makeText(this, "خطأ في قراءة الملف", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private void showDataPreview(List<List<String>> previewData) {
        if (previewData != null && !previewData.isEmpty()) {
            StringBuilder preview = new StringBuilder("معاينة البيانات:\n\n");
            
            int maxRows = Math.min(5, previewData.size());
            for (int i = 0; i < maxRows; i++) {
                List<String> row = previewData.get(i);
                preview.append("الصف ").append(i + 1).append(": ");
                
                int maxCols = Math.min(4, row.size());
                for (int j = 0; j < maxCols; j++) {
                    preview.append(row.get(j)).append(" | ");
                }
                
                if (row.size() > 4) {
                    preview.append("...");
                }
                preview.append("\n");
            }
            
            if (previewData.size() > 5) {
                preview.append("...\n");
            }
            
            textViewPreview.setText(preview.toString());
            cardPreview.setVisibility(View.VISIBLE);
        }
    }
    
    private void startColumnMapping() {
        if (excelColumns.isEmpty() || databaseFields.isEmpty()) {
            Toast.makeText(this, "لا توجد بيانات للمحاذاة", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // إعداد قائمة المحاذاة
        columnMappings.clear();
        for (DatabaseField dbField : databaseFields) {
            ColumnMapping mapping = new ColumnMapping(dbField, null);
            
            // محاولة المحاذاة التلقائية
            ExcelColumn bestMatch = findBestColumnMatch(dbField);
            if (bestMatch != null) {
                mapping.setExcelColumn(bestMatch);
            }
            
            columnMappings.add(mapping);
        }
        
        // إعداد RecyclerView
        mappingAdapter = new ColumnMappingAdapter(columnMappings, excelColumns, this::onMappingChanged);
        recyclerViewMapping.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMapping.setAdapter(mappingAdapter);
        
        layoutMappingSection.setVisibility(View.VISIBLE);
        
        // التحقق من إمكانية بدء الاستيراد
        checkMappingValidity();
    }
    
    private ExcelColumn findBestColumnMatch(DatabaseField dbField) {
        String fieldNameLower = dbField.getDisplayName().toLowerCase();
        String fieldKeyLower = dbField.getKey().toLowerCase();
        
        ExcelColumn bestMatch = null;
        int highestScore = 0;
        
        for (ExcelColumn excelColumn : excelColumns) {
            String columnNameLower = excelColumn.getName().toLowerCase();
            int score = 0;
            
            // التطابق المباشر
            if (columnNameLower.contains(fieldNameLower) || fieldNameLower.contains(columnNameLower)) {
                score += 10;
            }
            
            // التطابق بالمفتاح الإنجليزي
            if (columnNameLower.contains(fieldKeyLower) || fieldKeyLower.contains(columnNameLower)) {
                score += 8;
            }
            
            // كلمات مفتاحية مشتركة
            if (hasCommonKeywords(fieldNameLower, columnNameLower)) {
                score += 5;
            }
            
            if (score > highestScore) {
                highestScore = score;
                bestMatch = excelColumn;
            }
        }
        
        return highestScore >= 5 ? bestMatch : null;
    }
    
    private boolean hasCommonKeywords(String field, String column) {
        String[] fieldWords = field.split("\\s+");
        String[] columnWords = column.split("\\s+");
        
        for (String fieldWord : fieldWords) {
            for (String columnWord : columnWords) {
                if (fieldWord.length() > 2 && columnWord.length() > 2 && 
                    (fieldWord.contains(columnWord) || columnWord.contains(fieldWord))) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void onMappingChanged() {
        checkMappingValidity();
    }
    
    private void checkMappingValidity() {
        boolean hasRequiredMappings = true;
        
        for (ColumnMapping mapping : columnMappings) {
            if (mapping.getDatabaseField().isRequired() && mapping.getExcelColumn() == null) {
                hasRequiredMappings = false;
                break;
            }
        }
        
        buttonImportData.setEnabled(hasRequiredMappings);
    }
    
    private void confirmAndImportData() {
        // إظهار تأكيد الاستيراد
        new MaterialAlertDialogBuilder(this)
            .setTitle("تأكيد الاستيراد")
            .setMessage("هل أنت متأكد من استيراد البيانات؟\n\nسيتم إضافة البيانات إلى قاعدة البيانات الحالية.")
            .setPositiveButton("استيراد", (dialog, which) -> startDataImport())
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    private void startDataImport() {
        progressBarImport.setVisibility(View.VISIBLE);
        buttonImportData.setEnabled(false);
        
        // بدء عملية الاستيراد في خيط منفصل
        new Thread(() -> {
            try {
                ImportResult result = importHelper.importData(
                    selectedFileUri,
                    selectedImportType,
                    columnMappings
                );
                
                runOnUiThread(() -> {
                    progressBarImport.setVisibility(View.GONE);
                    buttonImportData.setEnabled(true);
                    
                    if (result.isSuccess()) {
                        showImportSuccessDialog(result);
                    } else {
                        showImportErrorDialog(result);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error importing data", e);
                runOnUiThread(() -> {
                    progressBarImport.setVisibility(View.GONE);
                    buttonImportData.setEnabled(true);
                    Toast.makeText(this, "خطأ في استيراد البيانات", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private void showImportSuccessDialog(ImportResult result) {
        String message = String.format(
            "تم الاستيراد بنجاح!\n\n" +
            "عدد السجلات المستوردة: %d\n" +
            "عدد السجلات المتجاهلة: %d\n" +
            "الوقت المستغرق: %.2f ثانية",
            result.getSuccessCount(),
            result.getSkippedCount(),
            result.getDurationSeconds()
        );
        
        new MaterialAlertDialogBuilder(this)
            .setTitle("نجح الاستيراد")
            .setMessage(message)
            .setPositiveButton("موافق", (dialog, which) -> finish())
            .setNeutralButton("عرض التفاصيل", (dialog, which) -> showImportDetails(result))
            .show();
    }
    
    private void showImportErrorDialog(ImportResult result) {
        String message = String.format(
            "فشل الاستيراد!\n\n" +
            "الخطأ: %s\n" +
            "عدد السجلات المعالجة: %d\n" +
            "عدد السجلات الناجحة: %d",
            result.getError(),
            result.getProcessedCount(),
            result.getSuccessCount()
        );
        
        new MaterialAlertDialogBuilder(this)
            .setTitle("خطأ في الاستيراد")
            .setMessage(message)
            .setPositiveButton("موافق", null)
            .setNeutralButton("عرض التفاصيل", (dialog, which) -> showImportDetails(result))
            .show();
    }
    
    private void showImportDetails(ImportResult result) {
        // يمكن إضافة نشاط منفصل لعرض تفاصيل الاستيراد
        Toast.makeText(this, "عرض التفاصيل قريباً", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    // Data classes
    public static class ExcelColumn {
        private int index;
        private String name;
        private String dataType;
        private List<String> sampleValues;
        
        public ExcelColumn(int index, String name, String dataType, List<String> sampleValues) {
            this.index = index;
            this.name = name;
            this.dataType = dataType;
            this.sampleValues = sampleValues;
        }
        
        // Getters
        public int getIndex() { return index; }
        public String getName() { return name; }
        public String getDataType() { return dataType; }
        public List<String> getSampleValues() { return sampleValues; }
    }
    
    public static class DatabaseField {
        private String key;
        private String displayName;
        private boolean required;
        private String dataType;
        
        public DatabaseField(String key, String displayName, boolean required, String dataType) {
            this.key = key;
            this.displayName = displayName;
            this.required = required;
            this.dataType = dataType;
        }
        
        // Getters
        public String getKey() { return key; }
        public String getDisplayName() { return displayName; }
        public boolean isRequired() { return required; }
        public String getDataType() { return dataType; }
    }
    
    public static class ColumnMapping {
        private DatabaseField databaseField;
        private ExcelColumn excelColumn;
        private String defaultValue;
        
        public ColumnMapping(DatabaseField databaseField, ExcelColumn excelColumn) {
            this.databaseField = databaseField;
            this.excelColumn = excelColumn;
            this.defaultValue = "";
        }
        
        // Getters and Setters
        public DatabaseField getDatabaseField() { return databaseField; }
        public ExcelColumn getExcelColumn() { return excelColumn; }
        public void setExcelColumn(ExcelColumn excelColumn) { this.excelColumn = excelColumn; }
        public String getDefaultValue() { return defaultValue; }
        public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    }
    
    public static class ExcelAnalysisResult {
        private boolean success;
        private String error;
        private List<ExcelColumn> columns;
        private List<List<String>> previewData;
        private int sheetCount;
        private int columnCount;
        private int rowCount;
        
        public ExcelAnalysisResult(boolean success, String error) {
            this.success = success;
            this.error = error;
            this.columns = new ArrayList<>();
            this.previewData = new ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public String getError() { return error; }
        public List<ExcelColumn> getColumns() { return columns; }
        public void setColumns(List<ExcelColumn> columns) { this.columns = columns; }
        public List<List<String>> getPreviewData() { return previewData; }
        public void setPreviewData(List<List<String>> previewData) { this.previewData = previewData; }
        public int getSheetCount() { return sheetCount; }
        public void setSheetCount(int sheetCount) { this.sheetCount = sheetCount; }
        public int getColumnCount() { return columnCount; }
        public void setColumnCount(int columnCount) { this.columnCount = columnCount; }
        public int getRowCount() { return rowCount; }
        public void setRowCount(int rowCount) { this.rowCount = rowCount; }
    }
    
    public static class ImportResult {
        private boolean success;
        private String error;
        private int processedCount;
        private int successCount;
        private int skippedCount;
        private long durationMs;
        private List<String> warnings;
        
        public ImportResult(boolean success, String error) {
            this.success = success;
            this.error = error;
            this.warnings = new ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public String getError() { return error; }
        public int getProcessedCount() { return processedCount; }
        public void setProcessedCount(int processedCount) { this.processedCount = processedCount; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getSkippedCount() { return skippedCount; }
        public void setSkippedCount(int skippedCount) { this.skippedCount = skippedCount; }
        public long getDurationMs() { return durationMs; }
        public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
        public double getDurationSeconds() { return durationMs / 1000.0; }
        public List<String> getWarnings() { return warnings; }
        public void addWarning(String warning) { this.warnings.add(warning); }
    }
}