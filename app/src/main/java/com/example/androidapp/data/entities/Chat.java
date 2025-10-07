package com.example.androidapp.data.entities;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;


@Entity(tableName = "chats")
public class Chat {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String message;
}
