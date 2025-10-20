package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Product;
import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products WHERE companyId = :companyId")
    LiveData<List<Product>> getAllProducts(String companyId);

    @Query("SELECT * FROM products WHERE companyId = :companyId")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products WHERE quantity <= minStockLevel AND companyId = :companyId")
    List<Product> getLowStockProducts(String companyId);

    @Query("SELECT * FROM products WHERE quantity <= minStockLevel")
    List<Product> getLowStockProducts();

    @Query("SELECT * FROM products WHERE name LIKE :searchPattern")
    List<Product> searchProducts(String searchPattern);

    @Query("SELECT * FROM products WHERE id = :id AND companyId = :companyId LIMIT 1")
    Product getProductById(String id, String companyId);

    @Query("SELECT COUNT(*) FROM products WHERE name = :name AND companyId = :companyId")
    int countProductByNameAndCompanyId(String name, String companyId);
}
