package com.example.androidapp.data.entities;
import androidx.room.ForeignKey;import androidx.room.Index;import androidx.room.TypeConverters;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "point_transactions")
public class PointTransaction {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public int points;
}
