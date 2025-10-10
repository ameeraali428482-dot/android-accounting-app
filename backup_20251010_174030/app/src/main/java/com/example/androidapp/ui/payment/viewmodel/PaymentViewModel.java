package com.example.androidapp.ui.payment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.data.entities.Payment;

import java.util.List;

public class PaymentViewModel extends AndroidViewModel {
    private PaymentDao paymentDao;

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        paymentDao = AppDatabase.getDatabase(application).paymentDao();
    }

    public LiveData<List<Payment>> getAllPayments(String companyId) {
        return paymentDao.getAllPayments(companyId);
    }

    public LiveData<Payment> getPaymentById(String paymentId, String companyId) {
        return paymentDao.getPaymentById(paymentId, companyId);
    }

    public void insert(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            paymentDao.insert(payment);
        });
    }

    public void update(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            paymentDao.update(payment);
        });
    }

    public void delete(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            paymentDao.delete(payment);
        });
    }
}
