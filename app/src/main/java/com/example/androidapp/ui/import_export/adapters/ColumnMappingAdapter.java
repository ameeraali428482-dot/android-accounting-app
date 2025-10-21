package com.example.androidapp.ui.import_export.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.ui.import_export.models.ColumnMapping;
import com.example.androidapp.ui.import_export.models.ImportDataType;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

/**
 * محول لعرض وإدارة ربط الأعمدة بين Excel وحقول النظام
 */
public class ColumnMappingAdapter extends RecyclerView.Adapter<ColumnMappingAdapter.ViewHolder> {
    
    private final List<ColumnMapping> columnMappings;
    private final ImportDataType dataType;
    private final String[] systemFields;
    private final String[] systemFieldsArabic;
    
    public ColumnMappingAdapter(List<ColumnMapping> columnMappings, ImportDataType dataType) {
        this.columnMappings = columnMappings;
        this.dataType = dataType;
        this.systemFields = dataType.getSystemFields();
        this.systemFieldsArabic = getArabicFieldNames(dataType);
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_column_mapping, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ColumnMapping mapping = columnMappings.get(position);
        holder.bind(mapping);
    }
    
    @Override
    public int getItemCount() {
        return columnMappings.size();
    }
    
    /**
     * الحصول على أسماء الحقول باللغة العربية
     */
    private String[] getArabicFieldNames(ImportDataType dataType) {
        switch (dataType) {
            case CUSTOMERS:
            case SUPPLIERS:
                return new String[]{
                    "الاسم", "رقم الهاتف", "المنطقة", "الرصيد المدين", "الرصيد الدائن",
                    "الرصيد الإجمالي", "اللقب", "العنوان", "البريد الإلكتروني"
                };
                
            case ITEMS:
                return new String[]{
                    "اسم الصنف", "الوحدة", "الكمية", "سعر الشراء", "سعر البيع",
                    "كمية المخزون", "التاريخ", "الوكيل", "الفئة", "الحد الأدنى"
                };
                
            case ACCOUNTS:
                return new String[]{
                    "اسم الحساب", "نوع الحساب", "الرصيد الافتتاحي",
                    "العملة", "الوصف", "الحالة"
                };
                
            case INVOICES:
                return new String[]{
                    "رقم الفاتورة", "تاريخ الفاتورة", "العميل", "نوع الفاتورة",
                    "المبلغ الإجمالي", "المبلغ المدفوع", "المبلغ المتبقي", "الحالة"
                };
                
            case PAYMENTS:
                return new String[]{
                    "رقم الدفع", "تاريخ الدفع", "العميل", "المبلغ",
                    "طريقة الدفع", "الوصف", "رقم الإيصال"
                };
                
            case EMPLOYEES:
                return new String[]{
                    "اسم الموظف", "المنصب", "الراتب الأساسي", "تاريخ التوظيف",
                    "رقم الهاتف", "العنوان", "الحالة"
                };
                
            default:
                return new String[0];
        }
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        
        private final TextView tvExcelColumn;
        private final MaterialAutoCompleteTextView autoCompleteSystemField;
        private final TextInputEditText etDefaultValue;
        private final TextView tvFieldType;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvExcelColumn = itemView.findViewById(R.id.tvExcelColumn);
            autoCompleteSystemField = itemView.findViewById(R.id.autoCompleteSystemField);
            etDefaultValue = itemView.findViewById(R.id.etDefaultValue);
            tvFieldType = itemView.findViewById(R.id.tvFieldType);
            
            setupAutoComplete();
        }
        
        private void setupAutoComplete() {
            // إعداد القائمة المنسدلة للحقول
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                itemView.getContext(),
                android.R.layout.simple_dropdown_item_1line,
                systemFieldsArabic
            );
            
            autoCompleteSystemField.setAdapter(adapter);
            
            // معالج تغيير التحديد
            autoCompleteSystemField.setOnItemClickListener((parent, view, position, id) -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    ColumnMapping mapping = columnMappings.get(adapterPosition);
                    mapping.setSystemField(systemFields[position]);
                    mapping.setSystemFieldArabic(systemFieldsArabic[position]);
                    
                    // تحديث نوع البيانات
                    updateFieldType(mapping, systemFields[position]);
                }
            });
        }
        
        public void bind(ColumnMapping mapping) {
            tvExcelColumn.setText(mapping.getExcelColumn());
            
            // تعيين الحقل المختار
            if (mapping.getSystemFieldArabic() != null && !mapping.getSystemFieldArabic().isEmpty()) {
                autoCompleteSystemField.setText(mapping.getSystemFieldArabic(), false);
            } else {
                autoCompleteSystemField.setText("", false);
            }
            
            // تعيين القيمة الافتراضية
            etDefaultValue.setText(mapping.getDefaultValue());
            
            // معالج تغيير القيمة الافتراضية
            etDefaultValue.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ColumnMapping columnMapping = columnMappings.get(position);
                        columnMapping.setDefaultValue(etDefaultValue.getText().toString());
                    }
                }
            });
            
            // عرض نوع البيانات
            updateFieldType(mapping, mapping.getSystemField());
        }
        
        private void updateFieldType(ColumnMapping mapping, String systemField) {
            String fieldType = getFieldType(systemField);
            tvFieldType.setText(fieldType);
            mapping.setDataType(fieldType);
            
            // تحديد ما إذا كان الحقل مطلوباً
            String[] mandatoryFields = dataType.getMandatoryFields();
            boolean isRequired = false;
            for (String mandatoryField : mandatoryFields) {
                if (mandatoryField.equals(systemField)) {
                    isRequired = true;
                    break;
                }
            }
            mapping.setRequired(isRequired);
            
            // تغيير لون الخلفية للحقول المطلوبة
            if (isRequired) {
                tvExcelColumn.setBackgroundColor(itemView.getContext().getColor(R.color.required_field_bg));
            } else {
                tvExcelColumn.setBackgroundColor(itemView.getContext().getColor(R.color.normal_field_bg));
            }
        }
        
        private String getFieldType(String systemField) {
            if (systemField == null || systemField.isEmpty()) {
                return "غير محدد";
            }
            
            // الحقول الرقمية
            String[] numericFields = {
                "debit_balance", "credit_balance", "total_balance",
                "quantity", "purchase_price", "sale_price", "stock_quantity",
                "opening_balance", "total_amount", "paid_amount", "remaining_amount",
                "amount", "basic_salary"
            };
            
            for (String numericField : numericFields) {
                if (numericField.equals(systemField)) {
                    return "رقم";
                }
            }
            
            // حقول التاريخ
            String[] dateFields = {
                "date", "invoice_date", "payment_date", "hire_date"
            };
            
            for (String dateField : dateFields) {
                if (dateField.equals(systemField)) {
                    return "تاريخ";
                }
            }
            
            // حقول البريد الإلكتروني
            if ("email".equals(systemField)) {
                return "بريد إلكتروني";
            }
            
            // حقول الهاتف
            if ("phone".equals(systemField)) {
                return "هاتف";
            }
            
            // باقي الحقول نصية
            return "نص";
        }
    }
}