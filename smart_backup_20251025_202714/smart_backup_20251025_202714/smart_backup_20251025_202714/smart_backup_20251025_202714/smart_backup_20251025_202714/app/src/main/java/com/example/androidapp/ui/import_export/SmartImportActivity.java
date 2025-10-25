package com.example.androidapp.ui.import_export;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.ui.import_export.adapters.ColumnMappingAdapter;
import com.example.androidapp.ui.import_export.adapters.DataPreviewAdapter;
import com.example.androidapp.ui.import_export.models.ColumnMapping;
import com.example.androidapp.ui.import_export.models.ImportDataType;
import com.example.androidapp.ui.import_export.models.ImportPreviewData;
import com.example.androidapp.utils.ExcelImportHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * نشاط الاستيراد الذكي من ملفات Excel
 * يوفر واجهة متدرجة وذكية لاستيراد البيانات مع ربط الأعمدة تلقائياً
 * 
 * @author MiniMax Agent
 * @version 1.0
 * @since 2025-10-20
 */
public class SmartImportActivity extends AppCompatActivity {
    
    private static final String TAG = "SmartImportActivity";
    private static final int FILE_PICKER_REQUEST = 1001;
    
    // خطوات الاستيراد
    private static final int STEP_SELECT_TYPE = 1;
    private static final int STEP_SELECT_FILE = 2;
    private static final int STEP_COLUMN_MAPPING = 3;
    private static final int STEP_DATA_PREVIEW = 4;
    private static final int STEP_IMPORT_PROCESS = 5;
    
    private int currentStep = STEP_SELECT_TYPE;
    
    // عناصر الواجهة
    private MaterialTextView tvStepTitle;
    private MaterialTextView tvStepDescription;
    private ProgressBar progressBar;
    private LinearLayout layoutStepContent;
    private MaterialButton btnNext;
    private MaterialButton btnPrevious;
    private MaterialButton btnCancel;
    
    // الخطوة 1: اختيار نوع البيانات
    private ChipGroup chipGroupDataTypes;
    private ImportDataType selectedDataType;
    
    // الخطوة 2: اختيار الملف
    private MaterialTextView tvSelectedFile;
    private MaterialButton btnSelectFile;
    private Uri selectedFileUri;
    
    // الخطوة 3: ربط الأعمدة
    private RecyclerView recyclerColumnMapping;
    private ColumnMappingAdapter columnMappingAdapter;
    private List<ColumnMapping> columnMappings;
    
    // الخطوة 4: معاينة البيانات
    private RecyclerView recyclerDataPreview;
    private DataPreviewAdapter dataPreviewAdapter;
    private List<ImportPreviewData> previewData;
    private MaterialTextView tvPreviewSummary;
    
    // الخطوة 5: عملية الاستيراد
    private ProgressBar progressImport;
    private MaterialTextView tvImportStatus;
    private MaterialTextView tvImportResults;
    
