package com.example.androidapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.ui.import_export.models.ColumnMapping;
import com.example.androidapp.ui.import_export.models.ImportDataType;
import com.example.androidapp.ui.import_export.models.ImportPreviewData;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * مساعد استيراد ملفات Excel بذكاء وفعالية
 * يوفر أدوات شاملة لقراءة وتحليل واستيراد بيانات Excel
 * 
 * @author MiniMax Agent
 * @version 1.0
 * @since 2025-10-20
 */
public class ExcelImportHelper {
    
    private static final String TAG = "ExcelImportHelper";
    
    private final Context context;
    private final AppDatabase database;
    private final ExecutorService executorService;
    
    // خريطة الكلمات المفتاحية للاكتشاف التلقائي
    private final Map<String, String[]> detectionKeywords;
    
    public ExcelImportHelper(Context context) {
        this.context = context;
        this.database = AppDatabase.getDatabase(context);
        this.executorService = Executors.newFixedThreadPool(2);
        
        initializeDetectionKeywords();
    }
    
    /**
     * تهيئة الكلمات المفتاحية للاكتشاف التلقائي
     */
    private void initializeDetectionKeywords() {
        detectionKeywords = new HashMap<>();
        
        // كلمات مفتاحية للأسماء
        detectionKeywords.put("name", new String[]{
            "اسم", "الاسم", "name", "اسم العميل", "اسم المورد", "اسم الصنف", "اسم الموظف"
        });
        
        // كلمات مفتاحية للهاتف
        detectionKeywords.put("phone", new String[]{
            "هاتف", "الهاتف", "phone", "رقم", "تليفون", "جوال", "موبايل"
        });
        
        // كلمات مفتاحية للمنطقة
        detectionKeywords.put("area", new String[]{
            "منطقة", "المنطقة", "area", "محافظة", "مدينة", "العنوان"
        });
        
        // كلمات مفتاحية للرصيد المدين
        detectionKeywords.put("debit_balance", new String[]{
            "له", "مديون", "debit", "دين", "الدين"
        });
        
        // كلمات مفتاحية للرصيد الدائن
        detectionKeywords.put("credit_balance", new String[]{
            "عليه", "دائن", "credit", "ائتمان"
        });
        
        // كلمات مفتاحية للإجمالي
        detectionKeywords.put("total_balance", new String[]{
            "إجمالي", "الإجمالي", "total", "المجموع", "الرصيد"
        });
        
        // كلمات مفتاحية للوحدة
        detectionKeywords.put("unit", new String[]{
            "وحدة", "الوحدة", "unit", "وحدة القياس"
        });
        
        // كلمات مفتاحية للكمية
        detectionKeywords.put("quantity", new String[]{
            "كمية", "الكمية", "quantity", "عدد", "العدد"
        });
        
        // كلمات مفتاحية لسعر الشراء
        detectionKeywords.put("purchase_price", new String[]{
            "سعر الشراء", "شراء", "purchase", "تكلفة"
        });
        
        // كلمات مفتاحية لسعر البيع
        detectionKeywords.put("sale_price", new String[]{
            "سعر البيع", "بيع", "sale", "سعر"
        });
    }
    
    /**
     * الحصول على اسم الملف من URI
     */
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting file name", e);
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    
    /**
     * الحصول على عناوين الأعمدة من ملف Excel
     */
    public List<String> getExcelHeaders(Uri fileUri) throws Exception {
        List<String> headers = new ArrayList<>();
        
        try (InputStream inputStream = context.getContentResolver().openInputStream(fileUri)) {
            Workbook workbook = createWorkbook(inputStream, getFileName(fileUri));
            Sheet sheet = workbook.getSheetAt(0);
            
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                for (Cell cell : headerRow) {
                    String headerValue = getCellValueAsString(cell);
                    headers.add(headerValue != null ? headerValue.trim() : "");
                }
            }
            
            workbook.close();
        }
        
