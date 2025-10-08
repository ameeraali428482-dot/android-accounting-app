package com.example.androidapp.data.repositories;

import android.app.Application;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.AccountDao;
import com.example.androidapp.data.entities.Account;

import java.util.List;
import java.util.concurrent.Future;

public class AccountRepository {
    private AccountDao accountDao;

    public AccountRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        accountDao = database.accountDao();
    }

    public Future<?> insert(Account account) {
        return AppDatabase.databaseWriteExecutor.submit(() -> accountDao.insert(account));
    }

    public Future<?> update(Account account) {
        return AppDatabase.databaseWriteExecutor.submit(() -> accountDao.update(account));
    }

    public Future<?> delete(Account account) {
        return AppDatabase.databaseWriteExecutor.submit(() -> accountDao.delete(account));
    }

    public Future<Account> getAccountByNameAndCompanyId(String name, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> accountDao.getAccountByNameAndCompanyId(name, companyId));
    }

    public Future<List<Account>> getAllAccounts(String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> accountDao.getAllAccounts(companyId));
    }
}
