package com.example.androidapp.ui.order;

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
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Order> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            startActivity(intent);
        });

        setupRecyclerView();
        loadOrders();
    }

    private void setupRecyclerView() {
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
                // IDs من order_list_row.xml
                TextView tvOrderId = itemView.findViewById(R.id.tvOrderId);
                TextView tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
                TextView tvOrderTotalAmount = itemView.findViewById(R.id.tvOrderTotalAmount);
                TextView tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);

                if (tvOrderId != null) tvOrderId.setText("#" + order.getId());
                if (tvOrderDate != null && order.getOrderDate() != null) {
                    tvOrderDate.setText(dateFormat.format(order.getOrderDate()));
                }
                if (tvOrderTotalAmount != null) tvOrderTotalAmount.setText(String.valueOf(order.getTotalAmount()));
                if (tvOrderStatus != null) tvOrderStatus.setText(order.getStatus());
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
}
