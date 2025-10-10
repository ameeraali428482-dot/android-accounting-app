import java.util.Date;
package com.example.androidapp.ui.accountstatement.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.AccountStatementDao;
import com.example.androidapp.data.entities.AccountStatement;
import com.example.androidapp.logic.AccountingManager;

import java.util.List;

public class AccountStatementViewModel extends AndroidViewModel {
    private AccountStatementDao accountStatementDao;
    private AccountingManager accountingManager;

    public AccountStatementViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        this.accountStatementDao = db.accountStatementDao();
        this.accountingManager = new AccountingManager(application);
    }

    public LiveData<List<AccountStatement>> getAllAccountStatementsForAccount(String companyId, String accountId) {
        return accountStatementDao.getAllAccountStatementsForAccount(companyId, accountId);
    }

    public LiveData<AccountStatement> getAccountStatementById(int statementId, String companyId, String accountId) {
        return accountStatementDao.getAccountStatementById(statementId, companyId, accountId);
    }

    public void insert(AccountStatement accountStatement) {
        accountingManager.calculateAndSaveAccountStatement(accountStatement);
    }

    public void update(AccountStatement accountStatement) {
        // For updates, we need to recalculate from the updated statement's date
        AppDatabase.databaseWriteExecutor.execute(() -> {
            accountStatementDao.update(accountStatement);
            accountingManager.recalculateRunningBalances(accountStatement.getCompanyId(), accountStatement.getAccountId(), accountStatement.getTransactionDate());
        });
    }

    public void delete(AccountStatement accountStatement) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            accountStatementDao.delete(accountStatement);
            // After deletion, recalculate running balances from the deleted statement's date
            accountingManager.recalculateRunningBalances(accountStatement.getCompanyId(), accountStatement.getAccountId(), accountStatement.getTransactionDate());
        });
    }
}
