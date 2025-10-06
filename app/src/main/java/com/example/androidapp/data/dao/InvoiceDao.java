package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

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
    List<Invoice> getAllInvoices(String companyId);

    @Query("SELECT * FROM invoices WHERE id = :id AND companyId = :companyId LIMIT 1")
    Invoice getInvoiceById(String id, String companyId);

    @Query("SELECT COUNT(*) FROM invoices WHERE invoiceNumber = :invoiceNumber AND companyId = :companyId")
    int countInvoiceByNumber(String invoiceNumber, String companyId);
}
