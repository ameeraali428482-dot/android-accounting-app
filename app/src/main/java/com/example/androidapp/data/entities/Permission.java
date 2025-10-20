package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "permissions")
public class Permission {
    @PrimaryKey
    @NonNull
    public String permission_id;
    public String name;
    public String description;
    public String category;
    public long createdAt;
    public long updatedAt;

    public Permission() {}

    @Ignore
    public Permission(@NonNull String permission_id, String name, String description, String category) {
        this.permission_id = permission_id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
