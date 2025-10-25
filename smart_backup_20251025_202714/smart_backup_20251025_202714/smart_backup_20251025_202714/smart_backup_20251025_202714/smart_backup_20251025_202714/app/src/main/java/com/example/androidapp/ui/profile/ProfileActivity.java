package com.example.androidapp.ui.profile;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileActivity extends AppCompatActivity {
    
    private MaterialToolbar toolbar;
    private TextInputLayout nameLayout, emailLayout, phoneLayout, companyLayout;
    private TextInputEditText nameEdit, emailEdit, phoneEdit, companyEdit;
    private MaterialButton saveButton;
    private SessionManager sessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        sessionManager = SessionManager.getInstance(this);
        
        initViews();
        setupToolbar();
        loadUserData();
        setupSaveButton();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        companyLayout = findViewById(R.id.companyLayout);
        nameEdit = findViewById(R.id.nameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        companyEdit = findViewById(R.id.companyEdit);
        saveButton = findViewById(R.id.saveButton);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("الملف الشخصي");
        }
    }
    
    private void loadUserData() {
        // تحميل بيانات المستخدم من SessionManager أو Database
        nameEdit.setText(sessionManager.getUserName());
        emailEdit.setText(sessionManager.getUserEmail());
        companyEdit.setText(sessionManager.getCompanyName());
        // يمكن إضافة المزيد من البيانات
    }
    
    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            String name = nameEdit.getText().toString().trim();
            String email = emailEdit.getText().toString().trim();
            String phone = phoneEdit.getText().toString().trim();
            String company = companyEdit.getText().toString().trim();
            
            // التحقق من صحة البيانات
            if (validateInput(name, email, phone, company)) {
                // حفظ البيانات في Database أو تحديث SessionManager
                saveUserProfile(name, email, phone, company);
            }
        });
    }
    
    private boolean validateInput(String name, String email, String phone, String company) {
        boolean isValid = true;
        
        if (name.isEmpty()) {
            nameLayout.setError("الاسم مطلوب");
            isValid = false;
        } else {
            nameLayout.setError(null);
        }
        
        if (email.isEmpty()) {
            emailLayout.setError("البريد الإلكتروني مطلوب");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }
        
        return isValid;
    }
    
    private void saveUserProfile(String name, String email, String phone, String company) {
        // حفظ البيانات في Database
        // يمكن إضافة منطق حفظ البيانات هنا
        
        // إظهار رسالة نجاح
        finish(); // العودة للشاشة السابقة
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}