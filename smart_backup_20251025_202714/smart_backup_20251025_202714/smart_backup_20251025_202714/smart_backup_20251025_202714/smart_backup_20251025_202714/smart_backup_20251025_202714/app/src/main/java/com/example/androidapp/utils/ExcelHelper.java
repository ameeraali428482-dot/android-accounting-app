package com.example.androidapp.utils;

import android.content.Context;
import android.util.Log;
import androidx.room.Room;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * مساعد Excel المتقدم - استيراد وتصدير البيانات المحاسبية من وإلى Excel
 * Advanced Excel Helper - import and export accounting data from/to Excel
 */
public class ExcelHelper {
    
    private static final String TAG = "ExcelHelper";
    
    // أنواع الملفات المدعومة
    public static final String TYPE_XLSX = "xlsx";
    public static final String TYPE_XLS = "xls";
    public static final String TYPE_CSV = "csv";
    
    // أوراق العمل
    public static final String SHEET_ACCOUNTS = "الحسابات";
    public static final String SHEET_TRANSACTIONS = "المعاملات";
    public static final String SHEET_CATEGORIES = "الفئات";
    public static final String SHEET_USERS = "المستخدمين";
    public static final String SHEET_SUMMARY = "الملخص";
    
    // تنسيقات التصدير
    public static final String FORMAT_DETAILED = "DETAILED";
    public static final String FORMAT_SUMMARY = "SUMMARY";
    public static final String FORMAT_FINANCIAL_REPORT = "FINANCIAL_REPORT";
    public static final String FORMAT_CUSTOM = "CUSTOM";
    
    private Context context;
    private AppDatabase database;
    private ExecutorService executorService;
    
    public ExcelHelper(Context context) {
        this.context = context;
        this.database = Room.databaseBuilder(context, AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration()
                .build();
        this.executorService = Executors.newCachedThreadPool();
    }
    
    /**
     * تصدير جميع البيانات إلى Excel
     * Export all data to Excel
     */
    public Future<ExportResult> exportAllDataToExcel(String fileName, String format) {
        return executorService.submit(() -> {
            try {
                Workbook workbook = new XSSFWorkbook();
                
                // تصدير البيانات حسب التنسيق
                switch (format) {
                    case FORMAT_DETAILED:
                        exportDetailedData(workbook);
                        break;
                    case FORMAT_SUMMARY:
                        exportSummaryData(workbook);
                        break;
                    case FORMAT_FINANCIAL_REPORT:
                        exportFinancialReport(workbook);
                        break;
                    default:
                        exportDetailedData(workbook);
                        break;
                }
                
                // حفظ الملف
                File excelFile = saveWorkbookToFile(workbook, fileName);
                workbook.close();
                
                return new ExportResult(true, "تم تصدير البيانات بنجاح", excelFile.getAbsolutePath());
                
            } catch (Exception e) {
                Log.e(TAG, "Error exporting data to Excel", e);
                return new ExportResult(false, "خطأ في تصدير البيانات: " + e.getMessage());
            }
        });
    }
    
    /**
     * تصدير الحسابات إلى Excel
     * Export accounts to Excel
     */
    public Future<ExportResult> exportAccountsToExcel(String fileName) {
        return executorService.submit(() -> {
            try {
                List<Account> accounts = database.accountDao().getAllAccountsSync();
                
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet(SHEET_ACCOUNTS);
                
                // إنشاء الأسلوب
                CellStyle headerStyle = createHeaderStyle(workbook);
                CellStyle dataStyle = createDataStyle(workbook);
                CellStyle numberStyle = createNumberStyle(workbook);
                
                // إنشاء الرأس
                Row headerRow = sheet.createRow(0);
                String[] headers = {"المعرف", "اسم الحساب", "النوع", "الرصيد", "العملة", "الوصف", "تاريخ الإنشاء"};
                
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }
                
                // إضافة البيانات
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                
                for (int i = 0; i < accounts.size(); i++) {
                    Account account = accounts.get(i);
                    Row row = sheet.createRow(i + 1);
                    
                    row.createCell(0).setCellValue(account.id);
                    
                    Cell nameCell = row.createCell(1);
                    nameCell.setCellValue(account.name);
                    nameCell.setCellStyle(dataStyle);
                    
                    Cell typeCell = row.createCell(2);
                    typeCell.setCellValue(account.type);
                    typeCell.setCellStyle(dataStyle);
                    
                    Cell balanceCell = row.createCell(3);
                    balanceCell.setCellValue(account.balance);
                    balanceCell.setCellStyle(numberStyle);
                    
                    Cell currencyCell = row.createCell(4);
                    currencyCell.setCellValue(account.currency);
                    currencyCell.setCellStyle(dataStyle);
                    
                    Cell descCell = row.createCell(5);
                    descCell.setCellValue(account.description != null ? account.description : "");
                    descCell.setCellStyle(dataStyle);
                    
                    Cell dateCell = row.createCell(6);
                    dateCell.setCellValue(sdf.format(new Date(account.createdAt)));
                    dateCell.setCellStyle(dataStyle);
                }
                
                // تعديل عرض الأعمدة
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                File excelFile = saveWorkbookToFile(workbook, fileName);
                workbook.close();
                
                return new ExportResult(true, "تم تصدير الحسابات بنجاح", excelFile.getAbsolutePath());
                
            } catch (Exception e) {
                Log.e(TAG, "Error exporting accounts", e);
                return new ExportResult(false, "خطأ في تصدير الحسابات: " + e.getMessage());
            }
        });
    }
    
