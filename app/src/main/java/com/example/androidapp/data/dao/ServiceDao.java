package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.Service;

@Dao
public interface ServiceDao {
    @Insert
    void insert(Service service);

    @Update
    void update(Service service);

    @Delete
    void delete(Service service);

    @Query("SELECT * FROM services")
    List<Service> getAllServices();

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    Service getServiceById(String id);
}
