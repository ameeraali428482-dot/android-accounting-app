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

/**
 * مساعد التصدير والاستيراد المبسط - تصدير البيانات إلى CSV واستيرادها
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
                
                // إنشاء محتوى CSV
                StringBuilder csvContent = new StringBuilder();
                csvContent.append("المعرف,اسم الحساب,النوع,الرصيد,العملة,الوصف,تاريخ الإنشاء\n");
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                
                for (Account account : accounts) {
                    csvContent.append(account.id).append(",")
                             .append("\"").append(account.name).append("\",")
                             .append("\"").append(account.type).append("\",")
                             .append(account.balance).append(",")
                             .append("\"").append(account.currency).append("\",")
                             .append("\"").append(account.description != null ? account.description : "").append("\",")
                             .append("\"").append(sdf.format(new Date(account.createdAt))).append("\"\n");
                }
                
                // حفظ الملف
                File csvFile = saveToFile(csvContent.toString(), fileName, "csv");
                
                return new ExportResult(true, "تم تصدير الحسابات بنجاح", csvFile.getAbsolutePath());
                
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
                
                // إنشاء محتوى CSV
                StringBuilder csvContent = new StringBuilder();
                csvContent.append("المعرف,المبلغ,التاريخ,الوصف,من حساب,إلى حساب,الفئة,النوع,المستخدم\n");
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                
                for (Transaction transaction : transactions) {
                    csvContent.append(transaction.id).append(",")
                             .append(transaction.amount).append(",")
                             .append("\"").append(sdf.format(new Date(transaction.date))).append("\",")
                             .append("\"").append(transaction.description != null ? transaction.description : "").append("\",")
                             .append("\"").append(getAccountName(transaction.fromAccountId)).append("\",")
                             .append("\"").append(getAccountName(transaction.toAccountId)).append("\",")
                             .append("\"").append(getCategoryName(transaction.categoryId)).append("\",")
                             .append("\"").append(transaction.type != null ? transaction.type : "").append("\",")
                             .append("\"").append(transaction.userId != null ? transaction.userId : "").append("\"\n");
                }
                
                // حفظ الملف
                File csvFile = saveToFile(csvContent.toString(), fileName, "csv");
                
                return new ExportResult(true, "تم تصدير المعاملات بنجاح", csvFile.getAbsolutePath());
                
            } catch (Exception e) {
                Log.e(TAG, "Error exporting transactions", e);
                return new ExportResult(false, "خطأ في تصدير المعاملات: " + e.getMessage());
            }
        });
    }
    
    /**
     * حفظ المحتوى إلى ملف
     */
    private File saveToFile(String content, String fileName, String extension) throws IOException {
        File exportDir = new File(context.getExternalFilesDir(null), "exports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        
        if (!fileName.endsWith("." + extension)) {
            fileName += "." + extension;
        }
        
        File file = new File(exportDir, fileName);
        
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8")) {
            writer.write(content);
        }
        
        return file;
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
}
