package com.example.accountingapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountingapp.advanced.BackupManager;
import com.example.accountingapp.advanced.ActivityLogManager;

import java.util.List;

public class BackupRestoreActivity extends AppCompatActivity {
    private RecyclerView recyclerViewBackups;
    private BackupAdapter backupAdapter;
    private BackupManager backupManager;
    private ActivityLogManager activityLogManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        
        initializeComponents();
        setupRecyclerView();
        loadBackups();
    }
    
    private void initializeComponents() {
        backupManager = BackupManager.getInstance(this);
        activityLogManager = ActivityLogManager.getInstance(this);
        
        findViewById(R.id.btnCreateBackup).setOnClickListener(v -> createNewBackup());
        findViewById(R.id.btnImportBackup).setOnClickListener(v -> importBackup());
    }
    
    private void setupRecyclerView() {
        recyclerViewBackups = findViewById(R.id.recyclerViewBackups);
        recyclerViewBackups.setLayoutManager(new LinearLayoutManager(this));
        
        backupAdapter = new BackupAdapter(this, this::onBackupAction);
        recyclerViewBackups.setAdapter(backupAdapter);
    }
    
    private void loadBackups() {
        List<BackupManager.BackupInfo> backups = backupManager.getAvailableBackups();
        backupAdapter.setBackups(backups);
    }
    
    private void createNewBackup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إنشاء نسخة احتياطية جديدة");
        builder.setMessage("هل تريد إنشاء نسخة احتياطية الآن؟");
        
        builder.setPositiveButton("نعم", (dialog, which) -> {
            boolean success = backupManager.createBackup("manual");
            if (success) {
                Toast.makeText(this, "تم إنشاء النسخة الاحتياطية بنجاح", Toast.LENGTH_SHORT).show();
                activityLogManager.logActivity(
                    ActivityLogManager.ActivityType.BACKUP_CREATED,
                    "إنشاء نسخة احتياطية يدوية",
                    "تم إنشاء نسخة احتياطية بواسطة المستخدم"
                );
                loadBackups();
            } else {
                Toast.makeText(this, "فشل في إنشاء النسخة الاحتياطية", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
    
    private void importBackup() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        startActivityForResult(intent, 100);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            // معالجة استيراد النسخة الاحتياطية
            // TODO: تطبيق استيراد النسخة الاحتياطية من الملف المختار
        }
    }
    
    private void onBackupAction(BackupManager.BackupInfo backup, String action) {
        switch (action) {
            case "restore":
                restoreBackup(backup);
                break;
            case "export":
                exportBackup(backup);
                break;
            case "delete":
                deleteBackup(backup);
                break;
        }
    }
    
    private void restoreBackup(BackupManager.BackupInfo backup) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("استعادة النسخة الاحتياطية");
        builder.setMessage("هل تريد استعادة هذه النسخة الاحتياطية؟\n\n" +
                "التاريخ: " + backup.getFormattedDate() + "\n" +
                "الحجم: " + backup.getFormattedSize());
        
        builder.setPositiveButton("استعادة", (dialog, which) -> {
            // TODO: تطبيق استعادة النسخة الاحتياطية
            activityLogManager.logActivity(
                ActivityLogManager.ActivityType.BACKUP_RESTORED,
                "استعادة نسخة احتياطية",
                "تم استعادة النسخة الاحتياطية: " + backup.fileName
            );
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
    
    private void exportBackup(BackupManager.BackupInfo backup) {
        // TODO: تطبيق تصدير النسخة الاحتياطية
        Toast.makeText(this, "سيتم تطبيق التصدير قريباً", Toast.LENGTH_SHORT).show();
    }
    
    private void deleteBackup(BackupManager.BackupInfo backup) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("حذف النسخة الاحتياطية");
        builder.setMessage("هل تريد حذف هذه النسخة الاحتياطية نهائياً؟");
        
        builder.setPositiveButton("حذف", (dialog, which) -> {
            boolean success = backupManager.deleteBackup(backup.fileName);
            if (success) {
                Toast.makeText(this, "تم حذف النسخة الاحتياطية", Toast.LENGTH_SHORT).show();
                loadBackups();
            } else {
                Toast.makeText(this, "فشل في حذف النسخة الاحتياطية", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
}
