package com.example.androidapp.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import androidx.room.Room;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.*;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * مساعد التصدير والاستيراد باستخدام CSV
 */
public class ExcelHelper {
    
    private static final String TAG = "ExcelHelper";
    
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
     * تصدير الحسابات إلى CSV
     */
    public Future<ExportResult> exportAccountsToCSV(String fileName) {
        return executorService.submit(() -> {
            try {
                List<Account> accounts = database.accountDao().getAllAccountsSync();
                
                // إعداد العناوين
                String[] headers = {
                    "المعرف", "اسم الحساب", "النوع", "الرصيد", "العملة", "الوصف", "تاريخ الإنشاء"
                };
                
                List<String[]> data = new ArrayList<>();
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                
                for (Account account : accounts) {
                    String[] row = {
                        String.valueOf(account.id),
                        account.name,
                        account.type,
                        String.valueOf(account.balance),
                        account.currency,
                        account.description != null ? account.description : "",
                        sdf.format(new Date(account.createdAt))
                    };
                    data.add(row);
                }
                
                // حفظ الملف
                File csvFile = saveToCSV(headers, data, fileName);
                
                return new ExportResult(true, "تم تصدير " + accounts.size() + " حساب بنجاح", csvFile.getAbsolutePath());
                
            } catch (Exception e) {
                Log.e(TAG, "Error exporting accounts", e);
                return new ExportResult(false, "خطأ في تصدير الحسابات: " + e.getMessage());
            }
        });
    }
    
    /**
     * تصدير المعاملات إلى CSV
     */
    public Future<ExportResult> exportTransactionsToCSV(String fileName, long fromDate, long toDate) {
        return executorService.submit(() -> {
            try {
                List<Transaction> transactions;
                if (fromDate > 0 && toDate > 0) {
                    transactions = database.transactionDao().getTransactionsByDateRangeSync(fromDate, toDate);
                } else {
                    transactions = database.transactionDao().getAllTransactionsSync();
                }
                
                // إعداد العناوين
                String[] headers = {
                    "المعرف", "المبلغ", "التاريخ", "الوصف", "من حساب", "إلى حساب", 
                    "الفئة", "النوع", "المستخدم", "آخر تحديث"
                };
                
                List<String[]> data = new ArrayList<>();
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                
                for (Transaction transaction : transactions) {
                    String[] row = {
                        String.valueOf(transaction.id),
                        String.valueOf(transaction.amount),
                        sdf.format(new Date(transaction.date)),
                        transaction.description != null ? transaction.description : "",
                        getAccountName(transaction.fromAccountId),
                        getAccountName(transaction.toAccountId),
                        getCategoryName(transaction.categoryId),
                        transaction.type != null ? transaction.type : "",
                        transaction.userId != null ? transaction.userId : "",
                        sdf.format(new Date(transaction.lastModified))
                    };
                    data.add(row);
                }
                
                // حفظ الملف
                File csvFile = saveToCSV(headers, data, fileName);
                
                return new ExportResult(true, "تم تصدير " + transactions.size() + " معاملة بنجاح", csvFile.getAbsolutePath());
                
            } catch (Exception e) {
                Log.e(TAG, "Error exporting transactions", e);
                return new ExportResult(false, "خطأ في تصدير المعاملات: " + e.getMessage());
            }
        });
    }
    
    /**
     * تصدير جميع البيانات إلى ملف CSV واحد
     */
    public Future<ExportResult> exportAllDataToCSV(String fileName) {
        return executorService.submit(() -> {
            try {
                List<Account> accounts = database.accountDao().getAllAccountsSync();
                List<Transaction> transactions = database.transactionDao().getAllTransactionsSync();
                List<Category> categories = database.categoryDao().getAllCategoriesSync();
                
                int totalRecords = accounts.size() + transactions.size() + categories.size();
                
                // حفظ كل نوع في ملف منفصل
                String accountsFile = saveToCSV(
                    new String[]{"المعرف", "اسم الحساب", "النوع", "الرصيد", "العملة", "الوصف"},
                    convertAccountsToCSV(accounts),
                    fileName + "_accounts"
                );
                
                String transactionsFile = saveToCSV(
                    new String[]{"المعرف", "المبلغ", "التاريخ", "الوصف", "من حساب", "إلى حساب", "الفئة"},
                    convertTransactionsToCSV(transactions),
                    fileName + "_transactions"
                );
                
                String categoriesFile = saveToCSV(
                    new String[]{"المعرف", "اسم الفئة", "الوصف", "النوع"},
                    convertCategoriesToCSV(categories),
                    fileName + "_categories"
                );
                
                return new ExportResult(true, 
                    "تم تصدير " + totalRecords + " سجل بنجاح\n" +
                    "الحسابات: " + accountsFile + "\n" +
                    "المعاملات: " + transactionsFile + "\n" +
                    "الفئات: " + categoriesFile,
                    accountsFile
                );
                
            } catch (Exception e) {
                Log.e(TAG, "Error exporting all data", e);
                return new ExportResult(false, "خطأ في تصدير البيانات: " + e.getMessage());
            }
        });
    }
    
