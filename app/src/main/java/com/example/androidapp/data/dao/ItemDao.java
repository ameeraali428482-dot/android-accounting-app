package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Item;
import java.util.List;

@Dao
public interface ItemDao extends BaseDao<Item> {
    
    // تم تغيير 'id' إلى 'itemId'
    @Query("SELECT * FROM items WHERE itemId = :itemId")
    Item getById(String itemId);

    @Query("SELECT * FROM items ORDER BY name")
    List<Item> getAll();
    
    // تم افتراض أن المفتاح الخارجي هو 'companyId' وهو ما يسبب المشكلة
    @Query("SELECT * FROM items WHERE companyId = :companyId ORDER BY name")
    LiveData<List<Item>> getAllItems(String companyId);

    @Query("SELECT * FROM items WHERE category = :category ORDER BY name")
    List<Item> getByCategory(String category);

    @Query("SELECT * FROM items WHERE name LIKE '%' || :searchTerm || '%' OR barcode LIKE '%' || :searchTerm || '%' ORDER BY name")
    List<Item> searchItems(String searchTerm);

    @Query("SELECT COUNT(*) FROM items")
    int getCount();

    // تم تغيير 'id' إلى 'itemId'
    @Query("DELETE FROM items WHERE itemId = :itemId")
    void deleteById(String itemId);
}
