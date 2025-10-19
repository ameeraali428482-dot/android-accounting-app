package com.example.androidapp.ui.main;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btnAccounts = findViewById(R.id.btnAccounts);
        Button btnTransactions = findViewById(R.id.btnTransactions);
        Button btnInvoices = findViewById(R.id.btnInvoices);
        Button btnReports = findViewById(R.id.btnReports);
        
        btnAccounts.setOnClickListener(v -> {
            // الانتقال إلى شاشة الحسابات
        });
        
        btnTransactions.setOnClickListener(v -> {
            // الانتقال إلى شاشة المعاملات
        });
        
        btnInvoices.setOnClickListener(v -> {
            // الانتقال إلى شاشة الفواتير
        });
        
        btnReports.setOnClickListener(v -> {
            // الانتقال إلى شاشة التقارير
        });
    }
}
