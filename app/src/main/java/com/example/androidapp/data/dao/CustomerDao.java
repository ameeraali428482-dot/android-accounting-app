package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Customer;
import java.util.List;

@Dao
public interface CustomerDao extends BaseDao<Customer> {
    
    @Query("SELECT * FROM customers WHERE id = :customerId")
    Customer getById(String customerId);

    @Query("SELECT * FROM customers ORDER BY customerName")
    List<Customer> getAll();
    
    @Query("SELECT * FROM customers ORDER BY customerName")
    List<Customer> getAllCustomers();

    @Query("SELECT * FROM customers WHERE customerName LIKE '%' || :searchTerm || '%' OR email LIKE '%' || :searchTerm || '%' ORDER BY customerName")
    List<Customer> searchCustomers(String searchTerm);

    @Query("SELECT * FROM customers WHERE email = :email")
    Customer getByEmail(String email);

    @Query("SELECT * FROM customers WHERE phone = :phone")
    Customer getByPhone(String phone);

    @Query("SELECT COUNT(*) FROM customers")
    int getCount();

    @Query("DELETE FROM customers WHERE id = :customerId")
    void deleteById(String customerId);
}
