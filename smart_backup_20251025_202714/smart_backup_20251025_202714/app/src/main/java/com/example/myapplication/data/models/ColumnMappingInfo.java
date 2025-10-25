package com.example.myapplication.data.models;

import com.example.myapplication.databinding.ItemColumnMappingBinding;

import java.io.Serializable;

/**
 * معلومات تعيين العمود
 * يربط بين عمود Excel وحقل قاعدة البيانات مع واجهة المستخدم
 */
public class ColumnMappingInfo implements Serializable {
    
    private ExcelColumnInfo excelColumn;        // معلومات عمود Excel
    private DatabaseFieldInfo databaseField;   // معلومات حقل قاعدة البيانات
    private ItemColumnMappingBinding binding;   // ربط واجهة المستخدم
    private boolean isMapped;                   // هل تم تعيين العمود
    private String mappingStatus;               // حالة التعيين
    private boolean isValid;                    // هل التعيين صحيح
    private String errorMessage;                // رسالة الخطأ إن وجدت
    
    // حالات التعيين
    public enum MappingStatus {
        NOT_MAPPED("غير معين", "#9E9E9E"),
        MAPPED("معين", "#4CAF50"),
        SUGGESTED("مقترح", "#FF9800"),
        ERROR("خطأ", "#F44336"),
        VALIDATED("محقق", "#2196F3");
        
        public final String displayName;
        public final String color;
        
        MappingStatus(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
    }
    
    private MappingStatus status = MappingStatus.NOT_MAPPED;
    
    public ColumnMappingInfo() {
    }
    
    public ColumnMappingInfo(ExcelColumnInfo excelColumn, ItemColumnMappingBinding binding) {
        this.excelColumn = excelColumn;
        this.binding = binding;
        this.isMapped = false;
        this.isValid = false;
        this.status = MappingStatus.NOT_MAPPED;
    }
    
    public ColumnMappingInfo(ExcelColumnInfo excelColumn, DatabaseFieldInfo databaseField) {
        this.excelColumn = excelColumn;
        this.databaseField = databaseField;
        this.isMapped = true;
        this.status = MappingStatus.MAPPED;
        this.isValid = validateMapping();
    }
    
    // Getters and Setters
    public ExcelColumnInfo getExcelColumn() {
        return excelColumn;
    }
    
    public void setExcelColumn(ExcelColumnInfo excelColumn) {
        this.excelColumn = excelColumn;
    }
    
    public DatabaseFieldInfo getDatabaseField() {
        return databaseField;
    }
    
    public void setDatabaseField(DatabaseFieldInfo databaseField) {
        this.databaseField = databaseField;
        this.isMapped = databaseField != null;
        if (isMapped) {
            this.status = MappingStatus.MAPPED;
            this.isValid = validateMapping();
        } else {
            this.status = MappingStatus.NOT_MAPPED;
            this.isValid = false;
        }
    }
    
    public ItemColumnMappingBinding getBinding() {
        return binding;
    }
    
    public void setBinding(ItemColumnMappingBinding binding) {
        this.binding = binding;
    }
    
    public boolean isMapped() {
        return isMapped;
    }
    
    public void setMapped(boolean mapped) {
        isMapped = mapped;
        if (!mapped) {
            this.databaseField = null;
            this.status = MappingStatus.NOT_MAPPED;
            this.isValid = false;
        }
    }
    
    public String getMappingStatus() {
        return mappingStatus;
    }
    
    public void setMappingStatus(String mappingStatus) {
        this.mappingStatus = mappingStatus;
    }
    
    public boolean isValid() {
        return isValid;
    }
    
