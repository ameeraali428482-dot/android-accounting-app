package com.example.myapplication.services;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.myapplication.data.models.ExcelColumnInfo;
import com.example.myapplication.data.models.DatabaseFieldInfo;
import com.example.myapplication.ui.dialogs.ExcelColumnMappingDialog;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * خدمة استيراد البيانات من ملفات Excel
 * تتعامل مع قراءة الملفات وتحليل البيانات وتطبيق التعيينات
 */
public class ExcelImportService {

    private static final String TAG = "ExcelImportService";
    private static final int MAX_PREVIEW_ROWS = 100;
    private static final int SAMPLE_DATA_ROW = 1; // الصف الثاني للعينة (0-indexed)

    private Context context;
    private ExecutorService executorService;

    // Listeners
    public interface OnExcelAnalysisListener {
        void onAnalysisComplete(List<ExcelColumnInfo> columns, int totalRows);
        void onAnalysisError(String error);
        void onProgress(int progress);
    }

    public interface OnDataImportListener {
        void onImportComplete(int importedRows, int skippedRows, int errorRows);
        void onImportError(String error);
        void onProgress(int currentRow, int totalRows);
    }

    public ExcelImportService(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * تحليل ملف Excel واستخراج معلومات الأعمدة
     */
    public void analyzeExcelFile(Uri fileUri, boolean firstRowIsHeader, OnExcelAnalysisListener listener) {
        executorService.submit(() -> {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
                if (inputStream == null) {
                    listener.onAnalysisError("فشل في فتح الملف");
                    return;
                }

                // Determine file type and create appropriate workbook
                Workbook workbook = createWorkbook(inputStream, fileUri.toString());
                if (workbook == null) {
                    listener.onAnalysisError("نوع ملف غير مدعوم");
                    return;
                }

                Sheet sheet = workbook.getSheetAt(0); // Use first sheet
                int totalRows = sheet.getPhysicalNumberOfRows();

                if (totalRows == 0) {
                    listener.onAnalysisError("الملف فارغ");
                    return;
                }

                listener.onProgress(25);

                // Analyze columns
                List<ExcelColumnInfo> columns = analyzeColumns(sheet, firstRowIsHeader);
                
                listener.onProgress(75);

                workbook.close();
                inputStream.close();

                listener.onProgress(100);
                listener.onAnalysisComplete(columns, totalRows);

            } catch (Exception e) {
                Log.e(TAG, "Error analyzing Excel file", e);
                listener.onAnalysisError("خطأ في تحليل الملف: " + e.getMessage());
            }
        });
    }

