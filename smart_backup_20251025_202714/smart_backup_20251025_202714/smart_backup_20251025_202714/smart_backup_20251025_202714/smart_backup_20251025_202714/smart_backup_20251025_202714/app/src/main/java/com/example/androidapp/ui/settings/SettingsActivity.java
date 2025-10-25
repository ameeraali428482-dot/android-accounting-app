package com.example.androidapp.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.androidapp.R;
import com.example.androidapp.ui.auth.LoginActivity;
import com.example.androidapp.ui.profile.ProfileActivity;
import com.example.androidapp.ui.about.AboutActivity;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {
    
    private MaterialToolbar toolbar;
    private MaterialSwitch notificationsSwitch;
    private MaterialSwitch darkModeSwitch;
    private MaterialSwitch autoBackupSwitch;
    private MaterialButton logoutButton;
    private MaterialButton aboutButton;
    private MaterialButton profileButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initViews();
        setupToolbar();
        setupSwitches();
        setupButtons();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        autoBackupSwitch = findViewById(R.id.autoBackupSwitch);
        logoutButton = findViewById(R.id.logoutButton);
        aboutButton = findViewById(R.id.aboutButton);
        profileButton = findViewById(R.id.profileButton);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("الإعدادات");
        }
    }
    
    private void setupSwitches() {
        // يمكن إضافة منطق حفظ واسترجاع إعدادات المستخدم هنا
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // حفظ إعدادات الإشعارات
        });
        
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // تطبيق الوضع المظلم
        });
        
        autoBackupSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // إعداد النسخ الاحتياطي التلقائي
        });
    }
    
    private void setupButtons() {
        profileButton.setOnClickListener(v -> {
            // التنقل إلى صفحة الملف الشخصي
            startActivity(new Intent(this, ProfileActivity.class));
        });
        
        aboutButton.setOnClickListener(v -> {
            // التنقل إلى صفحة حول التطبيق
            startActivity(new Intent(this, AboutActivity.class));
        });
        
        logoutButton.setOnClickListener(v -> {
            SessionManager sessionManager = SessionManager.getInstance(this);
            sessionManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
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