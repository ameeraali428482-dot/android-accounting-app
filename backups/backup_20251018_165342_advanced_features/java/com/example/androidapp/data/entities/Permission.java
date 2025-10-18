package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "permissions")
public class Permission {
    @PrimaryKey
    @NonNull
    private String id;
    private String action;
    private String description;
    private String group;

    public Permission(@NonNull String id, String action, String description, String group) {
        this.id = id;
        this.action = action;
        this.description = description;
        this.group = group;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getAction() { return action; }
    public String getDescription() { return description; }
    public String getGroup() { return group; }
    public String getName() { return action; } // Alias for action

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setAction(String action) { this.action = action; }
    public void setDescription(String description) { this.description = description; }
    public void setGroup(String group) { this.group = group; }
}
