package com.example.androidapp.data.repositories;

import android.app.Application;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.entities.Invoice;

import java.util.List;
import java.util.concurrent.Future;

public class InvoiceRepository {
    private InvoiceDao invoiceDao;

    public InvoiceRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        invoiceDao = database.invoiceDao();
    }

    public Future<?> insert(Invoice invoice) {
        return AppDatabase.databaseWriteExecutor.submit(() -> invoiceDao.insert(invoice));
    }

    public Future<?> update(Invoice invoice) {
        return AppDatabase.databaseWriteExecutor.submit(() -> invoiceDao.update(invoice));
    }

    public Future<?> delete(Invoice invoice) {
        return AppDatabase.databaseWriteExecutor.submit(() -> invoiceDao.delete(invoice));
    }

    public Future<Invoice> getInvoiceById(String id, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> invoiceDao.getInvoiceById(id, companyId));
    }

    public Future<List<Invoice>> getAllInvoices(String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> invoiceDao.getAllInvoices(companyId));
    }

    public Future<Integer> countInvoiceByNumber(String invoiceNumber, String companyId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> invoiceDao.countInvoiceByNumber(invoiceNumber, companyId));
    }

    public Future<Float> getTotalSalesByDateRange(String companyId, String startDate, String endDate) {
        return AppDatabase.databaseWriteExecutor.submit(() -> invoiceDao.getTotalSalesByDateRange(companyId, startDate, endDate));
    }
}
