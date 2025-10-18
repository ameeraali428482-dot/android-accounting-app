package com.example.accountingapp.advanced;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupManager {
    private static final String TAG = "BackupManager";
    private static final String PREFS_NAME = "backup_prefs";
    private static final String KEY_LAST_BACKUP_DATE = "last_backup_date";
    private static final String KEY_AUTO_BACKUP_ENABLED = "auto_backup_enabled";
    private static final String KEY_BACKUP_FREQUENCY = "backup_frequency";
    private static final String KEY_CLOUD_BACKUP_ENABLED = "cloud_backup_enabled";
    
    private static BackupManager instance;
    private SharedPreferences prefs;
    private Context context;
    
    private BackupManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized BackupManager getInstance(Context context) {
        if (instance == null) {
            instance = new BackupManager(context);
        }
        return instance;
    }
    
    // إنشاء نسخة احتياطية
    public boolean createBackup(String backupName) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "backup_" + timestamp + "_" + backupName + ".zip";
            
            File backupDir = new File(context.getExternalFilesDir(null), "backups");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            
            File backupFile = new File(backupDir, fileName);
            
            // إنشاء بيانات النسخة الاحتياطية
            JSONObject backupData = collectBackupData();
            
            // ضغط البيانات
            FileOutputStream fos = new FileOutputStream(backupFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            
            ZipEntry entry = new ZipEntry("backup_data.json");
            zos.putNextEntry(entry);
            zos.write(backupData.toString().getBytes());
            zos.closeEntry();
            zos.close();
            
            // تحديث تاريخ آخر نسخة احتياطية
            prefs.edit().putLong(KEY_LAST_BACKUP_DATE, System.currentTimeMillis()).apply();
            
            Log.d(TAG, "تم إنشاء نسخة احتياطية: " + fileName);
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إنشاء النسخة الاحتياطية", e);
            return false;
        }
    }
    
    // جمع بيانات النسخة الاحتياطية
    private JSONObject collectBackupData() throws JSONException {
        JSONObject backupData = new JSONObject();
        
        // معلومات النسخة الاحتياطية
        JSONObject metadata = new JSONObject();
        metadata.put("version", "1.0");
        metadata.put("created_date", System.currentTimeMillis());
        metadata.put("user_id", OfflineSessionManager.getInstance(context).getCurrentUserId());
        metadata.put("username", OfflineSessionManager.getInstance(context).getCurrentUsername());
        
        backupData.put("metadata", metadata);
        
        // هنا يمكن إضافة جمع البيانات من قاعدة البيانات
        // مثل الحسابات، الفواتير، إلخ
        
        return backupData;
    }
    
    // استعادة النسخة الاحتياطية
    public boolean restoreBackup(File backupFile) {
        try {
            FileInputStream fis = new FileInputStream(backupFile);
            ZipInputStream zis = new ZipInputStream(fis);
            
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("backup_data.json")) {
                    byte[] buffer = new byte[1024];
                    StringBuilder sb = new StringBuilder();
                    int length;
                    
                    while ((length = zis.read(buffer)) > 0) {
                        sb.append(new String(buffer, 0, length));
                    }
                    
                    JSONObject backupData = new JSONObject(sb.toString());
                    return processBackupData(backupData);
                }
            }
            
            zis.close();
            return false;
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في استعادة النسخة الاحتياطية", e);
            return false;
        }
    }
    
    // معالجة بيانات النسخة الاحتياطية
    private boolean processBackupData(JSONObject backupData) {
        try {
            JSONObject metadata = backupData.getJSONObject("metadata");
            
            // فحص توافق النسخة
            String version = metadata.getString("version");
            if (!version.equals("1.0")) {
                Log.w(TAG, "نسخة غير متوافقة: " + version);
                return false;
            }
            
            // هنا يمكن إضافة استعادة البيانات إلى قاعدة البيانات
            
            Log.d(TAG, "تم استعادة النسخة الاحتياطية بنجاح");
            return true;
            
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في معالجة بيانات النسخة الاحتياطية", e);
            return false;
        }
    }
    
    // الحصول على قائمة النسخ الاحتياطية
    public List<BackupInfo> getAvailableBackups() {
        List<BackupInfo> backups = new ArrayList<>();
        
        File backupDir = new File(context.getExternalFilesDir(null), "backups");
        if (backupDir.exists()) {
            File[] files = backupDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".zip")) {
                        BackupInfo info = new BackupInfo();
                        info.fileName = file.getName();
                        info.filePath = file.getAbsolutePath();
                        info.fileSize = file.length();
                        info.createdDate = file.lastModified();
                        
                        backups.add(info);
                    }
                }
            }
        }
        
        return backups;
    }
    
    // حذف النسخة الاحتياطية
    public boolean deleteBackup(String fileName) {
        File backupDir = new File(context.getExternalFilesDir(null), "backups");
        File backupFile = new File(backupDir, fileName);
        
        if (backupFile.exists()) {
            return backupFile.delete();
        }
        
        return false;
    }
    
    // تصدير النسخة الاحتياطية
    public boolean exportBackup(String fileName, String targetPath) {
        try {
            File backupDir = new File(context.getExternalFilesDir(null), "backups");
            File sourceFile = new File(backupDir, fileName);
            File targetFile = new File(targetPath);
            
            if (!sourceFile.exists()) {
                return false;
            }
            
            FileInputStream fis = new FileInputStream(sourceFile);
            FileOutputStream fos = new FileOutputStream(targetFile);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            
            fis.close();
            fos.close();
            
            Log.d(TAG, "تم تصدير النسخة الاحتياطية إلى: " + targetPath);
            return true;
            
        } catch (IOException e) {
            Log.e(TAG, "خطأ في تصدير النسخة الاحتياطية", e);
            return false;
        }
    }
    
    // فحص وجود نسخ احتياطية سحابية
    public boolean hasCloudBackups(String userId) {
        // هنا يمكن إضافة فحص النسخ الاحتياطية السحابية
        // مثل Firebase، Google Drive، إلخ
        return false;
    }
    
    // تمكين النسخ الاحتياطي التلقائي
    public void setAutoBackupEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_BACKUP_ENABLED, enabled).apply();
    }
    
    public boolean isAutoBackupEnabled() {
        return prefs.getBoolean(KEY_AUTO_BACKUP_ENABLED, false);
    }
    
    // تعيين تردد النسخ الاحتياطي
    public void setBackupFrequency(long frequencyInMillis) {
        prefs.edit().putLong(KEY_BACKUP_FREQUENCY, frequencyInMillis).apply();
    }
    
    public long getBackupFrequency() {
        return prefs.getLong(KEY_BACKUP_FREQUENCY, 24 * 60 * 60 * 1000); // يوم واحد افتراضياً
    }
    
    // فحص الحاجة للنسخ الاحتياطي
    public boolean needsBackup() {
        if (!isAutoBackupEnabled()) {
            return false;
        }
        
        long lastBackupDate = prefs.getLong(KEY_LAST_BACKUP_DATE, 0);
        long frequency = getBackupFrequency();
        
        return (System.currentTimeMillis() - lastBackupDate) > frequency;
    }
    
    // كلاس معلومات النسخة الاحتياطية
    public static class BackupInfo {
        public String fileName;
        public String filePath;
        public long fileSize;
        public long createdDate;
        public String description;
        
        public String getFormattedDate() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(new Date(createdDate));
        }
        
        public String getFormattedSize() {
            if (fileSize < 1024) {
                return fileSize + " B";
            } else if (fileSize < 1024 * 1024) {
                return String.format("%.1f KB", fileSize / 1024.0);
            } else {
                return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
            }
        }
    }
}
