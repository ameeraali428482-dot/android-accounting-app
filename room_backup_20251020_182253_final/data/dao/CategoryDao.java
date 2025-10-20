package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Category;
import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();
    
    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryById(int id);
    
    @Query("SELECT * FROM categories WHERE name LIKE :name")
    List<Category> getCategoriesByName(String name);
    
    @Insert
    void insertCategory(Category category);
    
    @Update
    void updateCategory(Category category);
    
    @Delete
    void deleteCategory(Category category);
}
