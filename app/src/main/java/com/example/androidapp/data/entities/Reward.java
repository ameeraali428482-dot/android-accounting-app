package com.example.androidapp.data.entities;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;


@Entity(tableName = "rewards")
public class Reward {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public int pointsRequired;
}
