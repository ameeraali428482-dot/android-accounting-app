package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Invoice;
import java.util.List;

@Dao
public interface InvoiceDao {
    @Insert
    void insert(Invoice invoice);

    @Update
    void update(Invoice invoice);

    @Delete
    void delete(Invoice invoice);

    @Query("SELECT * FROM invoices WHERE companyId = :companyId")
    LiveData<List<Invoice>> getAllInvoices(String companyId);

    @Query("SELECT * FROM invoices WHERE id = :id AND companyId = :companyId LIMIT 1")
    Invoice getInvoiceById(String id, String companyId);

    @Query("SELECT * FROM invoices WHERE companyId = :companyId AND invoiceType = :type")
    List<Invoice> getInvoicesByCompanyIdAndType(String companyId, String type);

    @Query("SELECT * FROM invoices WHERE supplierId = :supplierId")
    List<Invoice> getInvoicesBySupplierId(String supplierId);
    
    @Query("SELECT * FROM invoices WHERE customerId = :customerId")
    List<Invoice> getInvoicesByCustomerId(String customerId);
}
