package com.example.androidapp.ui.connection;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Connection;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;

public class ConnectionDetailActivity extends AppCompatActivity {

    private EditText nameEditText, typeEditText, statusEditText;
    private Button saveButton, deleteButton;
    private AppDatabase database;
    private SessionManager sessionManager;
    private String connectionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        nameEditText = // TODO: Fix findViewById;
        typeEditText = // TODO: Fix findViewById;
        statusEditText = // TODO: Fix findViewById;
        saveButton = // TODO: Fix findViewById;
        deleteButton = // TODO: Fix findViewById;

        if (getIntent().hasExtra("connection_id")) {
            connectionId = getIntent().getStringExtra("connection_id");
            loadConnectionData(connectionId);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> saveConnection());
        deleteButton.setOnClickListener(v -> deleteConnection());
    }

    private void loadConnectionData(String id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Connection connection = database.connectionDao().getConnectionById(id);
            runOnUiThread(() -> {
                if (connection != null) {
                    nameEditText.setText(connection.getName());
                    typeEditText.setText(connection.getType());
                    statusEditText.setText(connection.getStatus());
                } else {
                    Toast.makeText(this, "الاتصال غير موجود", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void saveConnection() {
        String name = nameEditText.getText().toString().trim();
        String type = typeEditText.getText().toString().trim();
        String status = statusEditText.getText().toString().trim();
        String companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || type.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        Connection connection;
        if (connectionId == null) {
            connection = new Connection(UUID.randomUUID().toString(), companyId, name, type, status);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                database.connectionDao().insert(connection);
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم إضافة الاتصال بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        } else {
            connection = new Connection(connectionId, companyId, name, type, status);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                database.connectionDao().update(connection);
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم تحديث الاتصال بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }

    private void deleteConnection() {
        if (connectionId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Connection connection = database.connectionDao().getConnectionById(connectionId);
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
}
