package com.example.androidapp.ui.account.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.AccountDao;
import com.example.androidapp.data.entities.Account;
import java.util.List;

public class AccountViewModel extends AndroidViewModel {
    private AccountDao accountDao;

    public AccountViewModel(@NonNull Application application) {
        super(application);
        accountDao = AppDatabase.getDatabase(application).accountDao();
    }

    public LiveData<List<Account>> getAllAccounts(String companyId) {
        return accountDao.getAllAccounts(companyId);
    }

    public LiveData<Account> getAccountById(String accountId, String companyId) {
        return accountDao.getAccountById(accountId, companyId);
    }

    public void insert(Account account) {
        AppDatabase.databaseWriteExecutor.execute(() -> accountDao.insert(account));
    }

    public void update(Account account) {
        AppDatabase.databaseWriteExecutor.execute(() -> accountDao.update(account));
    }

    public void delete(Account account) {
        AppDatabase.databaseWriteExecutor.execute(() -> accountDao.delete(account));
    }
}
