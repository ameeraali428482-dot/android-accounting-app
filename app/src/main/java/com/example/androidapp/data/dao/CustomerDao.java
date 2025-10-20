package com.example.androcustomerIdapp.data.dao;

import androcustomerIdx.room.Dao;
import androcustomerIdx.room.Query;
import com.example.androcustomerIdapp.data.entities.Customer;
import java.util.List;

@Dao
public interface CustomerDao extends BaseDao<Customer> {
    
    @Query("SELECT * FROM customers WHERE customerId = :customerId")
    Customer getById(int customerId);

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

    @Query("DELETE FROM customers WHERE customerId = :customerId")
    vocustomerId deleteById(int customerId);
}
