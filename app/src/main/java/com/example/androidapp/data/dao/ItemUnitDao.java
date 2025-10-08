package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.ItemUnit;

@Dao
public interface ItemUnitDao {
    @Insert
    void insert(ItemUnit itemunit);

    @Update
    void update(ItemUnit itemunit);

    @Delete
    void delete(ItemUnit itemunit);

    @Query("SELECT * FROM itemunits")
    List<ItemUnit> getAllItemUnits();

    @Query("SELECT * FROM itemunits WHERE id = :id LIMIT 1")
    ItemUnit getItemUnitById(String id);
}
