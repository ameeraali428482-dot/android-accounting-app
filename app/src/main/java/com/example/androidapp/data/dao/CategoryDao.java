package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Category> categories);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE id = :id")
    Category getById(String id);

    @Query("SELECT * FROM categories")
    List<Category> getAll();

    @Query("SELECT * FROM categories WHERE is_active = 1")
    List<Category> getActiveCategories();

    @Query("SELECT * FROM categories WHERE category_type = :categoryType")
    List<Category> getByCategoryType(String categoryType);

    @Query("SELECT * FROM categories WHERE category_type = :categoryType AND is_active = 1")
    List<Category> getActiveByCategoryType(String categoryType);

    @Query("SELECT * FROM categories WHERE parent_category_id = :parentCategoryId")
    List<Category> getSubCategories(String parentCategoryId);

    @Query("SELECT * FROM categories WHERE parent_category_id IS NULL")
    List<Category> getParentCategories();

    @Query("SELECT * FROM categories WHERE user_id = :userId")
    List<Category> getByUserId(String userId);

    @Query("SELECT * FROM categories WHERE company_id = :companyId")
    List<Category> getByCompanyId(String companyId);

    @Query("SELECT * FROM categories WHERE name LIKE '%' || :searchTerm || '%'")
    List<Category> searchByName(String searchTerm);

    @Query("SELECT * FROM categories WHERE name = :name")
    Category getByName(String name);

    @Query("SELECT COUNT(*) FROM categories WHERE category_type = :categoryType")
    int getCountByCategoryType(String categoryType);

    @Query("SELECT COUNT(*) FROM categories WHERE parent_category_id = :parentCategoryId")
    int getSubCategoryCount(String parentCategoryId);

    @Query("UPDATE categories SET is_active = 0 WHERE id = :categoryId")
    void deactivateCategory(String categoryId);

    @Query("UPDATE categories SET is_active = 1 WHERE id = :categoryId")
    void activateCategory(String categoryId);

    @Query("DELETE FROM categories WHERE is_active = 0")
    void deleteInactiveCategories();

    @Query("DELETE FROM categories WHERE company_id = :companyId")
    void deleteByCompanyId(String companyId);

    // Income categories
    @Query("SELECT * FROM categories WHERE category_type = 'INCOME' AND is_active = 1")
    List<Category> getIncomeCategories();

    // Expense categories
    @Query("SELECT * FROM categories WHERE category_type = 'EXPENSE' AND is_active = 1")
    List<Category> getExpenseCategories();

    // Asset categories
    @Query("SELECT * FROM categories WHERE category_type = 'ASSET' AND is_active = 1")
    List<Category> getAssetCategories();

    // Liability categories
    @Query("SELECT * FROM categories WHERE category_type = 'LIABILITY' AND is_active = 1")
    List<Category> getLiabilityCategories();
}
