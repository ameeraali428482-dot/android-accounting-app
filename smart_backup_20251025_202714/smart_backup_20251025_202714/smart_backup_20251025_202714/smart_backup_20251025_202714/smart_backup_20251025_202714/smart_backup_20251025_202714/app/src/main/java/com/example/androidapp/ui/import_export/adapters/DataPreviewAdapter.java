package com.example.androidapp.ui.import_export.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.ui.import_export.models.ImportPreviewData;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.Map;

/**
 * محول لعرض معاينة البيانات قبل الاستيراد
 */
public class DataPreviewAdapter extends RecyclerView.Adapter<DataPreviewAdapter.ViewHolder> {
    
    private final List<ImportPreviewData> previewDataList;
    
    public DataPreviewAdapter(List<ImportPreviewData> previewDataList) {
        this.previewDataList = previewDataList;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data_preview, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImportPreviewData previewData = previewDataList.get(position);
        holder.bind(previewData);
    }
    
    @Override
    public int getItemCount() {
        return previewDataList.size();
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        
        private final MaterialCardView cardView;
        private final TextView tvRowNumber;
        private final Chip chipStatus;
        private final LinearLayout layoutFields;
        private final TextView tvErrorMessage;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.cardView);
            tvRowNumber = itemView.findViewById(R.id.tvRowNumber);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            layoutFields = itemView.findViewById(R.id.layoutFields);
            tvErrorMessage = itemView.findViewById(R.id.tvErrorMessage);
        }
        
        public void bind(ImportPreviewData previewData) {
            // عرض رقم الصف
            tvRowNumber.setText("الصف " + previewData.getRowNumber());
            
            // عرض حالة السجل
            setupStatusChip(previewData);
            
            // عرض الحقول
            displayFields(previewData);
            
            // عرض رسائل الخطأ
            displayErrorMessage(previewData);
            
            // تلوين البطاقة حسب الحالة
            setCardColor(previewData);
        }
        
        private void setupStatusChip(ImportPreviewData previewData) {
            ImportPreviewData.ImportStatus status = previewData.getStatus();
            chipStatus.setText(status.getArabicName());
            
            // تلوين الـ chip حسب الحالة
            switch (status) {
                case VALID:
                    chipStatus.setChipBackgroundColorResource(R.color.success_color);
                    chipStatus.setTextColor(Color.WHITE);
                    break;
                case WARNING:
                    chipStatus.setChipBackgroundColorResource(R.color.warning_color);
                    chipStatus.setTextColor(Color.WHITE);
                    break;
                case ERROR:
                    chipStatus.setChipBackgroundColorResource(R.color.error_color);
                    chipStatus.setTextColor(Color.WHITE);
                    break;
                case DUPLICATE:
                    chipStatus.setChipBackgroundColorResource(R.color.duplicate_color);
                    chipStatus.setTextColor(Color.WHITE);
                    break;
            }
        }
        
        private void displayFields(ImportPreviewData previewData) {
            layoutFields.removeAllViews();
            
            Map<String, String> fieldValues = previewData.getFieldValues();
            Map<String, String> originalValues = previewData.getOriginalValues();
            
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                String originalValue = originalValues.get(fieldName);
                
                // إنشاء عرض للحقل
                View fieldView = createFieldView(fieldName, fieldValue, originalValue);
                layoutFields.addView(fieldView);
            }
        }
        
        private View createFieldView(String fieldName, String fieldValue, String originalValue) {
            LinearLayout fieldLayout = new LinearLayout(itemView.getContext());
            fieldLayout.setOrientation(LinearLayout.HORIZONTAL);
            fieldLayout.setPadding(8, 4, 8, 4);
            
            // اسم الحقل
            TextView tvFieldName = new TextView(itemView.getContext());
            tvFieldName.setText(getArabicFieldName(fieldName) + ":");
            tvFieldName.setTextSize(12);
            tvFieldName.setTextColor(itemView.getContext().getColor(R.color.secondary_text));
            tvFieldName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            
            // قيمة الحقل
            TextView tvFieldValue = new TextView(itemView.getContext());
            tvFieldValue.setText(fieldValue != null ? fieldValue : "فارغ");
            tvFieldValue.setTextSize(12);
            tvFieldValue.setTextColor(itemView.getContext().getColor(R.color.primary_text));
            tvFieldValue.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
            
            // تمييز القيم المعدلة
            if (originalValue != null && !originalValue.equals(fieldValue)) {
                tvFieldValue.setBackgroundColor(itemView.getContext().getColor(R.color.modified_field_bg));
                tvFieldValue.setPadding(4, 2, 4, 2);
            }
            
            fieldLayout.addView(tvFieldName);
            fieldLayout.addView(tvFieldValue);
            
            return fieldLayout;
        }
        
        private void displayErrorMessage(ImportPreviewData previewData) {
            if (previewData.getErrorMessage() != null && !previewData.getErrorMessage().isEmpty()) {
                tvErrorMessage.setText(previewData.getErrorMessage());
                tvErrorMessage.setVisibility(View.VISIBLE);
            } else {
                tvErrorMessage.setVisibility(View.GONE);
            }
        }
        
        private void setCardColor(ImportPreviewData previewData) {
            switch (previewData.getStatus()) {
                case VALID:
                    cardView.setStrokeColor(itemView.getContext().getColor(R.color.success_color));
                    cardView.setStrokeWidth(2);
                    break;
                case WARNING:
                    cardView.setStrokeColor(itemView.getContext().getColor(R.color.warning_color));
                    cardView.setStrokeWidth(2);
                    break;
                case ERROR:
                    cardView.setStrokeColor(itemView.getContext().getColor(R.color.error_color));
                    cardView.setStrokeWidth(3);
                    break;
                case DUPLICATE:
                    cardView.setStrokeColor(itemView.getContext().getColor(R.color.duplicate_color));
                    cardView.setStrokeWidth(2);
                    break;
                default:
                    cardView.setStrokeWidth(0);
                    break;
            }
        }
        
        private String getArabicFieldName(String fieldName) {
            switch (fieldName) {
                case "name":
                    return "الاسم";
                case "phone":
                    return "الهاتف";
                case "area":
                    return "المنطقة";
                case "debit_balance":
                    return "له (مديون)";
                case "credit_balance":
                    return "عليه (دائن)";
                case "total_balance":
                    return "الإجمالي";
                case "nickname":
                    return "اللقب";
                case "address":
                    return "العنوان";
                case "email":
                    return "البريد الإلكتروني";
                case "unit":
                    return "الوحدة";
                case "quantity":
                    return "الكمية";
                case "purchase_price":
                    return "سعر الشراء";
                case "sale_price":
                    return "سعر البيع";
                case "stock_quantity":
                    return "كمية المخزون";
                case "date":
                    return "التاريخ";
                case "agent":
                    return "الوكيل";
                case "category":
                    return "الفئة";
                case "minimum_stock":
                    return "الحد الأدنى";
                case "account_name":
                    return "اسم الحساب";
                case "account_type":
                    return "نوع الحساب";
                case "opening_balance":
                    return "الرصيد الافتتاحي";
                case "currency":
                    return "العملة";
                case "description":
                    return "الوصف";
                case "status":
                    return "الحالة";
                case "employee_name":
                    return "اسم الموظف";
                case "position":
                    return "المنصب";
                case "basic_salary":
                    return "الراتب الأساسي";
                case "hire_date":
                    return "تاريخ التوظيف";
                default:
                    return fieldName;
            }
        }
    }
}