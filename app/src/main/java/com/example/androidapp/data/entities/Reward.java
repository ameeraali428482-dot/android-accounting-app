package com.example.androidapp.data.entities;
import androidx.room.ForeignKey;import androidx.room.Index;import androidx.room.TypeConverters;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rewards")
public class Reward {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public int pointsRequired;
}
