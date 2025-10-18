package com.example.androidapp.ui.connection;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Connection;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ConnectionDetailActivity extends AppCompatActivity {
    private EditText etName, etType, etUrl;
    private Button btnSave, btnDel, btnTest;
    private AppDatabase db;
    private SessionManager sm;
    private String connectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_detail);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        etName  = findViewById(R.id.etConnectionName);
        etType  = findViewById(R.id.etConnectionType);
        etUrl   = findViewById(R.id.etConnectionUrl);
        btnSave = findViewById(R.id.btnSaveConnection);
        btnDel  = findViewById(R.id.btnDeleteConnection);
        btnTest = findViewById(R.id.btnTestConnection);

        connectionId = getIntent().getStringExtra("connection_id");
        if (connectionId != null) load();

        btnSave.setOnClickListener(v -> save());
        btnDel .setOnClickListener(v -> delete());
        btnTest.setOnClickListener(v -> Toast.makeText(this, "اختبار...", Toast.LENGTH_SHORT).show());
    }

    private void load() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Connection c = db.connectionDao().getConnectionByIdSync(connectionId);
            if (c != null) {
                runOnUiThread(() -> {
                    etName.setText(c.getConnectionName());
                    etType.setText(c.getConnectionType());
                    etUrl .setText(c.getConnectionUrl());
                });
            }
        });
    }

    private void save() {
        String name = etName.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String url  = etUrl .getText().toString().trim();
        String companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (name.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "أدخل الاسم والنوع", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            if (connectionId == null) {
                Connection c = new Connection();
                c.setId(UUID.randomUUID().toString());
                c.setCompanyId(companyId);
                c.setConnectionName(name);
                c.setConnectionType(type);
                c.setConnectionUrl(url);
                c.setStatus("inactive");
                db.connectionDao().insert(c);
            } else {
                Connection c = db.connectionDao().getConnectionByIdSync(connectionId);
                if (c != null) {
                    c.setConnectionName(name);
                    c.setConnectionType(type);
                    c.setConnectionUrl(url);
                    db.connectionDao().update(c);
                }
            }
            runOnUiThread(() -> { Toast.makeText(this, "تم الحفظ", Toast.LENGTH_SHORT).show(); finish(); });
        });
    }

    private void delete() {
        if (connectionId != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                Connection c = db.connectionDao().getConnectionByIdSync(connectionId);
                if (c != null) db.connectionDao().delete(c);
                runOnUiThread(() -> { Toast.makeText(this, "تم الحذف", Toast.LENGTH_SHORT).show(); finish(); });
            });
        }
    }
}
