package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.OrderItem;

import java.util.List;

@Dao
public interface OrderItemDao extends BaseDao<OrderItem> {
    
    @Query("SELECT * FROM order_items WHERE id = :id")
    OrderItem getById(long id);

    @Query("SELECT * FROM order_items WHERE order_id = :orderId")
    List<OrderItem> getByOrderId(String orderId);

    @Query("SELECT * FROM order_items WHERE item_id = :itemId")
    List<OrderItem> getByItemId(String itemId);

    @Query("SELECT * FROM order_items ORDER BY created_at DESC")
    List<OrderItem> getAll();

    @Query("DELETE FROM order_items WHERE order_id = :orderId")
    void deleteByOrderId(String orderId);

    @Query("DELETE FROM order_items WHERE item_id = :itemId")
    void deleteByItemId(String itemId);

    @Query("SELECT COUNT(*) FROM order_items WHERE order_id = :orderId")
    int getCountByOrderId(String orderId);

    @Query("SELECT SUM(total_price) FROM order_items WHERE order_id = :orderId")
    double getTotalAmountByOrderId(String orderId);

    @Query("SELECT SUM(quantity) FROM order_items WHERE item_id = :itemId")
    int getTotalQuantityByItemId(String itemId);
}
