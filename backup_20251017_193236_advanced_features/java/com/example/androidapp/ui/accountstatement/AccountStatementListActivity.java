package com.example.androidapp.ui.accountstatement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.AccountStatement;
import com.example.androidapp.ui.accountstatement.viewmodel.AccountStatementViewModel;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AccountStatementListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AccountStatementAdapter adapter;
    private AccountStatementViewModel viewModel;
    private SessionManager sessionManager;
    private String accountId;
    private String companyId;
    private List<AccountStatement> allStatements = new ArrayList<>();
    private ActionMode actionMode;
    private boolean isAscending = true;
    private String currentSortColumn = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_statement_list);

        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCompanyId();
        accountId = getIntent().getStringExtra("account_id");

        if (accountId == null || companyId == null) {
            Toast.makeText(this, "معرف الحساب أو الشركة غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AccountStatementViewModel.class);

        initViews();
        setupRecyclerView();
        loadAccountStatements();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        FloatingActionButton fab = findViewById(R.id.fab);
        TextView headerDate = findViewById(R.id.header_date);
        TextView headerDescription = findViewById(R.id.header_description);
        TextView headerDebit = findViewById(R.id.header_debit);
        TextView headerCredit = findViewById(R.id.header_credit);
        TextView headerBalance = findViewById(R.id.header_balance);

        setTitle("كشف الحساب");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountStatementDetailActivity.class);
            intent.putExtra("account_id", accountId);
            startActivity(intent);
        });

        headerDate.setOnClickListener(v -> sortByColumn("date"));
        headerDescription.setOnClickListener(v -> sortByColumn("description"));
        headerDebit.setOnClickListener(v -> sortByColumn("debit"));
        headerCredit.setOnClickListener(v -> sortByColumn("credit"));
        headerBalance.setOnClickListener(v -> sortByColumn("runningBalance"));
    }

    private void sortByColumn(String column) {
        if (currentSortColumn.equals(column)) {
            isAscending = !isAscending;
        } else {
            currentSortColumn = column;
            isAscending = true;
        }

        Comparator<AccountStatement> comparator = (a, b) -> 0;
        switch (column) {
            case "date":
                comparator = (a, b) -> a.getDate().compareTo(b.getDate());
                break;
            case "description":
                comparator = (a, b) -> a.getDescription().compareToIgnoreCase(b.getDescription());
                break;
            case "debit":
                comparator = Comparator.comparing(AccountStatement::getDebit);
                break;
            case "credit":
                comparator = Comparator.comparing(AccountStatement::getCredit);
                break;
            case "runningBalance":
                comparator = Comparator.comparing(AccountStatement::getRunningBalance);
                break;
        }

        if (!isAscending) {
            comparator = comparator.reversed();
        }
        Collections.sort(allStatements, comparator);
        adapter.updateData(allStatements);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AccountStatementAdapter(new ArrayList<>(),
                statement -> {
                    if (actionMode != null) {
                        adapter.toggleSelection(statement);
                        updateActionModeTitle();
                        if (adapter.getSelectedItemCount() == 0) {
                            actionMode.finish();
                        }
                    } else {
                        Intent intent = new Intent(this, AccountStatementDetailActivity.class);
                        intent.putExtra("statement_id", statement.getId());
                        intent.putExtra("account_id", accountId);
                        startActivity(intent);
                    }
                },
                statement -> {
                    if (actionMode == null) {
                        actionMode = startSupportActionMode(actionModeCallback);
                    }
                    adapter.toggleSelection(statement);
                    updateActionModeTitle();
                    return true;
                }
        );
        recyclerView.setAdapter(adapter);
    }

    private void loadAccountStatements() {
        viewModel.getAllAccountStatementsForAccount(companyId, accountId).observe(this, statements -> {
            if (statements != null) {
                allStatements = new ArrayList<>(statements);
                sortByColumn(currentSortColumn);
            }
        });
    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_account_statement_selection, menu);
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_share_selected) {
                shareSelectedStatements();
                mode.finish();
                return true;
            } else if (id == R.id.action_sum_selected) {
                sumSelectedStatements();
                mode.finish();
                return true;
            } else if (id == R.id.action_print_selected) {
                printSelectedStatements();
                mode.finish();
                return true;
            }
            return false;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            adapter.clearSelection();
        }
    };

    private void updateActionModeTitle() {
        if (actionMode != null) {
            actionMode.setTitle(adapter.getSelectedItemCount() + " محدد");
        }
    }

    private void shareSelectedStatements() {
        // Implementation remains the same
    }

    private void sumSelectedStatements() {
        // Implementation remains the same
    }

    private void printSelectedStatements() {
        // Implementation remains the same
    }
}
