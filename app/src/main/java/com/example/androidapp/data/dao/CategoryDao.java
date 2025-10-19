package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao extends BaseDao<Category> {
    
    @Query("SELECT * FROM categories WHERE id = :id")
    Category getById(long id);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Category> getAll();

    @Query("SELECT * FROM categories WHERE is_active = 1 ORDER BY name ASC")
    List<Category> getActiveCategories();

    @Query("SELECT * FROM categories WHERE category_type = :categoryType ORDER BY name ASC")
    List<Category> getByCategoryType(String categoryType);

    @Query("SELECT * FROM categories WHERE category_type = :categoryType AND is_active = 1 ORDER BY name ASC")
    List<Category> getActiveByCategoryType(String categoryType);

    @Query("SELECT * FROM categories WHERE parent_category_id = :parentCategoryId ORDER BY name ASC")
    List<Category> getSubCategories(String parentCategoryId);

    @Query("SELECT * FROM categories WHERE parent_category_id IS NULL OR parent_category_id = '' ORDER BY name ASC")
    List<Category> getParentCategories();

    @Query("SELECT * FROM categories WHERE user_id = :userId ORDER BY name ASC")
    List<Category> getByUserId(String userId);

    @Query("SELECT * FROM categories WHERE company_id = :companyId ORDER BY name ASC")
    List<Category> getByCompanyId(String companyId);

    @Query("SELECT * FROM categories WHERE name LIKE '%' || :searchTerm || '%' ORDER BY name ASC")
    List<Category> searchByName(String searchTerm);

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    Category getByName(String name);

    @Query("SELECT COUNT(*) FROM categories WHERE category_type = :categoryType")
    int getCountByCategoryType(String categoryType);

    @Query("SELECT COUNT(*) FROM categories WHERE parent_category_id = :parentCategoryId")
    int getSubCategoryCount(String parentCategoryId);

    @Update
    void update(Category category);

    @Query("UPDATE categories SET is_active = 0 WHERE id = :categoryId")
    void deactivateCategory(long categoryId);

    @Query("UPDATE categories SET is_active = 1 WHERE id = :categoryId")
    void activateCategory(long categoryId);

    @Query("DELETE FROM categories WHERE is_active = 0")
    void deleteInactiveCategories();

    @Query("DELETE FROM categories WHERE company_id = :companyId")
    void deleteByCompanyId(String companyId);

    @Query("SELECT * FROM categories WHERE category_type = 'INCOME' AND is_active = 1 ORDER BY name ASC")
    List<Category> getIncomeCategories();

    @Query("SELECT * FROM categories WHERE category_type = 'EXPENSE' AND is_active = 1 ORDER BY name ASC")
    List<Category> getExpenseCategories();

    @Query("SELECT * FROM categories WHERE category_type = 'ASSET' AND is_active = 1 ORDER BY name ASC")
    List<Category> getAssetCategories();

    @Query("SELECT * FROM categories WHERE category_type = 'LIABILITY' AND is_active = 1 ORDER BY name ASC")
    List<Category> getLiabilityCategories();
}
