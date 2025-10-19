package com.example.androidapp.ui.barcode;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BarcodeScannerActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // تنفيذ بسيط للماسح الضوئي - يمكن إضافة ZXing لاحقاً
        Toast.makeText(this, "ميزة مسح الباركود قيد التطوير", Toast.LENGTH_SHORT).show();
        finish();
    }
}
