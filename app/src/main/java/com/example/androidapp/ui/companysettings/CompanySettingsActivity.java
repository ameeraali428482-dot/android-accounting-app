package com.example.androidapp.ui.companysettings;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Company;
import com.example.androidapp.utils.SessionManager;
import java.util.concurrent.Executors;

public class CompanySettingsActivity extends AppCompatActivity {
    private EditText etName, etAddress, etPhone, etEmail;
    private Button btnSave;
    private AppDatabase db;
    private SessionManager sm;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_settings);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        etName    = findViewById(R.id.etCompanyName);
        etAddress = findViewById(R.id.etCompanyAddress);
        etPhone   = findViewById(R.id.etCompanyPhone);
        etEmail   = findViewById(R.id.etCompanyEmail);
        btnSave   = findViewById(R.id.btnSaveSettings);

        companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        loadCompany();

        btnSave.setOnClickListener(v -> save());
    }

    private void loadCompany() {
        if (companyId == null) return;
        db.companyDao().getCompanyById(companyId).observe(this, c -> {
            if (c != null) {
                etName   .setText(c.getCompanyName());
                etAddress.setText(c.getAddress());
                etPhone  .setText(c.getPhone());
                etEmail  .setText(c.getEmail());
            }
        });
    }

    private void save() {
        String name    = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone   = etPhone.getText().toString().trim();
        String email   = etEmail.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "أدخل اسم الشركة", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            Company c = db.companyDao().getCompanyByIdSync(companyId);
            if (c != null) {
                c.setCompanyName(name);
                c.setAddress(address);
                c.setPhone(phone);
                c.setEmail(email);
                db.companyDao().update(c);
            }
            runOnUiThread(() -> Toast.makeText(this, "تم الحفظ", Toast.LENGTH_SHORT).show());
        });
    }
}