        return headers;
    }
    
    /**
     * إنشاء كائن Workbook حسب نوع الملف
     */
    private Workbook createWorkbook(InputStream inputStream, String fileName) throws Exception {
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(inputStream);
        } else if (fileName.toLowerCase().endsWith(".xls")) {
            return new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("نوع ملف غير مدعوم: " + fileName);
        }
    }
    
    /**
     * اكتشاف تلقائي لنوع الحقل بناءً على اسم العمود
     */
    public String detectSystemField(String excelColumn, ImportDataType dataType) {
        if (excelColumn == null || excelColumn.trim().isEmpty()) {
            return "";
        }
        
        String columnLower = excelColumn.toLowerCase().trim();
        
        // البحث في الكلمات المفتاحية
        for (Map.Entry<String, String[]> entry : detectionKeywords.entrySet()) {
            String systemField = entry.getKey();
            String[] keywords = entry.getValue();
            
            for (String keyword : keywords) {
                if (columnLower.contains(keyword.toLowerCase())) {
                    // التحقق من أن الحقل متاح لنوع البيانات المحدد
                    String[] availableFields = dataType.getSystemFields();
                    for (String field : availableFields) {
                        if (field.equals(systemField)) {
                            return systemField;
                        }
                    }
                }
            }
        }
        
        // إذا لم يتم العثور على مطابقة، محاولة المطابقة المباشرة
        String[] availableFields = dataType.getSystemFields();
        for (String field : availableFields) {
            if (field.toLowerCase().contains(columnLower) || 
                columnLower.contains(field.toLowerCase())) {
                return field;
            }
        }
        
        return "";
    }
    
    /**
     * الحصول على عدد الصفوف الإجمالي
     */
    public int getTotalRowCount(Uri fileUri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(fileUri)) {
            Workbook workbook = createWorkbook(inputStream, getFileName(fileUri));
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            workbook.close();
            return Math.max(0, rowCount); // تجاهل صف العناوين
        } catch (Exception e) {
            Log.e(TAG, "Error getting row count", e);
            return 0;
        }
    }
    
    /**
     * الحصول على بيانات المعاينة
     */
    public List<ImportPreviewData> getPreviewData(Uri fileUri, List<ColumnMapping> mappings, int maxRows) throws Exception {
        List<ImportPreviewData> previewData = new ArrayList<>();
        
        try (InputStream inputStream = context.getContentResolver().openInputStream(fileUri)) {
            Workbook workbook = createWorkbook(inputStream, getFileName(fileUri));
            Sheet sheet = workbook.getSheetAt(0);
            
            // إنشاء خريطة للمطابقات
            Map<Integer, ColumnMapping> columnMappingMap = new HashMap<>();
            for (int i = 0; i < mappings.size(); i++) {
                ColumnMapping mapping = mappings.get(i);
                if (mapping.isValidMapping()) {
                    columnMappingMap.put(i, mapping);
                }
            }
            
            // قراءة البيانات (تبدأ من الصف 1 لتجاهل العناوين)
            int rowsProcessed = 0;
            for (int i = 1; i <= sheet.getLastRowNum() && rowsProcessed < maxRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                ImportPreviewData previewItem = new ImportPreviewData(i + 1);
                boolean hasData = false;
                
                // معالجة كل عمود مربوط
                for (Map.Entry<Integer, ColumnMapping> entry : columnMappingMap.entrySet()) {
                    int columnIndex = entry.getKey();
                    ColumnMapping mapping = entry.getValue();
                    
                    Cell cell = row.getCell(columnIndex);
                    String cellValue = getCellValueAsString(cell);
                    String processedValue = processCellValue(cellValue, mapping);
                    
                    previewItem.addFieldValue(mapping.getSystemField(), processedValue, cellValue);
                    
                    if (processedValue != null && !processedValue.trim().isEmpty()) {
                        hasData = true;
                    }
                }
                
                if (hasData) {
                    validatePreviewData(previewItem, mappings);
                    previewData.add(previewItem);
                    rowsProcessed++;
                }
            }
            
            workbook.close();
        }
        
        return previewData;
    }
    
    /**
     * معالجة قيمة الخلية
     */
    private String processCellValue(String cellValue, ColumnMapping mapping) {
        if (cellValue == null || cellValue.trim().isEmpty()) {
            if (mapping.isUseDefaultForEmpty()) {
                return mapping.getDefaultValue();
            } else {
                return "";
            }
        }
        
        // تنظيف القيمة
        String processedValue = cellValue.trim();
        
        // معالجة الأرقام
        if (mapping.getDataType() != null && mapping.getDataType().equals("number")) {
            try {
                // إزالة الفواصل والرموز غير الرقمية
                processedValue = processedValue.replaceAll("[^0-9.-]", "");
                if (processedValue.isEmpty()) {
                    return mapping.getDefaultValue();
                }
                // التحقق من صحة الرقم
                Double.parseDouble(processedValue);
            } catch (NumberFormatException e) {
                Log.w(TAG, "Invalid number format: " + cellValue);
                return mapping.getDefaultValue();
            }
        }
        
        return processedValue;
    }
    
    /**
     * التحقق من صحة بيانات المعاينة
     */
    private void validatePreviewData(ImportPreviewData previewData, List<ColumnMapping> mappings) {
        // التحقق من الحقول المطلوبة
        for (ColumnMapping mapping : mappings) {
            if (mapping.isRequired() && mapping.isValidMapping()) {
                String value = previewData.getFieldValue(mapping.getSystemField());
                if (value == null || value.trim().isEmpty()) {
                    previewData.addError("الحقل المطلوب '" + mapping.getSystemFieldArabic() + "' فارغ");
                }
            }
        }
        
        // التحقق من صحة الأرقام
        validateNumericFields(previewData);
        
        // التحقق من التكرار (يمكن تطويره لاحقاً)
        // checkForDuplicates(previewData);
    }
    
    /**
     * التحقق من صحة الحقول الرقمية
     */
    private void validateNumericFields(ImportPreviewData previewData) {
        String[] numericFields = {"debit_balance", "credit_balance", "total_balance", 
                                "quantity", "purchase_price", "sale_price", "basic_salary"};
        
        for (String field : numericFields) {
            String value = previewData.getFieldValue(field);
            if (value != null && !value.trim().isEmpty()) {
                try {
                    double numValue = Double.parseDouble(value);
                    if (numValue < 0 && !field.contains("balance")) {
                        previewData.addWarning("قيمة سالبة في حقل " + field);
                    }
                } catch (NumberFormatException e) {
                    previewData.addError("قيمة غير صحيحة في حقل " + field + ": " + value);
                }
            }
        }
    }
    
    /**
     * الحصول على قيمة الخلية كنص
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    return sdf.format(date);
                } else {
                    double numValue = cell.getNumericCellValue();
                    if (numValue == (long) numValue) {
                        return String.valueOf((long) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    /**
     * بدء عملية الاستيراد
     */
    public void importData(Uri fileUri, List<ColumnMapping> mappings, ImportDataType dataType, 
                          ImportProgressListener listener) {
        
        executorService.execute(() -> {
            try {
                ImportResult result = performImport(fileUri, mappings, dataType, listener);
                listener.onComplete(result);
            } catch (Exception e) {
                Log.e(TAG, "Import failed", e);
                listener.onError("فشل في الاستيراد: " + e.getMessage());
            }
        });
    }
    
    /**
     * تنفيذ عملية الاستيراد الفعلية
     */
    private ImportResult performImport(Uri fileUri, List<ColumnMapping> mappings, 
                                     ImportDataType dataType, ImportProgressListener listener) throws Exception {
        
        ImportResult result = new ImportResult();
        
        try (InputStream inputStream = context.getContentResolver().openInputStream(fileUri)) {
            Workbook workbook = createWorkbook(inputStream, getFileName(fileUri));
            Sheet sheet = workbook.getSheetAt(0);
            
            int totalRows = sheet.getLastRowNum();
            int successCount = 0;
            int errorCount = 0;
            
            listener.onProgress(0, "بدء عملية الاستيراد...");
            
            // إنشاء خريطة للمطابقات
            Map<Integer, ColumnMapping> columnMappingMap = new HashMap<>();
            for (int i = 0; i < mappings.size(); i++) {
                ColumnMapping mapping = mappings.get(i);
                if (mapping.isValidMapping()) {
                    columnMappingMap.put(i, mapping);
                }
            }
            
            // معالجة كل صف
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    boolean imported = importSingleRow(row, columnMappingMap, dataType);
                    if (imported) {
                        successCount++;
                    } else {
                        errorCount++;
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Error importing row " + (i + 1), e);
                    errorCount++;
                    result.addError((i + 1), e.getMessage());
                }
                
                // تحديث التقدم
                int progress = (i * 100) / totalRows;
                listener.onProgress(progress, "معالجة الصف " + (i + 1) + " من " + totalRows);
            }
            
            workbook.close();
            
            result.setSuccessCount(successCount);
            result.setErrorCount(errorCount);
            result.setTotalProcessed(successCount + errorCount);
            
            listener.onProgress(100, "تم الانتهاء من الاستيراد");
            
        }
        
        return result;
    }
    
    /**
     * استيراد صف واحد
     */
    private boolean importSingleRow(Row row, Map<Integer, ColumnMapping> mappings, ImportDataType dataType) throws Exception {
        
        Map<String, String> fieldValues = new HashMap<>();
        
        // استخراج القيم
        for (Map.Entry<Integer, ColumnMapping> entry : mappings.entrySet()) {
            int columnIndex = entry.getKey();
            ColumnMapping mapping = entry.getValue();
            
            Cell cell = row.getCell(columnIndex);
            String cellValue = getCellValueAsString(cell);
            String processedValue = processCellValue(cellValue, mapping);
            
            fieldValues.put(mapping.getSystemField(), processedValue);
        }
        
        // إدراج البيانات في قاعدة البيانات
        switch (dataType) {
            case CUSTOMERS:
                return insertCustomer(fieldValues);
            case SUPPLIERS:
                return insertSupplier(fieldValues);
            case ITEMS:
                return insertItem(fieldValues);
            case ACCOUNTS:
                return insertAccount(fieldValues);
            case EMPLOYEES:
                return insertEmployee(fieldValues);
            default:
                throw new IllegalArgumentException("نوع بيانات غير مدعوم: " + dataType);
        }
    }
    
    /**
     * إدراج عميل جديد
     */
    private boolean insertCustomer(Map<String, String> fieldValues) throws Exception {
        Customer customer = new Customer();
        
        customer.setName(fieldValues.get("name"));
        customer.setPhone(fieldValues.get("phone"));
        customer.setArea(fieldValues.get("area"));
        customer.setAddress(fieldValues.get("address"));
        customer.setEmail(fieldValues.get("email"));
        
        // معالجة الأرصدة
        String debitBalance = fieldValues.get("debit_balance");
        String creditBalance = fieldValues.get("credit_balance");
        
        if (debitBalance != null && !debitBalance.isEmpty()) {
            customer.setDebitBalance(Double.parseDouble(debitBalance));
        }
        
        if (creditBalance != null && !creditBalance.isEmpty()) {
            customer.setCreditBalance(Double.parseDouble(creditBalance));
        }
        
        // حساب الرصيد الإجمالي
        double totalBalance = customer.getDebitBalance() - customer.getCreditBalance();
        customer.setTotalBalance(totalBalance);
        
        // تعيين القيم الافتراضية
        customer.setCreatedAt(new Date());
        customer.setActive(true);
        
        database.customerDao().insert(customer);
        return true;
    }
    
    /**
     * إدراج مورد جديد (مشابه للعميل)
     */
    private boolean insertSupplier(Map<String, String> fieldValues) throws Exception {
        // كود مشابه لإدراج العميل ولكن في جدول الموردين
        return insertCustomer(fieldValues); // مؤقتاً
    }
    
    /**
     * إدراج صنف جديد
     */
    private boolean insertItem(Map<String, String> fieldValues) throws Exception {
        Item item = new Item();
        
        item.setName(fieldValues.get("name"));
        item.setUnit(fieldValues.get("unit"));
        item.setCategory(fieldValues.get("category"));
        
        // معالجة الأسعار والكميات
        String purchasePrice = fieldValues.get("purchase_price");
        String salePrice = fieldValues.get("sale_price");
        String quantity = fieldValues.get("quantity");
        
        if (purchasePrice != null && !purchasePrice.isEmpty()) {
            item.setPurchasePrice(Double.parseDouble(purchasePrice));
        }
        
        if (salePrice != null && !salePrice.isEmpty()) {
            item.setSalePrice(Double.parseDouble(salePrice));
        }
        
        if (quantity != null && !quantity.isEmpty()) {
            item.setQuantity(Double.parseDouble(quantity));
        }
        
        item.setCreatedAt(new Date());
        item.setActive(true);
        
        database.itemDao().insert(item);
        return true;
    }
    
    /**
     * إدراج حساب جديد
     */
    private boolean insertAccount(Map<String, String> fieldValues) throws Exception {
        Account account = new Account();
        
        account.setAccountName(fieldValues.get("account_name"));
        account.setAccountType(fieldValues.get("account_type"));
        account.setDescription(fieldValues.get("description"));
        
        String openingBalance = fieldValues.get("opening_balance");
        if (openingBalance != null && !openingBalance.isEmpty()) {
            account.setOpeningBalance(Double.parseDouble(openingBalance));
        }
        
        account.setCreatedAt(new Date());
        account.setActive(true);
        
        database.accountDao().insert(account);
        return true;
    }
    
    /**
     * إدراج موظف جديد
     */
    private boolean insertEmployee(Map<String, String> fieldValues) throws Exception {
        Employee employee = new Employee();
        
        employee.setEmployeeName(fieldValues.get("employee_name"));
        employee.setPosition(fieldValues.get("position"));
        employee.setPhone(fieldValues.get("phone"));
        employee.setAddress(fieldValues.get("address"));
        
        String basicSalary = fieldValues.get("basic_salary");
        if (basicSalary != null && !basicSalary.isEmpty()) {
            employee.setBasicSalary(Double.parseDouble(basicSalary));
        }
        
        employee.setCreatedAt(new Date());
        employee.setActive(true);
        
        database.employeeDao().insert(employee);
        return true;
    }
    
    /**
     * واجهة مستمع تقدم الاستيراد
     */
    public interface ImportProgressListener {
        void onProgress(int progress, String status);
        void onComplete(ImportResult result);
        void onError(String error);
    }
    
    /**
     * نتيجة عملية الاستيراد
     */
    public static class ImportResult {
        private int successCount;
        private int errorCount;
        private int totalProcessed;
        private List<String> errors;
        
        public ImportResult() {
            this.errors = new ArrayList<>();
        }
        
        public void addError(int rowNumber, String error) {
            errors.add("الصف " + rowNumber + ": " + error);
        }
        
        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            summary.append("نتائج الاستيراد:\n");
            summary.append("• تم بنجاح: ").append(successCount).append(" سجل\n");
            summary.append("• فشل: ").append(errorCount).append(" سجل\n");
            summary.append("• الإجمالي: ").append(totalProcessed).append(" سجل\n");
            
            if (!errors.isEmpty()) {
                summary.append("\nالأخطاء:\n");
                for (String error : errors) {
                    summary.append("• ").append(error).append("\n");
                }
            }
            
            return summary.toString();
        }
        
        // Getters and Setters
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getErrorCount() { return errorCount; }
        public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
        public int getTotalProcessed() { return totalProcessed; }
        public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }
        public List<String> getErrors() { return errors; }
    }
}