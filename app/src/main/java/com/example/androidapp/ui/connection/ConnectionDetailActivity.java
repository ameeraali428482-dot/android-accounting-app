package com.example.androidapp.ui.connection;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.ConnectionDao;
import com.example.androidapp.data.entities.Connection;
import com.example.androidapp.utils.SessionManager;

import java.util.UUID;

public class ConnectionDetailActivity extends AppCompatActivity {

    private EditText nameEditText, typeEditText, statusEditText;
    private Button saveButton, deleteButton;
    private ConnectionDao connectionDao;
    private SessionManager sessionManager;
    private String connectionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_detail);

        nameEditText = findViewById(R.id.connection_name_edit_text);
        typeEditText = findViewById(R.id.connection_type_edit_text);
        statusEditText = findViewById(R.id.connection_status_edit_text);
        saveButton = findViewById(R.id.save_connection_button);
        deleteButton = findViewById(R.id.delete_connection_button);

        connectionDao = new ConnectionDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

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
        Connection connection = connectionDao.getById(id);
        if (connection != null) {
            nameEditText.setText(connection.getName());
            typeEditText.setText(connection.getType());
            statusEditText.setText(connection.getStatus());
        }
    }

    private void saveConnection() {
        String name = nameEditText.getText().toString().trim();
        String type = typeEditText.getText().toString().trim();
        String status = statusEditText.getText().toString().trim();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || type.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول المطلوبة.", Toast.LENGTH_SHORT).show();
            return;
        }

        Connection connection;
        if (connectionId == null) {
            // New connection
            connection = new Connection(UUID.randomUUID().toString(), companyId, name, type, status);
            connectionDao.insert(connection);
            Toast.makeText(this, "تم إضافة الاتصال بنجاح.", Toast.LENGTH_SHORT).show();
        } else {
            // Existing connection
            connection = new Connection(connectionId, companyId, name, type, status);
            connectionDao.update(connection);
            Toast.makeText(this, "تم تحديث الاتصال بنجاح.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteConnection() {
        if (connectionId != null) {
            connectionDao.delete(connectionId);
            Toast.makeText(this, "تم حذف الاتصال بنجاح.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
