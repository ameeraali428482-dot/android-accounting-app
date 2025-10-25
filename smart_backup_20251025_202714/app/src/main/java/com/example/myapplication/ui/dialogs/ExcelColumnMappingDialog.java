package com.example.myapplication.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.models.ExcelColumnInfo;
import com.example.myapplication.data.models.DatabaseFieldInfo;
import com.example.myapplication.data.models.ColumnMappingInfo;
import com.example.myapplication.databinding.DialogExcelColumnMappingBinding;
import com.example.myapplication.databinding.ItemColumnMappingBinding;
import com.example.myapplication.utils.ExcelDataPreviewAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DialogFragment لتعيين أعمدة ملف Excel مع حقول قاعدة البيانات
 * يوفر واجهة سهلة الاستخدام لربط كل عمود Excel بحقل مناسب في قاعدة البيانات
 */
public class ExcelColumnMappingDialog extends DialogFragment {

    // Constants
    private static final String ARG_EXCEL_COLUMNS = "excel_columns";
    private static final String ARG_FILE_NAME = "file_name";
    private static final String ARG_DATA_TYPE = "data_type";

    // Data Types
    public enum DataType {
        CUSTOMERS("customers", "العملاء"),
        SUPPLIERS("suppliers", "الموردين"),
        ITEMS("items", "الأصناف"),
        ACCOUNTS("accounts", "الحسابات");

        public final String value;
        public final String displayName;

        DataType(String value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }
    }

    // Bindings
    private DialogExcelColumnMappingBinding binding;

    // Data
    private List<ExcelColumnInfo> excelColumns;
    private String fileName;
    private DataType selectedDataType = DataType.CUSTOMERS;
    private Map<String, String> columnMappings = new HashMap<>();
    private List<ColumnMappingInfo> mappingInfos = new ArrayList<>();

    // Adapters
    private ExcelDataPreviewAdapter previewAdapter;

    // Listeners
    private OnMappingCompleteListener listener;

    /**
     * Interface للاستماع لاكتمال عملية تعيين الأعمدة
     */
    public interface OnMappingCompleteListener {
        void onMappingComplete(Map<String, String> mappings, DataType dataType, 
                             boolean skipDuplicates, boolean validateData, boolean firstRowHeader);
        void onMappingCancelled();
    }

