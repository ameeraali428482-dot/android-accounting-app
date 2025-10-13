package com.example.androidapp.ui.order;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

public class OrderListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Order> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private FloatingActionButton fabAddOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fabAddOrder = findViewById(R.id.fabAddOrder);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddOrder.setOnClickListener(v -> {
            Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
            startActivity(intent);
        });

        loadOrders();
    }

    private void loadOrders() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<Order>() {
            @Override
            public void onItemClick(Order item) {
                Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
                intent.putExtra("order_id", item.getId());
                startActivity(intent);
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.order_list_row;
            }

            @Override
            protected void bindView(View itemView, Order order) {
                TextView tvOrderId = itemView.findViewById(R.id.tvOrderId);
                TextView tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
                TextView tvOrderTotalAmount = itemView.findViewById(R.id.tvOrderTotalAmount);
                TextView tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);

                tvOrderId.setText(order.getId());
                tvOrderDate.setText(order.getOrderDate());
                tvOrderTotalAmount.setText(String.valueOf(order.getTotalAmount()));
                tvOrderStatus.setText(order.getOrderStatus());
            }
        };

        recyclerView.setAdapter(adapter);

        database.orderDao().getAllOrders(companyId).observe(this, orders -> {
            if (orders != null) {
                adapter.updateData(orders);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}
