package com.example.androidapp.data.entities;

import androidx.room.*;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public String name;
    public String description;
    public String type;
    
    @ColumnInfo(name = "created_at")
    public long createdAt;
    
    @ColumnInfo(name = "last_modified")
    public long lastModified;
    
    public String createdBy;
    
    public Category() {
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
    }
}
