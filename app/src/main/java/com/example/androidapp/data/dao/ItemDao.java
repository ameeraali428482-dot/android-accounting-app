package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Item;
import java.util.List;

@Dao
public interface ItemDao extends BaseDao<Item> {
    
    @Query("SELECT * FROM items WHERE id = :id")
    Item getById(int id);

    @Query("SELECT * FROM items ORDER BY name")
    List<Item> getAll();
    
    @Query("SELECT * FROM items ORDER BY name")
    List<Item> getAllItems();

    @Query("SELECT * FROM items WHERE category = :category ORDER BY name")
    List<Item> getByCategory(String category);

    @Query("SELECT * FROM items WHERE name LIKE '%' || :searchTerm || '%' OR code LIKE '%' || :searchTerm || '%' ORDER BY name")
    List<Item> searchItems(String searchTerm);

    @Query("SELECT COUNT(*) FROM items")
    int getCount();

    @Query("DELETE FROM items WHERE id = :id")
    void deleteById(int id);
}
