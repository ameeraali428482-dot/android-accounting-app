package com.example.androidapp.ui.auth;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // تنفيذ مبسط لتسجيل الدخول
        Toast.makeText(this, "شاشة تسجيل الدخول - جاري التطوير", Toast.LENGTH_SHORT).show();
        finish();
    }
}
