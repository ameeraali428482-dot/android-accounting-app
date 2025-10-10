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
import com.example.androidapp.data.entities.Payment;
import java.util.List;





@Dao
public interface PaymentDao {
    @Insert
    void insert(Payment payment);

    @Update
    void update(Payment payment);

    @Delete
    void delete(Payment payment);

    @Query("SELECT * FROM payments WHERE companyId = :companyId")
    List<Payment> getAllPayments(String companyId);

    @Query("SELECT * FROM payments WHERE id = :id AND companyId = :companyId LIMIT 1")
    Payment getPaymentById(String id, String companyId);

    @Query("SELECT COUNT(*) FROM payments WHERE referenceNumber = :referenceNumber AND companyId = :companyId")
    int countPaymentsByReferenceNumber(String referenceNumber, String companyId);

    @Query("SELECT COUNT(*) FROM payments WHERE id = :id AND companyId = :companyId")
    int countPaymentById(String id, String companyId);
}

