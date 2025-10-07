package com.example.androidapp.data.entities;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;


@Entity(tableName = "point_transactions")
public class PointTransaction {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public int points;
}
