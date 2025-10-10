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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccountStatementListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AccountStatementAdapter adapter;
    private AccountStatementViewModel viewModel;
    private SessionManager sessionManager;
    private String accountId;
    private String companyId;
    private List<AccountStatement> allStatements = new ArrayList<>();
    private ActionMode actionMode;
    private boolean isAscending = true; // For sorting
    private String currentSortColumn = "transactionDate"; // Default sort column

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

        setTitle("كشف الحساب");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountStatementDetailActivity.class);
            intent.putExtra("account_id", accountId);
            startActivity(intent);
        });

        setupColumnHeaders();
    }

    private void setupColumnHeaders() {
        TextView headerDate = findViewById(R.id.header_date);
        TextView headerDescription = findViewById(R.id.header_description);
        TextView headerDebit = findViewById(R.id.header_debit);
        TextView headerCredit = findViewById(R.id.header_credit);
        TextView headerBalance = findViewById(R.id.header_balance);

        headerDate.setOnClickListener(v -> sortByColumn("transactionDate"));
        headerDescription.setOnClickListener(v -> sortByColumn("description"));
        headerDebit.setOnClickListener(v -> sortByColumn("debit"));
        headerCredit.setOnClickListener(v -> sortByColumn("credit"));
        headerBalance.setOnClickListener(v -> sortByColumn("runningBalance"));
    }

    private void sortByColumn(String column) {
        if (currentSortColumn.equals(column)) {
            isAscending = !isAscending; // Toggle sort order
        } else {
            currentSortColumn = column;
            isAscending = true; // Default to ascending for new column
        }

        Comparator<AccountStatement> comparator = null;
        switch (column) {
            case "transactionDate":
                comparator = (a, b) -> a.getTransactionDate().compareTo(b.getTransactionDate());
                break;
            case "description":
                comparator = (a, b) -> a.getDescription().compareToIgnoreCase(b.getDescription());
                break;
            case "debit":
                comparator = (a, b) -> Float.compare(a.getDebit(), b.getDebit());
                break;
            case "credit":
                comparator = (a, b) -> Float.compare(a.getCredit(), b.getCredit());
                break;
            case "runningBalance":
                comparator = (a, b) -> Float.compare(a.getRunningBalance(), b.getRunningBalance());
                break;
        }

        if (comparator != null) {
            if (!isAscending) {
                comparator = comparator.reversed();
            }
            Collections.sort(allStatements, comparator);
            adapter.updateData(allStatements);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AccountStatementAdapter(
                new ArrayList<>(),
                statement -> {
                    // Handle item click
                    if (actionMode != null) {
                        adapter.toggleSelection(statement);
                        updateActionModeTitle();
                        if (adapter.getSelectedItemCount() == 0) {
                            actionMode.finish();
                        }
                    } else {
                        // Normal click - open detail
                        Intent intent = new Intent(AccountStatementListActivity.this, AccountStatementDetailActivity.class);
                        intent.putExtra("statement_id", statement.getId());
                        intent.putExtra("account_id", accountId);
                        startActivity(intent);
                    }
                },
                statement -> {
                    // Handle item long click
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
        viewModel.getAllAccountStatementsForAccount(companyId, accountId)
                .observe(this, statements -> {
                    if (statements != null) {
                        allStatements = new ArrayList<>(statements);
                        sortByColumn(currentSortColumn); // Apply current sort after data load
                    }
                });
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_account_statement_selection, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

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
            int selectedCount = adapter.getSelectedItemCount();
            actionMode.setTitle(selectedCount + " محدد");
        }
    }

    private void shareSelectedStatements() {
        List<AccountStatement> selectedStatements = adapter.getSelectedStatements();
        StringBuilder shareText = new StringBuilder("كشف الحساب المحدد:\n\n");
        
        for (AccountStatement statement : selectedStatements) {
            shareText.append("التاريخ: ").append(statement.getTransactionDate()).append("\n");
            shareText.append("الوصف: ").append(statement.getDescription()).append("\n");
            shareText.append("مدين: ").append(statement.getDebit()).append("\n");
            shareText.append("دائن: ").append(statement.getCredit()).append("\n");
            shareText.append("الرصيد: ").append(statement.getRunningBalance()).append("\n\n");
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        startActivity(Intent.createChooser(shareIntent, "مشاركة كشف الحساب"));
    }

    private void sumSelectedStatements() {
        List<AccountStatement> selectedStatements = adapter.getSelectedStatements();
        double totalDebit = 0;
        double totalCredit = 0;
        
        for (AccountStatement statement : selectedStatements) {
            totalDebit += statement.getDebit();
            totalCredit += statement.getCredit();
        }

        String message = "إجمالي المحدد:\n" +
                "إجمالي المدين: " + totalDebit + "\n" +
                "إجمالي الدائن: " + totalCredit + "\n" +
                "الفرق: " + (totalCredit - totalDebit);
        
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void printSelectedStatements() {
        // TODO: Implement printing functionality (e.g., PDF export)
        Toast.makeText(this, "ميزة الطباعة قيد التطوير", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_statement_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_refresh) {
            loadAccountStatements();
            return true;
        } else if (id == R.id.action_reconcile) {
            // TODO: Implement reconciliation mode
            Toast.makeText(this, "وضع المراجعة قيد التطوير", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_export) {
            // TODO: Implement export functionality (e.g., PDF, CSV)
            Toast.makeText(this, "ميزة التصدير قيد التطوير", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAccountStatements();
    }
}
