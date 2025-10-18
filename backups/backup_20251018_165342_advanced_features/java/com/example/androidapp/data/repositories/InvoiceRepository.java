package com.example.androidapp.data.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.entities.Invoice;
import java.util.List;
import java.util.concurrent.Future;

public class InvoiceRepository {
    private InvoiceDao invoiceDao;

    public InvoiceRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        invoiceDao = db.invoiceDao();
    }

    public Future<Void> insert(Invoice invoice) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            invoiceDao.insert(invoice);
            return null;
        });
    }

    public Future<Void> update(Invoice invoice) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            invoiceDao.update(invoice);
            return null;
        });
    }

    public Future<Void> delete(Invoice invoice) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            invoiceDao.delete(invoice);
            return null;
        });
    }

    public LiveData<List<Invoice>> getAllInvoices(String companyId) {
        return invoiceDao.getAllInvoices(companyId);
    }

    public Future<Integer> countInvoicesByNumber(String invoiceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> invoiceDao.countInvoicesByNumber(invoiceNumber, companyId));
    }

    public Future<Float> getTotalSalesByDateRange(String companyId, String startDate, String endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> invoiceDao.getTotalSalesByDateRange(companyId, startDate, endDate));
    }
}
