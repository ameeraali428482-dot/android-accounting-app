package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Item;
import java.util.List;

@Dao
public interface ProductDao extends BaseDao<Item> {
    @Query("SELECT * FROM items ORDER BY itemName")
    List<Item> getAllProducts();

    @Query("SELECT * FROM items WHERE companyId = :companyId ORDER BY itemName")
    LiveData<List<Item>> getProductsByCompany(String companyId);

    @Query("SELECT * FROM items WHERE itemId = :id")
    Item getById(int id);
}