    // مساعدات
    private ExcelImportHelper excelHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_import);
        
        initializeComponents();
        setupUI();
        setupClickListeners();
        
        // بدء العملية من الخطوة الأولى
        showStep(STEP_SELECT_TYPE);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("استيراد ذكي");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initializeComponents() {
        excelHelper = new ExcelImportHelper(this);
        
        // عناصر الواجهة الأساسية
        tvStepTitle = findViewById(R.id.tvStepTitle);
        tvStepDescription = findViewById(R.id.tvStepDescription);
        progressBar = findViewById(R.id.progressBar);
        layoutStepContent = findViewById(R.id.layoutStepContent);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnCancel = findViewById(R.id.btnCancel);
        
        // تهيئة المتغيرات
        columnMappings = new ArrayList<>();
        previewData = new ArrayList<>();
    }
    
    private void setupUI() {
        updateStepProgress();
        setupDataTypeChips();
    }
    
    private void setupClickListeners() {
        btnNext.setOnClickListener(v -> handleNextStep());
        btnPrevious.setOnClickListener(v -> handlePreviousStep());
        btnCancel.setOnClickListener(v -> showCancelConfirmation());
    }
    
    /**
     * إعداد chips لاختيار نوع البيانات
     */
    private void setupDataTypeChips() {
        chipGroupDataTypes = new ChipGroup(this);
        chipGroupDataTypes.setSingleSelection(true);
        
        // إنشاء chips للأنواع المختلفة
        String[] dataTypes = {
            "العملاء", "الموردين", "الأصناف", "الحسابات", 
            "الفواتير", "المدفوعات", "الموظفين"
        };
        
        for (String type : dataTypes) {
            Chip chip = new Chip(this);
            chip.setText(type);
            chip.setCheckable(true);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedDataType = ImportDataType.fromArabicName(type);
                    btnNext.setEnabled(true);
                }
            });
            chipGroupDataTypes.addView(chip);
        }
    }
    
    /**
     * عرض خطوة معينة
     */
    private void showStep(int step) {
        currentStep = step;
        layoutStepContent.removeAllViews();
        updateStepProgress();
        
        switch (step) {
            case STEP_SELECT_TYPE:
                showSelectTypeStep();
                break;
            case STEP_SELECT_FILE:
                showSelectFileStep();
                break;
            case STEP_COLUMN_MAPPING:
                showColumnMappingStep();
                break;
            case STEP_DATA_PREVIEW:
                showDataPreviewStep();
                break;
            case STEP_IMPORT_PROCESS:
                showImportProcessStep();
                break;
        }
        
        updateNavigationButtons();
    }
    
    /**
     * الخطوة 1: اختيار نوع البيانات
     */
    private void showSelectTypeStep() {
        tvStepTitle.setText("اختيار نوع البيانات");
        tvStepDescription.setText("حدد نوع البيانات التي تريد استيرادها");
        
        MaterialCardView card = new MaterialCardView(this);
        card.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        card.setCardElevation(4);
        card.setRadius(12);
        
        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(24, 24, 24, 24);
        
        MaterialTextView title = new MaterialTextView(this);
        title.setText("أنواع البيانات المتاحة:");
        title.setTextSize(16);
        title.setTextColor(getColor(R.color.primary_text));
        
        cardContent.addView(title);
        cardContent.addView(chipGroupDataTypes);
        card.addView(cardContent);
        layoutStepContent.addView(card);
        
        btnNext.setEnabled(false);
    }
    
    /**
     * الخطوة 2: اختيار الملف
     */
    private void showSelectFileStep() {
        tvStepTitle.setText("اختيار ملف Excel");
        tvStepDescription.setText("اختر ملف Excel الذي يحتوي على البيانات");
        
        MaterialCardView card = new MaterialCardView(this);
        setupCard(card);
        
        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(24, 24, 24, 24);
        
        // عرض نوع البيانات المختار
        MaterialTextView tvDataType = new MaterialTextView(this);
        tvDataType.setText("نوع البيانات: " + selectedDataType.getArabicName());
        tvDataType.setTextSize(16);
        tvDataType.setTextColor(getColor(R.color.primary_color));
        
        // عرض متطلبات الملف
        MaterialTextView tvRequirements = new MaterialTextView(this);
        tvRequirements.setText(getFileRequirements());
        tvRequirements.setTextSize(14);
        tvRequirements.setTextColor(getColor(R.color.secondary_text));
        
        // زر اختيار الملف
        btnSelectFile = new MaterialButton(this);
        btnSelectFile.setText("اختيار ملف Excel");
        btnSelectFile.setIcon(getDrawable(R.drawable.ic_file_excel));
        btnSelectFile.setOnClickListener(v -> openFilePicker());
        
        // عرض الملف المختار
        tvSelectedFile = new MaterialTextView(this);
        tvSelectedFile.setText("لم يتم اختيار ملف");
        tvSelectedFile.setTextSize(14);
        tvSelectedFile.setVisibility(View.GONE);
        
        cardContent.addView(tvDataType);
        cardContent.addView(tvRequirements);
        cardContent.addView(btnSelectFile);
        cardContent.addView(tvSelectedFile);
        
        card.addView(cardContent);
        layoutStepContent.addView(card);
        
        btnNext.setEnabled(false);
    }
    
    /**
     * الخطوة 3: ربط الأعمدة
     */
    private void showColumnMappingStep() {
        tvStepTitle.setText("ربط الأعمدة");
        tvStepDescription.setText("حدد المطابقة بين أعمدة الملف وحقول النظام");
        
        // تحليل الملف والحصول على الأعمدة
        analyzeExcelFile();
        
        MaterialCardView card = new MaterialCardView(this);
        setupCard(card);
        
        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(24, 24, 24, 24);
        
        // عنوان
        MaterialTextView title = new MaterialTextView(this);
        title.setText("ربط أعمدة الملف بحقول النظام:");
        title.setTextSize(16);
        title.setTextColor(getColor(R.color.primary_text));
        
        // RecyclerView لربط الأعمدة
        recyclerColumnMapping = new RecyclerView(this);
        recyclerColumnMapping.setLayoutManager(new LinearLayoutManager(this));
        columnMappingAdapter = new ColumnMappingAdapter(columnMappings, selectedDataType);
        recyclerColumnMapping.setAdapter(columnMappingAdapter);
        
        // زر الاكتشاف التلقائي
        MaterialButton btnAutoDetect = new MaterialButton(this);
        btnAutoDetect.setText("اكتشاف تلقائي");
        btnAutoDetect.setIcon(getDrawable(R.drawable.ic_auto_detect));
        btnAutoDetect.setOnClickListener(v -> autoDetectColumns());
        
        cardContent.addView(title);
        cardContent.addView(btnAutoDetect);
        cardContent.addView(recyclerColumnMapping);
        
        card.addView(cardContent);
        layoutStepContent.addView(card);
        
        btnNext.setEnabled(true);
    }
    
    /**
     * الخطوة 4: معاينة البيانات
     */
    private void showDataPreviewStep() {
        tvStepTitle.setText("معاينة البيانات");
        tvStepDescription.setText("راجع البيانات قبل الاستيراد");
        
        // تحضير بيانات المعاينة
        preparePreviewData();
        
        MaterialCardView card = new MaterialCardView(this);
        setupCard(card);
        
        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(24, 24, 24, 24);
        
        // ملخص البيانات
        tvPreviewSummary = new MaterialTextView(this);
        tvPreviewSummary.setText(getPreviewSummary());
        tvPreviewSummary.setTextSize(16);
        tvPreviewSummary.setTextColor(getColor(R.color.primary_text));
        
        // RecyclerView لمعاينة البيانات
        recyclerDataPreview = new RecyclerView(this);
        recyclerDataPreview.setLayoutManager(new LinearLayoutManager(this));
        dataPreviewAdapter = new DataPreviewAdapter(previewData);
        recyclerDataPreview.setAdapter(dataPreviewAdapter);
        
        cardContent.addView(tvPreviewSummary);
        cardContent.addView(recyclerDataPreview);
        
        card.addView(cardContent);
        layoutStepContent.addView(card);
        
        btnNext.setText("بدء الاستيراد");
    }
    
    /**
     * الخطوة 5: عملية الاستيراد
     */
    private void showImportProcessStep() {
        tvStepTitle.setText("جاري الاستيراد");
        tvStepDescription.setText("يتم الآن استيراد البيانات إلى النظام");
        
        MaterialCardView card = new MaterialCardView(this);
        setupCard(card);
        
        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(24, 24, 24, 24);
        
        // شريط التقدم
        progressImport = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressImport.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 16
        ));
        
        // حالة الاستيراد
        tvImportStatus = new MaterialTextView(this);
        tvImportStatus.setText("بدء عملية الاستيراد...");
        tvImportStatus.setTextSize(14);
        
        // نتائج الاستيراد
        tvImportResults = new MaterialTextView(this);
        tvImportResults.setTextSize(14);
        tvImportResults.setVisibility(View.GONE);
        
        cardContent.addView(progressImport);
        cardContent.addView(tvImportStatus);
        cardContent.addView(tvImportResults);
        
        card.addView(cardContent);
        layoutStepContent.addView(card);
        
        btnNext.setEnabled(false);
        btnPrevious.setEnabled(false);
        
        // بدء عملية الاستيراد
        startImportProcess();
    }
    
    // المساعدات والأدوات
    
    private void setupCard(MaterialCardView card) {
        card.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        card.setCardElevation(4);
        card.setRadius(12);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) card.getLayoutParams();
        params.setMargins(0, 0, 0, 16);
    }
    
    private void updateStepProgress() {
        int progress = (currentStep * 100) / 5;
        progressBar.setProgress(progress);
    }
    
    private void updateNavigationButtons() {
        btnPrevious.setEnabled(currentStep > STEP_SELECT_TYPE);
        
        switch (currentStep) {
            case STEP_SELECT_TYPE:
                btnNext.setText("التالي");
                break;
            case STEP_SELECT_FILE:
                btnNext.setText("التالي");
                break;
            case STEP_COLUMN_MAPPING:
                btnNext.setText("معاينة");
                break;
            case STEP_DATA_PREVIEW:
                btnNext.setText("استيراد");
                break;
            case STEP_IMPORT_PROCESS:
                btnNext.setText("إنهاء");
                break;
        }
    }
    
    private String getFileRequirements() {
        if (selectedDataType == null) return "";
        
        StringBuilder requirements = new StringBuilder();
        requirements.append("متطلبات الملف لنوع \"").append(selectedDataType.getArabicName()).append("\":\n\n");
        
        String[] columns = selectedDataType.getRequiredColumns();
        for (int i = 0; i < columns.length; i++) {
            requirements.append("• عمود ").append(i + 1).append(": ").append(columns[i]).append("\n");
        }
        
        requirements.append("\nملاحظات:\n");
        requirements.append("• الصف الأول يجب أن يحتوي على العناوين\n");
        requirements.append("• الخانات الفارغة ستعتبر قيمة صفر\n");
        requirements.append("• يدعم الملف صيغ .xlsx و .xls");
        
        return requirements.toString();
    }
    
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "اختيار ملف Excel"), FILE_PICKER_REQUEST);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == FILE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                selectedFileUri = data.getData();
                String fileName = excelHelper.getFileName(selectedFileUri);
                tvSelectedFile.setText("الملف المختار: " + fileName);
                tvSelectedFile.setVisibility(View.VISIBLE);
                btnNext.setEnabled(true);
            }
        }
    }
    
    private void analyzeExcelFile() {
        if (selectedFileUri == null) return;
        
        try {
            List<String> headers = excelHelper.getExcelHeaders(selectedFileUri);
            columnMappings.clear();
            
            for (String header : headers) {
                ColumnMapping mapping = new ColumnMapping();
                mapping.setExcelColumn(header);
                mapping.setSystemField("");
                columnMappings.add(mapping);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error analyzing Excel file", e);
            Toast.makeText(this, "خطأ في تحليل الملف", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void autoDetectColumns() {
        // تطبيق خوارزمية الاكتشاف التلقائي
        for (ColumnMapping mapping : columnMappings) {
            String detectedField = excelHelper.detectSystemField(
                mapping.getExcelColumn(), selectedDataType
            );
            mapping.setSystemField(detectedField);
        }
        
        columnMappingAdapter.notifyDataSetChanged();
        Toast.makeText(this, "تم الاكتشاف التلقائي للأعمدة", Toast.LENGTH_SHORT).show();
    }
    
    private void preparePreviewData() {
        if (selectedFileUri == null) return;
        
        try {
            previewData = excelHelper.getPreviewData(selectedFileUri, columnMappings, 10);
        } catch (Exception e) {
            Log.e(TAG, "Error preparing preview data", e);
            Toast.makeText(this, "خطأ في تحضير بيانات المعاينة", Toast.LENGTH_SHORT).show();
        }
    }
    
    private String getPreviewSummary() {
        if (previewData.isEmpty()) return "لا توجد بيانات للمعاينة";
        
        return String.format("معاينة أول %d سجل من إجمالي %d سجل\nنوع البيانات: %s",
            previewData.size(),
            excelHelper.getTotalRowCount(selectedFileUri),
            selectedDataType.getArabicName()
        );
    }
    
    private void startImportProcess() {
        // تشغيل عملية الاستيراد في خيط منفصل
        new Thread(() -> {
            try {
                excelHelper.importData(selectedFileUri, columnMappings, selectedDataType,
                    new ExcelImportHelper.ImportProgressListener() {
                        @Override
                        public void onProgress(int progress, String status) {
                            runOnUiThread(() -> {
                                progressImport.setProgress(progress);
                                tvImportStatus.setText(status);
                            });
                        }
                        
                        @Override
                        public void onComplete(ExcelImportHelper.ImportResult result) {
                            runOnUiThread(() -> showImportResults(result));
                        }
                        
                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> showImportError(error));
                        }
                    });
                    
            } catch (Exception e) {
                Log.e(TAG, "Error during import", e);
                runOnUiThread(() -> showImportError("حدث خطأ أثناء الاستيراد: " + e.getMessage()));
            }
        }).start();
    }
    
    private void showImportResults(ExcelImportHelper.ImportResult result) {
        tvImportStatus.setText("تم الاستيراد بنجاح!");
        tvImportResults.setText(result.getSummary());
        tvImportResults.setVisibility(View.VISIBLE);
        
        btnNext.setText("إنهاء");
        btnNext.setEnabled(true);
        btnNext.setOnClickListener(v -> {
            setResult(Activity.RESULT_OK);
            finish();
        });
    }
    
    private void showImportError(String error) {
        tvImportStatus.setText("فشل الاستيراد!");
        tvImportResults.setText(error);
        tvImportResults.setTextColor(getColor(R.color.error_color));
        tvImportResults.setVisibility(View.VISIBLE);
        
        btnPrevious.setEnabled(true);
        btnNext.setText("إعادة المحاولة");
        btnNext.setEnabled(true);
        btnNext.setOnClickListener(v -> startImportProcess());
    }
    
    private void handleNextStep() {
        if (currentStep < STEP_IMPORT_PROCESS) {
            showStep(currentStep + 1);
        }
    }
    
    private void handlePreviousStep() {
        if (currentStep > STEP_SELECT_TYPE) {
            showStep(currentStep - 1);
        }
    }
    
    private void showCancelConfirmation() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("إلغاء الاستيراد")
            .setMessage("هل أنت متأكد من إلغاء عملية الاستيراد؟")
            .setPositiveButton("نعم", (dialog, which) -> finish())
            .setNegativeButton("لا", null)
            .show();
    }
}