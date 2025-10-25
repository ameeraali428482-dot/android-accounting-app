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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GenericAdapter<Order> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fab          = findViewById(R.id.fab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
            startActivity(intent);
        });

        loadOrders();
    }

    private void loadOrders() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) return;

        adapter = new GenericAdapter<>(new ArrayList<>(), item -> {
            Intent i = new Intent(OrderListActivity.this, OrderDetailActivity.class);
            i.putExtra("order_id", item.getId());
            startActivity(i);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.order_list_row;
            }

            @Override
            protected void bindView(View itemView, Order order) {
                TextView tvOrderId     = itemView.findViewById(R.id.tvOrderId);
                TextView tvOrderDate   = itemView.findViewById(R.id.tvOrderDate);
                TextView tvTotalAmount = itemView.findViewById(R.id.tvOrderTotalAmount);
                TextView tvStatus      = itemView.findViewById(R.id.tvOrderStatus);

                tvOrderId    .setText(order.getId());
                tvOrderDate  .setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(order.getOrderDate()));
                tvTotalAmount.setText(String.valueOf(order.getTotalAmount()));
                tvStatus     .setText(order.getStatus());
            }
        };

        recyclerView.setAdapter(adapter);
        database.orderDao().getAllOrders(companyId).observe(this, list -> adapter.updateData(list));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}
