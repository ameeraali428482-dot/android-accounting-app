package com.example.androidapp.ui.account.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.androidapp.data.dao.AccountDao;
import com.example.androidapp.data.entities.Account;

public class AccountViewModel extends ViewModel {
    private AccountDao accountDao;

    public AccountViewModel(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public LiveData<Account> getAccountById(String accountId, String companyId) {
        return accountDao.getAccountByIdLiveData(accountId, companyId);
    }
}
