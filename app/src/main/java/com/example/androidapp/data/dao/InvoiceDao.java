package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Invoice;

import java.util.List;

@Dao
public interface InvoiceDao extends BaseDao<Invoice> {
    
    @Query("SELECT * FROM invoices WHERE id = :id")
    Invoice getById(long id);

    @Query("SELECT * FROM invoices ORDER BY created_at DESC")
    List<Invoice> getAll();

    @Query("SELECT * FROM invoices WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    List<Invoice> getInvoicesByDateRange(long startDate, long endDate);

    @Query("SELECT SUM(total_amount) FROM invoices WHERE status = 'PAID' AND company_id = :companyId")
    double getTotalPaidAmount(String companyId);

    @Query("SELECT SUM(total_amount) FROM invoices WHERE status = 'PENDING' AND company_id = :companyId")
    double getTotalPendingAmount(String companyId);

    @Query("SELECT * FROM invoices WHERE customer_id = :customerId ORDER BY created_at DESC")
    List<Invoice> getByCustomerId(long customerId);

    @Query("SELECT * FROM invoices WHERE company_id = :companyId ORDER BY created_at DESC")
    List<Invoice> getByCompanyId(String companyId);

    @Query("SELECT * FROM invoices WHERE status = :status ORDER BY created_at DESC")
    List<Invoice> getByStatus(String status);

    @Query("SELECT COUNT(*) FROM invoices WHERE company_id = :companyId")
    int getCountByCompany(String companyId);

    @Query("UPDATE invoices SET status = :status WHERE id = :id")
    void updateStatus(long id, String status);

    @Query("DELETE FROM invoices WHERE company_id = :companyId")
    void deleteByCompanyId(String companyId);
}
