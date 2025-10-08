package com.example.androidapp.ui.supplier;
import com.example.androidapp.data.entities.Supplier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.SupplierDao;
import com.example.androidapp.models.Supplier;
import com.example.androidapp.ui.common.BaseListActivity;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SupplierListActivity extends BaseListActivity<Supplier> {

    private SupplierDao supplierDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_list);

        supplierDao = new SupplierDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        FloatingActionButton fabAddSupplier = findViewById(R.id.fab_add_supplier);
        fabAddSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SupplierListActivity.this, SupplierDetailActivity.class);
                startActivity(intent);
            }
        });

        // Initialize RecyclerView and other common elements from BaseListActivity
        recyclerView = findViewById(R.id.recyclerView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        adapter = createAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new GenericAdapter.OnItemClickListener<Supplier>() {
            @Override
            public void onItemClick(Supplier supplier) {
                Intent intent = new Intent(SupplierListActivity.this, SupplierDetailActivity.class);
                intent.putExtra("supplierId", supplier.getId());
                startActivity(intent);
            }
        });

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Refresh data when returning to this activity
    }

    @Override
    protected GenericAdapter<Supplier> createAdapter() {
        return new GenericAdapter<Supplier>(new ArrayList<>()) {
            @Override
            protected int getLayoutResId() {
                return R.layout.supplier_list_row;
            }

            @Override
            protected void bindView(View itemView, Supplier supplier) {
                TextView supplierName = itemView.findViewById(R.id.supplier_name);
                TextView supplierEmail = itemView.findViewById(R.id.supplier_email);
                TextView supplierPhone = itemView.findViewById(R.id.supplier_phone);

                supplierName.setText(supplier.getName());
                supplierEmail.setText(supplier.getEmail());
                supplierPhone.setText(supplier.getPhone());
            }
        };
    }

    @Override
    protected void loadData() {
        showLoading();
        // In a real app, this would be an async operation
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_CURRENT_ORG_ID);
        if (companyId != null) {
            List<Supplier> suppliers = supplierDao.getSuppliersByCompanyId(companyId);
            showData(suppliers);
        } else {
            showData(new ArrayList<>()); // No company selected or logged in
        }
    }

    @Override
    protected String getEmptyStateMessage() {
        return "لا يوجد موردون لعرضهم. اضغط على زر الإضافة لإنشاء مورد جديد.";
    }
}

