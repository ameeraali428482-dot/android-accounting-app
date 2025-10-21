package com.example.androidapp.ui.import_export.models;

/**
 * نموذج ربط الأعمدة بين ملف Excel وحقول النظام
 */
public class ColumnMapping {
    
    private String excelColumn;           // اسم العمود في Excel
    private String systemField;          // اسم الحقل في النظام
    private String systemFieldArabic;    // الاسم العربي للحقل
    private boolean isRequired;          // هل الحقل مطلوب
    private boolean isMapped;            // هل تم ربط العمود
    private String dataType;             // نوع البيانات (text, number, date, etc.)
    private String defaultValue;         // القيمة الافتراضية للخانات الفارغة
    private boolean useDefaultForEmpty;  // استخدام القيمة الافتراضية للخانات الفارغة
    
    public ColumnMapping() {
        this.isRequired = false;
        this.isMapped = false;
        this.useDefaultForEmpty = true;
        this.defaultValue = "0";
    }
    
    public ColumnMapping(String excelColumn, String systemField) {
        this();
        this.excelColumn = excelColumn;
        this.systemField = systemField;
        this.isMapped = true;
    }
    
    // Getters and Setters
    
    public String getExcelColumn() {
        return excelColumn;
    }
    
    public void setExcelColumn(String excelColumn) {
        this.excelColumn = excelColumn;
    }
    
    public String getSystemField() {
        return systemField;
    }
    
    public void setSystemField(String systemField) {
        this.systemField = systemField;
        this.isMapped = (systemField != null && !systemField.isEmpty());
    }
    
    public String getSystemFieldArabic() {
        return systemFieldArabic;
    }
    
    public void setSystemFieldArabic(String systemFieldArabic) {
        this.systemFieldArabic = systemFieldArabic;
    }
    
    public boolean isRequired() {
        return isRequired;
    }
    
    public void setRequired(boolean required) {
        isRequired = required;
    }
    
    public boolean isMapped() {
        return isMapped;
    }
    
    public void setMapped(boolean mapped) {
        isMapped = mapped;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public boolean isUseDefaultForEmpty() {
        return useDefaultForEmpty;
    }
    
    public void setUseDefaultForEmpty(boolean useDefaultForEmpty) {
        this.useDefaultForEmpty = useDefaultForEmpty;
    }
    
    /**
     * التحقق من صحة الربط
     */
    public boolean isValidMapping() {
        return isMapped && systemField != null && !systemField.trim().isEmpty();
    }
    
    /**
     * الحصول على اسم العمود للعرض
     */
    public String getDisplayName() {
        if (systemFieldArabic != null && !systemFieldArabic.isEmpty()) {
            return systemFieldArabic;
        }
        return systemField != null ? systemField : "غير محدد";
    }
    
    @Override
    public String toString() {
        return "ColumnMapping{" +
                "excelColumn='" + excelColumn + '\'' +
                ", systemField='" + systemField + '\'' +
                ", isMapped=" + isMapped +
                ", isRequired=" + isRequired +
                '}';
    }
}