package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.Voucher;




@Dao
public interface VoucherDao {
    @Insert
    void insert(Voucher voucher);

    @Update
    void update(Voucher voucher);

    @Delete
    void delete(Voucher voucher);

    @Query("SELECT * FROM vouchers")
    List<Voucher> getAllVouchers();

    @Query("SELECT * FROM vouchers WHERE id = :id LIMIT 1")
    Voucher getVoucherById(String id);
}
