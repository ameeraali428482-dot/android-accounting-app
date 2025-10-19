package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Item;

import java.util.List;

@Dao
public interface ItemDao extends BaseDao<Item> {
    
    @Query("SELECT * FROM items WHERE id = :id")
    Item getById(long id);

    @Query("SELECT * FROM items")
    List<Item> getAll();

    @Query("SELECT * FROM items WHERE categoryId = :categoryId")
    List<Item> getItemsByCategory(long categoryId);

    @Query("SELECT * FROM items WHERE companyId = :companyId")
    List<Item> getByCompanyId(String companyId);

    @Query("SELECT * FROM items WHERE name LIKE '%' || :searchTerm || '%'")
    List<Item> searchByName(String searchTerm);

    @Query("SELECT * FROM items WHERE isActive = 1")
    List<Item> getActiveItems();

    @Query("SELECT COUNT(*) FROM items WHERE categoryId = :categoryId")
    int getCountByCategory(long categoryId);

    @Query("UPDATE items SET isActive = 0 WHERE id = :id")
    void deactivateItem(long id);

    @Query("UPDATE items SET isActive = 1 WHERE id = :id")
    void activateItem(long id);

    @Query("DELETE FROM items WHERE companyId = :companyId")
    void deleteByCompanyId(String companyId);
}
