package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Item;

import java.util.List;

@Dao
public interface ItemDao extends BaseDao<Item> {
    
    @Query("SELECT * FROM items WHERE id = :id")
    Item getById(long id);

    @Query("SELECT * FROM items ORDER BY name ASC")
    List<Item> getAll();

    @Query("SELECT * FROM items WHERE category_id = :categoryId ORDER BY name ASC")
    List<Item> getItemsByCategory(long categoryId);

    @Query("SELECT * FROM items WHERE company_id = :companyId ORDER BY name ASC")
    List<Item> getByCompanyId(String companyId);

    @Query("SELECT * FROM items WHERE name LIKE '%' || :searchTerm || '%' ORDER BY name ASC")
    List<Item> searchByName(String searchTerm);

    @Query("SELECT * FROM items WHERE is_active = 1 ORDER BY name ASC")
    List<Item> getActiveItems();

    @Query("SELECT COUNT(*) FROM items WHERE category_id = :categoryId")
    int getCountByCategory(long categoryId);

    @Query("UPDATE items SET is_active = 0 WHERE id = :id")
    void deactivateItem(long id);

    @Query("UPDATE items SET is_active = 1 WHERE id = :id")
    void activateItem(long id);

    @Query("DELETE FROM items WHERE company_id = :companyId")
    void deleteByCompanyId(String companyId);
}
