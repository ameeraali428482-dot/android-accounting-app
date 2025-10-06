package com.example.androidapp.ui.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.ui.invoice.viewmodel.InvoiceViewModel;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class InvoiceListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InvoiceAdapter adapter;
    private InvoiceViewModel viewModel;
    private SessionManager sessionManager;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);

        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "معرف الشركة غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        initViews();
        setupRecyclerView();
        loadInvoices();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_invoices);
        FloatingActionButton fab = findViewById(R.id.fab_add_invoice);

        setTitle("الفواتير");

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, InvoiceDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InvoiceAdapter(new ArrayList<>(), invoice -> {
            // Handle invoice click - navigate to InvoiceDetailActivity
            Intent intent = new Intent(InvoiceListActivity.this, InvoiceDetailActivity.class);
            intent.putExtra("invoice_id", invoice.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadInvoices() {
        viewModel.getAllInvoices(companyId).observe(this, invoices -> {
            if (invoices != null) {
                adapter.updateData(invoices);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInvoices();
    }
}

