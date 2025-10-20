package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Category;
import java.util.List;

@Dao
public interface CategoryDao extends BaseDao<Category> {
    
    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryById(long id);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Category> getAllCategories();

    @Query("SELECT * FROM categories WHERE is_active = 1 ORDER BY name ASC")
    List<Category> getActiveCategories();
    
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    List<Category> getCategoriesByType(String type);

    @Query("SELECT * FROM categories WHERE type = :type AND is_active = 1 ORDER BY name ASC")
    List<Category> getActiveCategoriesByType(String type);

    @Query("SELECT * FROM categories WHERE created_by = :createdBy ORDER BY name ASC")
    List<Category> getCategoriesByCreator(String createdBy);

    @Query("SELECT * FROM categories WHERE created_by = :createdBy AND is_active = 1 ORDER BY name ASC")
    List<Category> getActiveCategoriesByCreator(String createdBy);
    
    @Query("SELECT * FROM categories WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    List<Category> searchCategories(String searchQuery);

    @Query("SELECT * FROM categories WHERE name LIKE '%' || :searchQuery || '%' AND is_active = 1 ORDER BY name ASC")
    List<Category> searchActiveCategories(String searchQuery);

    @Query("SELECT * FROM categories WHERE (name LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%') AND is_active = 1 ORDER BY name ASC")
    List<Category> searchCategoriesExtended(String searchQuery);

    @Query("SELECT * FROM categories WHERE is_default = 1 ORDER BY name ASC")
    List<Category> getDefaultCategories();

    @Query("SELECT * FROM categories WHERE is_default = 0 AND created_by = :createdBy ORDER BY name ASC")
    List<Category> getCustomCategoriesByCreator(String createdBy);
    
    @Query("SELECT COUNT(*) FROM categories")
    int getCategoriesCount();

    @Query("SELECT COUNT(*) FROM categories WHERE is_active = 1")
    int getActiveCategoriesCount();

    @Query("SELECT COUNT(*) FROM categories WHERE type = :type")
    int getCategoriesCountByType(String type);

    @Query("SELECT COUNT(*) FROM categories WHERE type = :type AND is_active = 1")
    int getActiveCategoriesCountByType(String type);

    @Query("SELECT COUNT(*) FROM categories WHERE created_by = :createdBy")
    int getCategoriesCountByCreator(String createdBy);

    @Query("UPDATE categories SET is_active = 0, last_modified = :lastModified WHERE id = :id AND is_default = 0")
    void deactivateCategory(long id, long lastModified);

    @Query("UPDATE categories SET is_active = 1, last_modified = :lastModified WHERE id = :id")
    void activateCategory(long id, long lastModified);

    @Query("UPDATE categories SET name = :name, description = :description, last_modified = :lastModified WHERE id = :id")
    void updateCategoryDetails(long id, String name, String description, long lastModified);

    @Query("UPDATE categories SET color = :color, icon = :icon, last_modified = :lastModified WHERE id = :id")
    void updateCategoryAppearance(long id, String color, String icon, long lastModified);

    @Query("UPDATE categories SET type = :type, last_modified = :lastModified WHERE id = :id")
    void updateCategoryType(long id, String type, long lastModified);
    
    @Query("DELETE FROM categories WHERE id = :id AND is_default = 0")
    void deleteCategory(long id);

    @Query("DELETE FROM categories WHERE created_by = :createdBy AND is_default = 0")
    void deleteCategoriesByCreator(String createdBy);

    // استعلامات إضافية مفيدة
    @Query("SELECT DISTINCT type FROM categories WHERE is_active = 1")
    List<String> getDistinctTypes();

    @Query("SELECT DISTINCT color FROM categories WHERE is_active = 1 AND color IS NOT NULL")
    List<String> getUsedColors();

    @Query("SELECT * FROM categories WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    List<Category> getCategoriesByDateRange(long startDate, long endDate);
}
