package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.androidapp.data.entities.Item;

import java.util.List;

@Dao
public interface ItemDao {
    @Insert
    void insert(Item item);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);

    @Query("SELECT * FROM items WHERE companyId = :companyId")
    List<Item> getAllItems(String companyId);

    @Query("SELECT * FROM items WHERE id = :id AND companyId = :companyId LIMIT 1")
    Item getItemById(String id, String companyId);

    @Query("SELECT COUNT(*) FROM items WHERE name = :name AND companyId = :companyId")
    int countItemByNameAndCompanyId(String name, String companyId);
}
