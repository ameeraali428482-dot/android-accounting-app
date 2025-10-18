package com.example.androidapp.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.CustomerDao;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class CustomerListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GenericAdapter<Customer> adapter;
    private CustomerDao customerDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        customerDao = AppDatabase.getDatabase(this).customerDao();
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fabAddCustomer = findViewById(R.id.fab_add_customer);

        fabAddCustomer.setOnClickListener(view -> {
            Intent intent = new Intent(CustomerListActivity.this, CustomerDetailActivity.class);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupAdapter();
        loadData();
    }

    private void setupAdapter() {
        adapter = new GenericAdapter<Customer>(new ArrayList<>(), customer -> {
            Intent intent = new Intent(CustomerListActivity.this, CustomerDetailActivity.class);
            intent.putExtra("customerId", customer.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.customer_list_row;
            }

            @Override
            protected void bindView(View itemView, Customer customer) {
                TextView customerName = itemView.findViewById(R.id.customer_name);
                TextView customerEmail = itemView.findViewById(R.id.customer_email);
                TextView customerPhone = itemView.findViewById(R.id.customer_phone);

                customerName.setText(customer.getName());
                customerEmail.setText(customer.getEmail());
                customerPhone.setText(customer.getPhone());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                List<Customer> customers = customerDao.getCustomersByCompanyId(companyId);
                runOnUiThread(() -> adapter.updateData(customers));
            });
        }
    }
}
