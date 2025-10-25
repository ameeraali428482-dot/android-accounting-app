package com.example.myapplication.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.data.models.ExcelColumnInfo;
import com.example.myapplication.databinding.ActivityExcelImportBinding;
import com.example.myapplication.services.ExcelImportService;
import com.example.myapplication.ui.dialogs.ExcelColumnMappingDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;
import java.util.Map;

/**
 * نشاط استيراد البيانات من ملفات Excel
 * يتيح للمستخدم اختيار ملف Excel وتعيين الأعمدة واستيراد البيانات
 */
public class ExcelImportActivity extends AppCompatActivity implements 
        ExcelImportService.OnExcelAnalysisListener,
        ExcelImportService.OnDataImportListener,
        ExcelColumnMappingDialog.OnMappingCompleteListener {

    private static final String TAG = "ExcelImportActivity";

    // View Binding
    private ActivityExcelImportBinding binding;

    // Services
    private ExcelImportService excelImportService;

    // Data
    private Uri selectedFileUri;
    private String selectedFileName;
    private List<ExcelColumnInfo> excelColumns;
    private ExcelColumnMappingDialog.DataType selectedDataType = ExcelColumnMappingDialog.DataType.CUSTOMERS;

    // File picker launcher
    private ActivityResultLauncher<String[]> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExcelImportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeServices();
        setupViews();
        setupFilePickerLauncher();
        setupEventListeners();
    }

    /**
     * تهيئة الخدمات
     */
    private void initializeServices() {
        excelImportService = new ExcelImportService(this);
    }

    /**
     * إعداد المكونات الأساسية
     */
    private void setupViews() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("استيراد من Excel");
        }

        // Setup initial state
        updateUIState(UIState.INITIAL);
    }

    /**
     * إعداد launcher لاختيار الملفات
     */
    private void setupFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    selectedFileName = getFileName(uri);
                    analyzeSelectedFile();
                }
            }
        );
    }

    /**
     * إعداد مستمعي الأحداث
     */
    private void setupEventListeners() {
        // Select file button
        binding.btnSelectFile.setOnClickListener(v -> selectExcelFile());

        // Analyze button
        binding.btnAnalyzeFile.setOnClickListener(v -> analyzeSelectedFile());

        // Import button
        binding.btnStartImport.setOnClickListener(v -> showColumnMappingDialog());

        // Data type chips
        binding.chipCustomers.setOnClickListener(v -> selectDataType(ExcelColumnMappingDialog.DataType.CUSTOMERS));
        binding.chipSuppliers.setOnClickListener(v -> selectDataType(ExcelColumnMappingDialog.DataType.SUPPLIERS));
        binding.chipItems.setOnClickListener(v -> selectDataType(ExcelColumnMappingDialog.DataType.ITEMS));
        binding.chipAccounts.setOnClickListener(v -> selectDataType(ExcelColumnMappingDialog.DataType.ACCOUNTS));

        // Back button
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * تحديد نوع البيانات
     */
    private void selectDataType(ExcelColumnMappingDialog.DataType dataType) {
        this.selectedDataType = dataType;
        
        // Update chip selections
        binding.chipCustomers.setChecked(dataType == ExcelColumnMappingDialog.DataType.CUSTOMERS);
        binding.chipSuppliers.setChecked(dataType == ExcelColumnMappingDialog.DataType.SUPPLIERS);
        binding.chipItems.setChecked(dataType == ExcelColumnMappingDialog.DataType.ITEMS);
        binding.chipAccounts.setChecked(dataType == ExcelColumnMappingDialog.DataType.ACCOUNTS);
    }

    /**
     * فتح منتقي الملفات
     */
    private void selectExcelFile() {
        String[] mimeTypes = {
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        };
        filePickerLauncher.launch(mimeTypes);
    }

    /**
     * تحليل الملف المحدد
     */
    private void analyzeSelectedFile() {
        if (selectedFileUri == null) {
            Toast.makeText(this, "يرجى اختيار ملف أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        updateUIState(UIState.ANALYZING);
        
        // Update file info
        binding.tvSelectedFile.setText(selectedFileName);
        binding.tvFileSize.setText("جاري التحليل...");

        // Start analysis
        excelImportService.analyzeExcelFile(
            selectedFileUri,
            binding.switchFirstRowHeader.isChecked(),
            this
        );
    }

    /**
     * عرض حوار تعيين الأعمدة
     */
    private void showColumnMappingDialog() {
        if (excelColumns == null || excelColumns.isEmpty()) {
            Toast.makeText(this, "يجب تحليل الملف أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        ExcelColumnMappingDialog dialog = ExcelColumnMappingDialog.newInstance(
            excelColumns,
            selectedFileName,
            selectedDataType
        );
        
        dialog.setOnMappingCompleteListener(this);
        dialog.show(getSupportFragmentManager(), "column_mapping");
    }

    /**
     * الحصول على اسم الملف من URI
     */
    private String getFileName(Uri uri) {
        String fileName = uri.getLastPathSegment();
        if (fileName == null) {
            fileName = "ملف Excel";
        }
        return fileName;
    }

    // ================ ExcelImportService.OnExcelAnalysisListener ================

    @Override
    public void onAnalysisComplete(List<ExcelColumnInfo> columns, int totalRows) {
        runOnUiThread(() -> {
            this.excelColumns = columns;
            
            // Update UI with analysis results
            binding.tvFileSize.setText(totalRows + " صف");
            binding.tvColumnsCount.setText(columns.size() + " عمود");
            
            // Show column preview
            StringBuilder columnNames = new StringBuilder();
            for (int i = 0; i < Math.min(columns.size(), 5); i++) {
                if (i > 0) columnNames.append("، ");
                columnNames.append(columns.get(i).getName());
            }
            if (columns.size() > 5) {
                columnNames.append("...");
            }
            binding.tvColumnPreview.setText(columnNames.toString());

            updateUIState(UIState.ANALYZED);
            
            Toast.makeText(this, "تم تحليل الملف بنجاح", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onAnalysisError(String error) {
        runOnUiThread(() -> {
            updateUIState(UIState.ERROR);
            
            new MaterialAlertDialogBuilder(this)
                .setTitle("خطأ في التحليل")
                .setMessage(error)
                .setPositiveButton("موافق", null)
                .show();
        });
    }

    @Override
    public void onProgress(int progress) {
        runOnUiThread(() -> {
            binding.progressAnalysis.setProgress(progress);
        });
    }

    // ================ ExcelImportService.OnDataImportListener ================

    @Override
    public void onImportComplete(int importedRows, int skippedRows, int errorRows) {
        runOnUiThread(() -> {
            updateUIState(UIState.COMPLETED);

            String message = String.format(
                "تم الاستيراد بنجاح!\n\nالسجلات المستوردة: %d\nالسجلات المتجاهلة: %d\nالسجلات الخاطئة: %d",
                importedRows, skippedRows, errorRows
            );

            new MaterialAlertDialogBuilder(this)
                .setTitle("اكتمل الاستيراد")
                .setMessage(message)
                .setPositiveButton("موافق", (dialog, which) -> {
                    // Return to previous activity or show success screen
                    setResult(RESULT_OK);
                    finish();
                })
                .show();
        });
    }

    @Override
    public void onImportError(String error) {
        runOnUiThread(() -> {
            updateUIState(UIState.ERROR);

            new MaterialAlertDialogBuilder(this)
                .setTitle("خطأ في الاستيراد")
                .setMessage(error)
                .setPositiveButton("موافق", null)
                .show();
        });
    }

    @Override
    public void onProgress(int currentRow, int totalRows) {
        runOnUiThread(() -> {
            int progress = (int) (((float) currentRow / totalRows) * 100);
            binding.progressImport.setProgress(progress);
            binding.tvImportProgress.setText(currentRow + " / " + totalRows + " سجل");
        });
    }

    // ================ ExcelColumnMappingDialog.OnMappingCompleteListener ================

    @Override
    public void onMappingComplete(Map<String, String> mappings, 
                                 ExcelColumnMappingDialog.DataType dataType, 
                                 boolean skipDuplicates, 
                                 boolean validateData, 
                                 boolean firstRowHeader) {
        
        updateUIState(UIState.IMPORTING);

        // Start import process
        excelImportService.importData(
            selectedFileUri,
            mappings,
            dataType,
            skipDuplicates,
            validateData,
            firstRowHeader,
            this
        );
    }

    @Override
    public void onMappingCancelled() {
        // User cancelled column mapping
        Toast.makeText(this, "تم إلغاء عملية الاستيراد", Toast.LENGTH_SHORT).show();
    }

    // ================ UI State Management ================

    private enum UIState {
        INITIAL,
        ANALYZING,
        ANALYZED,
        IMPORTING,
        COMPLETED,
        ERROR
    }

    /**
     * تحديث حالة واجهة المستخدم
     */
    private void updateUIState(UIState state) {
        // Hide all progress indicators first
        binding.progressAnalysis.setVisibility(View.GONE);
        binding.progressImport.setVisibility(View.GONE);
        binding.tvImportProgress.setVisibility(View.GONE);

        switch (state) {
            case INITIAL:
                binding.btnSelectFile.setEnabled(true);
                binding.btnAnalyzeFile.setEnabled(false);
                binding.btnStartImport.setEnabled(false);
                binding.cardFileInfo.setVisibility(View.GONE);
                binding.cardAnalysisResult.setVisibility(View.GONE);
                break;

            case ANALYZING:
                binding.btnSelectFile.setEnabled(false);
                binding.btnAnalyzeFile.setEnabled(false);
                binding.btnStartImport.setEnabled(false);
                binding.progressAnalysis.setVisibility(View.VISIBLE);
                binding.cardFileInfo.setVisibility(View.VISIBLE);
                break;

            case ANALYZED:
                binding.btnSelectFile.setEnabled(true);
                binding.btnAnalyzeFile.setEnabled(true);
                binding.btnStartImport.setEnabled(true);
                binding.cardAnalysisResult.setVisibility(View.VISIBLE);
                break;

            case IMPORTING:
                binding.btnSelectFile.setEnabled(false);
                binding.btnAnalyzeFile.setEnabled(false);
                binding.btnStartImport.setEnabled(false);
                binding.progressImport.setVisibility(View.VISIBLE);
                binding.tvImportProgress.setVisibility(View.VISIBLE);
                break;

            case COMPLETED:
                binding.btnSelectFile.setEnabled(true);
                binding.btnAnalyzeFile.setEnabled(false);
                binding.btnStartImport.setEnabled(false);
                break;

            case ERROR:
                binding.btnSelectFile.setEnabled(true);
                binding.btnAnalyzeFile.setEnabled(selectedFileUri != null);
                binding.btnStartImport.setEnabled(excelColumns != null && !excelColumns.isEmpty());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (excelImportService != null) {
            excelImportService.shutdown();
        }
    }
}