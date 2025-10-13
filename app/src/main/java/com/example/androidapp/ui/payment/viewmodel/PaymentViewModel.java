package com.example.androidapp.ui.payment.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.data.entities.Payment;

import java.util.List;
import java.util.concurrent.Executors;

public class PaymentViewModel extends AndroidViewModel {
    private PaymentDao paymentDao;

    public PaymentViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        paymentDao = database.paymentDao();
    }

    public LiveData<List<Payment>> getAllPayments(String companyId) {
        return paymentDao.getAllPayments(companyId);
    }

    public LiveData<Payment> getPaymentById(String paymentId, String companyId) {
        return paymentDao.getPaymentById(paymentId, companyId);
    }

    public void insert(Payment payment) {
        Executors.newSingleThreadExecutor().execute(() -> paymentDao.insert(payment));
    }

    public void update(Payment payment) {
        Executors.newSingleThreadExecutor().execute(() -> paymentDao.update(payment));
    }

    public void delete(Payment payment) {
        Executors.newSingleThreadExecutor().execute(() -> paymentDao.delete(payment));
    }
}
