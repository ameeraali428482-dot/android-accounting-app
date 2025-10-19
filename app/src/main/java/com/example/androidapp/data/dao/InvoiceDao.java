package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Invoice;
import java.util.List;

@Dao
public interface InvoiceDao extends BaseDao<Invoice> {
    
    @Query("SELECT * FROM invoices")
    List<Invoice> getAllInvoices();

    @Query("SELECT * FROM invoices WHERE companyId = :companyId")
    List<Invoice> getInvoicesByCompany(int companyId);

    @Query("SELECT * FROM invoices WHERE id = :id")
    Invoice getInvoiceById(int id);

    @Query("SELECT * FROM invoices WHERE customerId = :customerId")
    List<Invoice> getInvoicesByCustomer(int customerId);

    @Query("SELECT * FROM invoices WHERE status = :status")
    List<Invoice> getInvoicesByStatus(String status);

    @Query("SELECT * FROM invoices WHERE date BETWEEN :startDate AND :endDate")
    List<Invoice> getInvoicesByDateRange(long startDate, long endDate);

    @Query("SELECT SUM(total) FROM invoices WHERE companyId = :companyId AND status = 'paid'")
    double getTotalPaidAmount(int companyId);

    @Query("SELECT SUM(total) FROM invoices WHERE companyId = :companyId AND status = 'pending'")
    double getTotalPendingAmount(int companyId);

    @Query("SELECT COUNT(*) FROM invoices WHERE companyId = :companyId")
    int getInvoiceCount(int companyId);

    @Query("SELECT * FROM invoices WHERE invoiceNumber LIKE '%' || :query || '%'")
    List<Invoice> searchInvoices(String query);

    // طرق مطلوبة للـ MainActivity
    @Query("SELECT * FROM invoices WHERE companyId = :companyId")
    List<Invoice> getAllInvoices(int companyId);

    @Query("SELECT * FROM invoices WHERE status = 'paid'")
    List<Invoice> getPaidInvoices();

    @Query("SELECT * FROM invoices WHERE status = 'pending'")
    List<Invoice> getPendingInvoices();
}
