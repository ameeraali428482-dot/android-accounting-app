package com.example.androidapp.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class AboutActivity extends AppCompatActivity {
    
    private MaterialToolbar toolbar;
    private TextView appVersionText, appDescriptionText;
    private MaterialButton contactButton, websiteButton, privacyButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        initViews();
        setupToolbar();
        setupContent();
        setupButtons();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        appVersionText = findViewById(R.id.appVersionText);
        appDescriptionText = findViewById(R.id.appDescriptionText);
        contactButton = findViewById(R.id.contactButton);
        websiteButton = findViewById(R.id.websiteButton);
        privacyButton = findViewById(R.id.privacyButton);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("حول التطبيق");
        }
    }
    
    private void setupContent() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            appVersionText.setText("الإصدار " + versionName);
        } catch (Exception e) {
            appVersionText.setText("الإصدار 1.0");
        }
        
        appDescriptionText.setText(
            "تطبيق ذكي للمحاسبة وإدارة الأعمال يوفر حلولاً متكاملة لإدارة المبيعات، المخزون، العملاء، والتقارير المالية. " +
            "مصمم خصيصاً للشركات الصغيرة والمتوسطة في المنطقة العربية."
        );
    }
    
    private void setupButtons() {
        contactButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@smartaccounting.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "استفسار حول تطبيق المحاسبة الذكي");
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            }
        });
        
        websiteButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://smartaccounting.com"));
            if (browserIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(browserIntent);
            }
        });
        
        privacyButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://smartaccounting.com/privacy"));
            if (browserIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(browserIntent);
            }
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