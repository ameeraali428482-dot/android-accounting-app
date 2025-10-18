package com.example.androidapp.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;




public class BackupManager {

    private static final String TAG = "BackupManager";
    private static final String DATABASE_NAME = "business_database"; // Must match AppDatabase name
    private static final String BACKUP_FOLDER_NAME = "BusinessAppBackups";

    public static boolean backupDatabase(Context context) {
        try {
            File data = Environment.getDataDirectory();
            String currentDBPath = "//data//" + context.getPackageName() + "//databases//" + DATABASE_NAME;
            String backupDBPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + BACKUP_FOLDER_NAME + "/";

            File currentDB = new File(data, currentDBPath);
            File backupDir = new File(backupDBPath);

            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File backupDB = new File(backupDir, DATABASE_NAME + "_" + timeStamp + ".db");

            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Log.d(TAG, "Database backed up to: " + backupDB.getAbsolutePath());
                Toast.makeText(context, "تم إنشاء نسخة احتياطية بنجاح: " + backupDB.getName(), Toast.LENGTH_LONG).show();
                return true;
            } else {
                Log.e(TAG, "Database does not exist: " + currentDB.getAbsolutePath());
                Toast.makeText(context, "فشل النسخ الاحتياطي: قاعدة البيانات غير موجودة", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Backup failed: " + e.getMessage());
            Toast.makeText(context, "فشل النسخ الاحتياطي: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public static boolean restoreDatabase(Context context, String backupFilePath) {
        try {
            File data = Environment.getDataDirectory();
            String currentDBPath = "//data//" + context.getPackageName() + "//databases//" + DATABASE_NAME;

            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(backupFilePath);

            if (backupDB.exists()) {
                // Close the database before restoring
                // AppDatabase.getDatabase(context).close(); // This might cause issues if not handled carefully

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Log.d(TAG, "Database restored from: " + backupDB.getAbsolutePath());
                Toast.makeText(context, "تم استعادة قاعدة البيانات بنجاح من: " + backupDB.getName(), Toast.LENGTH_LONG).show();
                // Re-initialize the database after restore if needed
                return true;
            } else {
                Log.e(TAG, "Backup file does not exist: " + backupFilePath);
                Toast.makeText(context, "فشل الاستعادة: ملف النسخ الاحتياطي غير موجود", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Restore failed: " + e.getMessage());
            Toast.makeText(context, "فشل الاستعادة: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    // Placeholder for Google Drive backup/restore functionality
    public static void backupToGoogleDrive(Context context) {
        Toast.makeText(context, "جاري تنفيذ النسخ الاحتياطي إلى Google Drive (غير متاح حاليًا)", Toast.LENGTH_SHORT).show();
        // Implementation for Google Drive backup would go here
    }

    public static void restoreFromGoogleDrive(Context context) {
        Toast.makeText(context, "جاري تنفيذ الاستعادة من Google Drive (غير متاح حاليًا)", Toast.LENGTH_SHORT).show();
        // Implementation for Google Drive restore would go here
    }
}

