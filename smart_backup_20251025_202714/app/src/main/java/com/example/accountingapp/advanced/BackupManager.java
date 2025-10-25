package com.example.accountingapp.advanced;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class BackupManager {
    private static final String TAG = "BackupManager";
    private static final String PREFS_NAME = "backup_manager_prefs";
    private static final String KEY_LAST_BACKUP = "last_backup";
    private static final String KEY_AUTO_BACKUP_ENABLED = "auto_backup_enabled";
    private static final String KEY_BACKUP_FREQUENCY = "backup_frequency";
    private static final String BACKUP_FOLDER = "accounting_backups";
    
    private static BackupManager instance;
    private SharedPreferences prefs;
    private Context context;
    private SimpleDateFormat dateFormat;
    
    private BackupManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }
    
    public static synchronized BackupManager getInstance(Context context) {
        if (instance == null) {
            instance = new BackupManager(context);
        }
        return instance;
    }
    
    // إنشاء نسخة احتياطية شاملة
    public String createFullBackup() {
        try {
            JSONObject backup = new JSONObject();
            
            // معلومات النسخة الاحتياطية
            backup.put("backup_version", "1.0");
            backup.put("app_version", getAppVersion());
            backup.put("created_date", dateFormat.format(new Date()));
            backup.put("user_id", OfflineSessionManager.getInstance(context).getCurrentUserId());
            backup.put("username", OfflineSessionManager.getInstance(context).getCurrentUsername());
            
            // بيانات المحاسبة
            backup.put("accounts", getAccountsData());
            backup.put("transactions", getTransactionsData());
            backup.put("categories", getCategoriesData());
            backup.put("reports", getReportsData());
            backup.put("settings", getSettingsData());
            backup.put("activity_log", getActivityLogData());
            
            // حفظ النسخة الاحتياطية
            String backupFileName = "backup_" + System.currentTimeMillis() + ".json";
            File backupFile = saveBackupToFile(backup, backupFileName);
            
            // تحديث آخر نسخة احتياطية
            updateLastBackupTime();
            
            Log.d(TAG, "تم إنشاء نسخة احتياطية: " + backupFileName);
            return backupFile.getAbsolutePath();
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إنشاء النسخة الاحتياطية", e);
            return null;
        }
    }
    
    // استرجاع النسخة الاحتياطية مع دمج البيانات
    public boolean restoreBackup(String backupPath, boolean mergeWithExisting) {
        try {
            File backupFile = new File(backupPath);
            if (!backupFile.exists()) {
                Log.e(TAG, "ملف النسخة الاحتياطية غير موجود");
                return false;
            }
            
            // قراءة النسخة الاحتياطية
            String backupContent = readFileContent(backupFile);
            JSONObject backup = new JSONObject(backupContent);
            
            // فحص صحة النسخة الاحتياطية
            if (!validateBackup(backup)) {
                Log.e(TAG, "النسخة الاحتياطية غير صالحة");
                return false;
            }
            
            // إنشاء نسخة احتياطية حالية قبل الاستراجع
            String currentBackup = createFullBackup();
            
            if (mergeWithExisting) {
                // دمج البيانات
                mergeBackupData(backup);
            } else {
                // استبدال البيانات بالكامل
                replaceAllData(backup);
            }
            
            // تسجيل عملية الاسترجاع
            ActivityLogManager.getInstance(context).logActivity(
                "BACKUP_RESTORE",
                "تم استرجاع النسخة الاحتياطية: " + backupFile.getName(),
                ActivityLogManager.PRIORITY_HIGH
            );
            
            Log.d(TAG, "تم استرجاع النسخة الاحتياطية بنجاح");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في استرجاع النسخة الاحتياطية", e);
            return false;
        }
    }
    
    // فحص النسخ الاحتياطية المتاحة وإشعار المستخدم
    public void checkForAvailableBackupsAndNotify() {
        try {
            List<BackupInfo> availableBackups = getAvailableBackups();
            String currentUserId = OfflineSessionManager.getInstance(context).getCurrentUserId();
            
            // البحث عن نسخ احتياطية للمستخدم الحالي
            List<BackupInfo> userBackups = new ArrayList<>();
            for (BackupInfo backup : availableBackups) {
                if (backup.userId.equals(currentUserId)) {
                    userBackups.add(backup);
                }
            }
            
            if (!userBackups.isEmpty()) {
                // ترتيب النسخ حسب التاريخ (الأحدث أولاً)
                Collections.sort(userBackups, (a, b) -> b.createdDate.compareTo(a.createdDate));
                
                BackupInfo latestBackup = userBackups.get(0);
                
                // فحص ما إذا كانت النسخة أحدث من البيانات الحالية
                if (shouldOfferRestore(latestBackup)) {
                    showBackupRestoreNotification(latestBackup);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في فحص النسخ الاحتياطية", e);
        }
    }
    
    // دمج بيانات النسخة الاحتياطية مع البيانات الحالية
    private void mergeBackupData(JSONObject backup) throws JSONException {
        DataMerger merger = DataMerger.getInstance(context);
        
        // دمج الحسابات
        if (backup.has("accounts")) {
            merger.mergeAccounts(backup.getJSONArray("accounts"));
        }
        
        // دمج المعاملات
        if (backup.has("transactions")) {
            merger.mergeTransactions(backup.getJSONArray("transactions"));
        }
        
        // دمج الفئات
        if (backup.has("categories")) {
            merger.mergeCategories(backup.getJSONArray("categories"));
        }
        
        // دمج التقارير
        if (backup.has("reports")) {
            merger.mergeReports(backup.getJSONArray("reports"));
        }
        
        // دمج سجل الأنشطة
        if (backup.has("activity_log")) {
            merger.mergeActivityLog(backup.getJSONArray("activity_log"));
        }
        
        Log.d(TAG, "تم دمج بيانات النسخة الاحتياطية");
    }
    
    // الحصول على قائمة النسخ الاحتياطية المتاحة
    public List<BackupInfo> getAvailableBackups() {
        List<BackupInfo> backups = new ArrayList<>();
        
        try {
            File backupDir = new File(context.getFilesDir(), BACKUP_FOLDER);
            if (!backupDir.exists()) return backups;
            
            File[] backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (backupFiles == null) return backups;
            
            for (File file : backupFiles) {
                try {
                    String content = readFileContent(file);
                    JSONObject backup = new JSONObject(content);
                    
                    BackupInfo info = new BackupInfo();
                    info.fileName = file.getName();
                    info.filePath = file.getAbsolutePath();
                    info.userId = backup.optString("user_id", "");
                    info.username = backup.optString("username", "");
                    info.createdDate = dateFormat.parse(backup.getString("created_date"));
                    info.fileSize = file.length();
                    
                    backups.add(info);
                    
                } catch (Exception e) {
                    Log.w(TAG, "تخطي ملف نسخة احتياطية غير صالح: " + file.getName());
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في الحصول على النسخ الاحتياطية", e);
        }
        
        return backups;
    }
    
    // تصدير نسخة احتياطية
    public String exportBackup(String backupPath, String exportLocation) {
        try {
            File sourceFile = new File(backupPath);
            File exportDir = new File(exportLocation);
            
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            
            File exportFile = new File(exportDir, sourceFile.getName());
            
            // نسخ الملف
            copyFile(sourceFile, exportFile);
            
            Log.d(TAG, "تم تصدير النسخة الاحتياطية إلى: " + exportFile.getAbsolutePath());
            return exportFile.getAbsolutePath();
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في تصدير النسخة الاحتياطية", e);
            return null;
        }
    }
    
    // حذف نسخة احتياطية
    public boolean deleteBackup(String backupPath) {
        try {
            File backupFile = new File(backupPath);
            boolean deleted = backupFile.delete();
            
            if (deleted) {
                Log.d(TAG, "تم حذف النسخة الاحتياطية: " + backupPath);
            }
            
            return deleted;
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في حذف النسخة الاحتياطية", e);
            return false;
        }
    }
    
    // فحص ما إذا كان يجب عرض اقتراح الاسترجاع
    private boolean shouldOfferRestore(BackupInfo backup) {
        // فحص ما إذا كانت النسخة أحدث من آخر تفاعل مع التطبيق
        long lastActivity = OfflineSessionManager.getInstance(context).getLastLoginTime();
        return backup.createdDate.getTime() > lastActivity;
    }
    
    // عرض إشعار استرجاع النسخة الاحتياطية
    private void showBackupRestoreNotification(BackupInfo backup) {
        NotificationManager notificationManager = NotificationManager.getInstance(context);
        
        String message = String.format(
            "تم العثور على نسخة احتياطية حديثة من %s\nآخر تعديل: %s\nهل تريد استرجاعها؟",
            backup.username,
            dateFormat.format(backup.createdDate)
        );
        
        notificationManager.showBackupRestoreNotification(backup, message);
    }
    
    // فحص صحة النسخة الاحتياطية
    private boolean validateBackup(JSONObject backup) {
        try {
            return backup.has("backup_version") &&
                   backup.has("created_date") &&
                   backup.has("user_id");
        } catch (Exception e) {
            return false;
        }
    }
    
    // الحصول على بيانات الحسابات
    private JSONArray getAccountsData() throws JSONException {
        // هنا يجب جلب البيانات من قاعدة البيانات الفعلية
        return new JSONArray();
    }
    
    // الحصول على بيانات المعاملات
    private JSONArray getTransactionsData() throws JSONException {
        return new JSONArray();
    }
    
    // الحصول على بيانات الفئات
    private JSONArray getCategoriesData() throws JSONException {
        return new JSONArray();
    }
    
    // الحصول على بيانات التقارير
    private JSONArray getReportsData() throws JSONException {
        return new JSONArray();
    }
    
    // الحصول على بيانات الإعدادات
    private JSONObject getSettingsData() throws JSONException {
        return new JSONObject();
    }
    
    // الحصول على بيانات سجل الأنشطة
    private JSONArray getActivityLogData() throws JSONException {
        return ActivityLogManager.getInstance(context).exportActivityLog();
    }
    
    // الحصول على إصدار التطبيق
    private String getAppVersion() {
        try {
            return context.getPackageManager()
                   .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }
    
    // حفظ النسخة الاحتياطية في ملف
    private File saveBackupToFile(JSONObject backup, String fileName) throws IOException {
        File backupDir = new File(context.getFilesDir(), BACKUP_FOLDER);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        
        File backupFile = new File(backupDir, fileName);
        
        try (FileWriter writer = new FileWriter(backupFile)) {
            writer.write(backup.toString(2));
        }
        
        return backupFile;
    }
    
    // قراءة محتوى ملف
    private String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        return content.toString();
    }
    
    // نسخ ملف
    private void copyFile(File source, File destination) throws IOException {
        try (InputStream input = new FileInputStream(source);
             OutputStream output = new FileOutputStream(destination)) {
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        }
    }
    
    // استبدال جميع البيانات
    private void replaceAllData(JSONObject backup) throws JSONException {
        // هنا يجب تنفيذ منطق استبدال البيانات بالكامل
        Log.d(TAG, "استبدال جميع البيانات من النسخة الاحتياطية");
    }
    
    // تحديث وقت آخر نسخة احتياطية
    private void updateLastBackupTime() {
        prefs.edit().putLong(KEY_LAST_BACKUP, System.currentTimeMillis()).apply();
    }
    
    // فئة معلومات النسخة الاحتياطية
    public static class BackupInfo {
        public String fileName;
        public String filePath;
        public String userId;
        public String username;
        public Date createdDate;
        public long fileSize;
        
        public String getFormattedSize() {
            if (fileSize < 1024) return fileSize + " B";
            if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }
}
