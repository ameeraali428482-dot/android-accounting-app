import androidx.room.Ignore;
package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Index;

@Entity(tableName = "categories",
        indices = {
            @Index(value = {"name"}, unique = true),
            @Index(value = {"category_type"}),
            @Index(value = {"parent_category_id"}),
            @Index(value = {"user_id"}),
            @Index(value = {"company_id"})
        })
public class Category {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "category_type")
    public String categoryType;

    @ColumnInfo(name = "parent_category_id")
    public String parentCategoryId;

    @ColumnInfo(name = "is_active")
    public boolean isActive = true;

    @ColumnInfo(name = "color")
    public String color;

    @ColumnInfo(name = "icon")
    public String icon;

    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "company_id")
    public String companyId;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    public Category() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    @Ignore
    public Category(String name, String categoryType) {
        this();
        this.name = name;
        this.categoryType = categoryType;
    }
}
