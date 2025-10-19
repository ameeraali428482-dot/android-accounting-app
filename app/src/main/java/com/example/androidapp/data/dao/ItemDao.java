package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Item;
import java.util.List;

@Dao
public interface ItemDao extends BaseDao<Item> {
    
    @Query("SELECT * FROM items")
    List<Item> getAllItems();

    @Query("SELECT * FROM items WHERE companyId = :companyId")
    List<Item> getItemsByCompany(int companyId);

    @Query("SELECT * FROM items WHERE id = :id")
    Item getItemById(int id);

    @Query("SELECT * FROM items WHERE barcode = :barcode")
    Item getItemByBarcode(String barcode);

    @Query("SELECT * FROM items WHERE categoryId = :categoryId")
    List<Item> getItemsByCategory(int categoryId);

    @Query("SELECT * FROM items WHERE quantity < minStockLevel")
    List<Item> getLowStockItems();

    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    List<Item> searchItems(String query);

    @Query("UPDATE items SET quantity = :quantity WHERE id = :itemId")
    void updateQuantity(int itemId, double quantity);

    @Query("SELECT COUNT(*) FROM items WHERE companyId = :companyId")
    int getItemCount(int companyId);

    // طرق مطلوبة للتوافق مع Product
    @Query("SELECT * FROM items")
    List<Item> getAllProducts();

    @Query("SELECT * FROM items WHERE quantity < minStockLevel")
    List<Item> getLowStockProducts();

    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%'")
    List<Item> searchProducts(String query);
}
