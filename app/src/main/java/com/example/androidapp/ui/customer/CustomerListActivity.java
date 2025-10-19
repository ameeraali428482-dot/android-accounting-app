package com.example.androidapp.ui.customer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class CustomerListActivity extends AppCompatActivity {
    
    private EditText searchInput;
    private RecyclerView recyclerView;
    private Button btnAddCustomer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);
        
        searchInput = findViewById(R.id.searchInput);
        recyclerView = findViewById(R.id.recyclerView);
        btnAddCustomer = findViewById(R.id.btnAddCustomer);
        
        btnAddCustomer.setOnClickListener(v -> {
            // إضافة عميل جديد
        });
        
        setupRecyclerView();
    }
    
    private void setupRecyclerView() {
        // إعداد قائمة العملاء
    }
}
