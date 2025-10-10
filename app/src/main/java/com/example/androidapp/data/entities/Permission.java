package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity(tableName = "permissions")
public class Permission {
    @PrimaryKey
    private @NonNull String id;
    private String action;
    private String description;
    private String group;

    public Permission(String id, String action, String description, String group) {
        this.id = id;
        this.action = action;
        this.description = description;
        this.group = group;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public String getGroup() {
        return group;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}

