package com.example.androidapp.data.entities;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;


@Entity(tableName = "repairs")
public class Repair {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String deviceName;
    public String issueDescription;
}
