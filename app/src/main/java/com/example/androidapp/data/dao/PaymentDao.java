package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Payment;
import java.util.List;

@Dao
public interface PaymentDao {
    @Query("SELECT * FROM payments WHERE companyId = :companyId")
    LiveData<List<Payment>> getAllPayments(String companyId);

    @Query("SELECT * FROM payments WHERE id = :paymentId AND companyId = :companyId")
    Payment getPaymentById(String paymentId, String companyId);

    @Query("SELECT * FROM payments WHERE id = :paymentId AND companyId = :companyId")
    LiveData<Payment> getPaymentByIdLiveData(String paymentId, String companyId);

    @Insert
    void insert(Payment payment);

    @Update
    void update(Payment payment);

    @Delete
    void delete(Payment payment);
}
