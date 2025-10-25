package com.example.androidapp.ui.import_export;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * محول لعرض محاذاة الأعمدة بين Excel وقاعدة البيانات
 * Column Mapping Adapter for Excel-Database field mapping
 */
public class ColumnMappingAdapter extends RecyclerView.Adapter<ColumnMappingAdapter.MappingViewHolder> {
    
    private List<SmartExcelImportActivity.ColumnMapping> mappings;
    private List<SmartExcelImportActivity.ExcelColumn> excelColumns;
    private OnMappingChangeListener listener;
    
    public interface OnMappingChangeListener {
        void onMappingChanged();
    }
    
    public ColumnMappingAdapter(List<SmartExcelImportActivity.ColumnMapping> mappings,
                              List<SmartExcelImportActivity.ExcelColumn> excelColumns,
                              OnMappingChangeListener listener) {
        this.mappings = mappings;
        this.excelColumns = excelColumns;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public MappingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_column_mapping, parent, false);
        return new MappingViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MappingViewHolder holder, int position) {
        SmartExcelImportActivity.ColumnMapping mapping = mappings.get(position);
        holder.bind(mapping, position);
    }
    
    @Override
    public int getItemCount() {
        return mappings.size();
    }
    
    class MappingViewHolder extends RecyclerView.ViewHolder {
        
        private TextView textViewDatabaseField;
        private TextView textViewRequired;
        private TextView textViewDataType;
        private Spinner spinnerExcelColumn;
        private TextInputEditText editTextDefaultValue;
        private TextView textViewSampleValues;
        
        public MappingViewHolder(@NonNull View itemView) {
            super(itemView);
            
            textViewDatabaseField = itemView.findViewById(R.id.textViewDatabaseField);
            textViewRequired = itemView.findViewById(R.id.textViewRequired);
            textViewDataType = itemView.findViewById(R.id.textViewDataType);
            spinnerExcelColumn = itemView.findViewById(R.id.spinnerExcelColumn);
            editTextDefaultValue = itemView.findViewById(R.id.editTextDefaultValue);
            textViewSampleValues = itemView.findViewById(R.id.textViewSampleValues);
        }
        
        public void bind(SmartExcelImportActivity.ColumnMapping mapping, int position) {
            SmartExcelImportActivity.DatabaseField dbField = mapping.getDatabaseField();
            
            // عرض معلومات الحقل
            textViewDatabaseField.setText(dbField.getDisplayName());
            textViewRequired.setText(dbField.isRequired() ? "مطلوب" : "اختياري");
            textViewRequired.setTextColor(itemView.getContext().getResources().getColor(
                dbField.isRequired() ? android.R.color.holo_red_dark : android.R.color.darker_gray
            ));
            textViewDataType.setText("نوع البيانات: " + dbField.getDataType());
            
            // إعداد قائمة أعمدة Excel
            setupExcelColumnSpinner(mapping, position);
            
            // إعداد القيمة الافتراضية
            editTextDefaultValue.setText(mapping.getDefaultValue());
            editTextDefaultValue.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    mapping.setDefaultValue(editTextDefaultValue.getText().toString());
                    if (listener != null) {
                        listener.onMappingChanged();
                    }
                }
            });
            
            // عرض القيم العينية
            updateSampleValues(mapping);
        }
        
        private void setupExcelColumnSpinner(SmartExcelImportActivity.ColumnMapping mapping, int position) {
            List<String> columnNames = new ArrayList<>();
            columnNames.add("-- اختر العمود --");
            
            for (SmartExcelImportActivity.ExcelColumn column : excelColumns) {
                columnNames.add(String.format("%s (العمود %d)", 
                    column.getName(), column.getIndex() + 1));
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(),
                android.R.layout.simple_spinner_item, columnNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerExcelColumn.setAdapter(adapter);
            
            // تحديد الاختيار الحالي
            int selectedIndex = 0;
            if (mapping.getExcelColumn() != null) {
                for (int i = 0; i < excelColumns.size(); i++) {
                    if (excelColumns.get(i).getIndex() == mapping.getExcelColumn().getIndex()) {
                        selectedIndex = i + 1;
                        break;
                    }
                }
            }
            spinnerExcelColumn.setSelection(selectedIndex);
            
            // معالجة تغيير الاختيار
            spinnerExcelColumn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int selectedPosition, long id) {
                    if (selectedPosition == 0) {
                        mapping.setExcelColumn(null);
                    } else {
                        mapping.setExcelColumn(excelColumns.get(selectedPosition - 1));
                    }
                    
                    updateSampleValues(mapping);
                    
                    if (listener != null) {
                        listener.onMappingChanged();
                    }
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
        
        private void updateSampleValues(SmartExcelImportActivity.ColumnMapping mapping) {
            if (mapping.getExcelColumn() != null && 
                mapping.getExcelColumn().getSampleValues() != null &&
                !mapping.getExcelColumn().getSampleValues().isEmpty()) {
                
                StringBuilder samples = new StringBuilder("قيم عينية: ");
                List<String> sampleValues = mapping.getExcelColumn().getSampleValues();
                
                int maxSamples = Math.min(3, sampleValues.size());
                for (int i = 0; i < maxSamples; i++) {
                    samples.append(sampleValues.get(i));
                    if (i < maxSamples - 1) {
                        samples.append(", ");
                    }
                }
                
                if (sampleValues.size() > 3) {
                    samples.append("...");
                }
                
                textViewSampleValues.setText(samples.toString());
                textViewSampleValues.setVisibility(View.VISIBLE);
                
            } else {
                textViewSampleValues.setVisibility(View.GONE);
            }
        }
    }
}