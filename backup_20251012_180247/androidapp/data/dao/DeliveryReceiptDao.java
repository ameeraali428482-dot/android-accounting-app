package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.DeliveryReceipt;




@Dao
public interface DeliveryReceiptDao {
    @Insert
    void insert(DeliveryReceipt deliveryreceipt);

    @Update
    void update(DeliveryReceipt deliveryreceipt);

    @Delete
    void delete(DeliveryReceipt deliveryreceipt);

    @Query("SELECT * FROM delivery_receipts")
    List<DeliveryReceipt> getAllDeliveryReceipts();

    @Query("SELECT * FROM delivery_receipts WHERE id = :id LIMIT 1")
    DeliveryReceipt getDeliveryReceiptById(String id);
}