    public void setValid(boolean valid) {
        isValid = valid;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public MappingStatus getStatus() {
        return status;
    }
    
    public void setStatus(MappingStatus status) {
        this.status = status;
    }
    
    /**
     * التحقق من صحة التعيين
     */
    public boolean validateMapping() {
        if (!isMapped || databaseField == null) {
            this.errorMessage = "لم يتم تعيين العمود";
            this.status = MappingStatus.NOT_MAPPED;
            return false;
        }
        
        // Check if Excel column has data when database field is required
        if (databaseField.isRequired() && !excelColumn.isHasData()) {
            this.errorMessage = "العمود مطلوب ولكنه لا يحتوي على بيانات";
            this.status = MappingStatus.ERROR;
            return false;
        }
        
        // Validate sample data if available
        if (excelColumn.getSampleData() != null && !excelColumn.getSampleData().trim().isEmpty()) {
            if (!databaseField.validateValue(excelColumn.getSampleData())) {
                this.errorMessage = "البيانات النموذجية لا تتطابق مع نوع الحقل المتوقع";
                this.status = MappingStatus.ERROR;
                return false;
            }
        }
        
        this.errorMessage = null;
        this.status = MappingStatus.VALIDATED;
        return true;
    }
    
    /**
     * اقتراح تعيين تلقائي بناءً على أسماء الأعمدة
     */
    public boolean suggestMapping(DatabaseFieldInfo suggestedField) {
        if (suggestedField != null) {
            this.databaseField = suggestedField;
            this.isMapped = true;
            this.status = MappingStatus.SUGGESTED;
            this.isValid = validateMapping();
            return true;
        }
        return false;
    }
    
    /**
     * تأكيد التعيين المقترح
     */
    public void confirmSuggestedMapping() {
        if (status == MappingStatus.SUGGESTED) {
            this.status = MappingStatus.MAPPED;
            this.isValid = validateMapping();
        }
    }
    
    /**
     * إلغاء التعيين
     */
    public void clearMapping() {
        this.databaseField = null;
        this.isMapped = false;
        this.isValid = false;
        this.status = MappingStatus.NOT_MAPPED;
        this.errorMessage = null;
    }
    
    /**
     * الحصول على نص حالة التعيين
     */
    public String getStatusText() {
        return status.displayName;
    }
    
    /**
     * الحصول على لون حالة التعيين
     */
    public String getStatusColor() {
        return status.color;
    }
    
    /**
     * الحصول على وصف مفصل للتعيين
     */
    public String getDetailedDescription() {
        StringBuilder desc = new StringBuilder();
        
        desc.append("عمود Excel: ").append(excelColumn.getName()).append(" (").append(excelColumn.getIndex()).append(")");
        
        if (isMapped && databaseField != null) {
            desc.append("\nحقل قاعدة البيانات: ").append(databaseField.getDisplayName());
            desc.append("\nحالة التعيين: ").append(getStatusText());
            
            if (errorMessage != null) {
                desc.append("\nخطأ: ").append(errorMessage);
            }
        } else {
            desc.append("\nغير معين");
        }
        
        return desc.toString();
    }
    
    /**
     * تحويل البيانات من Excel إلى قاعدة البيانات
     */
    public Object transformData(String excelValue) {
        if (!isMapped || databaseField == null) {
            return excelValue;
        }
        
        return databaseField.convertValue(excelValue);
    }
    
    @Override
    public String toString() {
        return "ColumnMappingInfo{" +
                "excelColumn=" + (excelColumn != null ? excelColumn.getName() : "null") +
                ", databaseField=" + (databaseField != null ? databaseField.getDisplayName() : "null") +
                ", isMapped=" + isMapped +
                ", status=" + status +
                ", isValid=" + isValid +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ColumnMappingInfo that = (ColumnMappingInfo) o;
        
        if (excelColumn != null ? !excelColumn.equals(that.excelColumn) : that.excelColumn != null)
            return false;
        return databaseField != null ? databaseField.equals(that.databaseField) : that.databaseField == null;
    }
    
    @Override
    public int hashCode() {
        int result = excelColumn != null ? excelColumn.hashCode() : 0;
        result = 31 * result + (databaseField != null ? databaseField.hashCode() : 0);
        return result;
    }
}