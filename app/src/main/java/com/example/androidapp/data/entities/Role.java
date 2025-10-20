package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "roles")
public class Role {
    @PrimaryKey
    @NonNull
    public String role_id;
    public String name;
    public String description;
    public long createdAt;
    public long updatedAt;

    public Role() {}

    @Ignore
    public Role(@NonNull String role_id, String name, String description) {
        this.role_id = role_id;
        this.name = name;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
