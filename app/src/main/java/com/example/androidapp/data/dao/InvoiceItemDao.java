package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.InvoiceItem;
import java.util.List;

@Dao
public interface InvoiceItemDao {
    @Insert
    long insert(InvoiceItem invoiceItem);

    @Update
    void update(InvoiceItem invoiceItem);

    @Delete
    void delete(InvoiceItem invoiceItem);

    @Query("SELECT * FROM invoice_items WHERE invoice_id = :invoiceId")
    List<InvoiceItem> getInvoiceItemsByInvoiceId(String invoiceId);

    @Query("DELETE FROM invoice_items WHERE invoice_id = :invoiceId")
    void deleteItemsByInvoiceId(String invoiceId);

    @Query("SELECT COUNT(*) FROM invoice_items WHERE invoice_id = :invoiceId")
    int countInvoiceItemsForInvoice(String invoiceId);
}
