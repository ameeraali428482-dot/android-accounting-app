package com.example.androidapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.ui.invoices.InvoiceListActivity;
import com.example.androidapp.ui.items.ItemListActivity;
import com.example.androidapp.ui.customers.CustomerListActivity;
import com.example.androidapp.ui.reports.ReportsActivity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    
    private TextView tvTotalInvoices;
    private TextView tvPendingInvoices;
    private TextView tvLowStock;
    private TextView tvTotalRevenue;
    private RecyclerView rvRecentActivities;
    
    private AppDatabase database;
    private ExecutorService executor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupDatabase();
        loadDashboardData();
    }
    
    private void initViews() {
        tvTotalInvoices = findViewById(R.id.tv_total_invoices);
        tvPendingInvoices = findViewById(R.id.tv_pending_invoices);
        tvLowStock = findViewById(R.id.tv_low_stock);
        tvTotalRevenue = findViewById(R.id.tv_total_revenue);
        rvRecentActivities = findViewById(R.id.rv_recent_activities);
        
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void setupDatabase() {
        database = AppDatabase.getInstance(this);
        executor = Executors.newFixedThreadPool(4);
    }
    
    private void loadDashboardData() {
        executor.execute(() -> {
            try {
                // Load invoices data
                List<Invoice> allInvoices = database.invoiceDao().getAllInvoices();
                List<Invoice> pendingInvoices = database.invoiceDao().getPendingInvoices();
                
                // Load items data - تصحيح النوع من Product إلى Item
                List<Item> lowStockItems = database.productDao().getLowStockProducts();
                
                // Calculate revenue
                double totalRevenue = 0;
                List<Invoice> paidInvoices = database.invoiceDao().getPaidInvoices();
                for (Invoice invoice : paidInvoices) {
                    totalRevenue += invoice.getTotal();
                }
                
                final double finalRevenue = totalRevenue;
                
                runOnUiThread(() -> {
                    tvTotalInvoices.setText(String.valueOf(allInvoices.size()));
                    tvPendingInvoices.setText(String.valueOf(pendingInvoices.size()));
                    tvLowStock.setText(String.valueOf(lowStockItems.size()));
                    tvTotalRevenue.setText(String.format("%.2f", finalRevenue));
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    tvTotalInvoices.setText("0");
                    tvPendingInvoices.setText("0");
                    tvLowStock.setText("0");
                    tvTotalRevenue.setText("0.00");
                });
            }
        });
    }
    
    public void onInvoicesClicked(View view) {
        Intent intent = new Intent(this, InvoiceListActivity.class);
        startActivity(intent);
    }
    
    public void onItemsClicked(View view) {
        Intent intent = new Intent(this, ItemListActivity.class);
        startActivity(intent);
    }
    
    public void onCustomersClicked(View view) {
        Intent intent = new Intent(this, CustomerListActivity.class);
        startActivity(intent);
    }
    
    public void onReportsClicked(View view) {
        Intent intent = new Intent(this, ReportsActivity.class);
        startActivity(intent);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
