package com.example.androidapp.ui.accountstatement;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.AccountStatement;
import com.example.androidapp.logic.AccountingManager;
import com.example.androidapp.ui.accountstatement.viewmodel.AccountStatementViewModel;
import com.example.androidapp.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;






public class AccountStatementDetailActivity extends AppCompatActivity {

    private EditText etTransactionDate, etDescription, etDebit, etCredit, etReferenceType, etReferenceId;
    private Button btnSave;
    private AccountStatementViewModel viewModel;
    private AccountingManager accountingManager;
    private SessionManager sessionManager;
    private String accountId;
    private String companyId;
    private int statementId = -1; // -1 for new statement, otherwise existing statement ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_statement_detail);

        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCompanyId();

        accountId = getIntent().getStringExtra("account_id");
        statementId = getIntent().getIntExtra("statement_id", -1);

        if (accountId == null || companyId == null) {
            Toast.makeText(this, "معرف الحساب أو الشركة غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AccountStatementViewModel.class);
        accountingManager = new AccountingManager(this);

        initViews();
        setupListeners();

        if (statementId != -1) {
            setTitle("تعديل كشف الحساب");
            loadAccountStatementDetails(statementId);
        } else {
            setTitle("إضافة كشف حساب جديد");
            // Set current date as default for new statements
            etTransactionDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        etTransactionDate = findViewById(R.id.et_transaction_date);
        etDescription = findViewById(R.id.et_description);
        etDebit = findViewById(R.id.et_debit);
        etCredit = findViewById(R.id.et_credit);
        etReferenceType = findViewById(R.id.et_reference_type);
        etReferenceId = findViewById(R.id.et_reference_id);
        btnSave = findViewById(R.id.btn_save_statement);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveAccountStatement());
    }

    private void loadAccountStatementDetails(int id) {
        viewModel.getAccountStatementById(id, companyId, accountId).observe(this, statement -> {
            if (statement != null) {
                etTransactionDate.setText(statement.getTransactionDate());
                etDescription.setText(statement.getDescription());
                etDebit.setText(String.valueOf(statement.getDebit()));
                etCredit.setText(String.valueOf(statement.getCredit()));
                etReferenceType.setText(statement.getReferenceType());
                etReferenceId.setText(statement.getReferenceId());
            } else {
                Toast.makeText(this, "لم يتم العثور على كشف الحساب", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveAccountStatement() {
        String transactionDate = etTransactionDate.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        float debit = Float.parseFloat(etDebit.getText().toString().trim().isEmpty() ? "0" : etDebit.getText().toString().trim());
        float credit = Float.parseFloat(etCredit.getText().toString().trim().isEmpty() ? "0" : etCredit.getText().toString().trim());
        String referenceType = etReferenceType.getText().toString().trim();
        String referenceId = etReferenceId.getText().toString().trim();

        if (transactionDate.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال التاريخ والوصف", Toast.LENGTH_SHORT).show();
            return;
        }

        AccountStatement accountStatement;
        if (statementId == -1) {
            // New statement
            accountStatement = new AccountStatement(companyId, accountId, transactionDate, description, debit, credit, 0.0f, referenceType, referenceId);
            accountingManager.calculateAndSaveAccountStatement(accountStatement);
            Toast.makeText(this, "تم إضافة كشف الحساب بنجاح", Toast.LENGTH_SHORT).show();
        } else {
            // Existing statement
            accountStatement = new AccountStatement(companyId, accountId, transactionDate, description, debit, credit, 0.0f, referenceType, referenceId);
            accountStatement.setId(statementId);
            viewModel.update(accountStatement);
            accountingManager.recalculateRunningBalances(companyId, accountId, transactionDate);
            Toast.makeText(this, "تم تحديث كشف الحساب بنجاح", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
