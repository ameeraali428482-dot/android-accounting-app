package com.example.androidapp.ui.common;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.utils.SessionManager;

public class ImportExportActivity extends AppCompatActivity {
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        Button btnImportData = findViewById(R.id.btnImportData);
        Button btnExportData = findViewById(R.id.btnExportData);

        if (btnImportData != null) {
            btnImportData.setOnClickListener(v -> importData());
        }

        if (btnExportData != null) {
            btnExportData.setOnClickListener(v -> exportData());
        }
    }

    private void importData() {
        Toast.makeText(this, "سيتم تفعيل الاستيراد قريباً", Toast.LENGTH_SHORT).show();
    }

    private void exportData() {
        Toast.makeText(this, "سيتم تفعيل التصدير قريباً", Toast.LENGTH_SHORT).show();
    }
}
