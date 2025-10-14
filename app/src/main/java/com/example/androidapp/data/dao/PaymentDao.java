package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
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
    LiveData<List<Payment>> getAllPayments(String companyId);

    @Query("SELECT * FROM payments WHERE id = :id AND companyId = :companyId LIMIT 1")
    Payment getPaymentById(String id, String companyId);

    @Query("SELECT COUNT(*) FROM payments WHERE referenceNumber = :referenceNumber AND companyId = :companyId")
    int countPaymentByReferenceNumber(String referenceNumber, String companyId);

    @Query("SELECT COUNT(*) FROM payments WHERE id = :id AND companyId = :companyId")
    int countPaymentById(String id, String companyId);
}
