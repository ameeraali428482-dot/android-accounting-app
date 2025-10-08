package com.example.androidapp.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.models.Payment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PaymentListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PaymentAdapter adapter;
    private PaymentDao paymentDao;
    private String companyId = "default_company"; // Replace with actual company ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);

        recyclerView = findViewById(R.id.recyclerViewPayments);
        FloatingActionButton fabAddPayment = findViewById(R.id.fabAddPayment);

        AppDatabase db = AppDatabase.getDatabase(this);
        paymentDao = db.paymentDao();

        setupRecyclerView();
        loadPayments();

        fabAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentListActivity.this, PaymentDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentAdapter(this, payment -> {
            Intent intent = new Intent(PaymentListActivity.this, PaymentDetailActivity.class);
            intent.putExtra("payment_id", payment.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadPayments() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Payment> payments = paymentDao.getAllPayments(companyId);
            runOnUiThread(() -> {
                if (payments != null && !payments.isEmpty()) {
                    adapter.setPayments(payments);
                } else {
                    Toast.makeText(this, "لا توجد مدفوعات", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPayments(); // Refresh data when returning to this activity
    }
}
