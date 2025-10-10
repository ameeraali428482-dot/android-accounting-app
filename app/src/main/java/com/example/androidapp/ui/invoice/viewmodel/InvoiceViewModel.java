package com.example.androidapp.ui.invoice.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.entities.Invoice;
import java.util.List;






public class InvoiceViewModel extends AndroidViewModel {
    private InvoiceDao invoiceDao;

    public InvoiceViewModel(@NonNull Application application) {
        super(application);
        invoiceDao = AppDatabase.getDatabase(application).invoiceDao();
    }

    public LiveData<List<Invoice>> getAllInvoices(String companyId) {
        return invoiceDao.getAllInvoices(companyId);
    }

    public LiveData<Invoice> getInvoiceById(String invoiceId, String companyId) {
        return invoiceDao.getInvoiceById(invoiceId, companyId);
    }

    public void insert(Invoice invoice) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            invoiceDao.insert(invoice);
        });
    }

    public void update(Invoice invoice) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            invoiceDao.update(invoice);
        });
    }

    public void delete(Invoice invoice) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            invoiceDao.delete(invoice);
        });
    }
}
