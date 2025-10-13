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
    private EditText etCompanyName;
    private EditText etCompanyAddress;
    private EditText etCompanyPhone;
    private EditText etCompanyEmail;
    private Button btnSaveSettings;

    private AppDatabase database;
    private SessionManager sessionManager;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_settings);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        etCompanyName = findViewById(R.id.etCompanyName);
        etCompanyAddress = findViewById(R.id.etCompanyAddress);
        etCompanyPhone = findViewById(R.id.etCompanyPhone);
        etCompanyEmail = findViewById(R.id.etCompanyEmail);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        loadCompanySettings();

        btnSaveSettings.setOnClickListener(v -> saveSettings());
    }

    private void loadCompanySettings() {
        if (companyId != null) {
            database.companyDao().getCompanyById(companyId).observe(this, company -> {
                if (company != null) {
                    etCompanyName.setText(company.getCompanyName());
                    etCompanyAddress.setText(company.getAddress());
                    etCompanyPhone.setText(company.getPhone());
                    etCompanyEmail.setText(company.getEmail());
                }
            });
        }
    }

    private void saveSettings() {
        String name = etCompanyName.getText().toString().trim();
        String address = etCompanyAddress.getText().toString().trim();
        String phone = etCompanyPhone.getText().toString().trim();
        String email = etCompanyEmail.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال اسم الشركة", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            database.companyDao().getCompanyById(companyId).observeForever(company -> {
                if (company != null) {
                    company.setCompanyName(name);
                    company.setAddress(address);
                    company.setPhone(phone);
                    company.setEmail(email);
                    database.companyDao().update(company);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم حفظ الإعدادات بنجاح", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }
}
