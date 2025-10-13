package com.example.androidapp.ui.role;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.utils.SessionManager;

import java.util.UUID;
import java.util.concurrent.Executors;

public class RoleDetailActivity extends AppCompatActivity {
    private EditText etRoleName;
    private EditText etRoleDescription;
    private RecyclerView rvPermissions;
    private Button btnSaveRole;
    private Button btnDeleteRole;
    
    private AppDatabase database;
    private SessionManager sessionManager;
    private String roleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_detail);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        etRoleName = findViewById(R.id.etRoleName);
        etRoleDescription = findViewById(R.id.etRoleDescription);
        rvPermissions = findViewById(R.id.rvPermissions);
        btnSaveRole = findViewById(R.id.btnSaveRole);
        btnDeleteRole = findViewById(R.id.btnDeleteRole);

        rvPermissions.setLayoutManager(new LinearLayoutManager(this));

        roleId = getIntent().getStringExtra("role_id");

        if (roleId != null) {
            loadRoleDetails();
        }

        btnSaveRole.setOnClickListener(v -> saveRole());
        btnDeleteRole.setOnClickListener(v -> deleteRole());
    }

    private void loadRoleDetails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Role role = database.roleDao().getRoleByIdSync(roleId);
            if (role != null) {
                runOnUiThread(() -> {
                    etRoleName.setText(role.getName());
                    etRoleDescription.setText(role.getDescription());
                });
            }
        });
    }

    private void saveRole() {
        String roleName = etRoleName.getText().toString().trim();
        String roleDescription = etRoleDescription.getText().toString().trim();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (roleName.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال اسم الدور", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            if (roleId == null) {
                Role role = new Role(
                    UUID.randomUUID().toString(),
                    roleName,
                    roleDescription,
                    companyId,
                    false
                );
                database.roleDao().insert(role);
            } else {
                Role role = database.roleDao().getRoleByIdSync(roleId);
                if (role != null) {
                    role.setName(roleName);
                    role.setDescription(roleDescription);
                    database.roleDao().update(role);
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ الدور بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteRole() {
        if (roleId != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                Role role = database.roleDao().getRoleByIdSync(roleId);
                if (role != null) {
                    database.roleDao().delete(role);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم حذف الدور بنجاح", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        }
    }
}