    /**
     * إنشاء instance جديدة من الـ Dialog
     */
    public static ExcelColumnMappingDialog newInstance(List<ExcelColumnInfo> excelColumns, 
                                                      String fileName, DataType dataType) {
        ExcelColumnMappingDialog dialog = new ExcelColumnMappingDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EXCEL_COLUMNS, new ArrayList<>(excelColumns));
        args.putString(ARG_FILE_NAME, fileName);
        args.putSerializable(ARG_DATA_TYPE, dataType);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);

        if (getArguments() != null) {
            excelColumns = (List<ExcelColumnInfo>) getArguments().getSerializable(ARG_EXCEL_COLUMNS);
            fileName = getArguments().getString(ARG_FILE_NAME);
            selectedDataType = (DataType) getArguments().getSerializable(ARG_DATA_TYPE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setWindowAnimations(R.style.DialogSlideAnimation);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        binding = DialogExcelColumnMappingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews();
        setupDataTypeChips();
        setupColumnMappings();
        setupEventListeners();
    }

    /**
     * إعداد المكونات الأساسية للواجهة
     */
    private void setupViews() {
        // Set file name
        binding.tvFileName.setText(fileName);
        binding.tvSheetCount.setText("ورقة عمل 1");

        // Setup preview RecyclerView
        previewAdapter = new ExcelDataPreviewAdapter();
        binding.rvPreviewData.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPreviewData.setAdapter(previewAdapter);
    }

    /**
     * إعداد chips اختيار نوع البيانات
     */
    private void setupDataTypeChips() {
        // Set initial selection
        updateSelectedDataType(selectedDataType);

        // Setup chip listeners
        binding.chipCustomers.setOnClickListener(v -> updateSelectedDataType(DataType.CUSTOMERS));
        binding.chipSuppliers.setOnClickListener(v -> updateSelectedDataType(DataType.SUPPLIERS));
        binding.chipItems.setOnClickListener(v -> updateSelectedDataType(DataType.ITEMS));
        binding.chipAccounts.setOnClickListener(v -> updateSelectedDataType(DataType.ACCOUNTS));
    }

    /**
     * تحديث نوع البيانات المحدد
     */
    private void updateSelectedDataType(DataType dataType) {
        this.selectedDataType = dataType;

        // Update chip selections
        binding.chipCustomers.setChecked(dataType == DataType.CUSTOMERS);
        binding.chipSuppliers.setChecked(dataType == DataType.SUPPLIERS);
        binding.chipItems.setChecked(dataType == DataType.ITEMS);
        binding.chipAccounts.setChecked(dataType == DataType.ACCOUNTS);

        // Rebuild column mappings with new field options
        setupColumnMappings();
    }

    /**
     * إعداد قوائم تعيين الأعمدة
     */
    private void setupColumnMappings() {
        binding.containerColumnMapping.removeAllViews();
        mappingInfos.clear();

        List<DatabaseFieldInfo> availableFields = getAvailableFieldsForDataType(selectedDataType);

        for (ExcelColumnInfo column : excelColumns) {
            View mappingView = createColumnMappingView(column, availableFields);
            binding.containerColumnMapping.addView(mappingView);
        }
    }

    /**
     * إنشاء عرض لتعيين عمود واحد
     */
    private View createColumnMappingView(ExcelColumnInfo column, List<DatabaseFieldInfo> fields) {
        ItemColumnMappingBinding itemBinding = ItemColumnMappingBinding.inflate(
            getLayoutInflater(), binding.containerColumnMapping, false);

        // Set Excel column info
        itemBinding.tvExcelColumnName.setText(column.getName());
        itemBinding.tvExcelColumnIndex.setText(column.getIndex());
        itemBinding.tvSampleData.setText(column.getSampleData());

        // Setup database field dropdown
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("عدم التعيين"); // Option for no mapping

        for (DatabaseFieldInfo field : fields) {
            fieldNames.add(field.getDisplayName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
            android.R.layout.simple_dropdown_item_1line, fieldNames);
        itemBinding.actvDatabaseField.setAdapter(adapter);

        // Auto-suggest mapping based on column name similarity
        String suggestedField = findBestFieldMatch(column.getName(), fields);
        if (suggestedField != null) {
            itemBinding.actvDatabaseField.setText(suggestedField);
            updateMappingStatus(itemBinding, true);
        }

        // Setup field selection listener
        itemBinding.actvDatabaseField.setOnItemClickListener((parent, view, position, id) -> {
            String selectedField = fieldNames.get(position);
            boolean isMapped = !selectedField.equals("عدم التعيين");
            
            updateMappingStatus(itemBinding, isMapped);
            
            if (isMapped) {
                // Find the actual field value
                String fieldValue = null;
                for (DatabaseFieldInfo field : fields) {
                    if (field.getDisplayName().equals(selectedField)) {
                        fieldValue = field.getValue();
                        break;
                    }
                }
                columnMappings.put(column.getIndex(), fieldValue);
            } else {
                columnMappings.remove(column.getIndex());
            }

            updateImportButtonState();
        });

        // Setup more options button
        itemBinding.btnMoreOptions.setOnClickListener(v -> showColumnOptions(column, itemBinding));

        // Store mapping info for later use
        ColumnMappingInfo mappingInfo = new ColumnMappingInfo(column, itemBinding);
        mappingInfos.add(mappingInfo);

        return itemBinding.getRoot();
    }

    /**
     * تحديث حالة المطابقة البصرية
     */
    private void updateMappingStatus(ItemColumnMappingBinding itemBinding, boolean isMapped) {
        if (isMapped) {
            itemBinding.llMappingStatus.setVisibility(View.VISIBLE);
            itemBinding.ivMappingStatus.setImageResource(R.drawable.ic_check_circle);
            itemBinding.ivMappingStatus.setColorFilter(getContext().getColor(R.color.success_color));
            itemBinding.tvMappingStatus.setText("مطابق");
            itemBinding.tvMappingStatus.setTextColor(getContext().getColor(R.color.success_color));
        } else {
            itemBinding.llMappingStatus.setVisibility(View.GONE);
        }
    }

    /**
     * البحث عن أفضل مطابقة للحقل بناءً على اسم العمود
     */
    private String findBestFieldMatch(String columnName, List<DatabaseFieldInfo> fields) {
        String lowerColumnName = columnName.toLowerCase();
        
        // Define common mapping patterns
        Map<String, String> commonMappings = new HashMap<>();
        commonMappings.put("الاسم", "name");
        commonMappings.put("name", "name");
        commonMappings.put("رقم", "number");
        commonMappings.put("number", "number");
        commonMappings.put("هاتف", "phone");
        commonMappings.put("phone", "phone");
        commonMappings.put("عنوان", "address");
        commonMappings.put("address", "address");
        commonMappings.put("ايميل", "email");
        commonMappings.put("email", "email");
        commonMappings.put("مدين", "debit");
        commonMappings.put("debit", "debit");
        commonMappings.put("دائن", "credit");
        commonMappings.put("credit", "credit");

        String suggestedFieldValue = commonMappings.get(lowerColumnName);
        if (suggestedFieldValue != null) {
            for (DatabaseFieldInfo field : fields) {
                if (field.getValue().equals(suggestedFieldValue)) {
                    return field.getDisplayName();
                }
            }
        }

        return null;
    }

    /**
     * عرض خيارات إضافية للعمود
     */
    private void showColumnOptions(ExcelColumnInfo column, ItemColumnMappingBinding itemBinding) {
        // TODO: Implement column options menu (data transformation, validation rules, etc.)
        Toast.makeText(getContext(), "خيارات العمود: " + column.getName(), Toast.LENGTH_SHORT).show();
    }

    /**
     * الحصول على قائمة الحقول المتاحة لنوع البيانات المحدد
     */
    private List<DatabaseFieldInfo> getAvailableFieldsForDataType(DataType dataType) {
        List<DatabaseFieldInfo> fields = new ArrayList<>();

        switch (dataType) {
            case CUSTOMERS:
                fields.add(new DatabaseFieldInfo("name", "الاسم"));
                fields.add(new DatabaseFieldInfo("phone", "رقم الهاتف"));
                fields.add(new DatabaseFieldInfo("address", "العنوان"));
                fields.add(new DatabaseFieldInfo("email", "البريد الإلكتروني"));
                fields.add(new DatabaseFieldInfo("debit", "مدين"));
                fields.add(new DatabaseFieldInfo("credit", "دائن"));
                fields.add(new DatabaseFieldInfo("notes", "ملاحظات"));
                break;

            case SUPPLIERS:
                fields.add(new DatabaseFieldInfo("name", "الاسم"));
                fields.add(new DatabaseFieldInfo("phone", "رقم الهاتف"));
                fields.add(new DatabaseFieldInfo("address", "العنوان"));
                fields.add(new DatabaseFieldInfo("email", "البريد الإلكتروني"));
                fields.add(new DatabaseFieldInfo("company", "الشركة"));
                fields.add(new DatabaseFieldInfo("debit", "مدين"));
                fields.add(new DatabaseFieldInfo("credit", "دائن"));
                fields.add(new DatabaseFieldInfo("notes", "ملاحظات"));
                break;

            case ITEMS:
                fields.add(new DatabaseFieldInfo("name", "اسم الصنف"));
                fields.add(new DatabaseFieldInfo("code", "كود الصنف"));
                fields.add(new DatabaseFieldInfo("barcode", "الباركود"));
                fields.add(new DatabaseFieldInfo("category", "الفئة"));
                fields.add(new DatabaseFieldInfo("unit", "الوحدة"));
                fields.add(new DatabaseFieldInfo("price", "السعر"));
                fields.add(new DatabaseFieldInfo("cost", "التكلفة"));
                fields.add(new DatabaseFieldInfo("quantity", "الكمية"));
                fields.add(new DatabaseFieldInfo("min_quantity", "الحد الأدنى"));
                fields.add(new DatabaseFieldInfo("description", "الوصف"));
                break;

            case ACCOUNTS:
                fields.add(new DatabaseFieldInfo("name", "اسم الحساب"));
                fields.add(new DatabaseFieldInfo("code", "رقم الحساب"));
                fields.add(new DatabaseFieldInfo("type", "نوع الحساب"));
                fields.add(new DatabaseFieldInfo("parent_account", "الحساب الأب"));
                fields.add(new DatabaseFieldInfo("debit", "مدين"));
                fields.add(new DatabaseFieldInfo("credit", "دائن"));
                fields.add(new DatabaseFieldInfo("notes", "ملاحظات"));
                break;
        }

        return fields;
    }

    /**
     * إعداد مستمعي الأحداث
     */
    private void setupEventListeners() {
        // Close button
        binding.btnClose.setOnClickListener(v -> dismiss());

        // Cancel button
        binding.btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMappingCancelled();
            }
            dismiss();
        });

        // Preview button
        binding.btnPreview.setOnClickListener(v -> showPreview());

        // Import button
        binding.btnImport.setOnClickListener(v -> performImport());

        // Switch listeners
        binding.switchFirstRowHeader.setOnCheckedChangeListener((buttonView, isChecked) -> 
            updateImportButtonState());
    }

    /**
     * عرض معاينة البيانات
     */
    private void showPreview() {
        if (columnMappings.isEmpty()) {
            Toast.makeText(getContext(), "يجب تعيين عمود واحد على الأقل", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Load and display preview data based on current mappings
        binding.cardPreview.setVisibility(View.VISIBLE);
        binding.tvPreviewCount.setText("5 سجلات");
        
        // Simulate preview data
        List<Map<String, String>> previewData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, String> row = new HashMap<>();
            for (Map.Entry<String, String> mapping : columnMappings.entrySet()) {
                row.put(mapping.getValue(), "عينة " + (i + 1));
            }
            previewData.add(row);
        }
        
        previewAdapter.updateData(previewData, new ArrayList<>(columnMappings.values()));
        
        Toast.makeText(getContext(), "تم تحميل معاينة البيانات", Toast.LENGTH_SHORT).show();
    }

    /**
     * تنفيذ عملية الاستيراد
     */
    private void performImport() {
        if (columnMappings.isEmpty()) {
            Toast.makeText(getContext(), "يجب تعيين عمود واحد على الأقل", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listener != null) {
            listener.onMappingComplete(
                columnMappings,
                selectedDataType,
                binding.switchSkipDuplicates.isChecked(),
                binding.switchValidateData.isChecked(),
                binding.switchFirstRowHeader.isChecked()
            );
        }

        dismiss();
    }

    /**
     * تحديث حالة زر الاستيراد
     */
    private void updateImportButtonState() {
        boolean hasMapping = !columnMappings.isEmpty();
        binding.btnImport.setEnabled(hasMapping);
        binding.btnPreview.setEnabled(hasMapping);
    }

    /**
     * تعيين المستمع للاستجابة لاكتمال التعيين
     */
    public void setOnMappingCompleteListener(OnMappingCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}