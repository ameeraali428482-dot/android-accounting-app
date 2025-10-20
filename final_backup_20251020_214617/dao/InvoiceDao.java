package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Invoice;
import java.util.List;

@Dao
public interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY issue_date DESC")
    List<Invoice> getAllInvoices();
    
    @Query("SELECT * FROM invoices WHERE id = :id")
    Invoice getInvoiceById(int id);
    
    @Query("SELECT * FROM invoices WHERE customer_id = :customerId")
    List<Invoice> getInvoicesByCustomerId(int customerId);
    
    @Query("SELECT * FROM invoices WHERE status = :status")
    List<Invoice> getInvoicesByStatus(String status);
    
    @Query("SELECT SUM(total_amount) FROM invoices WHERE customer_id = :customerId")
    double getTotalAmountByCustomer(int customerId);
    
    @Insert
    void insertInvoice(Invoice invoice);
    
    @Update
    void updateInvoice(Invoice invoice);
    
    @Delete
    void deleteInvoice(Invoice invoice);
}
