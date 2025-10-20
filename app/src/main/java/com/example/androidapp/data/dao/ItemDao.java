package com.example.androitemIdapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Item;
import java.util.List;

@Dao
public interface ItemDao extends BaseDao<Item> {
    
    @Query("SELECT * FROM items WHERE itemId = :itemId")
    Item getById(int itemId);

    @Query("SELECT * FROM items ORDER BY itemName")
    List<Item> getAll();
    
    @Query("SELECT * FROM items ORDER BY itemName")
    List<Item> getAllItems();

    @Query("SELECT * FROM items WHERE category = :category ORDER BY itemName")
    List<Item> getByCategory(String category);

    @Query("SELECT * FROM items WHERE itemName LIKE '%' || :searchTerm || '%' OR code LIKE '%' || :searchTerm || '%' ORDER BY itemName")
    List<Item> searchItems(String searchTerm);

    @Query("SELECT COUNT(*) FROM items")
    int getCount();

    @Query("DELETE FROM items WHERE itemId = :itemId")
    void deleteById(int itemId);
}
