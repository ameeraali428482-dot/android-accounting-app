package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Invoice;

import java.util.List;

@Dao
public interface InvoiceDao extends BaseDao<Invoice> {
    
    @Query("SELECT * FROM invoices WHERE id = :id")
    Invoice getById(long id);

    @Query("SELECT * FROM invoices")
    List<Invoice> getAll();

    @Query("SELECT * FROM invoices WHERE date BETWEEN :startDate AND :endDate")
    List<Invoice> getInvoicesByDateRange(long startDate, long endDate);

    @Query("SELECT SUM(total) FROM invoices WHERE status = 'PAID'")
    double getTotalPaidAmount();

    @Query("SELECT SUM(total) FROM invoices WHERE status = 'PENDING'")
    double getTotalPendingAmount();

    @Query("SELECT * FROM invoices WHERE customerId = :customerId")
    List<Invoice> getByCustomerId(long customerId);

    @Query("SELECT * FROM invoices WHERE companyId = :companyId")
    List<Invoice> getByCompanyId(String companyId);

    @Query("SELECT * FROM invoices WHERE status = :status")
    List<Invoice> getByStatus(String status);

    @Query("SELECT COUNT(*) FROM invoices WHERE companyId = :companyId")
    int getCountByCompany(String companyId);

    @Query("UPDATE invoices SET status = :status WHERE id = :id")
    void updateStatus(long id, String status);

    @Query("DELETE FROM invoices WHERE companyId = :companyId")
    void deleteByCompanyId(String companyId);
}
