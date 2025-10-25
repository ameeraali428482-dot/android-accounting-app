package com.example.androidapp.ui.import_export.models;

import java.util.HashMap;
import java.util.Map;

/**
 * نموذج بيانات المعاينة قبل الاستيراد
 */
public class ImportPreviewData {
    
    private int rowNumber;                      // رقم الصف في Excel
    private Map<String, String> fieldValues;   // قيم الحقول
    private Map<String, String> originalValues; // القيم الأصلية من Excel
    private boolean hasErrors;                  // هل يحتوي على أخطاء
    private String errorMessage;               // رسالة الخطأ
    private ImportStatus status;               // حالة السجل
    
    public enum ImportStatus {
        VALID("صحيح"),
        WARNING("تحذير"),
        ERROR("خطأ"),
        DUPLICATE("مكرر");
        
        private final String arabicName;
        
        ImportStatus(String arabicName) {
            this.arabicName = arabicName;
        }
        
        public String getArabicName() {
            return arabicName;
        }
    }
    
    public ImportPreviewData() {
        this.fieldValues = new HashMap<>();
        this.originalValues = new HashMap<>();
        this.hasErrors = false;
        this.status = ImportStatus.VALID;
    }
    
    public ImportPreviewData(int rowNumber) {
        this();
        this.rowNumber = rowNumber;
    }
    
    // Getters and Setters
    
    public int getRowNumber() {
        return rowNumber;
    }
    
    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }
    
    public Map<String, String> getFieldValues() {
        return fieldValues;
    }
    
    public void setFieldValues(Map<String, String> fieldValues) {
        this.fieldValues = fieldValues;
    }
    
    public Map<String, String> getOriginalValues() {
        return originalValues;
    }
    
    public void setOriginalValues(Map<String, String> originalValues) {
        this.originalValues = originalValues;
    }
    
    public boolean hasErrors() {
        return hasErrors;
    }
    
    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
        if (hasErrors && status == ImportStatus.VALID) {
            this.status = ImportStatus.ERROR;
        }
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.hasErrors = (errorMessage != null && !errorMessage.isEmpty());
    }
    
    public ImportStatus getStatus() {
        return status;
    }
    
    public void setStatus(ImportStatus status) {
        this.status = status;
    }
    
    /**
     * إضافة قيمة حقل
     */
    public void addFieldValue(String fieldName, String value, String originalValue) {
        fieldValues.put(fieldName, value);
        originalValues.put(fieldName, originalValue);
    }
    
    /**
     * الحصول على قيمة حقل
     */
    public String getFieldValue(String fieldName) {
        return fieldValues.get(fieldName);
    }
    
    /**
     * الحصول على القيمة الأصلية
     */
    public String getOriginalValue(String fieldName) {
        return originalValues.get(fieldName);
    }
    
    /**
     * التحقق من وجود قيمة للحقل
     */
    public boolean hasFieldValue(String fieldName) {
        return fieldValues.containsKey(fieldName);
    }
    
    /**
     * التحقق من أن الحقل فارغ
     */
    public boolean isFieldEmpty(String fieldName) {
        String value = fieldValues.get(fieldName);
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * إضافة تحذير
     */
    public void addWarning(String warning) {
        if (status == ImportStatus.VALID) {
            status = ImportStatus.WARNING;
        }
        
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = warning;
        } else {
            errorMessage += "; " + warning;
        }
    }
    
    /**
     * إضافة خطأ
     */
    public void addError(String error) {
        status = ImportStatus.ERROR;
        hasErrors = true;
        
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = error;
        } else {
            errorMessage += "; " + error;
        }
    }
    
    /**
     * التحقق من صحة البيانات الأساسية
     */
    public boolean isValidForImport() {
        return status != ImportStatus.ERROR && !hasErrors;
    }
    
    /**
     * الحصول على ملخص السجل
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("الصف ").append(rowNumber).append(" - ");
        summary.append(status.getArabicName());
        
        if (errorMessage != null && !errorMessage.isEmpty()) {
            summary.append(": ").append(errorMessage);
        }
        
        return summary.toString();
    }
    
    @Override
    public String toString() {
        return "ImportPreviewData{" +
                "rowNumber=" + rowNumber +
                ", status=" + status +
                ", hasErrors=" + hasErrors +
                ", fieldCount=" + fieldValues.size() +
                '}';
    }
}