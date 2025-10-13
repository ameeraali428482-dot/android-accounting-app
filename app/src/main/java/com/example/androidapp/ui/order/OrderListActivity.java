package com.example.androidapp.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Order> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadOrders();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        setTitle("الطلبات");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GenericAdapter<Order>(new ArrayList<>(), order -> {
            Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
            intent.putExtra("order_id", order.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.order_list_row;
            }

            @Override
            protected void bindView(View itemView, Order order) {
                TextView tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
                TextView tvOrderDateDisplay = itemView.findViewById(R.id.tvOrderDateDisplay);
                TextView tvTotalAmountDisplay = itemView.findViewById(R.id.tvTotalAmountDisplay);
                TextView tvStatusDisplay = itemView.findViewById(R.id.tvStatusDisplay);

                if (tvOrderNumber != null) tvOrderNumber.setText("طلب #" + order.getOrderNumber());
                if (tvOrderDateDisplay != null) tvOrderDateDisplay.setText(order.getOrderDate());
                if (tvTotalAmountDisplay != null) tvTotalAmountDisplay.setText(currencyFormat.format(order.getTotalAmount()));
                if (tvStatusDisplay != null) tvStatusDisplay.setText(order.getStatus());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void loadOrders() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId != null) {
            database.orderDao().getAllOrders(companyId).observe(this, orders -> {
                if (orders != null) {
                    adapter.updateData(orders);
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
        loadOrders();
    }
}
