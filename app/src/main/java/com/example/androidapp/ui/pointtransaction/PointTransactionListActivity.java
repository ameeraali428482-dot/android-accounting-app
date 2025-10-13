package com.example.androidapp.ui.pointtransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.PointTransaction;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PointTransactionListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<PointTransaction> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private FloatingActionButton fabAddPointTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_transaction_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fabAddPointTransaction = findViewById(R.id.fabAddPointTransaction);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddPointTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(PointTransactionListActivity.this, PointTransactionDetailActivity.class);
            startActivity(intent);
        });

        loadPointTransactions();
    }

    private void loadPointTransactions() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<PointTransaction>() {
            @Override
            public void onItemClick(PointTransaction item) {
                Intent intent = new Intent(PointTransactionListActivity.this, PointTransactionDetailActivity.class);
                intent.putExtra("pointtransaction_id", item.getId());
                startActivity(intent);
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.point_transaction_list_row;
            }

            @Override
            protected void bindView(View itemView, PointTransaction pointTransaction) {
                TextView tvDescription = itemView.findViewById(R.id.tvDescription);
                TextView tvPoints = itemView.findViewById(R.id.tvPoints);
                TextView tvDate = itemView.findViewById(R.id.tvDate);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                tvDescription.setText(pointTransaction.getDescription());
                tvPoints.setText(String.valueOf(pointTransaction.getPoints()));
                tvDate.setText(dateFormat.format(pointTransaction.getTransactionDate()));
            }
        };

        recyclerView.setAdapter(adapter);

        database.pointTransactionDao().getAllPointTransactions(companyId).observe(this, pointTransactions -> {
            if (pointTransactions != null) {
                adapter.updateData(pointTransactions);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPointTransactions();
    }
}
