package com.example.androidapp.ui.companysettings;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.CompanySettings;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;

public class CompanySettingsActivity extends AppCompatActivity {
    private EditText etCompanyName, etCompanyAddress, etCompanyPhone, etCompanyEmail;
    private Button btnSaveSettings;
    private AppDatabase database;
    private SessionManager sessionManager;
    private CompanySettings currentSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_settings);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        loadSettings();

        btnSaveSettings.setOnClickListener(v -> saveSettings());
    }

    private void initViews() {
        etCompanyName = findViewById(R.id.etCompanyName);
        etCompanyAddress = findViewById(R.id.etCompanyAddress);
        etCompanyPhone = findViewById(R.id.etCompanyPhone);
        etCompanyEmail = findViewById(R.id.etCompanyEmail);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
    }

    private void loadSettings() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            CompanySettings settings = database.companySettingsDao().getCompanySettings();
            runOnUiThread(() -> {
                if (settings != null) {
                    currentSettings = settings;
                    etCompanyName.setText(settings.getCompanyName());
                    etCompanyAddress.setText(settings.getCompanyAddress());
                    etCompanyPhone.setText(settings.getCompanyPhone());
                    etCompanyEmail.setText(settings.getCompanyEmail());
                }
            });
        });
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

        AppDatabase.databaseWriteExecutor.execute(() -> {
            String companyId = sessionManager.getCurrentCompanyId();
            
            if (currentSettings == null) {
                CompanySettings settings = new CompanySettings(
                    UUID.randomUUID().toString(),
                    companyId,
                    name,
                    address,
                    phone,
                    email
                );
                database.companySettingsDao().insert(settings);
            } else {
                currentSettings.setCompanyName(name);
                currentSettings.setCompanyAddress(address);
                currentSettings.setCompanyPhone(phone);
                currentSettings.setCompanyEmail(email);
                database.companySettingsDao().update(currentSettings);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ الإعدادات بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
