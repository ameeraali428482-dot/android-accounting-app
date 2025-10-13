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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, PaymentDetailActivity.class);
                startActivity(intent);
            });
        }

        setupRecyclerView();
        loadPayments();
    }

    private void setupRecyclerView() {
        adapter = new GenericAdapter<Payment>(new ArrayList<>(), payment -> {
            Intent intent = new Intent(PaymentListActivity.this, PaymentDetailActivity.class);
            intent.putExtra("payment_id", payment.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.payment_list_row;
            }

            @Override
            protected void bindView(View itemView, Payment payment) {
                TextView tvPaymentDate = itemView.findViewById(R.id.tvPaymentDate);
                TextView tvPaymentAmount = itemView.findViewById(R.id.tvPaymentAmount);
                TextView tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);

                if (tvPaymentDate != null) tvPaymentDate.setText(payment.getPaymentDate());
                if (tvPaymentAmount != null) tvPaymentAmount.setText(String.valueOf(payment.getAmount()));
                if (tvPaymentMethod != null) tvPaymentMethod.setText(payment.getPaymentMethod());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void loadPayments() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId != null) {
            database.paymentDao().getAllPaymentsLive(companyId).observe(this, payments -> {
                if (payments != null) {
                    adapter.updateData(payments);
                }
            });
        }
    }
}
