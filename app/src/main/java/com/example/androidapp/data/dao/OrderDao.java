package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.models.Order;

import java.util.List;

@Dao
public interface OrderDao extends BaseDao<Order> {
    @Query("SELECT * FROM orders WHERE companyId = :companyId ORDER BY orderDate DESC")
    LiveData<List<Order>> getAllOrders(int companyId);

    @Query("SELECT * FROM orders WHERE id = :orderId AND companyId = :companyId")
    LiveData<Order> getOrderById(int orderId, int companyId);

    @Insert
    long insert(Order order);

    @Update
    void update(Order order);

    @Delete
    void delete(Order order);
}
