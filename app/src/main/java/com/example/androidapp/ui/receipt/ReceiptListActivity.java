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
    private AppDatabase db;
    private SessionManager sm;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_list);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fab          = findViewById(R.id.fab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(v -> {
            Intent i = new Intent(ReceiptListActivity.this, ReceiptDetailActivity.class);
            startActivity(i);
        });

        loadReceipts();
    }

    private void loadReceipts() {
        String companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) return;

        adapter = new GenericAdapter<>(new ArrayList<>(), item -> {
            Intent i = new Intent(ReceiptListActivity.this, ReceiptDetailActivity.class);
            i.putExtra("receipt_id", item.getId());
            startActivity(i);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.receipt_list_row;
            }

            @Override
            protected void bindView(View itemView, Receipt r) {
                TextView tvNum  = itemView.findViewById(R.id.tvReceiptNumber);
                TextView tvAmt  = itemView.findViewById(R.id.tvReceiptAmount);
                TextView tvDate = itemView.findViewById(R.id.tvReceiptDate);

                tvNum .setText(r.getReceiptNumber());
                tvAmt .setText(String.valueOf(r.getTotalAmount()));
                tvDate.setText(r.getReceiptDate());
            }
        };

        recyclerView.setAdapter(adapter);
        db.receiptDao().getAllReceipts(companyId).observe(this, list -> adapter.updateData(list));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReceipts();
    }
}
