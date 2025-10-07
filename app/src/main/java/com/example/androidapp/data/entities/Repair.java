package com.example.androidapp.data.entities;
import androidx.room.ForeignKey;import androidx.room.Index;import androidx.room.TypeConverters;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "repairs")
public class Repair {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String deviceName;
    public String issueDescription;
}
