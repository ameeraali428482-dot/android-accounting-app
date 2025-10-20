package com.example.accountingapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.accountingapp.R;

public class BackupRestoreActivity extends AppCompatActivity {

    private RecyclerView backupsRecyclerView;
    private Button createBackupButton;
    private Button importBackupButton;
    private ProgressBar progressBar;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);

        backupsRecyclerView = findViewById(R.id.backupsRecyclerView);
        createBackupButton = findViewById(R.id.createBackupButton);
        importBackupButton = findViewById(R.id.importBackupButton);
        progressBar = findViewById(R.id.progressBar);
        statusText = findViewById(R.id.statusText);

        // TODO: تكملة التهيئة وتشغيل التابع الخاص بالنسخ والاستعادة
    }
}
