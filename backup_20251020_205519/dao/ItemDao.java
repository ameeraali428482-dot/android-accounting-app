package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Item;
import java.util.List;

@Dao
public interface ItemDao extends BaseDao<Item> {
    
    @Query("SELECT * FROM items ORDER BY name")
    List<Item> getAllItems();
    
    @Query("SELECT * FROM items WHERE id = :id")
    Item getItemById(long id);
    
    @Query("SELECT * FROM items WHERE categoryId = :categoryId ORDER BY name")
    List<Item> getItemsByCategory(long categoryId);
    
    @Query("SELECT * FROM items WHERE name LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%' ORDER BY name")
    List<Item> searchItems(String searchQuery);
    
    @Query("SELECT * FROM items WHERE is_active = 1 ORDER BY name")
    List<Item> getActiveItems();
    
    @Query("SELECT COUNT(*) FROM items WHERE categoryId = :categoryId")
    int getCountByCategory(long categoryId);
    
    @Query("UPDATE items SET is_active = 0 WHERE id = :id")
    void deactivateItem(long id);
    
    @Query("UPDATE items SET is_active = 1 WHERE id = :id")
    void activateItem(long id);
    
    @Query("UPDATE items SET stockQuantity = stockQuantity - :quantity WHERE id = :id")
    void decreaseStock(long id, int quantity);
    
    @Query("UPDATE items SET stockQuantity = stockQuantity + :quantity WHERE id = :id")
    void increaseStock(long id, int quantity);
    
    @Query("DELETE FROM items WHERE id = :id")
    void deleteItem(long id);
}
