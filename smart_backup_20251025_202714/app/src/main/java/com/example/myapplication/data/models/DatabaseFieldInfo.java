package com.example.myapplication.data.models;

import java.io.Serializable;

/**
 * معلومات حقل قاعدة البيانات
 * يحتوي على معرف الحقل واسمه المعروض
 */
public class DatabaseFieldInfo implements Serializable {
    
    private String value;           // القيمة الفعلية للحقل (اسم العمود في قاعدة البيانات)
    private String displayName;     // الاسم المعروض للمستخدم
    private String dataType;        // نوع البيانات المتوقع
    private boolean required;       // هل الحقل مطلوب
    private String description;     // وصف الحقل
    private String defaultValue;    // القيمة الافتراضية
    private String validationRule;  // قاعدة التحقق
    
    public DatabaseFieldInfo() {
    }
    
    public DatabaseFieldInfo(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }
    
    public DatabaseFieldInfo(String value, String displayName, String dataType) {
        this.value = value;
        this.displayName = displayName;
        this.dataType = dataType;
    }
    
    public DatabaseFieldInfo(String value, String displayName, String dataType, boolean required) {
        this.value = value;
        this.displayName = displayName;
        this.dataType = dataType;
        this.required = required;
    }
    
    public DatabaseFieldInfo(String value, String displayName, String dataType, 
                           boolean required, String description, String defaultValue) {
        this.value = value;
        this.displayName = displayName;
        this.dataType = dataType;
        this.required = required;
        this.description = description;
        this.defaultValue = defaultValue;
    }
    
    // Getters and Setters
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public String getValidationRule() {
        return validationRule;
    }
    
    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }
    
    /**
     * التحقق من صحة قيمة الحقل
     */
    public boolean validateValue(String value) {
        // Check if required field is empty
        if (required && (value == null || value.trim().isEmpty())) {
            return false;
        }
        
        // If empty and not required, it's valid
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        
        // Validate based on data type
        if (dataType != null) {
            switch (dataType.toUpperCase()) {
                case "NUMBER":
                case "DOUBLE":
                case "FLOAT":
                    try {
                        Double.parseDouble(value.trim());
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    
                case "INTEGER":
                case "INT":
                    try {
                        Integer.parseInt(value.trim());
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    
                case "EMAIL":
                    return value.trim().matches(".*@.*\\..*");
                    
                case "PHONE":
                    return value.trim().matches("^[+]?[0-9\\s\\-\\(\\)]+$") && 
                           value.trim().length() >= 8;
                           
                case "DATE":
                    return value.trim().matches("\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}") ||
                           value.trim().matches("\\d{4}[/-]\\d{1,2}[/-]\\d{1,2}");
                           
                case "STRING":
                case "TEXT":
                default:
                    return true; // Any string is valid for text fields
            }
        }
        
        // Apply custom validation rule if exists
        if (validationRule != null && !validationRule.isEmpty()) {
            try {
                return value.trim().matches(validationRule);
            } catch (Exception e) {
                return true; // If regex fails, assume valid
            }
        }
        
        return true;
    }
    
    /**
     * تحويل القيمة إلى النوع المناسب
     */
    public Object convertValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue != null ? defaultValue : 
                   (dataType != null && dataType.contains("NUMBER") ? "0" : "");
        }
        
        String trimmed = value.trim();
        
        if (dataType != null) {
            switch (dataType.toUpperCase()) {
                case "NUMBER":
                case "DOUBLE":
                case "FLOAT":
                    try {
                        return Double.parseDouble(trimmed);
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                    
                case "INTEGER":
                case "INT":
                    try {
                        return Integer.parseInt(trimmed);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                    
                default:
                    return trimmed;
            }
        }
        
        return trimmed;
    }
    
    /**
     * الحصول على وصف مفصل للحقل
     */
    public String getDetailedDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append(displayName);
        
        if (required) {
            desc.append(" (مطلوب)");
        }
        
        if (dataType != null) {
            desc.append(" - نوع: ").append(dataType);
        }
        
        if (description != null && !description.isEmpty()) {
            desc.append("\n").append(description);
        }
        
        if (defaultValue != null && !defaultValue.isEmpty()) {
            desc.append("\nالقيمة الافتراضية: ").append(defaultValue);
        }
        
        return desc.toString();
    }
    
    @Override
    public String toString() {
        return "DatabaseFieldInfo{" +
                "value='" + value + '\'' +
                ", displayName='" + displayName + '\'' +
                ", dataType='" + dataType + '\'' +
                ", required=" + required +
                ", description='" + description + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", validationRule='" + validationRule + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        DatabaseFieldInfo that = (DatabaseFieldInfo) o;
        
        return value != null ? value.equals(that.value) : that.value == null;
    }
    
    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}