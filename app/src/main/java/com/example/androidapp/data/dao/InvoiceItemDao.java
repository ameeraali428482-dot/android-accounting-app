package com.example.androidapp.data.dao;

import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.data.entities.Voucher;
import com.example.androidapp.data.entities.Company;
import com.example.androidapp.data.entities.Doctor;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.Supplier;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.data.entities.Trophy;
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.data.entities.Repair;
import com.example.androidapp.data.entities.Chat;
import com.example.androidapp.data.entities.UserReward;
import com.example.androidapp.data.entities.Reward;
import com.example.androidapp.data.entities.PointTransaction;
import com.example.androidapp.data.entities.Campaign;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
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
    List<InvoiceItem> getInvoiceItemsForInvoice(String invoiceId);

    @Query("SELECT * FROM invoice_items WHERE id = :id LIMIT 1")
    InvoiceItem getInvoiceItemById(int id);

    @Query("SELECT COUNT(*) FROM invoice_items WHERE invoiceId = :invoiceId")
    int countInvoiceItemsForInvoice(String invoiceId);
}
