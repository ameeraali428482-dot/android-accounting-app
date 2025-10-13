package com.example.androidapp.ui.role;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;

public class RoleDetailActivity extends AppCompatActivity {
    private EditText etrolename, etroledescription;
    private RecyclerView rvpermissions;
    private Button btnSaveRole, btnDeleteRole;
    private AppDatabase database;
    private SessionManager sessionManager;
    private String roleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        // IDs من activity_role_detail.xml
        etrolename = findViewById(R.id.etrolename);
        etroledescription = findViewById(R.id.etroledescription);
        rvpermissions = findViewById(R.id.rvpermissions);
        btnSaveRole = findViewById(R.id.btnSaveRole);
        btnDeleteRole = findViewById(R.id.btnDeleteRole);

        roleId = getIntent().getStringExtra("role_id");
        if (roleId != null) {
            loadRole();
        }

        btnSaveRole.setOnClickListener(v -> saveRole());
        btnDeleteRole.setOnClickListener(v -> deleteRole());
    }

    private void loadRole() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Role role = database.roleDao().getRoleByIdSync(roleId);
            runOnUiThread(() -> {
                if (role != null) {
                    etrolename.setText(role.getName());
                    etroledescription.setText(role.getDescription());
                }
            });
        });
    }

    private void saveRole() {
        String name = etrolename.getText().toString().trim();
        String description = etroledescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال اسم الدور", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            Role role = new Role(
                roleId != null ? roleId : UUID.randomUUID().toString(),
                sessionManager.getCurrentCompanyId(),
                name,
                description,
                true
            );
            
            if (roleId == null) {
                database.roleDao().insert(role);
            } else {
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
                Role role = database.roleDao().getRoleByIdSync(roleId);
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
