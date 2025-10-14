package com.example.androidapp.ui.companysettings;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.CompanyDao;
import com.example.androidapp.data.entities.Company;
import com.example.androidapp.utils.SessionManager;

public class CompanySettingsActivity extends AppCompatActivity {
    private EditText etName, etAddress, etPhone, etEmail;
    private Button btnSave;
    private CompanyDao companyDao;
    private SessionManager sm;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_settings);

        companyDao = AppDatabase.getInstance(this).companyDao();
        sm = new SessionManager(this);

        etName    = findViewById(R.id.etCompanyName);
        etAddress = findViewById(R.id.etCompanyAddress);
        etPhone   = findViewById(R.id.etCompanyPhone);
        etEmail   = findViewById(R.id.etCompanyEmail);
        btnSave   = findViewById(R.id.btnSaveSettings);

        companyId = sm.getCurrentCompanyId();

        loadCompany();

        btnSave.setOnClickListener(v -> save());
    }

    private void loadCompany() {
        if (companyId == null) return;
        companyDao.getCompanyById(companyId).observe(this, c -> {
            if (c != null) {
                etName.setText(c.getName());
                etAddress.setText(c.getAddress());
                etPhone.setText(c.getPhone());
                // etEmail.setText(c.getEmail()); // Company entity does not have email
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

        AppDatabase.databaseWriteExecutor.execute(() -> {
            Company c = companyDao.getCompanyByIdSync(companyId);
            if (c != null) {
                c.setName(name);
                c.setAddress(address);
                c.setPhone(phone);
                // c.setEmail(email); // Company entity does not have email
                companyDao.update(c);
            }
            runOnUiThread(() -> Toast.makeText(this, "تم الحفظ", Toast.LENGTH_SHORT).show());
        });
    }
}
