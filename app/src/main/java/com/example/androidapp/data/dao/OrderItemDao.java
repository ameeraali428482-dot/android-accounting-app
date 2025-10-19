package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.OrderItem;

import java.util.List;

@Dao
public interface OrderItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OrderItem orderItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<OrderItem> orderItems);

    @Update
    void update(OrderItem orderItem);

    @Delete
    void delete(OrderItem orderItem);

    @Query("SELECT * FROM order_items WHERE id = :id")
    OrderItem getById(String id);

    @Query("SELECT * FROM order_items WHERE order_id = :orderId")
    List<OrderItem> getByOrderId(String orderId);

    @Query("SELECT * FROM order_items WHERE item_id = :itemId")
    List<OrderItem> getByItemId(String itemId);

    @Query("SELECT * FROM order_items")
    List<OrderItem> getAll();

    @Query("DELETE FROM order_items WHERE order_id = :orderId")
    void deleteByOrderId(String orderId);

    @Query("DELETE FROM order_items WHERE item_id = :itemId")
    void deleteByItemId(String itemId);

    @Query("SELECT COUNT(*) FROM order_items WHERE order_id = :orderId")
    int getCountByOrderId(String orderId);

    @Query("SELECT SUM(total_price + tax_amount) FROM order_items WHERE order_id = :orderId")
    double getTotalAmountByOrderId(String orderId);
}