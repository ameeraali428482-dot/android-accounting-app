package com.example.androidapp.data.repositories;

import android.app.Application;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.data.entities.Payment;

import java.util.List;
import java.util.concurrent.Future;

public class PaymentRepository {
    private PaymentDao paymentDao;

    public PaymentRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        paymentDao = database.paymentDao();
    }

    public Future<?> insert(Payment payment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.insert(payment));
    }

    public Future<?> update(Payment payment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.update(payment));
    }

    public Future<?> delete(Payment payment) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.delete(payment));
    }

    public Future<Payment> getPaymentById(String id, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.getPaymentById(id, companyId));
    }

    public Future<List<Payment>> getAllPayments(String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.getAllPayments(companyId));
    }

    public Future<Integer> countPaymentByReferenceNumber(String referenceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> paymentDao.countPaymentByReferenceNumber(referenceNumber, companyId));
    }
}
