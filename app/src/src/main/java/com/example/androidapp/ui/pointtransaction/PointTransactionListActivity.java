package com.example.androidapp.ui.pointtransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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
import java.util.List;
import java.util.Locale;

public class PointTransactionListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<PointTransaction> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_transaction_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadPointTransactions();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        FloatingActionButton fab = findViewById(R.id.fab);

        setTitle("سجل النقاط");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, PointTransactionDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<>(
                new ArrayList<>(),
                R.layout.point_transaction_list_row,
                (pointTransaction, view) -> {
                    // Bind data to views
                    TextView tvDescription = view.findViewById(R.id.tv_description);
                    TextView tvPoints = view.findViewById(R.id.tv_points);
                    TextView tvDate = view.findViewById(R.id.tv_date);

                    tvDescription.setText(pointTransaction.getDescription());
                    tvPoints.setText(String.format(Locale.getDefault(), "%+d نقطة", pointTransaction.getPoints()));
                    tvDate.setText(dateFormat.format(pointTransaction.getTransactionDate()));

                    if (pointTransaction.getType().equals("EARN")) {
                        tvPoints.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    } else {
                        tvPoints.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }
                },
                pointTransaction -> {
                    Intent intent = new Intent(this, PointTransactionDetailActivity.class);
                    intent.putExtra("point_transaction_id", pointTransaction.getId());
                    startActivity(intent);
                }
        );
        
        recyclerView.setAdapter(adapter);
    }

    private void loadPointTransactions() {
        database.pointTransactionDao().getAllPointTransactions(sessionManager.getCurrentCompanyId())
                .observe(this, pointTransactions -> {
                    if (pointTransactions != null) {
                        adapter.updateData(pointTransactions);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                loadPointTransactions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPointTransactions();
    }
}
