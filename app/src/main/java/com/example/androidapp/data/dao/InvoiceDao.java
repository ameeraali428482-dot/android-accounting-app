package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Invoice;
import java.util.List;

@Dao
public interface InvoiceDao extends BaseDao<Invoice> {
    
    @Query("SELECT * FROM invoices ORDER BY created_date DESC")
    List<Invoice> getAllInvoices();
    
    @Query("SELECT * FROM invoices WHERE id = :id")
    Invoice getInvoiceById(long id);
    
    @Query("SELECT * FROM invoices WHERE created_date BETWEEN :startDate AND :endDate ORDER BY created_date DESC")
    List<Invoice> getInvoicesByDateRange(long startDate, long endDate);
    
    @Query("SELECT COALESCE(SUM(total_amount), 0) FROM invoices WHERE status = 'PAID'")
    double getTotalPaidAmount();
    
    @Query("SELECT COALESCE(SUM(total_amount), 0) FROM invoices WHERE status = 'PENDING'")
    double getTotalPendingAmount();
    
    @Query("SELECT * FROM invoices WHERE customer_id = :customerId ORDER BY created_date DESC")
    List<Invoice> getInvoicesByCustomer(long customerId);
    
    @Query("SELECT * FROM invoices WHERE status = :status ORDER BY created_date DESC")
    List<Invoice> getInvoicesByStatus(String status);
    
    @Query("UPDATE invoices SET status = :status WHERE id = :id")
    void updateInvoiceStatus(long id, String status);
    
    @Query("DELETE FROM invoices WHERE id = :id")
    void deleteInvoice(long id);
}
