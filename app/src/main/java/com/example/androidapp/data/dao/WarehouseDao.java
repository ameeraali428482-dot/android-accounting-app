package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.androidapp.models.Warehouse;

import java.util.List;

@Dao
public interface WarehouseDao {
    @Insert
    void insert(Warehouse warehouse);

    @Update
    void update(Warehouse warehouse);

    @Delete
    void delete(Warehouse warehouse);

    @Query("SELECT * FROM warehouses WHERE companyId = :companyId")
    List<Warehouse> getAllWarehouses(String companyId);

    @Query("SELECT * FROM warehouses WHERE id = :id AND companyId = :companyId LIMIT 1")
    Warehouse getWarehouseById(String id, String companyId);

    @Query("SELECT COUNT(*) FROM warehouses WHERE name = :name AND companyId = :companyId")
    int countWarehouseByName(String name, String companyId);
}

