package com.example.myapplication.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * محول عرض معاينة البيانات المستوردة من Excel
 * يعرض عينة من البيانات بناءً على التعيينات المحددة
 */
public class ExcelDataPreviewAdapter extends RecyclerView.Adapter<ExcelDataPreviewAdapter.PreviewViewHolder> {

    private List<Map<String, String>> previewData = new ArrayList<>();
    private List<String> fieldNames = new ArrayList<>();

    public ExcelDataPreviewAdapter() {
    }

    /**
     * تحديث البيانات المعروضة
     */
    public void updateData(List<Map<String, String>> data, List<String> fields) {
        this.previewData.clear();
        this.fieldNames.clear();
        
        if (data != null) {
            this.previewData.addAll(data);
        }
        
        if (fields != null) {
            this.fieldNames.addAll(fields);
        }
        
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_excel_preview_row, parent, false);
        return new PreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        if (position < previewData.size()) {
            Map<String, String> rowData = previewData.get(position);
            holder.bind(rowData, fieldNames, position + 1);
        }
    }

    @Override
    public int getItemCount() {
        return previewData.size();
    }

    /**
     * ViewHolder لعرض صف واحد من البيانات المعاينة
     */
    static class PreviewViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvRowNumber;
        private ViewGroup containerFields;

        public PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRowNumber = itemView.findViewById(R.id.tv_row_number);
            containerFields = itemView.findViewById(R.id.container_fields);
        }

        public void bind(Map<String, String> rowData, List<String> fieldNames, int rowNumber) {
            // Set row number
            tvRowNumber.setText(String.valueOf(rowNumber));
            
            // Clear previous field views
            containerFields.removeAllViews();
            
            // Add field views
            for (String fieldName : fieldNames) {
                String value = rowData.get(fieldName);
                if (value == null) value = "";
                
                View fieldView = createFieldView(fieldName, value);
                containerFields.addView(fieldView);
            }
        }
        
        private View createFieldView(String fieldName, String value) {
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
            View fieldView = inflater.inflate(R.layout.item_preview_field, containerFields, false);
            
            TextView tvFieldName = fieldView.findViewById(R.id.tv_field_name);
            TextView tvFieldValue = fieldView.findViewById(R.id.tv_field_value);
            
            tvFieldName.setText(fieldName);
            tvFieldValue.setText(value);
            
            return fieldView;
        }
    }
}