package com.example.androidapp.ui.payment.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.data.entities.Payment;

public class PaymentViewModel extends ViewModel {
    private PaymentDao paymentDao;

    public PaymentViewModel(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    public LiveData<Payment> getPaymentById(String paymentId, String companyId) {
        return paymentDao.getPaymentByIdLiveData(paymentId, companyId);
    }
}
