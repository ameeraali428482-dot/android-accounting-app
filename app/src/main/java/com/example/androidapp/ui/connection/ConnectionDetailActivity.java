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
    private EditText etConnectionName;
    private EditText etConnectionType;
    private EditText etConnectionUrl;
    private Button btnSaveConnection;
    private Button btnDeleteConnection;
    private Button btnTestConnection;

    private AppDatabase database;
    private SessionManager sessionManager;
    private String connectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_detail);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        etConnectionName = findViewById(R.id.etConnectionName);
        etConnectionType = findViewById(R.id.etConnectionType);
        etConnectionUrl = findViewById(R.id.etConnectionUrl);
        btnSaveConnection = findViewById(R.id.btnSaveConnection);
        btnDeleteConnection = findViewById(R.id.btnDeleteConnection);
        btnTestConnection = findViewById(R.id.btnTestConnection);

        connectionId = getIntent().getStringExtra("connection_id");

        if (connectionId != null) {
            loadConnectionDetails();
        }

        btnSaveConnection.setOnClickListener(v -> saveConnection());
        btnDeleteConnection.setOnClickListener(v -> deleteConnection());
        btnTestConnection.setOnClickListener(v -> testConnection());
    }

    private void loadConnectionDetails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Connection connection = database.connectionDao().getConnectionByIdSync(connectionId);
            if (connection != null) {
                runOnUiThread(() -> {
                    etConnectionName.setText(connection.getConnectionName());
                    etConnectionType.setText(connection.getConnectionType());
                    etConnectionUrl.setText(connection.getConnectionUrl());
                });
            }
        });
    }

    private void saveConnection() {
        String name = etConnectionName.getText().toString().trim();
        String type = etConnectionType.getText().toString().trim();
        String url = etConnectionUrl.getText().toString().trim();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (name.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            if (connectionId == null) {
                Connection connection = new Connection(
                    UUID.randomUUID().toString(),
                    companyId,
                    name,
                    type,
                    url,
                    "",
                    "",
                    "inactive"
                );
                database.connectionDao().insert(connection);
            } else {
                Connection connection = database.connectionDao().getConnectionByIdSync(connectionId);
                if (connection != null) {
                    connection.setConnectionName(name);
                    connection.setConnectionType(type);
                    connection.setConnectionUrl(url);
                    database.connectionDao().update(connection);
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ الاتصال بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteConnection() {
        if (connectionId != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                Connection connection = database.connectionDao().getConnectionByIdSync(connectionId);
                if (connection != null) {
                    database.connectionDao().delete(connection);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم حذف الاتصال بنجاح", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        }
    }

    private void testConnection() {
        Toast.makeText(this, "اختبار الاتصال...", Toast.LENGTH_SHORT).show();
    }
}
