package com.example.androidapp.ui.supplier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Supplier;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SupplierListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Supplier> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private FloatingActionButton fabAddSupplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fabAddSupplier = findViewById(R.id.fabAddSupplier);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddSupplier.setOnClickListener(v -> {
            Intent intent = new Intent(SupplierListActivity.this, SupplierDetailActivity.class);
            startActivity(intent);
        });

        loadSuppliers();
    }

    private void loadSuppliers() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<Supplier>() {
            @Override
            public void onItemClick(Supplier item) {
                Intent intent = new Intent(SupplierListActivity.this, SupplierDetailActivity.class);
                intent.putExtra("supplier_id", item.getId());
                startActivity(intent);
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.supplier_list_row;
            }

            @Override
            protected void bindView(View itemView, Supplier supplier) {
                TextView tvSupplierName = itemView.findViewById(R.id.tvSupplierName);
                TextView tvSupplierPhone = itemView.findViewById(R.id.tvSupplierPhone);
                TextView tvSupplierEmail = itemView.findViewById(R.id.tvSupplierEmail);

                tvSupplierName.setText(supplier.getSupplierName());
                tvSupplierPhone.setText(supplier.getPhone());
                tvSupplierEmail.setText(supplier.getEmail());
            }
        };

        recyclerView.setAdapter(adapter);

        database.supplierDao().getAllSuppliers(companyId).observe(this, suppliers -> {
            if (suppliers != null) {
                adapter.updateData(suppliers);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuppliers();
    }
}
