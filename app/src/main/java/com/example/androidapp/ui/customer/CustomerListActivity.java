package com.example.androidapp.ui.customer;
import android.view.ViewGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.ui.common.EnhancedBaseActivity;
import com.example.androidapp.ui.customer.viewmodel.CustomerViewModel;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CustomerListActivity extends EnhancedBaseActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private CustomerViewModel viewModel;
    private FloatingActionButton fabAddCustomer;
    private SessionManager sessionManager;
    private String companyId;
    private List<Customer> allCustomers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "معرف الشركة غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupRecyclerView();
        setupViewModel();
        setupFloatingActionButton();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("دليل العملاء");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view_customers);
        fabAddCustomer = findViewById(R.id.fab_add_customer);
    }

    private void setupRecyclerView() {
        adapter = new CustomerAdapter(this, allCustomers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(customer -> {
            Intent intent = new Intent(CustomerListActivity.this, CustomerDetailActivity.class);
            intent.putExtra("customerId", customer.getId());
            startActivity(intent);
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        viewModel.getAllCustomers(companyId).observe(this, customers -> {
            if (customers != null) {
                allCustomers.clear();
                allCustomers.addAll(customers);
                adapter.updateData(customers);
            }
        });
    }

    private void setupFloatingActionButton() {
        fabAddCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerDetailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_list, menu);
        
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        
        if (searchView != null) {
            searchView.setQueryHint("البحث في العملاء...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    performSearch(query);
                    return true;
                }
                
                @Override
                public boolean onQueryTextChange(String newText) {
                    filterCustomers(newText);
                    return true;
                }
            });
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterCustomers(String query) {
        List<Customer> filtered = new ArrayList<>();
        if (query.isEmpty()) {
            filtered.addAll(allCustomers);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Customer customer : allCustomers) {
                if (customer.getName() != null && customer.getName().toLowerCase().contains(lowerQuery) ||
                    customer.getEmail() != null && customer.getEmail().toLowerCase().contains(lowerQuery) ||
                    customer.getPhone() != null && customer.getPhone().toLowerCase().contains(lowerQuery)) {
                    filtered.add(customer);
                }
            }
        }
        adapter.updateData(filtered);
    }

    @Override
    protected void performSearch(String query) {
        filterCustomers(query);
    }

    @Override
    protected String getAutoReadContent() {
        return "صفحة دليل العملاء. تحتوي على " + allCustomers.size() + " عميل. يمكنك البحث أو إضافة عميل جديد";
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        if (companyId != null) {
            viewModel.refreshCustomers(companyId);
        }
    }

    // Customer Adapter class
    private static class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {
        private final List<Customer> customers;
        private final CustomerListActivity activity;
        private OnItemClickListener onItemClickListener;

        public interface OnItemClickListener {
            void onItemClick(Customer customer);
        }

        public CustomerAdapter(CustomerListActivity activity, List<Customer> customers) {
            this.activity = activity;
            this.customers = customers;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.onItemClickListener = listener;
        }

        public void updateData(List<Customer> newCustomers) {
            customers.clear();
            customers.addAll(newCustomers);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = activity.getLayoutInflater().inflate(R.layout.item_customer, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Customer customer = customers.get(position);
            holder.bind(customer);
            
            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(customer);
                }
            });
        }

        @Override
        public int getItemCount() {
            return customers.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView customerName;
            private final TextView customerEmail;
            private final TextView customerPhone;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                customerName = itemView.findViewById(R.id.customer_name);
                customerEmail = itemView.findViewById(R.id.customer_email);
                customerPhone = itemView.findViewById(R.id.customer_phone);
            }

            public void bind(Customer customer) {
                customerName.setText(customer.getName());
                customerEmail.setText(customer.getEmail());
                customerPhone.setText(customer.getPhone());
            }
        }
    }
}
