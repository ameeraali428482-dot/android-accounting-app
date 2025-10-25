package com.example.androidapp.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Payment;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PaymentListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Payment> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private FloatingActionButton fabAddPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fabAddPayment = findViewById(R.id.fabAddPayment);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddPayment.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentListActivity.this, PaymentDetailActivity.class);
            startActivity(intent);
        });

        loadPayments();
    }

    private void loadPayments() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<Payment>() {
            @Override
            public void onItemClick(Payment item) {
                Intent intent = new Intent(PaymentListActivity.this, PaymentDetailActivity.class);
                intent.putExtra("payment_id", item.getId());
                startActivity(intent);
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.payment_list_row;
            }

            @Override
            protected void bindView(View itemView, Payment payment) {
                TextView tvPaymentAmount = itemView.findViewById(R.id.tvPaymentAmount);
                TextView tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
                TextView tvPaymentDate = itemView.findViewById(R.id.tvPaymentDate);

                tvPaymentAmount.setText(String.valueOf(payment.getAmount()));
                tvPaymentMethod.setText(payment.getPaymentMethod());
                tvPaymentDate.setText(payment.getPaymentDate());
            }
        };

        recyclerView.setAdapter(adapter);

        database.paymentDao().getAllPayments(companyId).observe(this, payments -> {
            if (payments != null) {
                adapter.updateData(payments);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPayments();
    }
}