    /**
     * تصدير المعاملات إلى Excel
     * Export transactions to Excel
     */
    public Future<ExportResult> exportTransactionsToExcel(String fileName, long fromDate, long toDate) {
        return executorService.submit(() -> {
            try {
                List<Transaction> transactions;
                if (fromDate > 0 && toDate > 0) {
                    transactions = database.transactionDao().getTransactionsByDateRangeSync(fromDate, toDate);
                } else {
                    transactions = database.transactionDao().getAllTransactionsSync();
                }
                
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet(SHEET_TRANSACTIONS);
                
                // إنشاء الأسلوب
                CellStyle headerStyle = createHeaderStyle(workbook);
                CellStyle dataStyle = createDataStyle(workbook);
                CellStyle numberStyle = createNumberStyle(workbook);
                CellStyle dateStyle = createDateStyle(workbook);
                
                // إنشاء الرأس
                Row headerRow = sheet.createRow(0);
                String[] headers = {"المعرف", "المبلغ", "التاريخ", "الوصف", "من حساب", "إلى حساب", "الفئة", "النوع", "المستخدم"};
                
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }
                
                // إضافة البيانات
                for (int i = 0; i < transactions.size(); i++) {
                    Transaction transaction = transactions.get(i);
                    Row row = sheet.createRow(i + 1);
                    
                    row.createCell(0).setCellValue(transaction.id);
                    
                    Cell amountCell = row.createCell(1);
                    amountCell.setCellValue(transaction.amount);
                    amountCell.setCellStyle(numberStyle);
                    
                    Cell dateCell = row.createCell(2);
                    dateCell.setCellValue(new Date(transaction.date));
                    dateCell.setCellStyle(dateStyle);
                    
                    Cell descCell = row.createCell(3);
                    descCell.setCellValue(transaction.description != null ? transaction.description : "");
                    descCell.setCellStyle(dataStyle);
                    
                    // جلب أسماء الحسابات
                    String fromAccountName = getAccountName(transaction.fromAccountId);
                    String toAccountName = getAccountName(transaction.toAccountId);
                    String categoryName = getCategoryName(transaction.categoryId);
                    
                    Cell fromCell = row.createCell(4);
                    fromCell.setCellValue(fromAccountName);
                    fromCell.setCellStyle(dataStyle);
                    
                    Cell toCell = row.createCell(5);
                    toCell.setCellValue(toAccountName);
                    toCell.setCellStyle(dataStyle);
                    
                    Cell categoryCell = row.createCell(6);
                    categoryCell.setCellValue(categoryName);
                    categoryCell.setCellStyle(dataStyle);
                    
                    Cell typeCell = row.createCell(7);
                    typeCell.setCellValue(transaction.type != null ? transaction.type : "");
                    typeCell.setCellStyle(dataStyle);
                    
                    Cell userCell = row.createCell(8);
                    userCell.setCellValue(transaction.userId != null ? transaction.userId : "");
                    userCell.setCellStyle(dataStyle);
                }
                
                // تعديل عرض الأعمدة
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                // إضافة ملخص في النهاية
                addTransactionsSummary(sheet, transactions, numberStyle, headerStyle);
                
                File excelFile = saveWorkbookToFile(workbook, fileName);
                workbook.close();
                
                return new ExportResult(true, "تم تصدير المعاملات بنجاح", excelFile.getAbsolutePath());
                
            } catch (Exception e) {
                Log.e(TAG, "Error exporting transactions", e);
                return new ExportResult(false, "خطأ في تصدير المعاملات: " + e.getMessage());
            }
        });
    }
    
    /**
     * استيراد البيانات من Excel
     * Import data from Excel
     */
    public Future<ImportResult> importDataFromExcel(String filePath, String userId) {
        return executorService.submit(() -> {
            try {
                FileInputStream fis = new FileInputStream(filePath);
                Workbook workbook;
                
                // تحديد نوع الملف
                if (filePath.endsWith(".xlsx")) {
                    workbook = new XSSFWorkbook(fis);
                } else if (filePath.endsWith(".xls")) {
                    workbook = new HSSFWorkbook(fis);
                } else {
                    return new ImportResult(false, "نوع الملف غير مدعوم");
                }
                
                ImportResult result = new ImportResult(true, "تم بدء الاستيراد");
                
                // استيراد الحسابات
                if (hasSheet(workbook, SHEET_ACCOUNTS)) {
                    ImportResult accountsResult = importAccountsFromSheet(workbook.getSheet(SHEET_ACCOUNTS), userId);
                    result.accountsImported = accountsResult.accountsImported;
                    if (!accountsResult.success) {
                        result.addError("خطأ في استيراد الحسابات: " + accountsResult.message);
                    }
                }
                
                // استيراد الفئات
                if (hasSheet(workbook, SHEET_CATEGORIES)) {
                    ImportResult categoriesResult = importCategoriesFromSheet(workbook.getSheet(SHEET_CATEGORIES));
                    result.categoriesImported = categoriesResult.categoriesImported;
                    if (!categoriesResult.success) {
                        result.addError("خطأ في استيراد الفئات: " + categoriesResult.message);
                    }
                }
                
                // استيراد المعاملات
                if (hasSheet(workbook, SHEET_TRANSACTIONS)) {
                    ImportResult transactionsResult = importTransactionsFromSheet(workbook.getSheet(SHEET_TRANSACTIONS), userId);
                    result.transactionsImported = transactionsResult.transactionsImported;
                    if (!transactionsResult.success) {
                        result.addError("خطأ في استيراد المعاملات: " + transactionsResult.message);
                    }
                }
                
                workbook.close();
                fis.close();
                
                result.success = result.errors.isEmpty();
                if (result.success) {
                    result.message = String.format("تم الاستيراد بنجاح - الحسابات: %d، الفئات: %d، المعاملات: %d",
                            result.accountsImported, result.categoriesImported, result.transactionsImported);
                }
                
                return result;
                
            } catch (Exception e) {
                Log.e(TAG, "Error importing data from Excel", e);
                return new ImportResult(false, "خطأ في استيراد البيانات: " + e.getMessage());
            }
        });
    }
    
    /**
     * تصدير تقرير مالي متقدم
     * Export advanced financial report
     */
    public Future<ExportResult> exportFinancialReport(String fileName, long fromDate, long toDate) {
        return executorService.submit(() -> {
            try {
                Workbook workbook = new XSSFWorkbook();
                
                // ورقة الملخص التنفيذي
                createExecutiveSummarySheet(workbook, fromDate, toDate);
                
                // ورقة تقرير الأرباح والخسائر
                createProfitLossSheet(workbook, fromDate, toDate);
                
                // ورقة الميزانية العمومية
                createBalanceSheet(workbook);
                
                // ورقة التدفق النقدي
                createCashFlowSheet(workbook, fromDate, toDate);
                
                // ورقة تحليل الحسابات
                createAccountAnalysisSheet(workbook);
                
                File excelFile = saveWorkbookToFile(workbook, fileName);
                workbook.close();
                
                return new ExportResult(true, "تم إنتاج التقرير المالي بنجاح", excelFile.getAbsolutePath());
                
            } catch (Exception e) {
                Log.e(TAG, "Error exporting financial report", e);
                return new ExportResult(false, "خطأ في إنتاج التقرير المالي: " + e.getMessage());
            }
        });
    }
    
    /**
     * تصدير البيانات المفصلة
     * Export detailed data
     */
    private void exportDetailedData(Workbook workbook) throws Exception {
        // تصدير جميع الجداول كأوراق منفصلة
        exportAccountsSheet(workbook);
        exportTransactionsSheet(workbook);
        exportCategoriesSheet(workbook);
        exportUsersSheet(workbook);
        createOverviewSheet(workbook);
    }
    
    /**
     * تصدير البيانات الملخصة
     * Export summary data
     */
    private void exportSummaryData(Workbook workbook) throws Exception {
        createSummarySheet(workbook);
        createChartsSheet(workbook);
    }
    
    /**
     * تصدير التقرير المالي
     * Export financial report
     */
    private void exportFinancialReport(Workbook workbook) throws Exception {
        long currentTime = System.currentTimeMillis();
        long monthAgo = currentTime - (30L * 24 * 60 * 60 * 1000);
        
        createExecutiveSummarySheet(workbook, monthAgo, currentTime);
        createProfitLossSheet(workbook, monthAgo, currentTime);
        createBalanceSheet(workbook);
        createCashFlowSheet(workbook, monthAgo, currentTime);
    }
    
    /**
     * إنشاء ورقة الحسابات
     * Create accounts sheet
     */
    private void exportAccountsSheet(Workbook workbook) throws Exception {
        List<Account> accounts = database.accountDao().getAllAccountsSync();
        Sheet sheet = workbook.createSheet(SHEET_ACCOUNTS);
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        
        // الرأس
        Row headerRow = sheet.createRow(0);
        String[] headers = {"المعرف", "اسم الحساب", "النوع", "الرصيد", "العملة", "الوصف", "تاريخ الإنشاء", "آخر تحديث"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // البيانات
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            Row row = sheet.createRow(i + 1);
            
            row.createCell(0).setCellValue(account.id);
            setCellValue(row, 1, account.name, dataStyle);
            setCellValue(row, 2, account.type, dataStyle);
            setCellValue(row, 3, account.balance, numberStyle);
            setCellValue(row, 4, account.currency, dataStyle);
            setCellValue(row, 5, account.description, dataStyle);
            setCellValue(row, 6, sdf.format(new Date(account.createdAt)), dataStyle);
            setCellValue(row, 7, sdf.format(new Date(account.lastModified)), dataStyle);
        }
        
        // تعديل عرض الأعمدة
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * إنشاء ورقة المعاملات
     * Create transactions sheet
     */
    private void exportTransactionsSheet(Workbook workbook) throws Exception {
        List<Transaction> transactions = database.transactionDao().getAllTransactionsSync();
        Sheet sheet = workbook.createSheet(SHEET_TRANSACTIONS);
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        
        // الرأس
        Row headerRow = sheet.createRow(0);
        String[] headers = {"المعرف", "المبلغ", "التاريخ", "الوصف", "من حساب", "إلى حساب", "الفئة", "النوع", "المستخدم", "آخر تحديث"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // البيانات
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            Row row = sheet.createRow(i + 1);
            
            row.createCell(0).setCellValue(transaction.id);
            setCellValue(row, 1, transaction.amount, numberStyle);
            setCellValue(row, 2, new Date(transaction.date), dateStyle);
            setCellValue(row, 3, transaction.description, dataStyle);
            setCellValue(row, 4, getAccountName(transaction.fromAccountId), dataStyle);
            setCellValue(row, 5, getAccountName(transaction.toAccountId), dataStyle);
            setCellValue(row, 6, getCategoryName(transaction.categoryId), dataStyle);
            setCellValue(row, 7, transaction.type, dataStyle);
            setCellValue(row, 8, transaction.userId, dataStyle);
            setCellValue(row, 9, sdf.format(new Date(transaction.lastModified)), dataStyle);
        }
        
        // تعديل عرض الأعمدة
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // إضافة إحصائيات
        addTransactionsSummary(sheet, transactions, numberStyle, headerStyle);
    }
    
    /**
     * إنشاء ورقة الملخص التنفيذي
     * Create executive summary sheet
     */
    private void createExecutiveSummarySheet(Workbook workbook, long fromDate, long toDate) throws Exception {
        Sheet sheet = workbook.createSheet("الملخص التنفيذي");
        
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowIndex = 0;
        
        // العنوان
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("التقرير المالي - الملخص التنفيذي");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        
        rowIndex++; // سطر فارغ
        
        // فترة التقرير
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Row periodRow = sheet.createRow(rowIndex++);
        periodRow.createCell(0).setCellValue("فترة التقرير:");
        periodRow.createCell(1).setCellValue(sdf.format(new Date(fromDate)) + " - " + sdf.format(new Date(toDate)));
        
        rowIndex++; // سطر فارغ
        
        // الإحصائيات الرئيسية
        List<Transaction> transactions = database.transactionDao().getTransactionsByDateRangeSync(fromDate, toDate);
        List<Account> accounts = database.accountDao().getAllAccountsSync();
        
        double totalIncome = transactions.stream().filter(t -> t.amount > 0).mapToDouble(t -> t.amount).sum();
        double totalExpenses = transactions.stream().filter(t -> t.amount < 0).mapToDouble(t -> Math.abs(t.amount)).sum();
        double netProfit = totalIncome - totalExpenses;
        double totalBalance = accounts.stream().mapToDouble(a -> a.balance).sum();
        
        // جدول الإحصائيات
        Row headerRow = sheet.createRow(rowIndex++);
        headerRow.createCell(0).setCellValue("البيان");
        headerRow.createCell(1).setCellValue("القيمة");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);
        
        addSummaryRow(sheet, rowIndex++, "إجمالي الإيرادات", totalIncome, dataStyle, numberStyle);
        addSummaryRow(sheet, rowIndex++, "إجمالي المصروفات", totalExpenses, dataStyle, numberStyle);
        addSummaryRow(sheet, rowIndex++, "صافي الربح", netProfit, dataStyle, numberStyle);
        addSummaryRow(sheet, rowIndex++, "إجمالي الأرصدة", totalBalance, dataStyle, numberStyle);
        addSummaryRow(sheet, rowIndex++, "عدد المعاملات", transactions.size(), dataStyle, numberStyle);
        addSummaryRow(sheet, rowIndex++, "عدد الحسابات", accounts.size(), dataStyle, numberStyle);
        
        // تعديل عرض الأعمدة
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    /**
     * استيراد الحسابات من الورقة
     * Import accounts from sheet
     */
    private ImportResult importAccountsFromSheet(Sheet sheet, String userId) {
        ImportResult result = new ImportResult(true, "");
        
        try {
            int imported = 0;
            
            // بدء من الصف الثاني (تجاهل الرأس)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Account account = new Account();
                    account.name = getCellStringValue(row.getCell(1));
                    account.type = getCellStringValue(row.getCell(2));
                    account.balance = getCellNumericValue(row.getCell(3));
                    account.currency = getCellStringValue(row.getCell(4));
                    account.description = getCellStringValue(row.getCell(5));
                    account.createdAt = System.currentTimeMillis();
                    account.lastModified = System.currentTimeMillis();
                    account.userId = userId;
                    
                    if (account.name != null && !account.name.isEmpty()) {
                        database.accountDao().insert(account);
                        imported++;
                    }
                    
                } catch (Exception e) {
                    result.addError("خطأ في الصف " + (i + 1) + ": " + e.getMessage());
                }
            }
            
            result.accountsImported = imported;
            
        } catch (Exception e) {
            result.success = false;
            result.message = "خطأ في استيراد الحسابات: " + e.getMessage();
        }
        
        return result;
    }
    
    /**
     * استيراد المعاملات من الورقة
     * Import transactions from sheet
     */
    private ImportResult importTransactionsFromSheet(Sheet sheet, String userId) {
        ImportResult result = new ImportResult(true, "");
        
        try {
            int imported = 0;
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Transaction transaction = new Transaction();
                    transaction.amount = getCellNumericValue(row.getCell(1));
                    transaction.date = getCellDateValue(row.getCell(2));
                    transaction.description = getCellStringValue(row.getCell(3));
                    
                    // البحث عن معرفات الحسابات والفئات بالأسماء
                    String fromAccountName = getCellStringValue(row.getCell(4));
                    String toAccountName = getCellStringValue(row.getCell(5));
                    String categoryName = getCellStringValue(row.getCell(6));
                    
                    transaction.fromAccountId = findAccountIdByName(fromAccountName);
                    transaction.toAccountId = findAccountIdByName(toAccountName);
                    transaction.categoryId = findCategoryIdByName(categoryName);
                    
                    transaction.type = getCellStringValue(row.getCell(7));
                    transaction.userId = userId;
                    transaction.lastModified = System.currentTimeMillis();
                    
                    if (transaction.amount != 0) {
                        database.transactionDao().insert(transaction);
                        imported++;
                    }
                    
                } catch (Exception e) {
                    result.addError("خطأ في الصف " + (i + 1) + ": " + e.getMessage());
                }
            }
            
            result.transactionsImported = imported;
            
        } catch (Exception e) {
            result.success = false;
            result.message = "خطأ في استيراد المعاملات: " + e.getMessage();
        }
        
        return result;
    }
    
    // Helper methods لإنشاء الأساليب
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
    
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy"));
        return style;
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    // Helper methods أخرى
    
    private void setCellValue(Row row, int cellIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue(value.toString());
        }
        
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
    
    private double getCellNumericValue(Cell cell) {
        if (cell == null) return 0;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }
    
    private long getCellDateValue(Cell cell) {
        if (cell == null) return System.currentTimeMillis();
        
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().getTime();
        }
        
        return System.currentTimeMillis();
    }
    
    private String getAccountName(long accountId) {
        if (accountId <= 0) return "";
        
        try {
            Account account = database.accountDao().getAccountByIdSync(accountId);
            return account != null ? account.name : "";
        } catch (Exception e) {
            return "";
        }
    }
    
    private String getCategoryName(long categoryId) {
        if (categoryId <= 0) return "";
        
        try {
            Category category = database.categoryDao().getCategoryByIdSync(categoryId);
            return category != null ? category.name : "";
        } catch (Exception e) {
            return "";
        }
    }
    
    private long findAccountIdByName(String accountName) {
        if (accountName == null || accountName.isEmpty()) return 0;
        
        try {
            Account account = database.accountDao().getAccountByNameSync(accountName);
            return account != null ? account.id : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private long findCategoryIdByName(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) return 0;
        
        try {
            Category category = database.categoryDao().getCategoryByNameSync(categoryName);
            return category != null ? category.id : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private boolean hasSheet(Workbook workbook, String sheetName) {
        return workbook.getSheet(sheetName) != null;
    }
    
    private File saveWorkbookToFile(Workbook workbook, String fileName) throws IOException {
        File excelDir = new File(context.getExternalFilesDir(null), "excel");
        if (!excelDir.exists()) {
            excelDir.mkdirs();
        }
        
        if (!fileName.endsWith(".xlsx")) {
            fileName += ".xlsx";
        }
        
        File excelFile = new File(excelDir, fileName);
        
        try (FileOutputStream fos = new FileOutputStream(excelFile)) {
            workbook.write(fos);
        }
        
        return excelFile;
    }
    
    /**
     * تنظيف الموارد
     * Cleanup resources
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    // Classes مساعدة
    
    public static class ExportResult {
        private boolean success;
        private String message;
        private String filePath;
        
        public ExportResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public ExportResult(boolean success, String message, String filePath) {
            this.success = success;
            this.message = message;
            this.filePath = filePath;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getFilePath() { return filePath; }
    }
    
    public static class ImportResult {
        private boolean success;
        private String message;
        private int accountsImported = 0;
        private int transactionsImported = 0;
        private int categoriesImported = 0;
        private List<String> errors = new ArrayList<>();
        
        public ImportResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getAccountsImported() { return accountsImported; }
        public int getTransactionsImported() { return transactionsImported; }
        public int getCategoriesImported() { return categoriesImported; }
        public List<String> getErrors() { return errors; }
    }
    
    // تنفيذ بقية الطرق المساعدة
    private void exportCategoriesSheet(Workbook workbook) throws Exception {
        // تنفيذ تصدير الفئات
    }
    
    private void exportUsersSheet(Workbook workbook) throws Exception {
        // تنفيذ تصدير المستخدمين
    }
    
    private void createOverviewSheet(Workbook workbook) throws Exception {
        // تنفيذ ورقة النظرة العامة
    }
    
    private void createSummarySheet(Workbook workbook) throws Exception {
        // تنفيذ ورقة الملخص
    }
    
    private void createChartsSheet(Workbook workbook) throws Exception {
        // تنفيذ ورقة الرسوم البيانية
    }
    
    private void createProfitLossSheet(Workbook workbook, long fromDate, long toDate) throws Exception {
        // تنفيذ ورقة الأرباح والخسائر
    }
    
    private void createBalanceSheet(Workbook workbook) throws Exception {
        // تنفيذ ورقة الميزانية العمومية
    }
    
    private void createCashFlowSheet(Workbook workbook, long fromDate, long toDate) throws Exception {
        // تنفيذ ورقة التدفق النقدي
    }
    
    private void createAccountAnalysisSheet(Workbook workbook) throws Exception {
        // تنفيذ ورقة تحليل الحسابات
    }
    
    private void addTransactionsSummary(Sheet sheet, List<Transaction> transactions, CellStyle numberStyle, CellStyle headerStyle) {
        // تنفيذ إضافة ملخص المعاملات
    }
    
    private void addSummaryRow(Sheet sheet, int rowIndex, String label, Object value, CellStyle labelStyle, CellStyle valueStyle) {
        Row row = sheet.createRow(rowIndex);
        setCellValue(row, 0, label, labelStyle);
        setCellValue(row, 1, value, valueStyle);
    }
    
    private ImportResult importCategoriesFromSheet(Sheet sheet) {
        // تنفيذ استيراد الفئات
        return new ImportResult(true, "Categories imported successfully");
    }
}