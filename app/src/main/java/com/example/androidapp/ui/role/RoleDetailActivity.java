package com.example.androidapp.ui.role;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Permission;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import java.util.ArrayList;
import java.util.UUID;

public class RoleDetailActivity extends AppCompatActivity {
    private EditText etRoleName;
    private EditText etRoleDescription;
    private RecyclerView rvPermissions;
    private Button btnSaveRole;
    private Button btnDeleteRole;
    
    private AppDatabase database;
    private SessionManager sessionManager;
    private GenericAdapter<Permission> permissionsAdapter;
    private String roleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupPermissionsRecyclerView();
        
        roleId = getIntent().getStringExtra("role_id");
        if (roleId != null) {
            loadRoleData(roleId);
            btnDeleteRole.setVisibility(View.VISIBLE);
        } else {
            btnDeleteRole.setVisibility(View.GONE);
        }

        btnSaveRole.setOnClickListener(v -> saveRole());
        btnDeleteRole.setOnClickListener(v -> deleteRole());
    }

    private void initViews() {
        etRoleName = findViewById(R.id.etRoleNameInput);
        etRoleDescription = findViewById(R.id.etRoleDescInput);
        rvPermissions = findViewById(R.id.rvPermissionsList);
        btnSaveRole = findViewById(R.id.btnSaveRole);
        btnDeleteRole = findViewById(R.id.btnDeleteRole);
    }

    private void setupPermissionsRecyclerView() {
        rvPermissions.setLayoutManager(new LinearLayoutManager(this));
        permissionsAdapter = new GenericAdapter<Permission>(new ArrayList<>(), permission -> {
            // Handle permission click
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.permission_list_row;
            }

            @Override
            protected void bindView(View view, Permission permission) {
                // Bind permission data
            }
        };
        rvPermissions.setAdapter(permissionsAdapter);
    }

    private void loadRoleData(String id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Role role = database.roleDao().getRoleById(id, sessionManager.getCurrentCompanyId());
            runOnUiThread(() -> {
                if (role != null) {
                    etRoleName.setText(role.getName());
                    etRoleDescription.setText(role.getDescription());
                }
            });
        });
    }

    private void saveRole() {
        String name = etRoleName.getText().toString().trim();
        String description = etRoleDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال اسم الدور", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (roleId == null) {
                Role role = new Role(UUID.randomUUID().toString(), sessionManager.getCurrentCompanyId(), name, description);
                database.roleDao().insert(role);
            } else {
                Role role = new Role(roleId, sessionManager.getCurrentCompanyId(), name, description);
                database.roleDao().update(role);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteRole() {
        if (roleId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Role role = database.roleDao().getRoleById(roleId, sessionManager.getCurrentCompanyId());
                if (role != null) {
                    database.roleDao().delete(role);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        }
    }
}
