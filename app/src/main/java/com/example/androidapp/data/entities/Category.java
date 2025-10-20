package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "categories",
        foreignKeys = @ForeignKey(entity = User.class,
                                  parentColumns = "id",
                                  childColumns = "created_by",
                                  onDelete = ForeignKey.SET_NULL),
        indices = {@Index("created_by"), @Index("type")})
public class Category {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public String name;
    public String description;
    public String type; // "INCOME", "EXPENSE", "TRANSFER", "GENERAL"
    public String color; // للعرض في الواجهة
    public String icon; // أيقونة التصنيف
    
    @ColumnInfo(name = "created_by")
    public String createdBy;
    
    @ColumnInfo(name = "created_at")
    public long createdAt;
    
    @ColumnInfo(name = "last_modified")
    public long lastModified;
    
    @ColumnInfo(name = "is_active", defaultValue = "1")
    public boolean isActive;
    
    @ColumnInfo(name = "is_default", defaultValue = "0")
    public boolean isDefault; // للتصنيفات الافتراضية التي لا يمكن حذفها

    // Constructor الرئيسي لـ Room
    public Category(long id, String name, String description, String type, String color, 
                   String icon, String createdBy, long createdAt, long lastModified, 
                   boolean isActive, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.color = color;
        this.icon = icon;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.isActive = isActive;
        this.isDefault = isDefault;
    }

    // Constructor مبسط
    @Ignore
    public Category(String name, String type, String createdBy) {
        this.name = name;
        this.type = type;
        this.createdBy = createdBy;
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.isActive = true;
        this.isDefault = false;
    }

    // Constructor فارغ
    @Ignore
    public Category() {
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.isActive = true;
        this.isDefault = false;
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
