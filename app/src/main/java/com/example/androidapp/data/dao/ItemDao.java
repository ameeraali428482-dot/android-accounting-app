package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Item;
import java.util.List;

@Dao
public interface ItemDao extends BaseDao<Item> {
    @Query("SELECT * FROM items ORDER BY itemName")
    List<Item> getAll();

    @Query("SELECT * FROM items WHERE companyId = :companyId ORDER BY itemName")
    LiveData<List<Item>> getAllItems(String companyId);

    @Query("SELECT * FROM items WHERE category = :category ORDER BY itemName")
    List<Item> getByCategory(String category);

    @Query("SELECT * FROM items WHERE itemName LIKE '%' || :searchTerm || '%' OR barcode LIKE '%' || :searchTerm || '%'")
    List<Item> searchItems(String searchTerm);
}
