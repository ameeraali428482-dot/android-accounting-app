package com.example.androidapp.ui.account;

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
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.ui.account.viewmodel.AccountViewModel;
import com.example.androidapp.ui.accountstatement.AccountStatementListActivity;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class AccountListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AccountAdapter adapter;
    private AccountViewModel viewModel;
    private SessionManager sessionManager;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);

        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "معرف الشركة غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        initViews();
        setupRecyclerView();
        loadAccounts();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_accounts);
        FloatingActionButton fab = findViewById(R.id.fab_add_account);
        setTitle("الحسابات");

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AccountAdapter(new ArrayList<>(), account -> {
            Intent intent = new Intent(AccountListActivity.this, AccountStatementListActivity.class);
            intent.putExtra("account_id", account.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadAccounts() {
        viewModel.getAllAccounts(companyId).observe(this, accounts -> {
            if (accounts != null) {
                adapter.updateData(accounts);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAccounts();
    }
}