    /**
     * إنشاء كائن Workbook بناءً على نوع الملف
     */
    private Workbook createWorkbook(InputStream inputStream, String fileName) throws IOException {
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(inputStream);
        } else if (fileName.toLowerCase().endsWith(".xls")) {
            return new HSSFWorkbook(inputStream);
        } else {
            return null;
        }
    }

    /**
     * تحليل أعمدة الورقة واستخراج المعلومات
     */
    private List<ExcelColumnInfo> analyzeColumns(Sheet sheet, boolean firstRowIsHeader) {
        List<ExcelColumnInfo> columns = new ArrayList<>();
        
        Row firstRow = sheet.getRow(0);
        if (firstRow == null) {
            return columns;
        }

        int lastCellNum = firstRow.getLastCellNum();
        
        for (int colIndex = 0; colIndex < lastCellNum; colIndex++) {
            String columnIndex = ExcelColumnInfo.getColumnLetter(colIndex);
            String columnName;
            String sampleData;

            if (firstRowIsHeader) {
                // Use first row as column name
                Cell headerCell = firstRow.getCell(colIndex);
                columnName = getCellStringValue(headerCell);
                
                // Get sample data from second row
                Row sampleRow = sheet.getRow(SAMPLE_DATA_ROW);
                if (sampleRow != null) {
                    Cell sampleCell = sampleRow.getCell(colIndex);
                    sampleData = getCellStringValue(sampleCell);
                } else {
                    sampleData = "";
                }
            } else {
                // Use generic column name
                columnName = "العمود " + columnIndex;
                
                // Get sample data from first row
                Cell sampleCell = firstRow.getCell(colIndex);
                sampleData = getCellStringValue(sampleCell);
            }

            // Create column info
            ExcelColumnInfo columnInfo = new ExcelColumnInfo(
                columnIndex, 
                columnName.isEmpty() ? "العمود " + columnIndex : columnName,
                sampleData,
                null,
                colIndex
            );
            
            // Guess data type based on sample
            columnInfo.guessDataType();
            
            columns.add(columnInfo);
        }

        return columns;
    }

    /**
     * الحصول على قيمة نصية من خلية
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        double numValue = cell.getNumericCellValue();
                        // Check if it's a whole number
                        if (numValue == Math.floor(numValue)) {
                            return String.valueOf((long) numValue);
                        } else {
                            return String.valueOf(numValue);
                        }
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                case BLANK:
                    return "";
                default:
                    return "";
            }
        } catch (Exception e) {
            Log.w(TAG, "Error reading cell value", e);
            return "";
        }
    }

    /**
     * استيراد البيانات من Excel إلى قاعدة البيانات
     */
    public void importData(Uri fileUri, 
                          Map<String, String> columnMappings,
                          ExcelColumnMappingDialog.DataType dataType,
                          boolean skipDuplicates,
                          boolean validateData,
                          boolean firstRowHeader,
                          OnDataImportListener listener) {
        
        executorService.submit(() -> {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
                if (inputStream == null) {
                    listener.onImportError("فشل في فتح الملف");
                    return;
                }

                Workbook workbook = createWorkbook(inputStream, fileUri.toString());
                if (workbook == null) {
                    listener.onImportError("نوع ملف غير مدعوم");
                    return;
                }

                Sheet sheet = workbook.getSheetAt(0);
                int totalRows = sheet.getPhysicalNumberOfRows();
                int startRow = firstRowHeader ? 1 : 0; // Skip header if present
                
                int importedRows = 0;
                int skippedRows = 0;
                int errorRows = 0;

                // Process each row
                for (int rowIndex = startRow; rowIndex < totalRows; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) {
                        skippedRows++;
                        continue;
                    }

                    try {
                        Map<String, Object> rowData = extractRowData(row, columnMappings);
                        
                        if (validateData && !validateRowData(rowData, dataType)) {
                            errorRows++;
                            continue;
                        }

                        if (skipDuplicates && isDuplicateRecord(rowData, dataType)) {
                            skippedRows++;
                            continue;
                        }

                        // Insert data into database
                        if (insertDataToDatabase(rowData, dataType)) {
                            importedRows++;
                        } else {
                            errorRows++;
                        }

                        // Update progress
                        int progress = (int) (((float) (rowIndex - startRow + 1) / (totalRows - startRow)) * 100);
                        listener.onProgress(rowIndex - startRow + 1, totalRows - startRow);

                    } catch (Exception e) {
                        Log.w(TAG, "Error processing row " + rowIndex, e);
                        errorRows++;
                    }
                }

                workbook.close();
                inputStream.close();

                listener.onImportComplete(importedRows, skippedRows, errorRows);

            } catch (Exception e) {
                Log.e(TAG, "Error importing data", e);
                listener.onImportError("خطأ في استيراد البيانات: " + e.getMessage());
            }
        });
    }

    /**
     * استخراج بيانات الصف بناءً على التعيينات
     */
    private Map<String, Object> extractRowData(Row row, Map<String, String> columnMappings) {
        Map<String, Object> rowData = new HashMap<>();

        for (Map.Entry<String, String> mapping : columnMappings.entrySet()) {
            String excelColumn = mapping.getKey();
            String databaseField = mapping.getValue();

            // Convert column letter to index
            int columnIndex = getColumnIndexFromLetter(excelColumn);
            Cell cell = row.getCell(columnIndex);
            
            String cellValue = getCellStringValue(cell);
            
            // Convert empty values to appropriate defaults
            if (cellValue.isEmpty()) {
                cellValue = "0"; // Default for empty cells as requested
            }

            rowData.put(databaseField, cellValue);
        }

        return rowData;
    }

    /**
     * تحويل حرف العمود إلى فهرس رقمي
     */
    private int getColumnIndexFromLetter(String columnLetter) {
        int result = 0;
        for (int i = 0; i < columnLetter.length(); i++) {
            result = result * 26 + (columnLetter.charAt(i) - 'A' + 1);
        }
        return result - 1; // Convert to 0-based index
    }

    /**
     * التحقق من صحة بيانات الصف
     */
    private boolean validateRowData(Map<String, Object> rowData, ExcelColumnMappingDialog.DataType dataType) {
        // TODO: Implement validation logic based on data type
        // For example, check required fields, data formats, etc.
        return true;
    }

    /**
     * التحقق من تكرار السجل
     */
    private boolean isDuplicateRecord(Map<String, Object> rowData, ExcelColumnMappingDialog.DataType dataType) {
        // TODO: Implement duplicate checking logic
        // This would typically involve querying the database to check if a record with 
        // similar key fields already exists
        return false;
    }

    /**
     * إدراج البيانات في قاعدة البيانات
     */
    private boolean insertDataToDatabase(Map<String, Object> rowData, ExcelColumnMappingDialog.DataType dataType) {
        try {
            // TODO: Implement actual database insertion based on data type
            switch (dataType) {
                case CUSTOMERS:
                    return insertCustomer(rowData);
                case SUPPLIERS:
                    return insertSupplier(rowData);
                case ITEMS:
                    return insertItem(rowData);
                case ACCOUNTS:
                    return insertAccount(rowData);
                default:
                    return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error inserting data to database", e);
            return false;
        }
    }

    /**
     * إدراج بيانات العميل
     */
    private boolean insertCustomer(Map<String, Object> rowData) {
        // TODO: Implement customer insertion
        Log.d(TAG, "Inserting customer: " + rowData);
        return true;
    }

    /**
     * إدراج بيانات المورد
     */
    private boolean insertSupplier(Map<String, Object> rowData) {
        // TODO: Implement supplier insertion
        Log.d(TAG, "Inserting supplier: " + rowData);
        return true;
    }

    /**
     * إدراج بيانات الصنف
     */
    private boolean insertItem(Map<String, Object> rowData) {
        // TODO: Implement item insertion
        Log.d(TAG, "Inserting item: " + rowData);
        return true;
    }

    /**
     * إدراج بيانات الحساب
     */
    private boolean insertAccount(Map<String, Object> rowData) {
        // TODO: Implement account insertion
        Log.d(TAG, "Inserting account: " + rowData);
        return true;
    }

    /**
     * إغلاق الخدمة وتحرير الموارد
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}