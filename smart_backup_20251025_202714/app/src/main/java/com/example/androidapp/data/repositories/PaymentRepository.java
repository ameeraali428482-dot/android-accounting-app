package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.data.entities.Payment;
import java.util.List;
import java.util.concurrent.Future;

public class PaymentRepository {
    private PaymentDao paymentDao;

    public PaymentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        paymentDao = db.paymentDao();
    }

    public Future<Void> insert(Payment payment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            paymentDao.insert(payment);
            return null;
        });
    }

    public Future<Void> update(Payment payment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            paymentDao.update(payment);
            return null;
        });
    }

    public Future<Void> delete(Payment payment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            paymentDao.delete(payment);
            return null;
        });
    }

    public LiveData<List<Payment>> getAllPayments(String companyId) {
        return paymentDao.getAllPayments(companyId);
    }

    public Future<Integer> countPaymentByReferenceNumber(String referenceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.countPaymentByReferenceNumber(referenceNumber, companyId));
    }
}
