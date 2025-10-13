package com.example.androidapp.ui.receipt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Receipt;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ReceiptListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Receipt> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private FloatingActionButton fabAddReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fabAddReceipt = findViewById(R.id.fabAddReceipt);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddReceipt.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiptListActivity.this, ReceiptDetailActivity.class);
            startActivity(intent);
        });

        loadReceipts();
    }

    private void loadReceipts() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<Receipt>() {
            @Override
            public void onItemClick(Receipt item) {
                Intent intent = new Intent(ReceiptListActivity.this, ReceiptDetailActivity.class);
                intent.putExtra("receipt_id", item.getId());
                startActivity(intent);
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.receipt_list_row;
            }

            @Override
            protected void bindView(View itemView, Receipt receipt) {
                TextView tvReceiptNumber = itemView.findViewById(R.id.tvReceiptNumber);
                TextView tvReceiptAmount = itemView.findViewById(R.id.tvReceiptAmount);
                TextView tvReceiptDate = itemView.findViewById(R.id.tvReceiptDate);

                tvReceiptNumber.setText(receipt.getReceiptNumber());
                tvReceiptAmount.setText(String.valueOf(receipt.getTotalAmount()));
                tvReceiptDate.setText(receipt.getReceiptDate());
            }
        };

        recyclerView.setAdapter(adapter);

        database.receiptDao().getAllReceipts(companyId).observe(this, receipts -> {
            if (receipts != null) {
                adapter.updateData(receipts);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReceipts();
    }
}
