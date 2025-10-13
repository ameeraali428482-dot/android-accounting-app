package com.example.androidapp.ui.payment.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.data.entities.Payment;
import java.util.List;

public class PaymentViewModel extends AndroidViewModel {
    private PaymentDao paymentDao;
    private AppDatabase database;

    public PaymentViewModel(Application application) {
        super(application);
        database = AppDatabase.getDatabase(application);
        paymentDao = database.paymentDao();
    }

    public LiveData<List<Payment>> getAllPayments(String companyId) {
        return paymentDao.getAllPaymentsLive(companyId);
    }

    public LiveData<Payment> getPaymentById(String paymentId, String companyId) {
        return paymentDao.getPaymentByIdLive(paymentId, companyId);
    }

    public void insert(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> paymentDao.insert(payment));
    }

    public void update(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> paymentDao.update(payment));
    }

    public void delete(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> paymentDao.delete(payment));
    }
}
