package com.example.androidapp.ui.order;

import java.util.Date;
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
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<Order>(new ArrayList<>(), null) {
            @Override
            protected int getLayoutResId() {
                return R.layout.order_list_row;
            }

            @Override
            protected void bindView(View itemView, Order order) {
                TextView tvOrderId = itemView.findViewById(R.id.tvOrderId);
                TextView tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
                TextView tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
                TextView tvStatus = itemView.findViewById(R.id.tvStatus);
                TextView tvNotes = itemView.findViewById(R.id.tvNotes);

                tvOrderId.setText(order.getId());
                tvOrderDate.setText(dateFormat.format(order.getOrderDate()));
                tvTotalAmount.setText(currencyFormat.format(order.getTotalAmount()));
                tvStatus.setText(order.getStatus());

                if (order.getNotes() != null && !order.getNotes().isEmpty()) {
                    tvNotes.setText(order.getNotes());
                } else {
                    tvNotes.setText("");
                }

                int statusBackground;
                if (order.getStatus() != null) {
                    switch (order.getStatus()) {
                        case "Completed":
                            statusBackground = R.drawable.status_active_background;
                            break;
                        case "Processing":
                            statusBackground = R.drawable.status_draft_background;
                            break;
                        case "Cancelled":
                            statusBackground = R.drawable.status_inactive_background;
                            break;
                        default:
                            statusBackground = R.drawable.status_pending_background;
                            break;
                    }
                    tvStatus.setBackgroundResource(statusBackground);
                }
            }
        };
        
        adapter.setOnItemClickListener(order -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("order_id", order.getId());
            startActivity(intent);
        });
        
        recyclerView.setAdapter(adapter);
    }

    private void loadOrders() {
        database.orderDao().getAllOrders(sessionManager.getCurrentCompanyId()).observe(this, orders -> {
            if (orders != null) {
                adapter.updateData(orders);
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
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            loadOrders();
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
