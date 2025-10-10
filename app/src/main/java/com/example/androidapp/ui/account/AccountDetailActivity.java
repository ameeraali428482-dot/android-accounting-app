package com.example.androidapp.ui.account;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.ui.account.viewmodel.AccountViewModel;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;






public class AccountDetailActivity extends AppCompatActivity {

    private EditText nameEditText, accountNumberEditText, balanceEditText, accountTypeEditText;
    private Button saveButton, deleteButton;
    private AccountViewModel viewModel;
    private SessionManager sessionManager;
    private String accountId = null;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "معرف الشركة غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        initViews();
        setupListeners();

        if (getIntent().hasExtra("account_id")) {
            accountId = getIntent().getStringExtra("account_id");
            loadAccountData(accountId);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        nameEditText = findViewById(R.id.account_name_edit_text);
        accountNumberEditText = findViewById(R.id.account_number_edit_text);
        balanceEditText = findViewById(R.id.account_balance_edit_text);
        accountTypeEditText = findViewById(R.id.account_type_edit_text);
        saveButton = findViewById(R.id.save_account_button);
        deleteButton = findViewById(R.id.delete_account_button);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveAccount());
        deleteButton.setOnClickListener(v -> deleteAccount());
    }

    private void loadAccountData(String id) {
        viewModel.getAccountById(id, companyId).observe(this, account -> {
            if (account != null) {
                nameEditText.setText(account.getAccountName());
                accountNumberEditText.setText(account.getAccountNumber());
                balanceEditText.setText(String.valueOf(account.getCurrentBalance()));
                accountTypeEditText.setText(account.getAccountType());
            } else {
                Toast.makeText(this, "لم يتم العثور على الحساب", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveAccount() {
        String name = nameEditText.getText().toString().trim();
        String accountNumber = accountNumberEditText.getText().toString().trim();
        String balanceStr = balanceEditText.getText().toString().trim();
        String accountType = accountTypeEditText.getText().toString().trim();

        if (name.isEmpty() || accountNumber.isEmpty() || balanceStr.isEmpty() || accountType.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول.", Toast.LENGTH_SHORT).show();
            return;
        }

        float balance = Float.parseFloat(balanceStr);

        Account account;
        if (accountId == null) {
            // New account
            account = new Account(UUID.randomUUID().toString(), companyId, name, accountType, accountNumber, balance);
            viewModel.insert(account);
            Toast.makeText(this, "تم إضافة الحساب بنجاح.", Toast.LENGTH_SHORT).show();
        } else {
            // Existing account
            account = new Account(accountId, companyId, name, accountType, accountNumber, balance);
            viewModel.update(account);
            Toast.makeText(this, "تم تحديث الحساب بنجاح.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteAccount() {
        if (accountId != null) {
            viewModel.getAccountById(accountId, companyId).observe(this, account -> {
                if (account != null) {
                    viewModel.delete(account);
                    Toast.makeText(this, "تم حذف الحساب بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
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

