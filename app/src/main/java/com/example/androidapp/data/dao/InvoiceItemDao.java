package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.androidapp.data.entities.InvoiceItem;
import java.util.List;

@Dao
public interface InvoiceItemDao {
    @Insert
    void insert(InvoiceItem invoiceItem);

    @Update
    void update(InvoiceItem invoiceItem);

    @Delete
    void delete(InvoiceItem invoiceItem);

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId")
    List<InvoiceItem> getInvoiceItemsByInvoiceId(String invoiceId);

    @Query("SELECT * FROM invoice_items WHERE id = :id LIMIT 1")
    InvoiceItem getInvoiceItemById(String id);

    @Query("SELECT COUNT(*) FROM invoice_items WHERE invoiceId = :invoiceId")
    int countInvoiceItemsForInvoice(String invoiceId);
}
