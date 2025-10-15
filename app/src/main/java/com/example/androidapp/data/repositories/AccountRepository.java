package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.AccountDao;
import com.example.androidapp.data.entities.Account;
import java.util.List;
import java.util.concurrent.Future;

public class AccountRepository {
    private AccountDao accountDao;

    public AccountRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        accountDao = db.accountDao();
    }

    public Future<Void> insert(Account account) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            accountDao.insert(account);
            return null;
        });
    }

    public Future<Void> update(Account account) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            accountDao.update(account);
            return null;
        });
    }

    public Future<Void> delete(Account account) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            accountDao.delete(account);
            return null;
        });
    }

    public LiveData<List<Account>> getAllAccounts(String companyId) {
        return accountDao.getAllAccounts(companyId);
    }

    public LiveData<Account> getAccountById(String accountId, String companyId) {
        return accountDao.getAccountById(accountId, companyId);
    }

    public Future<Account> getAccountByNameAndCompanyId(String accountName, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> accountDao.getAccountByNameAndCompanyId(accountName, companyId));
    }
}
