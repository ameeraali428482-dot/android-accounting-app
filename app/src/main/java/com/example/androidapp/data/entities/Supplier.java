package com.example.androidapp.data.entities;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;


@Entity(tableName = "suppliers")
public class Supplier {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String contactInfo;
}
