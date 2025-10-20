package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Customer;
import java.util.List;

@Dao
public interface CustomerDao extends BaseDao<Customer> {
    
    @Query("SELECT * FROM customers WHERE id = :id")
    Customer getById(int id);

    @Query("SELECT * FROM customers ORDER BY name")
    List<Customer> getAll();
    
    @Query("SELECT * FROM customers ORDER BY name")
    List<Customer> getAllCustomers();

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :searchTerm || '%' OR email LIKE '%' || :searchTerm || '%' ORDER BY name")
    List<Customer> searchCustomers(String searchTerm);

    @Query("SELECT * FROM customers WHERE email = :email")
    Customer getByEmail(String email);

    @Query("SELECT * FROM customers WHERE phone = :phone")
    Customer getByPhone(String phone);

    @Query("SELECT COUNT(*) FROM customers")
    int getCount();

    @Query("DELETE FROM customers WHERE id = :id")
    void deleteById(int id);
}
