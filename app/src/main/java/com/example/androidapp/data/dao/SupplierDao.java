package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.Supplier;

@Dao
public interface SupplierDao {
    @Insert
    void insert(Supplier supplier);

    @Update
    void update(Supplier supplier);

    @Delete
    void delete(Supplier supplier);

    @Query("SELECT * FROM suppliers")
    List<Supplier> getAllSuppliers();

    @Query("SELECT * FROM suppliers WHERE id = :id LIMIT 1")
    Supplier getById(String id);

    @Query("SELECT * FROM suppliers WHERE companyId = :companyId")
    LiveData<List<Supplier>> getAllSuppliers(String companyId);
    
    @Query("SELECT * FROM suppliers WHERE companyId = :companyId")
    List<Supplier> getSuppliersByCompanyId(String companyId);
}
