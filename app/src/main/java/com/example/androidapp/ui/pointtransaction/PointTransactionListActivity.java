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
    private AppDatabase db;
    private SessionManager sm;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_transaction_list);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fab          = findViewById(R.id.fab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(v -> {
            Intent i = new Intent(PointTransactionListActivity.this, PointTransactionDetailActivity.class);
            startActivity(i);
        });

        loadTrx();
    }

    private void loadTrx() {
        String companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) return;

        adapter = new GenericAdapter<>(new ArrayList<>(), item -> {
            Intent i = new Intent(PointTransactionListActivity.this, PointTransactionDetailActivity.class);
            i.putExtra("pointtransaction_id", item.getId());
            startActivity(i);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.point_transaction_list_row;
            }

            @Override
            protected void bindView(View itemView, PointTransaction p) {
                TextView tvDesc = itemView.findViewById(R.id.tvDescription);
                TextView tvPts  = itemView.findViewById(R.id.tvPoints);
                TextView tvDate = itemView.findViewById(R.id.tvDate);

                tvDesc.setText(p.getDescription());
                tvPts .setText(String.valueOf(p.getPoints()));
                tvDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(p.getTransactionDate()));
            }
        };

        recyclerView.setAdapter(adapter);
        db.pointTransactionDao().getAllPointTransactions(companyId).observe(this, list -> adapter.updateData(list));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrx();
    }
}