    /**
     * حفظ البيانات إلى ملف CSV
     */
    private File saveToCSV(String[] headers, List<String[]> data, String fileName) throws IOException {
        File exportDir = new File(context.getExternalFilesDir(null), "exports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        
        if (!fileName.endsWith(".csv")) {
            fileName += ".csv";
        }
        
        File csvFile = new File(exportDir, fileName);
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile, false))) {
            // كتابة العناوين
            writer.writeNext(headers);
            
            // كتابة البيانات
            for (String[] row : data) {
                writer.writeNext(row);
            }
        }
        
        return csvFile;
    }
    
    /**
     * تحويل الحسابات إلى تنسيق CSV
     */
    private List<String[]> convertAccountsToCSV(List<Account> accounts) {
        List<String[]> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        for (Account account : accounts) {
            String[] row = {
                String.valueOf(account.id),
                account.name,
                account.type,
                String.valueOf(account.balance),
                account.currency,
                account.description != null ? account.description : ""
            };
            data.add(row);
        }
        
        return data;
    }
    
    /**
     * تحويل المعاملات إلى تنسيق CSV
     */
    private List<String[]> convertTransactionsToCSV(List<Transaction> transactions) {
        List<String[]> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        for (Transaction transaction : transactions) {
            String[] row = {
                String.valueOf(transaction.id),
                String.valueOf(transaction.amount),
                sdf.format(new Date(transaction.date)),
                transaction.description != null ? transaction.description : "",
                getAccountName(transaction.fromAccountId),
                getAccountName(transaction.toAccountId),
                getCategoryName(transaction.categoryId)
            };
            data.add(row);
        }
        
        return data;
    }
    
    /**
     * تحويل الفئات إلى تنسيق CSV
     */
    private List<String[]> convertCategoriesToCSV(List<Category> categories) {
        List<String[]> data = new ArrayList<>();
        
        for (Category category : categories) {
            String[] row = {
                String.valueOf(category.id),
                category.name,
                category.description != null ? category.description : "",
                category.type != null ? category.type : ""
            };
            data.add(row);
        }
        
        return data;
    }
    
    // Helper methods
    
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
    
    /**
     * استيراد البيانات من ملف CSV
     */
    public Future<ImportResult> importDataFromCSV(Uri fileUri, String dataType, String userId) {
        return executorService.submit(() -> {
            try {
                List<List<String>> data = ExcelUtil.importDataFromCSV(context, fileUri);
                
                if (data.isEmpty()) {
                    return new ImportResult(false, "الملف فارغ أو تالف");
                }
                
                int importedCount = 0;
                List<String> errors = new ArrayList<>();
                
                switch (dataType) {
                    case "ACCOUNTS":
                        importedCount = importAccounts(data, userId, errors);
                        break;
                    case "TRANSACTIONS":
                        importedCount = importTransactions(data, userId, errors);
                        break;
                    case "CATEGORIES":
                        importedCount = importCategories(data, errors);
                        break;
                    default:
                        return new ImportResult(false, "نوع البيانات غير مدعوم");
                }
                
                ImportResult result = new ImportResult(true, 
                    "تم استيراد " + importedCount + " سجل بنجاح");
                result.setImportedCount(importedCount);
                result.setErrors(errors);
                
                return result;
                
            } catch (Exception e) {
                Log.e(TAG, "Error importing data from CSV", e);
                return new ImportResult(false, "خطأ في استيراد البيانات: " + e.getMessage());
            }
        });
    }
    
    private int importAccounts(List<List<String>> data, String userId, List<String> errors) {
        int imported = 0;
        
        for (int i = 1; i < data.size(); i++) { // تخطي العنوان
            List<String> row = data.get(i);
            if (row.size() < 2) continue;
            
            try {
                Account account = new Account();
                account.name = row.get(0);
                account.type = row.size() > 1 ? row.get(1) : "عام";
                account.balance = row.size() > 2 ? Double.parseDouble(row.get(2)) : 0.0;
                account.currency = row.size() > 3 ? row.get(3) : "SAR";
                account.description = row.size() > 4 ? row.get(4) : "";
                account.userId = userId;
                
                database.accountDao().insert(account);
                imported++;
                
            } catch (Exception e) {
                errors.add("خطأ في الصف " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        return imported;
    }
    
    private int importTransactions(List<List<String>> data, String userId, List<String> errors) {
        int imported = 0;
        // تنفيذ استيراد المعاملات
        return imported;
    }
    
    private int importCategories(List<List<String>> data, List<String> errors) {
        int imported = 0;
        // تنفيذ استيراد الفئات
        return imported;
    }
    
    /**
     * تنظيف الموارد
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
        private int importedCount;
        private List<String> errors;
        
        public ImportResult(boolean success, String message) {
            this.success = success;
            this.message = message;
            this.errors = new ArrayList<>();
        }
        
        public void setImportedCount(int count) { this.importedCount = count; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getImportedCount() { return importedCount; }
        public List<String> getErrors() { return errors; }
    }
}
