package com.example.myapplication.data.models;

import java.io.Serializable;

/**
 * معلومات عمود Excel
 * يحتوي على بيانات العمود من ملف Excel
 */
public class ExcelColumnInfo implements Serializable {
    
    private String index;        // فهرس العمود (A, B, C, etc.)
    private String name;         // اسم العمود
    private String sampleData;   // عينة من البيانات
    private String dataType;     // نوع البيانات المتوقع
    private boolean hasData;     // هل يحتوي على بيانات
    private int columnPosition;  // موقع العمود الرقمي
    
    public ExcelColumnInfo() {
    }
    
    public ExcelColumnInfo(String index, String name, String sampleData) {
        this.index = index;
        this.name = name;
        this.sampleData = sampleData;
        this.hasData = sampleData != null && !sampleData.trim().isEmpty();
    }
    
    public ExcelColumnInfo(String index, String name, String sampleData, String dataType, int columnPosition) {
        this.index = index;
        this.name = name;
        this.sampleData = sampleData;
        this.dataType = dataType;
        this.columnPosition = columnPosition;
        this.hasData = sampleData != null && !sampleData.trim().isEmpty();
    }
    
    // Getters and Setters
    public String getIndex() {
        return index;
    }
    
    public void setIndex(String index) {
        this.index = index;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSampleData() {
        return sampleData;
    }
    
    public void setSampleData(String sampleData) {
        this.sampleData = sampleData;
        this.hasData = sampleData != null && !sampleData.trim().isEmpty();
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public boolean isHasData() {
        return hasData;
    }
    
    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }
    
    public int getColumnPosition() {
        return columnPosition;
    }
    
    public void setColumnPosition(int columnPosition) {
        this.columnPosition = columnPosition;
    }
    
    /**
     * تحويل الرقم إلى حرف العمود (A, B, C, etc.)
     */
    public static String getColumnLetter(int columnIndex) {
        StringBuilder result = new StringBuilder();
        while (columnIndex >= 0) {
            result.insert(0, (char) ('A' + columnIndex % 26));
            columnIndex = columnIndex / 26 - 1;
        }
        return result.toString();
    }
    
    /**
     * تخمين نوع البيانات من العينة
     */
    public void guessDataType() {
        if (sampleData == null || sampleData.trim().isEmpty()) {
            this.dataType = "STRING";
            return;
        }
        
        String trimmed = sampleData.trim();
        
        // Check if numeric
        try {
            Double.parseDouble(trimmed);
            this.dataType = "NUMBER";
            return;
        } catch (NumberFormatException e) {
            // Not a number
        }
        
        // Check if date-like
        if (trimmed.matches("\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}") ||
            trimmed.matches("\\d{4}[/-]\\d{1,2}[/-]\\d{1,2}")) {
            this.dataType = "DATE";
            return;
        }
        
        // Check if email-like
        if (trimmed.matches(".*@.*\\..*")) {
            this.dataType = "EMAIL";
            return;
        }
        
        // Check if phone-like
        if (trimmed.matches("^[+]?[0-9\\s\\-\\(\\)]+$") && trimmed.length() >= 8) {
            this.dataType = "PHONE";
            return;
        }
        
        // Default to string
        this.dataType = "STRING";
    }
    
    @Override
    public String toString() {
        return "ExcelColumnInfo{" +
                "index='" + index + '\'' +
                ", name='" + name + '\'' +
                ", sampleData='" + sampleData + '\'' +
                ", dataType='" + dataType + '\'' +
                ", hasData=" + hasData +
                ", columnPosition=" + columnPosition +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ExcelColumnInfo that = (ExcelColumnInfo) o;
        
        return index != null ? index.equals(that.index) : that.index == null;
    }
    
    @Override
    public int hashCode() {
        return index != null ? index.hashCode() : 0;
    }
}