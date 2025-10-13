package com.example.androidapp.ui.pointtransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointtransaction_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadPointTransactions();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        setTitle("معاملات النقاط");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, PointTransactionDetailActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GenericAdapter<PointTransaction>(new ArrayList<>(), pointTransaction -> {
            Intent intent = new Intent(PointTransactionListActivity.this, PointTransactionDetailActivity.class);
            intent.putExtra("pointtransaction_id", pointTransaction.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.pointtransaction_list_row;
            }

            @Override
            protected void bindView(View itemView, PointTransaction pointTransaction) {
                TextView tvType = itemView.findViewById(R.id.tvType);
                TextView tvPointsValue = itemView.findViewById(R.id.tvPointsValue);
                TextView tvDateValue = itemView.findViewById(R.id.tvDateValue);

                if (tvType != null) {
                    tvType.setText(pointTransaction.getDescription() != null ? pointTransaction.getDescription() : pointTransaction.getType());
                }
                if (tvPointsValue != null) {
                    tvPointsValue.setText(pointTransaction.getPoints() + " نقطة");
                }
                if (tvDateValue != null && pointTransaction.getDate() != null) {
                    tvDateValue.setText(dateFormat.format(pointTransaction.getDate()));
                }
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void loadPointTransactions() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId != null) {
            database.pointTransactionDao().getAllPointTransactions(companyId).observe(this, pointTransactions -> {
                if (pointTransactions != null) {
                    adapter.updateData(pointTransactions);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPointTransactions();
    }
}
