package com.example.androidapp.ui.role;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Permission;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class RoleDetailActivity extends AppCompatActivity {
    private EditText etRoleName, etRoleDescription;
    private RecyclerView rvPermissions;
    private GenericAdapter<Permission> permissionsAdapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private int roleId = -1;
    private Role currentRole;
    private List<Permission> allPermissions;
    private List<Permission> selectedPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupPermissionsRecyclerView();
        loadAllPermissions();

        roleId = getIntent().getIntExtra("role_id", -1);
        if (roleId != -1) {
            setTitle("تعديل الدور");
            loadRole();
        } else {
            setTitle("إضافة دور جديد");
            selectedPermissions = new ArrayList<>();
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
                (permission, view) -> {
                    TextView tvPermissionName = view.findViewById(R.id.tv_permission_name);
                    tvPermissionName.setText(permission.getName());
                    // Check if this permission is selected for the current role
                    if (selectedPermissions != null && selectedPermissions.contains(permission)) {
                        view.setBackgroundColor(getResources().getColor(R.color.light_blue)); // Highlight selected
                    } else {
                        view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                },
                permission -> {
                    // Toggle selection
                    if (selectedPermissions.contains(permission)) {
                        selectedPermissions.remove(permission);
                    } else {
                        selectedPermissions.add(permission);
                    }
                    permissionsAdapter.notifyDataSetChanged();
                }
        );
        rvPermissions.setAdapter(permissionsAdapter);
    }

    private void loadAllPermissions() {
        database.permissionDao().getAllPermissions()
                .observe(this, permissions -> {
                    if (permissions != null) {
                        allPermissions = permissions;
                        permissionsAdapter.updateData(allPermissions);
                        if (roleId != -1 && currentRole != null) {
                            // After loading all permissions, update selection based on current role
                            updateSelectedPermissions();
                        }
                    }
                });
    }

    private void loadRole() {
        database.roleDao().getRoleById(roleId, sessionManager.getCurrentCompanyId())
                .observe(this, role -> {
                    if (role != null) {
                        currentRole = role;
                        etRoleName.setText(currentRole.getName());
                        etRoleDescription.setText(currentRole.getDescription());
                        // Load associated permissions
                        database.permissionDao().getPermissionsForRole(roleId)
                                .observe(this, permissions -> {
                                    selectedPermissions = new ArrayList<>(permissions);
                                    updateSelectedPermissions();
                                });
                    }
                });
    }

    private void updateSelectedPermissions() {
        if (allPermissions != null && selectedPermissions != null) {
            permissionsAdapter.updateData(allPermissions);
            permissionsAdapter.notifyDataSetChanged(); // Refresh UI to show selections
        }
    }

    private void saveRole() {
        String name = etRoleName.getText().toString().trim();
        String description = etRoleDescription.getText().toString().trim();

        if (name.isEmpty()) {
            etRoleName.setError("اسم الدور مطلوب");
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (roleId == -1) {
                // Create new role
                Role newRole = new Role(
                        sessionManager.getCurrentCompanyId(),
                        name,
                        description
                );
                long newRoleId = database.roleDao().insert(newRole);
                // Insert role-permission associations
                for (Permission p : selectedPermissions) {
                    database.roleDao().insertRolePermission(newRoleId, p.getId());
                }
            } else {
                // Update existing role
                currentRole.setName(name);
                currentRole.setDescription(description);
                database.roleDao().update(currentRole);
                // Update role-permission associations
                database.roleDao().deleteRolePermissions(roleId); // Clear existing
                for (Permission p : selectedPermissions) {
                    database.roleDao().insertRolePermission(roleId, p.getId());
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                saveRole();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
