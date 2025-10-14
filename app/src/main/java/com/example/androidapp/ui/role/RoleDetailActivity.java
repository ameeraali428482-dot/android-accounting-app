package com.example.androidapp.ui.role;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;
import java.util.concurrent.Executors;

public class RoleDetailActivity extends AppCompatActivity {
    private EditText etName, etDesc;
    private Button btnSave, btnDel;
    private AppDatabase db;
    private SessionManager sm;
    private String roleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_detail);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        etName  = findViewById(R.id.etRoleName);
        etDesc  = findViewById(R.id.etRoleDescription);
        btnSave = findViewById(R.id.btnSaveRole);
        btnDel  = findViewById(R.id.btnDeleteRole);

        roleId = getIntent().getStringExtra("role_id");
        if (roleId != null) load();

        btnSave.setOnClickListener(v -> save());
        btnDel .setOnClickListener(v -> delete());
    }

    private void load() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Role r = db.roleDao().getRoleByIdSync(roleId);
            if (r != null) {
                runOnUiThread(() -> {
                    etName.setText(r.getName());
                    etDesc.setText(r.getDescription());
                });
            }
        });
    }

    private void save() {
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (name.isEmpty()) {
            Toast.makeText(this, "أدخل اسم الدور", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            if (roleId == null) {
                Role r = new Role();
                r.setId(UUID.randomUUID().toString());
                r.setCompanyId(companyId);
                r.setName(name);
                r.setDescription(desc);
                db.roleDao().insert(r);
            } else {
                Role r = db.roleDao().getRoleByIdSync(roleId);
                if (r != null) {
                    r.setName(name);
                    r.setDescription(desc);
                    db.roleDao().update(r);
                }
            }
            runOnUiThread(() -> { Toast.makeText(this, "تم الحفظ", Toast.LENGTH_SHORT).show(); finish(); });
        });
    }

    private void delete() {
        if (roleId != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                Role r = db.roleDao().getRoleByIdSync(roleId);
                if (r != null) db.roleDao().delete(r);
                runOnUiThread(() -> { Toast.makeText(this, "تم الحذف", Toast.LENGTH_SHORT).show(); finish(); });
            });
        }
    }
}
