package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Category;
import java.util.List;

@Dao
public interface CategoryDao extends BaseDao<Category> {
    
    @Query("SELECT * FROM categories ORDER BY name")
    List<Category> getAllCategories();
    
    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryById(long id);
    
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name")
    List<Category> getCategoriesByType(String type);
    
    @Query("SELECT * FROM categories WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name")
    List<Category> searchCategories(String searchQuery);
    
    @Query("SELECT COUNT(*) FROM categories")
    int getCategoriesCount();
    
    @Query("DELETE FROM categories WHERE id = :id")
    void deleteCategory(long id);
}
