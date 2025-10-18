package com.example.accountingapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.accountingapp.advanced.*;
import java.util.List;

public class BackupRestoreActivity extends AppCompatActivity {
    private static final String TAG = "BackupRestoreActivity";
    
    private RecyclerView backupsRecyclerView;
    private BackupAdapter backupAdapter;
    private Button createBackupButton;
    private Button importBackupButton;
    private ProgressBar progressBar;
    private TextView statusText;
    
    private BackupManager backupManager;
    private NotificationManager notificationManager;
    private ActivityLogManager activityLogManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        
        initializeManagers();
        initializeViews();
        setupRecyclerView();
        handleIntent();
        loadAvailableBackups();
    }
    
    private void initializeManagers() {
        backupManager = BackupManager.getInstance(this);
        notificationManager = NotificationManager.getInstance(this);
        activityLogManager = ActivityLogManager.getInstance(this);
    }
    
    private void initializeViews() {
        backupsRecyclerView = findViewById(R.id.backupsRecyclerView);
        createBackupButton = findViewById(R.id.createBackupButton);
        importBackupButton = findViewById(R.id.importBackupButton);
        progressBar = findViewById(R.id.progressBar);
        statusText = findViewById(R.id.statusText);
        
        createBackupButton.setOnClickListener(v -> createNewBackup());
        importBackupButton.setOnClickListener(v -> importExternalBackup());
    }
    
    private void setupRecyclerView() {
        backupAdapter = new BackupAdapter(this::onBackupSelected);
        backupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        backupsRecyclerView.setAdapter(backupAdapter);
    }
    
    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String backupPath = intent.getStringExtra("backup_path");
            String action = intent.getStringExtra("action");
            
            if (backupPath != null && action != null) {
                if ("restore".equals(action)) {
                    showRestoreConfirmation(backupPath, false);
                } else if ("merge".equals(action)) {
                    showRestoreConfirmation(backupPath, true);
                }
            }
        }
    }
    
    private void createNewBackup() {
        new AsyncTask<Void, Void, String>() {
            private ProgressDialog progressDialog;
            
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(
                    BackupRestoreActivity.this,
                    "إنشاء نسخة احتياطية",
                    "جاري إنشاء النسخة الاحتياطية...",
                    true,
                    false
                );
            }
            
            @Override
            protected String doInBackground(Void... voids) {
                return backupManager.createFullBackup();
            }
            
            @Override
            protected void onPostExecute(String backupPath) {
                progressDialog.dismiss();
                
                if (backupPath != null) {
                    Toast.makeText(BackupRestoreActivity.this,
                                 "تم إنشاء النسخة الاحتياطية بنجاح", Toast.LENGTH_SHORT).show();
                    
                    activityLogManager.logActivity(
                        ActivityLogManager.TYPE_BACKUP,
                        "تم إنشاء نسخة احتياطية جديدة",
                        ActivityLogManager.PRIORITY_MEDIUM
                    );
                    
                    loadAvailableBackups();
                } else {
                    Toast.makeText(BackupRestoreActivity.this,
                                 "فشل في إنشاء النسخة الاحتياطية", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    
    private void importExternalBackup() {
        // هنا يمكن إضافة كود لاستيراد نسخة احتياطية من ملف خارجي
        Toast.makeText(this, "استيراد النسخ الاحتياطية الخارجية غير متاح حالياً", 
                      Toast.LENGTH_SHORT).show();
    }
    
    private void loadAvailableBackups() {
        new AsyncTask<Void, Void, List<BackupManager.BackupInfo>>() {
            @Override
            protected List<BackupManager.BackupInfo> doInBackground(Void... voids) {
                return backupManager.getAvailableBackups();
            }
            
            @Override
            protected void onPostExecute(List<BackupManager.BackupInfo> backups) {
                backupAdapter.updateBackups(backups);
                
                if (backups.isEmpty()) {
                    statusText.setText("لا توجد نسخ احتياطية متاحة");
                    statusText.setVisibility(View.VISIBLE);
                } else {
                    statusText.setVisibility(View.GONE);
                }
            }
        }.execute();
    }
    
    private void onBackupSelected(BackupManager.BackupInfo backup) {
        showBackupOptionsDialog(backup);
    }
    
    private void showBackupOptionsDialog(BackupManager.BackupInfo backup) {
        String[] options = {"استرجاع (استبدال الكل)", "دمج مع البيانات الحالية", 
                           "تصدير", "حذف", "عرض التفاصيل"};
        
        new AlertDialog.Builder(this)
            .setTitle("خيارات النسخة الاحتياطية")
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0: // استرجاع
                        showRestoreConfirmation(backup.filePath, false);
                        break;
                    case 1: // دمج
                        showRestoreConfirmation(backup.filePath, true);
                        break;
                    case 2: // تصدير
                        exportBackup(backup);
                        break;
                    case 3: // حذف
                        showDeleteConfirmation(backup);
                        break;
                    case 4: // تفاصيل
                        showBackupDetails(backup);
                        break;
                }
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    private void showRestoreConfirmation(String backupPath, boolean merge) {
        String message = merge ? 
            "هل تريد دمج هذه النسخة الاحتياطية مع البيانات الحالية؟\n\n" +
            "سيتم الاحتفاظ بجميع البيانات الحالية وإضافة البيانات الجديدة من النسخة الاحتياطية." :
            "هل تريد استرجاع هذه النسخة الاحتياطية؟\n\n" +
            "تحذير: سيتم استبدال جميع البيانات الحالية!";
        
        new AlertDialog.Builder(this)
            .setTitle(merge ? "دمج النسخة الاحتياطية" : "استرجاع النسخة الاحتياطية")
            .setMessage(message)
            .setPositiveButton(merge ? "دمج" : "استرجاع", (dialog, which) -> {
                restoreBackup(backupPath, merge);
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    private void restoreBackup(String backupPath, boolean merge) {
        new AsyncTask<Void, Void, Boolean>() {
            private ProgressDialog progressDialog;
            
            @Override
            protected void onPreExecute() {
                String message = merge ? "جاري دمج النسخة الاحتياطية..." : 
                                       "جاري استرجاع النسخة الاحتياطية...";
                progressDialog = ProgressDialog.show(
                    BackupRestoreActivity.this,
                    merge ? "دمج البيانات" : "استرجاع البيانات",
                    message,
                    true,
                    false
                );
            }
            
            @Override
            protected Boolean doInBackground(Void... voids) {
                return backupManager.restoreBackup(backupPath, merge);
            }
            
            @Override
            protected void onPostExecute(Boolean success) {
                progressDialog.dismiss();
                
                if (success) {
                    String message = merge ? "تم دمج النسخة الاحتياطية بنجاح" : 
                                           "تم استرجاع النسخة الاحتياطية بنجاح";
                    
                    Toast.makeText(BackupRestoreActivity.this, message, Toast.LENGTH_LONG).show();
                    
                    activityLogManager.logActivity(
                        ActivityLogManager.TYPE_RESTORE,
                        merge ? "تم دمج نسخة احتياطية" : "تم استرجاع نسخة احتياطية",
                        ActivityLogManager.PRIORITY_HIGH
                    );
                    
                    notificationManager.showGeneralNotification(
                        "تم الانتهاء من العملية",
                        message,
                        ActivityLogManager.PRIORITY_MEDIUM
                    );
                    
                    // إعادة تحديث القائمة
                    loadAvailableBackups();
                    
                } else {
                    String message = merge ? "فشل في دمج النسخة الاحتياطية" : 
                                           "فشل في استرجاع النسخة الاحتياطية";
                    Toast.makeText(BackupRestoreActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    
    private void exportBackup(BackupManager.BackupInfo backup) {
        // هنا يمكن إضافة كود لتصدير النسخة الاحتياطية
        Toast.makeText(this, "تصدير النسخة الاحتياطية غير متاح حالياً", Toast.LENGTH_SHORT).show();
    }
    
    private void showDeleteConfirmation(BackupManager.BackupInfo backup) {
        new AlertDialog.Builder(this)
            .setTitle("حذف النسخة الاحتياطية")
            .setMessage("هل تريد حذف هذه النسخة الاحتياطية نهائياً؟\n\nلا يمكن التراجع عن هذا الإجراء.")
            .setPositiveButton("حذف", (dialog, which) -> {
                deleteBackup(backup);
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    private void deleteBackup(BackupManager.BackupInfo backup) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return backupManager.deleteBackup(backup.filePath);
            }
            
            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Toast.makeText(BackupRestoreActivity.this,
                                 "تم حذف النسخة الاحتياطية", Toast.LENGTH_SHORT).show();
                    
                    activityLogManager.logActivity(
                        ActivityLogManager.TYPE_BACKUP,
                        "تم حذف نسخة احتياطية: " + backup.fileName,
                        ActivityLogManager.PRIORITY_LOW
                    );
                    
                    loadAvailableBackups();
                } else {
                    Toast.makeText(BackupRestoreActivity.this,
                                 "فشل في حذف النسخة الاحتياطية", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    
    private void showBackupDetails(BackupManager.BackupInfo backup) {
        String details = String.format(
            "اسم الملف: %s\n\n" +
            "المستخدم: %s\n\n" +
            "تاريخ الإنشاء: %s\n\n" +
            "حجم الملف: %s",
            backup.fileName,
            backup.username,
            backup.createdDate.toString(),
            backup.getFormattedSize()
        );
        
        new AlertDialog.Builder(this)
            .setTitle("تفاصيل النسخة الاحتياطية")
            .setMessage(details)
            .setPositiveButton("موافق", null)
            .show();
    }
}
