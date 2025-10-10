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

    private EditText companyNameEditText, companyAddressEditText, companyPhoneEditText, companyEmailEditText;
    private Button saveButton;
    private SessionManager sessionManager;
    private String companyId;
    private String companySettingsId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_settings);

        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadCompanySettings();
    }

    private void initViews() {
        companyNameEditText = findViewById(R.id.company_name_edit_text);
        companyAddressEditText = findViewById(R.id.company_address_edit_text);
        companyPhoneEditText = findViewById(R.id.company_phone_edit_text);
        companyEmailEditText = findViewById(R.id.company_email_edit_text);
        saveButton = findViewById(R.id.save_company_settings_button);

        saveButton.setOnClickListener(v -> saveCompanySettings());
    }

    private void loadCompanySettings() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            CompanySettings settings = AppDatabase.getDatabase(this).companySettingsDao().getCompanySettingsByCompanyId(companyId);
            runOnUiThread(() -> {
                if (settings != null) {
                    populateSettingsFields(settings);
                    companySettingsId = settings.getId();
                }
            });
        });
    }

    private void populateSettingsFields(CompanySettings settings) {
        if (settings != null) {
            companyNameEditText.setText(settings.getCompanyName());
            companyAddressEditText.setText(settings.getCompanyAddress());
            companyPhoneEditText.setText(settings.getCompanyPhone());
            companyEmailEditText.setText(settings.getCompanyEmail());
        }
    }

    private void saveCompanySettings() {
        String name = companyNameEditText.getText().toString().trim();
        String address = companyAddressEditText.getText().toString().trim();
        String phone = companyPhoneEditText.getText().toString().trim();
        String email = companyEmailEditText.getText().toString().trim();

        if (name.isEmpty()) {
            companyNameEditText.setError("اسم الشركة مطلوب");
            return;
        }

        CompanySettings settings = new CompanySettings(
            companySettingsId != null ? companySettingsId : UUID.randomUUID().toString(),
            companyId,
            name,
            address,
            phone,
            email
        );

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (companySettingsId == null) {
                AppDatabase.getDatabase(this).companySettingsDao().insert(settings);
            } else {
                AppDatabase.getDatabase(this).companySettingsDao().update(settings);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ إعدادات الشركة بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
