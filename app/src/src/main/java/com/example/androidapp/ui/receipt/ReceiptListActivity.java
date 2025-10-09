package com.example.androidapp.ui.receipt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.ReceiptDao;
import com.example.androidapp.data.entities.Receipt;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ReceiptListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReceiptAdapter adapter;
    private ReceiptDao receiptDao;
    private String companyId = "default_company"; // Replace with actual company ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_list);

        recyclerView = findViewById(R.id.recyclerViewReceipts);
        FloatingActionButton fabAddReceipt = findViewById(R.id.fabAddReceipt);

        AppDatabase db = AppDatabase.getDatabase(this);
        receiptDao = db.receiptDao();

        setupRecyclerView();
        loadReceipts();

        fabAddReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceiptListActivity.this, ReceiptDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReceiptAdapter(this, receipt -> {
            Intent intent = new Intent(ReceiptListActivity.this, ReceiptDetailActivity.class);
            intent.putExtra("receipt_id", receipt.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadReceipts() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Receipt> receipts = receiptDao.getAllReceipts(companyId);
            runOnUiThread(() -> {
                if (receipts != null && !receipts.isEmpty()) {
                    adapter.setReceipts(receipts);
                } else {
                    Toast.makeText(this, "لا توجد إيصالات", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReceipts(); // Refresh data when returning to this activity
    }
}
