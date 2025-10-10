package com.example.androidapp.ui.role;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Permission;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.data.entities.RolePermission;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleDetailActivity extends AppCompatActivity {
    private EditText etRoleName, etRoleDescription;
    private RecyclerView rvPermissions;
    private GenericAdapter<Permission> permissionsAdapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private String roleId = null;
    private Role currentRole;
    private List<Permission> allPermissions = new ArrayList<>();
    private List<Permission> selectedPermissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupPermissionsRecyclerView();
        loadAllPermissions();

        roleId = getIntent().getStringExtra("role_id");
        if (roleId != null) {
            setTitle("تعديل الدور");
        } else {
            setTitle("إضافة دور جديد");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        etRoleName = findViewById(R.id.et_role_name);
        etRoleDescription = findViewById(R.id.et_role_description);
        rvPermissions = findViewById(R.id.rv_permissions);
    }

    private void setupPermissionsRecyclerView() {
        rvPermissions.setLayoutManager(new LinearLayoutManager(this));
        permissionsAdapter = new GenericAdapter<>(
                new ArrayList<>(),
                R.layout.permission_list_row,
                (view, permission) -> {
                    TextView tvPermissionName = view.findViewById(R.id.tv_permission_name);
                    tvPermissionName.setText(permission.getAction());
                    if (selectedPermissions.stream().anyMatch(p -> p.getId().equals(permission.getId()))) {
                        view.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    } else {
                        view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                },
                permission -> {
                    if (selectedPermissions.stream().anyMatch(p -> p.getId().equals(permission.getId()))) {
                        selectedPermissions.removeIf(p -> p.getId().equals(permission.getId()));
                    } else {
                        selectedPermissions.add(permission);
                    }
                    permissionsAdapter.notifyDataSetChanged();
                }
        );
        rvPermissions.setAdapter(permissionsAdapter);
    }

    private void loadAllPermissions() {
        database.permissionDao().getAllPermissions().observe(this, permissions -> {
            if (permissions != null) {
                allPermissions = permissions;
                permissionsAdapter.updateData(allPermissions);
                if (roleId != null) {
                    loadRole();
                }
            }
        });
    }

    private void loadRole() {
        database.roleDao().getRoleById(roleId).observe(this, role -> {
            if (role != null) {
                currentRole = role;
                etRoleName.setText(currentRole.getName());
                etRoleDescription.setText(currentRole.getDescription());
                database.roleDao().getPermissionsForRole(roleId).observe(this, permissions -> {
                    selectedPermissions = new ArrayList<>(permissions);
                    permissionsAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void saveRole() {
        String name = etRoleName.getText().toString().trim();
        String description = etRoleDescription.getText().toString().trim();

        if (name.isEmpty()) {
            etRoleName.setError("اسم الدور مطلوب");
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (roleId == null) {
                String newRoleId = UUID.randomUUID().toString();
                Role newRole = new Role(newRoleId, name, description, sessionManager.getCurrentCompanyId(), false);
                database.roleDao().insert(newRole);
                for (Permission p : selectedPermissions) {
                    database.roleDao().insertRolePermission(new RolePermission(newRoleId, p.getId()));
                }
            } else {
                currentRole.setName(name);
                currentRole.setDescription(description);
                database.roleDao().update(currentRole);
                database.roleDao().deleteRolePermissions(roleId);
                for (Permission p : selectedPermissions) {
                    database.roleDao().insertRolePermission(new RolePermission(roleId, p.getId()));
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ الدور بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_save) {
            saveRole();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
