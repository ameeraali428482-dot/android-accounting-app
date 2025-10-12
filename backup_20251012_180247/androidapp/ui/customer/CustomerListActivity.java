package com.example.androidapp.ui.customer;

import com.example.androidapp.data.entities.Customer;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.CustomerDao;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.ui.common.BaseListActivity;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;





public class CustomerListActivity extends BaseListActivity<Customer> {

    private CustomerDao customerDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        customerDao = new CustomerDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        fabAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerListActivity.this, CustomerDetailActivity.class);
                startActivity(intent);
            }
        });

        // Initialize RecyclerView and other common elements from BaseListActivity

        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        adapter = createAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new GenericAdapter.OnItemClickListener<Customer>() {
            @Override
            public void onItemClick(Customer customer) {
                Intent intent = new Intent(CustomerListActivity.this, CustomerDetailActivity.class);
                intent.putExtra("customerId", customer.getId());
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
    protected GenericAdapter<Customer> createAdapter() {
        return new GenericAdapter<Customer>(new ArrayList<>()) {
            @Override
            protected int getLayoutResId() {
                return R.layout.customer_list_row;
            }

            @Override
            protected void bindView(View itemView, Customer customer) {

                customerName.setText(customer.getName());
                customerEmail.setText(customer.getEmail());
                customerPhone.setText(customer.getPhone());
            }
        };
    }

    @Override
    protected void loadData() {
        showLoading();
        // In a real app, this would be an async operation
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_CURRENT_ORG_ID);
        if (companyId != null) {
            List<Customer> customers = customerDao.getCustomersByCompanyId(companyId);
            showData(customers);
        } else {
            showData(new ArrayList<>()); // No company selected or logged in
        }
    }

    @Override
    protected String getEmptyStateMessage() {
        return "لا يوجد عملاء لعرضهم. اضغط على زر الإضافة لإنشاء عميل جديد.";
    }
}

