package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.Customer;

@Dao
public interface CustomerDao {
    @Insert
    void insert(Customer customer);

    @Update
    void update(Customer customer);

    @Delete
    void delete(Customer customer);

    @Query("SELECT * FROM customers")
    List<Customer> getAllCustomers();

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    Customer getById(String id);

    @Query("SELECT * FROM customers WHERE companyId = :companyId")
    List<Customer> getCustomersByCompanyId(String companyId);
}
