package com.example.androidapp.ui.companysettings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.CompanySettingsDao;
import com.example.androidapp.data.entities.CompanySettings;
import com.example.androidapp.utils.SessionManager;

import java.util.UUID;

public class CompanySettingsActivity extends AppCompatActivity {

    private EditText companyNameEditText, companyAddressEditText, companyPhoneEditText, companyEmailEditText;
    private Button saveButton;
    private CompanySettingsDao companySettingsDao;
    private SessionManager sessionManager;
    private String companySettingsId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_settings);

        companyNameEditText = findViewById(R.id.company_name_edit_text);
        companyAddressEditText = findViewById(R.id.company_address_edit_text);
        companyPhoneEditText = findViewById(R.id.company_phone_edit_text);
        companyEmailEditText = findViewById(R.id.company_email_edit_text);
        saveButton = findViewById(R.id.save_company_settings_button);

        companySettingsDao = App.getDatabaseHelper().companySettingsDao();
        sessionManager = new SessionManager(this);

        loadCompanySettings();

        saveButton.setOnClickListener(v -> saveCompanySettings());
    }

    private void loadCompanySettings() {
        String companyId = sessionManager.getCompanyId();
        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        CompanySettings settings = companySettingsDao.getSettingsByCompanyId(companyId);
        if (settings != null) {
            companySettingsId = settings.getId();
            if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { companyNameEditText.setText(settings.getCompanyName()); } } } } } } }
            if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { companyAddressEditText.setText(settings.getCompanyAddress()); } } } } } } }
            if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { companyPhoneEditText.setText(settings.getCompanyPhone()); } } } } } } }
            if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { if (settings != null) { companyEmailEditText.setText(settings.getCompanyEmail()); } } } } } } }
        } else {
            // If no settings exist, initialize with empty fields
            companyNameEditText.setText("");
            companyAddressEditText.setText("");
            companyPhoneEditText.setText("");
            companyEmailEditText.setText("");
        }
    }

    private void saveCompanySettings() {
        String companyName = companyNameEditText.getText().toString().trim();
        String companyAddress = companyAddressEditText.getText().toString().trim();
        String companyPhone = companyPhoneEditText.getText().toString().trim();
        String companyEmail = companyEmailEditText.getText().toString().trim();
        String companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (companyName.isEmpty()) {
            Toast.makeText(this, "اسم الشركة لا يمكن أن يكون فارغًا.", Toast.LENGTH_SHORT).show();
            return;
        }

        CompanySettings settings;
        if (companySettingsId == null) {
            // New settings
            settings = new CompanySettings(UUID.randomUUID().toString(), companyId, companyName, companyAddress, companyPhone, companyEmail);
            companySettingsDao.insert(settings);
            Toast.makeText(this, "تم حفظ إعدادات الشركة بنجاح.", Toast.LENGTH_SHORT).show();
        } else {
            // Existing settings
            settings = new CompanySettings(companySettingsId, companyId, companyName, companyAddress, companyPhone, companyEmail);
            companySettingsDao.update(settings);
            Toast.makeText(this, "تم تحديث إعدادات الشركة بنجاح.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
